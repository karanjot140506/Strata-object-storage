const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function getToken() {
  return localStorage.getItem('ems_token');
}

async function request(path, { method = 'GET', body, isForm = false, isBlob = false, headers = {} } = {}) {
  const token = getToken();
  const finalHeaders = { ...headers };

  if (!isForm && body !== undefined) {
    finalHeaders['Content-Type'] = 'application/json';
  }
  if (token) {
    finalHeaders['Authorization'] = `Bearer ${token}`;
  }

  const res = await fetch(`${BASE_URL}${path}`, {
    method,
    headers: finalHeaders,
    body: isForm ? body : body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (isBlob) {
    if (!res.ok) {
      const err = new Error(`Request failed (${res.status})`);
      err.status = res.status;
      throw err;
    }
    const disposition = res.headers.get('content-disposition') || '';
    const match = disposition.match(/filename="?([^"]+)"?/);
    return { blob: await res.blob(), filename: match ? match[1] : 'download' };
  }

  const contentType = res.headers.get('content-type') || '';
  const isJson = contentType.includes('application/json');
  const text = await res.text();
  const data = isJson && text ? JSON.parse(text) : text;

  if (!res.ok) {
    const message = (isJson && data && data.message) || (typeof data === 'string' && data) || `Request failed (${res.status})`;
    const error = new Error(message);
    error.status = res.status;
    throw error;
  }

  return data;
}

export const api = {
  // Auth
  login: (payload) => request('/api/auth/login', { method: 'POST', body: payload }),
  register: (payload) => request('/api/auth/register', { method: 'POST', body: payload }),

  // Buckets
  getBuckets: () => request('/api/buckets'),
  createBucket: (bucketName) => request('/api/buckets', { method: 'POST', body: { bucketName } }),
  deleteBucket: (bucketName) => request(`/api/buckets/${encodeURIComponent(bucketName)}`, { method: 'DELETE' }),

  // Upload
  uploadFile: (bucketName, file, uploadedBy) => {
    const form = new FormData();
    form.append('file', file);
    form.append('bucketName', bucketName);
    form.append('uploadedBy', uploadedBy);
    return request('/api/objects/upload', { method: 'POST', body: form, isForm: true });
  },
  downloadFile: (id) => request(`/api/objects/download/${id}`, { isBlob: true }),
  deleteFileHard: (id) => request(`/api/objects/${id}`, { method: 'DELETE' }),
  copyFile: (payload) => request('/api/objects/copy', { method: 'POST', body: payload }),
  moveFile: (payload) => request('/api/objects/move', { method: 'POST', body: payload }),
  renameFile: (payload) => request('/api/objects/rename', { method: 'POST', body: payload }),

  // Metadata / search
  listAllMetadata: (page = 0, size = 50) => request(`/api/metadata?page=${page}&size=${size}`),
  getMetadata: (id) => request(`/api/metadata/${id}`),
  searchByName: (name, page = 0, size = 50) => request(`/api/metadata/search?name=${encodeURIComponent(name)}&page=${page}&size=${size}`),
  searchByBucket: (bucketName, page = 0, size = 100) => request(`/api/metadata/bucket/${encodeURIComponent(bucketName)}?page=${page}&size=${size}`),
  searchByFileType: (fileType, page = 0, size = 50) => request(`/api/metadata/type/${encodeURIComponent(fileType)}?page=${page}&size=${size}`),
  countAll: () => request('/api/metadata/count'),

  // Lifecycle
  archiveFile: (id) => request(`/api/lifecycle/archive/${id}`, { method: 'PUT' }),
  restoreFile: (id) => request(`/api/lifecycle/restore/${id}`, { method: 'PUT' }),
  softDeleteFile: (id) => request(`/api/lifecycle/soft-delete/${id}`, { method: 'PUT' }),
  permanentDeleteFile: (id) => request(`/api/lifecycle/permanent-delete/${id}`, { method: 'DELETE' }),
  listActive: (page = 0, size = 50) => request(`/api/lifecycle/active?page=${page}&size=${size}`),
  listArchived: (page = 0, size = 50) => request(`/api/lifecycle/archived?page=${page}&size=${size}`),
  listDeleted: (page = 0, size = 50) => request(`/api/lifecycle/deleted?page=${page}&size=${size}`),

  // Statistics (admin only)
  getStatistics: () => request('/api/statistics'),
};

export { BASE_URL };
