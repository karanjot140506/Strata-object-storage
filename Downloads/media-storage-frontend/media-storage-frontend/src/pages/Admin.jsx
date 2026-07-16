import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import AppShell from '../components/AppShell';
import StorageBar from '../components/StorageBar';
import HashPill from '../components/HashPill';
import { api } from '../api/client';
import { useToast } from '../components/Toast';

function formatBytes(bytes) {
  if (!bytes && bytes !== 0) return '—';
  if (bytes === 0) return '0 B';
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${units[i]}`;
}

export default function Admin() {
  const { push } = useToast();
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [trash, setTrash] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [statsData, trashPage] = await Promise.all([
        api.getStatistics(),
        api.listDeleted(0, 20),
      ]);
      setStats(statsData);
      setTrash(trashPage.content || []);
    } catch (err) {
      push(err.message, 'error');
    } finally {
      setLoading(false);
    }
  }, [push]);

  useEffect(() => { load(); }, [load]);

  const handlePermanentDelete = async (id) => {
    if (!window.confirm('Permanently delete this file? This cannot be undone.')) return;
    setBusyId(id);
    try {
      await api.permanentDeleteFile(id);
      push('File permanently deleted', 'success');
      load();
    } catch (err) {
      push(err.message, 'error');
    } finally {
      setBusyId(null);
    }
  };

  if (loading) {
    return (
      <AppShell>
        <div className="loader"><span className="spinner" /> Loading control room…</div>
      </AppShell>
    );
  }

  const statusSegments = stats ? [
    { label: 'Active', value: stats.activeFiles, color: 'var(--active)' },
    { label: 'Archived', value: stats.archivedFiles, color: 'var(--archived)' },
    { label: 'Deleted', value: stats.deletedFiles, color: 'var(--deleted)' },
  ] : [];

  const maxTypeCount = stats?.fileTypeBreakdown?.length
    ? Math.max(...stats.fileTypeBreakdown.map((t) => t.count))
    : 1;

  return (
    <AppShell>
      <div className="page-header">
        <div>
          <h1 className="page-title">Control Room</h1>
          <p className="page-subtitle">Storage-wide statistics and trash management — admin only.</p>
        </div>
      </div>

      <div className="stat-grid">
        <div className="stat-card">
          <div className="stat-label">Total files</div>
          <div className="stat-value">{stats?.totalFiles ?? 0}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Total buckets</div>
          <div className="stat-value">{stats?.totalBuckets ?? 0}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Storage used</div>
          <div className="stat-value">{formatBytes(stats?.totalStorageBytes)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">In trash</div>
          <div className="stat-value">{stats?.deletedFiles ?? 0}</div>
        </div>
      </div>

      <div className="dash-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
        <div className="panel">
          <div className="panel-header"><h3>Lifecycle strata</h3></div>
          <div style={{ padding: 18 }}>
            <StorageBar segments={statusSegments} height={14} />
            <div className="legend">
              {statusSegments.map((s) => (
                <div className="legend-item" key={s.label}>
                  <span className="legend-dot" style={{ background: s.color }} />
                  {s.label} · {s.value}
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="panel">
          <div className="panel-header"><h3>File types</h3></div>
          <div style={{ padding: '8px 18px 18px' }}>
            {!stats?.fileTypeBreakdown?.length ? (
              <div className="empty-state" style={{ padding: '20px 0' }}>No files uploaded yet.</div>
            ) : stats.fileTypeBreakdown.slice(0, 6).map((t) => (
              <div key={t.fileType} style={{ marginBottom: 12 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12.5, marginBottom: 5 }}>
                  <span className="mono">{t.fileType}</span>
                  <span style={{ color: 'var(--text-faint)' }}>{t.count} · {formatBytes(t.totalSize)}</span>
                </div>
                <StorageBar segments={[{ label: t.fileType, value: t.count, color: 'var(--signal)' }, { label: '', value: maxTypeCount - t.count, color: 'var(--surface-hover)' }]} height={6} />
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="panel" style={{ marginTop: 22 }}>
        <div className="panel-header"><h3>Trash · {trash.length} file{trash.length !== 1 ? 's' : ''}</h3></div>
        {trash.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-title">Trash is empty</div>
          </div>
        ) : (
          <table className="file-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Bucket</th>
                <th>Checksum</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {trash.map((f) => (
                <tr key={f.id}>
                  <td>
                    <div className="file-name" style={{ cursor: 'pointer' }} onClick={() => navigate(`/files/${f.id}`)}>{f.originalName}</div>
                  </td>
                  <td className="mono">{f.bucketName}</td>
                  <td><HashPill value={f.checksum} /></td>
                  <td style={{ textAlign: 'right' }}>
                    <button className="btn btn-danger btn-sm" onClick={() => handlePermanentDelete(f.id)} disabled={busyId === f.id}>
                      {busyId === f.id ? 'Deleting…' : 'Delete forever'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </AppShell>
  );
}
