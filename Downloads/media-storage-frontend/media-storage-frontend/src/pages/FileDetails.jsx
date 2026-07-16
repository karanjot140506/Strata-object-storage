import { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import AppShell from '../components/AppShell';
import Modal from '../components/Modal';
import StatusBadge from '../components/StatusBadge';
import HashPill from '../components/HashPill';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';

function formatBytes(bytes) {
  if (!bytes && bytes !== 0) return '—';
  if (bytes === 0) return '0 B';
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${units[i]}`;
}

function formatDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });
}

export default function FileDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const { push } = useToast();

  const [meta, setMeta] = useState(null);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [modal, setModal] = useState(null); // 'rename' | 'move' | 'copy'
  const [inputValue, setInputValue] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await api.getMetadata(id);
      setMeta(data);
    } catch (err) {
      push(err.message, 'error');
    } finally {
      setLoading(false);
    }
  }, [id, push]);

  useEffect(() => { load(); }, [load]);

  const runAction = async (fn, successMsg) => {
    setBusy(true);
    try {
      await fn();
      push(successMsg, 'success');
      await load();
    } catch (err) {
      push(err.message, 'error');
    } finally {
      setBusy(false);
    }
  };

  const handleDownload = async () => {
    setBusy(true);
    try {
      const { blob, filename } = await api.downloadFile(id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = meta?.originalName || filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      push(err.message, 'error');
    } finally {
      setBusy(false);
    }
  };

  const handleArchive = () => runAction(() => api.archiveFile(id), 'File archived');
  const handleRestore = () => runAction(() => api.restoreFile(id), 'File restored');
  const handleSoftDelete = () => runAction(() => api.softDeleteFile(id), 'File moved to trash');
  const handlePermanentDelete = async () => {
    if (!window.confirm('Permanently delete this file? This cannot be undone.')) return;
    setBusy(true);
    try {
      await api.permanentDeleteFile(id);
      push('File permanently deleted', 'success');
      navigate('/dashboard');
    } catch (err) {
      push(err.message, 'error');
      setBusy(false);
    }
  };

  const openModal = (type, initial = '') => {
    setInputValue(initial);
    setModal(type);
  };

  const submitModal = async () => {
    if (!inputValue.trim()) return;
    try {
      if (modal === 'rename') {
        await api.renameFile({ bucketName: meta.bucketName, oldStoredName: meta.storedName, newStoredName: inputValue.trim() });
        push('File renamed', 'success');
      } else if (modal === 'move') {
        await api.moveFile({ sourceBucket: meta.bucketName, targetBucket: inputValue.trim(), objectKey: meta.storedName });
        push('File moved', 'success');
      } else if (modal === 'copy') {
        await api.copyFile({ sourceBucket: meta.bucketName, targetBucket: inputValue.trim(), objectKey: meta.storedName });
        push('File copied', 'success');
      }
      setModal(null);
      await load();
    } catch (err) {
      push(err.message, 'error');
    }
  };

  if (loading) {
    return (
      <AppShell>
        <div className="loader"><span className="spinner" /> Loading file…</div>
      </AppShell>
    );
  }

  if (!meta) {
    return (
      <AppShell>
        <div className="empty-state">
          <div className="empty-state-title">File not found</div>
        </div>
      </AppShell>
    );
  }

  return (
    <AppShell>
      <div className="back-link" onClick={() => navigate('/dashboard')}>← Back to dashboard</div>

      <div className="page-header">
        <div>
          <h1 className="page-title">{meta.originalName}</h1>
          <p className="page-subtitle">
            <span className="mono">{meta.bucketName}</span> · <StatusBadge status={meta.status} />
          </p>
        </div>
        <HashPill value={meta.checksum} />
      </div>

      <div className="details-grid">
        <div className="details-item">
          <div className="details-label">Content type</div>
          <div className="details-value mono">{meta.contentType || '—'}</div>
        </div>
        <div className="details-item">
          <div className="details-label">Size</div>
          <div className="details-value">{formatBytes(meta.fileSize)}</div>
        </div>
        <div className="details-item">
          <div className="details-label">Uploaded by</div>
          <div className="details-value">{meta.uploadedBy || '—'}</div>
        </div>
        <div className="details-item">
          <div className="details-label">Uploaded at</div>
          <div className="details-value">{formatDate(meta.uploadedAt)}</div>
        </div>
        <div className="details-item">
          <div className="details-label">Last updated</div>
          <div className="details-value">{formatDate(meta.updatedAt)}</div>
        </div>
        <div className="details-item">
          <div className="details-label">Stored name</div>
          <div className="details-value mono">{meta.storedName}</div>
        </div>
        {meta.archivedAt && (
          <div className="details-item">
            <div className="details-label">Archived at</div>
            <div className="details-value">{formatDate(meta.archivedAt)}</div>
          </div>
        )}
        {meta.deletedAt && (
          <div className="details-item">
            <div className="details-label">Deleted at</div>
            <div className="details-value">{formatDate(meta.deletedAt)}</div>
          </div>
        )}
        <div className="details-item">
          <div className="details-label">ETag</div>
          <div className="details-value mono">{meta.etag || '—'}</div>
        </div>
      </div>

      <div className="action-row">
        <button className="btn btn-primary" onClick={handleDownload} disabled={busy}>↓ Download</button>
        <button className="btn btn-ghost" onClick={() => openModal('rename', meta.storedName)} disabled={busy}>Rename</button>
        <button className="btn btn-ghost" onClick={() => openModal('move')} disabled={busy}>Move to bucket</button>
        <button className="btn btn-ghost" onClick={() => openModal('copy')} disabled={busy}>Copy to bucket</button>

        {meta.status === 'ACTIVE' && (
          <button className="btn btn-ghost" onClick={handleArchive} disabled={busy}>Archive</button>
        )}
        {meta.status === 'ARCHIVED' && (
          <button className="btn btn-ghost" onClick={handleRestore} disabled={busy}>Restore</button>
        )}
        {meta.status !== 'DELETED' && (
          <button className="btn btn-danger" onClick={handleSoftDelete} disabled={busy}>Move to trash</button>
        )}
        {meta.status === 'DELETED' && isAdmin && (
          <button className="btn btn-danger" onClick={handlePermanentDelete} disabled={busy}>Permanently delete</button>
        )}
      </div>

      {modal && (
        <Modal
          title={modal === 'rename' ? 'Rename file' : modal === 'move' ? 'Move to bucket' : 'Copy to bucket'}
          onClose={() => setModal(null)}
          actions={
            <>
              <button className="btn btn-ghost" onClick={() => setModal(null)}>Cancel</button>
              <button className="btn btn-primary" onClick={submitModal}>Confirm</button>
            </>
          }
        >
          <div className="field">
            <label>{modal === 'rename' ? 'New file name' : 'Target bucket'}</label>
            <input autoFocus value={inputValue} onChange={(e) => setInputValue(e.target.value)} />
          </div>
        </Modal>
      )}
    </AppShell>
  );
}
