import { createContext, useContext, useState, useCallback } from 'react';
import { api } from '../api/client';

const AuthContext = createContext(null);

function loadStoredUser() {
  const token = localStorage.getItem('ems_token');
  const username = localStorage.getItem('ems_username');
  const email = localStorage.getItem('ems_email');
  const role = localStorage.getItem('ems_role');
  if (!token || !username) return null;
  return { token, username, email, role };
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadStoredUser);

  const persist = (auth) => {
    localStorage.setItem('ems_token', auth.token);
    localStorage.setItem('ems_username', auth.username);
    localStorage.setItem('ems_email', auth.email || '');
    localStorage.setItem('ems_role', auth.role);
    setUser({ token: auth.token, username: auth.username, email: auth.email, role: auth.role });
  };

  const login = useCallback(async (username, password) => {
    const auth = await api.login({ username, password });
    persist(auth);
    return auth;
  }, []);

  const register = useCallback(async (username, email, password) => {
    const auth = await api.register({ username, email, password });
    persist(auth);
    return auth;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('ems_token');
    localStorage.removeItem('ems_username');
    localStorage.removeItem('ems_email');
    localStorage.removeItem('ems_role');
    setUser(null);
  }, []);

  const isAdmin = user?.role === 'ADMIN';

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isAdmin }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
