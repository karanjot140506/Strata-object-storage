import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function AppShell({ children }) {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <div className="brand-mark">S</div>
          <div>
            <div className="brand-name">STRATA</div>
            <div className="brand-sub">Object Storage Console</div>
          </div>
        </div>

        <nav className="sidebar-nav">
          <NavLink to="/dashboard" className={({ isActive }) => `nav-item ${isActive ? 'nav-item-active' : ''}`}>
            <span className="nav-icon">▤</span> Dashboard
          </NavLink>
          {isAdmin && (
            <NavLink to="/admin" className={({ isActive }) => `nav-item ${isActive ? 'nav-item-active' : ''}`}>
              <span className="nav-icon">◈</span> Control Room
            </NavLink>
          )}
        </nav>

        <div className="sidebar-footer">
          <div className="user-card">
            <div className="user-avatar">{user?.username?.[0]?.toUpperCase()}</div>
            <div className="user-meta">
              <div className="user-name">{user?.username}</div>
              <div className={`user-role ${isAdmin ? 'user-role-admin' : ''}`}>{user?.role}</div>
            </div>
          </div>
          <button className="btn btn-ghost btn-block" onClick={handleLogout}>Sign out</button>
        </div>
      </aside>

      <main className="content">{children}</main>
    </div>
  );
}
