import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import { api } from '../utils/api';
import '../styles/Login.css';

export default function Login({ onLogin }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { isDark, toggleTheme } = useTheme();

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const response = await api.post('/api/auth/login', { username, password });
      if (response.status === 200) {
        const data = await response.json();
        onLogin(data.username);
        navigate('/');
      } else {
        const data = await response.json();
        setError(data.error || 'Login failed.');
      }
    } catch (err) {
      setError('Could not reach the server.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-container">
      <button className="theme-toggle" onClick={toggleTheme} title="Toggle dark mode">
        {isDark ? '☀️' : '🌙'}
      </button>
      <div className="login-card">
        <h1>Welcome!</h1>
        <br/>
        <form onSubmit={handleSubmit} className="login-form">
          <div className="login-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              placeholder=""
              autoComplete="username"
            />
          </div>
          <div className="login-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder=""
              autoComplete="current-password"
            />
          </div>
          {error && <p className="login-error">{error}</p>}
          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
      </div>
    </div>
  );
}