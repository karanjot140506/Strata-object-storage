import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import AppShell from '../components/AppShell';
import Modal from '../components/Modal';
import StatusBadge from '../components/StatusBadge';
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

export default function Dashboard() {
  const { user, isAdmin } = useAuth();
  const { push } = useToast();
  const navigate = useNavigate();

  const [buckets, setBuckets] = useState([]);
  const [selectedBucket, setSelectedBucket] = useState(null);
  const [files, setFiles] = useState([]);
  const [search, setSearch] = useState('');
  const [loadingBuckets, setLoadingBuckets] = useState(true);
  const [loadingFiles, setLoadingFiles] = useState(false);
  const [showNewBucket, setShowNewBucket] = useState(false);
  const [newBucketName, setNewBucketName] = useState('');
  const [uploading, setUploading] = useState(false);

  const loadBuckets = useCallback(async () => {
    setLoadingBuckets(true);
    try {
      const data = await api.getBuckets();
      setBuckets(data || []);
      if (data && data.length > 0 && !selectedBucket) {
        setSelectedBucket(data[0]);
      }
    } catch (err) {
      push(err.message, 'error');
    } finally {
      setLoadingBuckets(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadFiles = useCallback(async (bucketName) => {
    if (!bucketName) return;
    setLoadingFiles(true);
    try {
      const page = await api.searchByBucket(bucketName);
      setFiles(page.content || []);
    } catch (err) {
      push(err.message, 'error');
    } finally {
      setLoadingFiles(false);
    }
  }, [push]);

  useEffect(() => { loadBuckets(); }, [loadBuckets]);
  useEffect(() => { if (selectedBucket) loadFiles(selectedBucket); }, [selectedBucket, loadFiles]);

  const handleCreateBucket = async (e) => {
    e.preventDefault();
    if (!newBucketName.trim()) return;
    try {
      await api.createBucket(newBucketName.trim());
      push(`Bucket "${newBucketName.trim()}" created`, 'success');
      setShowNewBucket(false);
      setSelectedBucket(newBucketName.trim());
      setNewBucketName('');
      loadBuckets();
    } catch (err) {
      push(err.message, 'error');
    }
  };

  const handleDeleteBucket = async (bucketName, e) => {
    e.stopPropagation();
    if (!window.confirm(`Delete bucket "${bucketName}"? This cannot be undone.`)) return;
    try {
      await api.deleteBucket(bucketName);
      push(`Bucket "${bucketName}" deleted`, 'success');
      if (selectedBucket === bucketName) setSelectedBucket(null);
      loadBuckets();
    } catch (err) {
      push(err.message, 'error');
    }
  };

  const handleUpload = async (e) => {
    const file = e.target.files[0];
    if (!file || !selectedBucket) return;
    setUploading(true);
    try {
      await api.uploadFile(selectedBucket, file, user.username);
      push(`Uploaded "${file.name}"`, 'success');
      loadFiles(selectedBucket);
    } catch (err) {
      push(err.message, 'error');
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  };

  const filteredFiles = search
    ? files.filter((f) => f.originalName?.toLowerCase().includes(search.toLowerCase()))
    : files;

  return (
    <AppShell>
      <div className="page-header">
        <div>
          <h1 className="page-title">Dashboard</h1>
          <p className="page-subtitle">Browse buckets, manage objects, and track their lifecycle.</p>
        </div>
        {isAdmin && (
          <button className="btn btn-primary" onClick={() => setShowNewBucket(true)}>+ New bucket</button>
        )}
      </div>

      <div className="dash-grid">
        <div className="panel">
          <div className="panel-header"><h3>Buckets</h3></div>
          {loadingBuckets ? (
            <div className="loader"><span className="spinner" /> Loading buckets…</div>
          ) : buckets.length === 0 ? (
            <div className="bucket-empty">No buckets yet.</div>
          ) : (
            <div className="bucket-list">
              {buckets.map((b) => (
                <div
                  key={b}
                  className={`bucket-item ${selectedBucket === b ? 'bucket-item-active' : ''}`}
                  onClick={() => setSelectedBucket(b)}
                >
                  <span className="mono">{b}</span>
                  {isAdmin && (
                    <button className="icon-btn" onClick={(e) => handleDeleteBucket(b, e)} title="Delete bucket">✕</button>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="panel">
          <div className="file-toolbar">
            <input
              className="search-input"
              placeholder={selectedBucket ? `Search files in ${selectedBucket}…` : 'Select a bucket first'}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              disabled={!selectedBucket}
            />
            <label className={`btn btn-primary btn-sm ${!selectedBucket || uploading ? 'btn-disabled' : ''}`} style={{ cursor: selectedBucket ? 'pointer' : 'not-allowed' }}>
              {uploading ? 'Uploading…' : '↑ Upload'}
              <input type="file" hidden onChange={handleUpload} disabled={!selectedBucket || uploading} />
            </label>
          </div>

          {!selectedBucket ? (
            <div className="empty-state">
              <div className="empty-state-title">No bucket selected</div>
              Choose a bucket on the left to see its files.
            </div>
          ) : loadingFiles ? (
            <div className="loader"><span className="spinner" /> Loading files…</div>
          ) : filteredFiles.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-title">This bucket is empty</div>
              Upload a file to get started.
            </div>
          ) : (
            <table className="file-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Size</th>
                  <th>Status</th>
                  <th>Uploaded</th>
                </tr>
              </thead>
              <tbody>
                {filteredFiles.map((f) => (
                  <tr key={f.id} className="file-row" onClick={() => navigate(`/files/${f.id}`)}>
                    <td>
                      <div className="file-name">{f.originalName}</div>
                      <div className="file-name-sub mono">{f.fileType || f.contentType}</div>
                    </td>
                    <td className="mono">{formatBytes(f.fileSize)}</td>
                    <td><StatusBadge status={f.status} /></td>
                    <td>{formatDate(f.uploadedAt)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {showNewBucket && (
        <Modal
          title="Create bucket"
          onClose={() => setShowNewBucket(false)}
          actions={
            <>
              <button className="btn btn-ghost" onClick={() => setShowNewBucket(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={handleCreateBucket}>Create</button>
            </>
          }
        >
          <form onSubmit={handleCreateBucket}>
            <div className="field">
              <label htmlFor="bucketName">Bucket name</label>
              <input
                id="bucketName"
                autoFocus
                value={newBucketName}
                onChange={(e) => setNewBucketName(e.target.value)}
                placeholder="e.g. product-images"
              />
            </div>
          </form>
        </Modal>
      )}
    </AppShell>
  );
}
