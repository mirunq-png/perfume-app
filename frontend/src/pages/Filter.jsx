import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { formatText } from '../utils/formatText';
import { api } from '../utils/api';
import '../styles/Filter.css';

export default function Filter() {
  const navigate = useNavigate();
  const [filterType, setFilterType] = useState('note');
  const [noteInput, setNoteInput] = useState('');
  const [selectedSeason, setSelectedSeason] = useState('WINTER');
  const [perfumes, setPerfumes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searched, setSearched] = useState(false);

  const filterData = async () => {
    if (filterType === 'note' && !noteInput.trim()) {
      setError('Please enter a note to search for.');
      return;
    }
    setLoading(true);
    setError(null);
    setSearched(true);
    try {
      const url = filterType === 'note'
        ? `/api/perfume?note=${encodeURIComponent(noteInput.trim())}`
        : `/api/perfume?season=${selectedSeason}`;
      const response = await api.get(url);
      if (!response.ok) throw new Error('Failed to fetch from API');
      const data = await response.json();
      setPerfumes(data);
    } catch (err) {
      console.error('API Error:', err);
      setError('Something went wrong searching the collection.');
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && filterType === 'note') filterData();
  };

  return (
    <motion.div className="filter-container" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} transition={{ duration: 0.5 }}>
      <div className="filter-header">
        <button className="back-btn" onClick={() => navigate('/')}>← Back</button>
        <h1>Filter and search your collection!</h1>
      </div>

      <div className="filter-content">
        <div className="search-group">
          <label>What would you like to do?</label>
          <div className="filter-type-toggle">
            <button className={`toggle-btn ${filterType === 'note' ? 'active' : ''}`} onClick={() => { setFilterType('note'); setSearched(false); setPerfumes([]); }}>
              Search by note
            </button>
            <button className={`toggle-btn ${filterType === 'season' ? 'active' : ''}`} onClick={() => { setFilterType('season'); setSearched(false); setPerfumes([]); }}>
              Search by season
            </button>
          </div>

          <div className="search-input-group">
            {filterType === 'note' ? (
              <input type="text" id="searched-note" placeholder="e.g. vanilla, musk" autoComplete="off" value={noteInput} onChange={(e) => setNoteInput(e.target.value)} onKeyPress={handleKeyPress} />
            ) : (
              <select className="season-select" value={selectedSeason} onChange={(e) => setSelectedSeason(e.target.value)}>
                <option value="WINTER">Winter</option>
                <option value="SPRING">Spring</option>
                <option value="SUMMER">Summer</option>
                <option value="FALL">Fall</option>
              </select>
            )}
            <button className="search-btn" onClick={filterData} disabled={loading}>
              {loading ? 'Searching...' : 'Find Matches'}
            </button>
          </div>
        </div>

        <div className="results-container">
          {loading && <p className="status-message">Searching the collection...</p>}
          {error && <p className="status-message error">{error}</p>}
          {!loading && searched && perfumes.length === 0 && !error && (
            <p className="status-message">No perfumes found for this search.</p>
          )}
          {!loading && perfumes.length > 0 && (
            <>
              <h3>Found {perfumes.length} matches {filterType === 'note' ? `containing '${noteInput}'` : `for ${formatText(selectedSeason)}`}:</h3>
              <ul className="results-list">
                {perfumes.map((p, index) => (
                  <motion.li key={p.id || index} className="result-card" initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: index * 0.05 }} onClick={() => navigate(`/layer/${p.id}`)} style={{ cursor: 'pointer' }}>
                    <div className="result-card-header">
                      <span className="result-name">[{index + 1}] {formatText(p.brand.name)} - {formatText(p.name)}</span>
                    </div>
                    <div className="result-notes">
                      <span className="notes-label">Notes: </span>
                      {p.notes.map(n => formatText(n.note.name)).join(', ')}
                    </div>
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