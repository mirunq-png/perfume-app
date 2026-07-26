import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { formatText } from '../utils/formatText';
import '../styles/Layer.css';

export default function Layer() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [targetPerfume, setTargetPerfume] = useState(null);
  const [recommendations, setRecommendations] = useState([]);
  const [recCount, setRecCount] = useState(3);
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchTargetPerfume() {
      if (!id) return;
      try {
        const response = await fetch(`/api/perfume?id=${id}&limit=1`);
        if (!response.ok) throw new Error('Failed to load perfume name');
        const data = await response.json();
        setTargetPerfume(data);
      } catch (err) {
        console.error('Error loading perfume name:', err);
        setError('Could not load perfume name.');
      } finally {
        setInitialLoading(false);
      }
    }

    fetchTargetPerfume();
  }, [id]);

  const generateRecommendations = async () => {
    if (!id) return;
    setLoading(true);
    setRecommendations([]);
    setError(null);

    try {
      const response = await fetch(`/api/perfume?id=${id}&limit=${recCount}`);
      if (!response.ok) throw new Error('Failed to fetch recommendations');
      
      const data = await response.json();
      if (data.baseName && !targetPerfume) {
         setTargetPerfume({ baseName: data.baseName });
      }
      setRecommendations(data.recommendations || []);
    } catch (err) {
      console.error('API Error:', err);
      setError('Something went wrong finding matches.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <motion.div
      className="layer-container"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="layer-header">
        <button className="back-btn" onClick={() => navigate('/collection')}>
          ← Back
        </button>
        <h1>
          {initialLoading ? 'Loading...' : 
           targetPerfume ? `Matches for: ${formatText(targetPerfume.baseName)}` : 
           'Find layering matches'}
        </h1>
      </div>

      <div className="layer-content">
        <div className="rec-controls">
          <p>How many recommendations do you want?</p>
          <div className="rec-input-group">
            <input
              type="number"
              id="rec-count"
              min="1"
              value={recCount}
              onChange={(e) => setRecCount(parseInt(e.target.value) || 1)}
            />
            <button 
              className="find-matches-btn" 
              onClick={generateRecommendations}
              disabled={loading || !id}
            >
              {loading ? 'Finding...' : 'Find matches'}
            </button>
          </div>
        </div>

        <div className="results-container">
          {loading && <p className="status-message">Finding the perfect matches...</p>}
          
          {error && <p className="status-message error">{error}</p>}

          {!loading && recommendations.length > 0 && (
            <>
              <h3>Found {recommendations.length} matches:</h3>
              <ul className="matches-list">
                {recommendations.map((item, index) => (
                  <motion.li 
                    key={index} 
                    className="match-card"
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: index * 0.1 }}
                  >
                    <div className="match-card-header">
                      <span className="match-name">
                        [{index + 1}] {formatText(item.perfume.brand.name)} - {formatText(item.perfume.name)}
                      </span>
                      <span className="match-score">
                        {item.score != null ? `${item.score}% match` : 'N/A'}
                      </span>
                    </div>
                    <details className="match-details">
                      <summary>Why this works?</summary>
                      <p className="match-explanation">
                        {item.explanation}
                      </p>
                    </details>
                  </motion.li>
                ))}
              </ul>
            </>
          )}
        </div>
      </div>
    </motion.div>
  );
}
