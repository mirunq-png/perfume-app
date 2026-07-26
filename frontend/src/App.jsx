import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Collection from './pages/Collection';
import Add from './pages/Add';
import Edit from './pages/Edit';
import Layer from './pages/Layer';
import Filter from './pages/Filter';
import { ThemeProvider } from './context/ThemeContext';
import './styles/theme.css';
import './App.css';

function App() {
  return (
    <ThemeProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/collection" element={<Collection />} />
          <Route path="/add" element={<Add />} />
          <Route path="/edit/:id" element={<Edit />} />
          <Route path="/layer/:id" element={<Layer />} />
          <Route path="/filter" element={<Filter />} />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
