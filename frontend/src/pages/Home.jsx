import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import { api } from '../utils/api';
import '../styles/Home.css';

export default function Home({ onLogout, user }) {
  const [welcomeImage, setWelcomeImage] = useState('');
  const navigate = useNavigate();
  const { isDark, toggleTheme } = useTheme();

  useEffect(() => {
    const perfumeImages = [
      '/img/image1.png',
      '/img/image2.png',
      '/img/image3.png',
    ];
    const randomIndex = Math.floor(Math.random() * perfumeImages.length);
    setWelcomeImage(perfumeImages[randomIndex]);
  }, []);

  async function handleLogout() {
    try {
      await api.post('/api/auth/logout');
    } catch (err) {
      console.error('Logout error:', err);
    } finally {
      onLogout();
      navigate('/login');
    }
  }

  return (
    <div className="home-container">
      <button className="theme-toggle" onClick={toggleTheme} title="Toggle dark mode">
        {isDark ? '☀️' : '🌙'}
      </button>
      <button className="logout-btn" onClick={handleLogout} title="Log out">
        Sign out
      </button>

      <div className="home-content">
        <h1>Welcome{user ? `, ${user}` : ''}!</h1>
        <div className="horizontal-menu">
          <button onClick={() => navigate('/collection')}>View collection</button>
          <button onClick={() => navigate('/filter')}>Filter collection</button>
        </div>
        {welcomeImage && (
          <img
            id="welcome-image"
            src={welcomeImage}
            alt="Perfume illustration"
            className="welcome-image"
          />
        )}
      </div>
    </div>
  );
}