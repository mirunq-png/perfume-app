import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import '../styles/Home.css';

export default function Home() {
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

  return (
    <div className="home-container">
      <button className="theme-toggle" onClick={toggleTheme} title="Toggle dark mode">
        {isDark ? '☀️' : '🌙'}
      </button>
      
      <div className="home-content">
        <h1>Welcome!</h1>
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
