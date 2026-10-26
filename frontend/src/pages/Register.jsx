import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useTheme } from '../context/ThemeContext';
import { api } from '../utils/api';
import '../styles/Login.css';

export default function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { isDark, toggleTheme } = useTheme();

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const response = await api.post('/api/auth/register', { username, password });
      if (response.status === 201) {
        setSuccess(true);
        setTimeout(() => navigate('/login'), 2000); // 2s timeout, sent to login
      } else {
        const data = await response.json();
        setError(data.error || 'Registration failed.');
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
      <motion.div
              className="login-card"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, ease: "easeOut" }}
            >
        <h1>Register</h1>
        <br/>
        {success ? (
          <p className="login-success">Registration successful! Redirecting to login...</p>
        ) : (
          <form onSubmit={handleSubmit} className="login-form">
            <div className="login-group">
              <label htmlFor="username">Username</label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={e => setUsername(e.target.value)}
                autoComplete="username"
                required
              />
            </div>
            <div className="login-group">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                autoComplete="new-password"
                required
              />
            </div>
            {error && <p className="login-error">{error}</p>}
            <button type="submit" className="login-btn" disabled={loading}>
              {loading ? 'Registering...' : 'Sign up'}
            </button>
            <p style={{ textAlign: 'center', marginTop: '15px', fontSize: '0.9rem' }}>
              Already have an account?{' '}
              <button
                type="button"
                onClick={() => navigate('/login')}
                style={{ background: 'none', border: 'none', color: '#ff6699', cursor: 'pointer', padding: 0, fontSize: '0.9rem' }}
              >
                Log in
              </button>
            </p>
          </form>
        )}
      </motion.div>
    </div>
  );
}