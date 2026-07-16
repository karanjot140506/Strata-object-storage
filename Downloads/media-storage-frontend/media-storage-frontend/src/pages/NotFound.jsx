import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="auth-page">
      <div className="auth-card" style={{ textAlign: 'center' }}>
        <div className="brand-mark" style={{ margin: '0 auto 18px' }}>?</div>
        <h1 className="auth-title">Page not found</h1>
        <p className="auth-subtitle">This route doesn't exist in the console.</p>
        <Link to="/dashboard" className="btn btn-primary btn-block">Back to dashboard</Link>
      </div>
    </div>
  );
}
