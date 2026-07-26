import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { formatText } from '../utils/formatText';
import '../styles/AddEdit.css';

export default function PerfumeForm({ onSubmit, loading, initialData = null }) {
  const [formData, setFormData] = useState({
    name: initialData?.name || '',
    brand: initialData?.brand?.name || '',
    newBrand: '',
    ml: initialData?.ml || '100',
    type: initialData?.type || 'EDP',
    topNotes: '',
    heartNotes: '',
    baseNotes: '',
    rating: initialData?.rating || '',
    seasons: [],
    fragranticaUrl: initialData?.fragranticaUrl || '',
  });

  const [brands, setBrands] = useState([]);
  const [brandSelectValue, setBrandSelectValue] = useState(initialData?.brand?.name || '');
  const [message, setMessage] = useState({ text: '', type: '' });
  const [scrapingLoading, setScrapingLoading] = useState(false);
  const [showFragrantica, setShowFragrantica] = useState(false);
  const [missingFields, setMissingFields] = useState([]);

  const mlOptions = ['2', '5', '10', '30', '50', '80', '90', '100'];
  const seasonOptions = ['WINTER', 'SPRING', 'SUMMER', 'FALL'];

  useEffect(() => {
    loadBrands();
    if (initialData?.notes) {
      const topNotes = initialData.notes
        .filter((n) => n.layer === 'TOP')
        .map((n) => n.note.name)
        .join(', ');
      const heartNotes = initialData.notes
        .filter((n) => n.layer === 'HEART')
        .map((n) => n.note.name)
        .join(', ');
      const baseNotes = initialData.notes
        .filter((n) => n.layer === 'BASE')
        .map((n) => n.note.name)
        .join(', ');

      setFormData((prev) => ({
        ...prev,
        topNotes,
        heartNotes,
        baseNotes,
      }));
    }

    if (initialData?.seasons) {
      setFormData((prev) => ({
        ...prev,
        seasons: initialData.seasons,
      }));
    }

    if (initialData?.name) {
      setFormData((prev) => ({
        ...prev,
        name: formatText(initialData.name),
      }));
    }
  }, [initialData]);

  async function loadBrands() {
    try {
      const response = await fetch('/api/brands');
      if (!response.ok) throw new Error('Failed to load brands');
      const data = await response.json();
      setBrands(data);
    } catch (err) {
      setMessage({ text: 'Failed to load brands', type: 'error' });
    }
  }

  async function scrapeFragrantica() {
    if (!formData.fragranticaUrl.trim()) {
      setMessage({ text: 'Please enter a Fragrantica URL', type: 'error' });
      return;
    }

    setScrapingLoading(true);
    setMissingFields([]);
    try {
      const response = await fetch(`/api/import/fragrantica?url=${encodeURIComponent(formData.fragranticaUrl)}`);

      if (!response.ok) throw new Error(`Server responded with ${response.status}`);
      const scrapedData = await response.json();

      console.log('[React Debug] Raw scraped data from server:', scrapedData);

      const imported = [];
      const missing = [];
      const newFields = {}; // all the updates are stored here

      if (scrapedData.perfumeName) {
        newFields.name = scrapedData.perfumeName;
        imported.push('name');
      } else {
        missing.push('name');
      }

      if (scrapedData.brandName) {
        newFields.brand = scrapedData.brandName;
        setBrandSelectValue(scrapedData.brandName);
        imported.push('brand');
      } else {
        missing.push('brand');
      }

      if (scrapedData.type) {
        newFields.type = scrapedData.type;
        imported.push('type');
      } else {
        missing.push('type');
      }

      if (scrapedData.rating != null && !isNaN(scrapedData.rating)) {
        newFields.rating = scrapedData.rating;
        imported.push('rating');
      } else {
        missing.push('rating');
      }

      if (scrapedData.topNotes) {
        newFields.topNotes = scrapedData.topNotes;
        imported.push('top notes');
      } else {
        missing.push('top notes');
      }

      if (scrapedData.heartNotes) {
        newFields.heartNotes = scrapedData.heartNotes;
        imported.push('heart notes');
      } else {
        missing.push('heart notes');
      }

      if (scrapedData.baseNotes) {
        newFields.baseNotes = scrapedData.baseNotes;
        imported.push('base notes');
      } else {
        missing.push('base notes');
      }

      if (Array.isArray(scrapedData.seasons) && scrapedData.seasons.length > 0) {
        newFields.seasons = scrapedData.seasons;
        imported.push('seasons');
      } else {
        missing.push('seasons');
      }

      missing.push('ml');

      setFormData((prev) => ({
        ...prev,
        ...newFields,
      }));

      setMissingFields(missing);

      if (imported.length === 0) {
        setMessage({ text: 'Nothing could be imported from that URL.', type: 'error' });
      }
    } catch (err) {
      setMessage({ text: 'Could not load data from that URL.', type: 'error' });
      console.error('Autofill failed:', err);
    } finally {
      setScrapingLoading(false);
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    if (missingFields.includes(name)) {
      setMissingFields(prev => prev.filter(f => f !== name));
    } else if (name === 'newBrand' && missingFields.includes('brand')) {
      setMissingFields(prev => prev.filter(f => f !== 'brand'));
    }
  };

  const handleSeasonChange = (season) => {
    setFormData((prev) => ({
      ...prev,
      seasons: prev.seasons.includes(season)
        ? prev.seasons.filter((s) => s !== season)
        : [...prev.seasons, season],
    }));
    if (missingFields.includes('seasons')) {
      setMissingFields(prev => prev.filter(f => f !== 'seasons'));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage({ text: '', type: '' });

    // validate
    if (!formData.name.trim()) {
      setMessage({ text: 'Perfume name is required', type: 'error' });
      return;
    }
    if (!brandSelectValue && !formData.newBrand.trim()) {
      setMessage({ text: 'Brand name is required', type: 'error' });
      return;
    }
    const finalBrand = brandSelectValue === 'NEW' ? formData.newBrand.trim() : brandSelectValue;
    if (!finalBrand) {
      setMessage({ text: 'Brand name is required', type: 'error' });
      return;
    }
    if (!formData.baseNotes.trim()) {
      setMessage({ text: 'Base notes are required', type: 'error' });
      return;
    }
    if (formData.seasons.length === 0) {
      setMessage({ text: 'Please select at least one season', type: 'error' });
      return;
    }

    const submitData = {
      name: formData.name.trim(),
      brand: finalBrand,
      ml: parseInt(formData.ml),
      type: formData.type,
      topNotes: formData.topNotes.trim(),
      heartNotes: formData.heartNotes.trim(),
      baseNotes: formData.baseNotes.trim(),
      rating: formData.rating ? parseFloat(formData.rating) : null,
      seasons: formData.seasons.join(', '),
      fragranticaUrl: formData.fragranticaUrl.trim() || null,
    };

    await onSubmit(submitData);
  };

  return (
    <motion.form
      onSubmit={handleSubmit}
      className="perfume-form"
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      {message.text && (
        <motion.div
          className={`message ${message.type}`}
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.2 }}
        >
          {message.text}
        </motion.div>
      )}

      {!initialData && (
        <>
          <div className="fragrantica-toggle">
            <button
              type="button"
              className="toggle-btn"
              onClick={() => setShowFragrantica(!showFragrantica)}
            >
              {showFragrantica ? '✕' : '+'} Scrape from Fragrantica
            </button>
          </div>

          {showFragrantica && (
            <motion.div
              className="fragrantica-section"
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              transition={{ duration: 0.3 }}
            >
              <div className="fragrantica-input-group">
                <input
                  type="url"
                  id="fragranticaUrl"
                  name="fragranticaUrl"
                  value={formData.fragranticaUrl}
                  onChange={handleChange}
                  placeholder="e.g. https://www.fragrantica.com/perfume/..."
                />
                <button
                  type="button"
                  className="scrape-btn"
                  onClick={scrapeFragrantica}
                  disabled={scrapingLoading}
                >
                  {scrapingLoading ? (
                    <span className="loading-dots">
                      Scraping<span>.</span><span>.</span><span>.</span>
                    </span>
                  ) : (
                    'Scrape Data'
                  )}
                </button>
              </div>
            </motion.div>
          )}
        </>
      )}

      <div className="form-group">
        <label htmlFor="name">
          Perfume name *
          {missingFields.includes('name') && <span className="warning-indicator" title="Not found in Fragrantica">!</span>}
        </label>
        <input
          type="text"
          id="name"
          name="name"
          className={missingFields.includes('name') ? 'warning-border' : ''}
          value={formData.name}
          onChange={handleChange}
          placeholder="..."
        />
      </div>

      <div className="form-group">
        <label htmlFor="brand">
          Brand *
          {missingFields.includes('brand') && <span className="warning-indicator" title="Not found in Fragrantica">!</span>}
        </label>
        <select
          id="brand"
          className={missingFields.includes('brand') ? 'warning-border' : ''}
          value={brandSelectValue}
          onChange={(e) => {
            const val = e.target.value;
            setBrandSelectValue(val);
            setFormData((prev) => ({ ...prev, brand: val, newBrand: '' }));
            if (missingFields.includes('brand')) {
              setMissingFields(prev => prev.filter(f => f !== 'brand'));
            }
          }}
        >
          <option value="">Select a brand...</option>
          {brands.map((brand) => (
            <option key={brand} value={brand}>
              {formatText(brand)}
            </option>
          ))}
          {brandSelectValue && !brands.includes(brandSelectValue) && brandSelectValue !== 'NEW' && (
            <option value={brandSelectValue}>{formatText(brandSelectValue)}</option>
          )}
          <option value="NEW">+ Add new brand...</option>
        </select>
      </div>

      {brandSelectValue === 'NEW' && (
        <motion.div
          className="form-group"
          initial={{ opacity: 0, height: 0 }}
          animate={{ opacity: 1, height: 'auto' }}
          exit={{ opacity: 0, height: 0 }}
        >
          <label htmlFor="newBrand">New brand name *</label>
          <input
            type="text"
            id="newBrand"
            name="newBrand"
            value={formData.newBrand}
            onChange={handleChange}
            placeholder="Brand name"
          />
        </motion.div>
      )}

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="ml">
            Volume (ml) *
            {missingFields.includes('ml') && <span className="warning-indicator" title="Not found in Fragrantica">!</span>}
          </label>
          <select 
            id="ml" 
            name="ml" 
            className={missingFields.includes('ml') ? 'warning-border' : ''}
            value={formData.ml} 
            onChange={handleChange}
          >
            {mlOptions.map((ml) => (
              <option key={ml} value={ml}>
                {ml} ml
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="type">
            Type *
            {missingFields.includes('type') && <span className="warning-indicator" title="Not found in Fragrantica">!</span>}
          </label>
          <select 
            id="type" 
            name="type" 
            className={missingFields.includes('type') ? 'warning-border' : ''}
            value={formData.type} 
            onChange={handleChange}
          >
            <option value="EDP">Eau de Parfum</option>
            <option value="ETP">Eau de Toilette</option>
            <option value="BM">Body Mist</option>
          </select>
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="topNotes">
          Top notes
          {missingFields.includes('top notes') && <span className="warning-indicator" title="Not found in Fragrantica">!</span>}
        </label>
        <input
          type="text"
          id="topNotes"
          name="topNotes"
          className={missingFields.includes('top notes') ? 'warning-border' : ''}
          value={formatText(formData.topNotes)}
          onChange={handleChange}
          placeholder="e.g. strawberry, lemon (comma separated)"
        />
      </div>

      <div className="form-group">
        <label htmlFor="heartNotes">
          Heart notes
          {missingFields.includes('heart notes') && <span className="warning-indicator" title="Not found in Fragrantica">!</span>}
        </label>
        <input
          type="text"
          id="heartNotes"
          name="heartNotes"
          className={missingFields.includes('heart notes') ? 'warning-border' : ''}
          value={formatText(formData.heartNotes)}
          onChange={handleChange}
          placeholder="e.g. orange blossom, jasmine (comma separated)"
        />
      </div>

      <div className="form-group">
        <label htmlFor="baseNotes">
          Base notes *
          {missingFields.includes('base notes') && <span className="warning-indicator" title="Not found in Fragrantica">!</span>}
        </label>
        <input
          type="text"
          id="baseNotes"
          name="baseNotes"
          className={missingFields.includes('base notes') ? 'warning-border' : ''}
          value={formatText(formData.baseNotes)}
          onChange={handleChange}
          placeholder="e.g. musk, vanilla (comma separated)"
          required
        />
      </div>

      <div className="form-group">
        <label>
          Seasons *
          {missingFields.includes('seasons') && <span className="warning-indicator" title="Not found in Fragrantica">!</span>}
        </label>
        <div className={`checkbox-group ${missingFields.includes('seasons') ? 'warning-border' : ''}`}>
          {seasonOptions.map((season) => {
            const isChecked = Array.isArray(formData.seasons) && formData.seasons.some(
              (s) => s.toUpperCase() === season.toUpperCase()
            );

            return (
              <label key={season} className="checkbox-label">
                <input
                  type="checkbox"
                  checked={isChecked}
                  onChange={() => handleSeasonChange(season)}
                />
                {formatText(season)}
              </label>
            );
          })}
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="rating">
          Rating (0-10)
          {missingFields.includes('rating') && <span className="warning-indicator" title="Not found in Fragrantica">!</span>}
        </label>
        <input
          type="number"
          id="rating"
          name="rating"
          className={missingFields.includes('rating') ? 'warning-border' : ''}
          value={formData.rating}
          onChange={handleChange}
          placeholder="e.g. 9.5"
          min="0"
          max="10"
          step="0.1"
        />
      </div>

      <button type="submit" className="submit-btn" disabled={loading}>
        {loading ? 'Saving...' : 'Save perfume!'}
      </button>
    </motion.form>
  );
}
