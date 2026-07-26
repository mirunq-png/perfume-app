import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { motion } from 'framer-motion';
import PerfumeForm from '../components/PerfumeForm';
import '../styles/AddEdit.css';

export default function Edit() {
  const [perfume, setPerfume] = useState(null);
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    loadPerfume();
  }, [id]);

  const loadPerfume = async () => {
    try {
      const response = await fetch(`/api/perfume?fetch=${id}`);
      if (!response.ok) throw new Error('Perfume not found');
      const data = await response.json();
      setPerfume(data);
    } catch (err) {
      console.error('Error:', err);
      alert('Error loading perfume: ' + err.message);
      navigate('/collection');
    } finally {
      setPageLoading(false);
    }
  };

  const handleSubmit = async (formData) => {
    setLoading(true);
    try {
      const response = await fetch('/api/perfume', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ...formData, id }),
      });

      if (!response.ok) throw new Error('Failed to update perfume');
      navigate('/collection');
    } catch (err) {
      console.error('Error:', err);
      alert('Error updating perfume: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <motion.div
        className="loading-container"
        animate={{ opacity: [0.5, 1, 0.5] }}
        transition={{ duration: 1.5, repeat: Infinity }}
      >
        Loading perfume...
      </motion.div>
    );
  }

  return (
    <motion.div
      className="form-page-container"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.3 }}
    >
      <div className="form-page-header">
        <button className="back-btn" onClick={() => navigate('/collection')}>
          ← Back
        </button>
        <h1>Edit perfume</h1>
        <div style={{ width: '60px' }}></div>
      </div>

      <div className="form-page-content">
        {perfume && <PerfumeForm onSubmit={handleSubmit} loading={loading} initialData={perfume} />}
      </div>
    </motion.div>
  );
}
