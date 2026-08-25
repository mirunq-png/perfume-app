import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import PerfumeForm from '../components/PerfumeForm';
import { api } from '../utils/api';
import '../styles/AddEdit.css';

export default function Add() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (formData) => {
    setLoading(true);
    try {
      const response = await api.post('/api/perfume', formData);
      if (!response.ok) throw new Error('Failed to save perfume');
      navigate('/collection');
    } catch (err) {
      console.error('Error:', err);
      alert('Error saving perfume: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <motion.div
      className="form-page-container"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.3 }}
    >
      <div className="form-page-header">
        <button className="back-btn" onClick={() => navigate('/collection')}>← Back</button>
        <h1>Add a new perfume</h1>
        <div style={{ width: '60px' }}></div>
      </div>
      <div className="form-page-content">
        <PerfumeForm onSubmit={handleSubmit} loading={loading} />
      </div>
    </motion.div>
  );
}