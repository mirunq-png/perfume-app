import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { formatText } from '../utils/formatText';
import '../styles/Collection.css';

export default function Collection() {
  const [perfumes, setPerfumes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedId, setSelectedId] = useState(null);
  const [selectedRowTop, setSelectedRowTop] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    loadPerfumes();
  }, []);

  async function loadPerfumes() {
    try {
      const response = await fetch('/api/perfume');
      if (!response.ok) throw new Error('Failed to fetch perfumes');
      const data = await response.json();
      setPerfumes(data);
      setError(null);
    } catch (err) {
      console.error('API error:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  const handleSelectRow = (perfumeId, event) => {
    const row = event.currentTarget;
    const rect = row.getBoundingClientRect();
    const tableContainer = row.closest('.table-container');
    const containerRect = tableContainer.getBoundingClientRect();
    
    setSelectedId(perfumeId);
    setSelectedRowTop(rect.top - containerRect.top);
  };

  return (
    <motion.div
      className="collection-container"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="collection-header">
        <button className="back-btn" onClick={() => navigate('/')}>
          ← Back
        </button>
        <h1>Your collection</h1>
        <button className="add-btn" onClick={() => navigate('/add')}>
          Add new 🩷
        </button>
      </div>

      {loading && (
        <motion.div
          className="loading"
          animate={{ opacity: [0.5, 1, 0.5] }}
          transition={{ duration: 1.5, repeat: Infinity }}
        >
          Loading your collection...
        </motion.div>
      )}

      {error && (
        <motion.div
          className="error"
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
        >
          Error: {error}
        </motion.div>
      )}

      {!loading && perfumes.length === 0 && (
        <motion.div
          className="empty-state"
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.5 }}
        >
          <p>No perfumes yet. Add your first one!</p>
          <button onClick={() => navigate('/add')}>Add Perfume</button>
        </motion.div>
      )}

      {!loading && perfumes.length > 0 && (
        <div className="table-container">
          <motion.div
            className="table-wrapper"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5 }}
          >
            <table className="perfume-table">
              <thead>
                <tr>
                  <th>Brand</th>
                  <th>Name</th>
                  <th>Volume</th>
                  <th>Notes</th>
                  <th>Season</th>
                  <th>Rating</th>
                </tr>
              </thead>
              <tbody>
                {perfumes.map((perfume) => {
                  const topNotes = perfume.notes
                    ?.filter((n) => n.layer === 'TOP')
                    .map((n) => formatText(n.note.name))
                    .join(', ') || '-';
                  const heartNotes = perfume.notes
                    ?.filter((n) => n.layer === 'HEART')
                    .map((n) => formatText(n.note.name))
                    .join(', ') || '-';
                  const baseNotes = perfume.notes
                    ?.filter((n) => n.layer === 'BASE')
                    .map((n) => formatText(n.note.name))
                    .join(', ') || '-';
                  const seasons = perfume.seasons?.length > 0 
                    ? perfume.seasons.map(s => formatText(s)).join(', ') 
                    : '-';

                  return (
                    <motion.tr
                      key={perfume.id}
                      className={`${selectedId === perfume.id ? 'selected-row' : ''}`}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ duration: 0.3 }}
                      onClick={(e) => handleSelectRow(perfume.id, e)}
                      style={{ cursor: 'pointer' }}
                    >
                      <td>{formatText(perfume.brand.name)}</td>
                      <td>{formatText(perfume.name)}</td>
                      <td>{perfume.ml} ml</td>
                      <td>
                        <div className="notes-column">
                          <div className="note-item">
                            <strong>Top</strong>
                            <p>{topNotes}</p>
                          </div>
                          <div className="note-item">
                            <strong>Heart</strong>
                            <p>{heartNotes}</p>
                          </div>
                          <div className="note-item">
                            <strong>Base</strong>
                            <p>{baseNotes}</p>
                          </div>
                        </div>
                      </td>
                      <td>{seasons}</td>
                      <td>{perfume.rating ? `${perfume.rating}/10` : 'N/A'}</td>
                    </motion.tr>
                  );
                })}
              </tbody>
            </table>
          </motion.div>

          {selectedId && (
            <motion.div
              key={`actions-${selectedId}`}
              className="floating-actions"
              style={{
                top: selectedRowTop,
              }}
              initial={{ opacity: 0, x: 50 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 50 }}
              transition={{ duration: 0.15, ease: 'easeOut' }}
            >
              <motion.button
                className="action-btn layer-btn"
                onClick={() => navigate(`/layer/${selectedId}`)}
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.15, delay: 0.02 }}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Layer
              </motion.button>
              <motion.button
                className="action-btn edit-btn"
                onClick={() => navigate(`/edit/${selectedId}`)}
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.15, delay: 0.05 }}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Edit
              </motion.button>
            </motion.div>
          )}
        </div>
      )}
    </motion.div>
  );
}
