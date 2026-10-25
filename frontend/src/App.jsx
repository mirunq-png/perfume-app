import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Home from './pages/Home';
import Collection from './pages/Collection';
import Add from './pages/Add';
import Edit from './pages/Edit';
import Layer from './pages/Layer';
import Filter from './pages/Filter';
import Login from './pages/Login';
import { ThemeProvider } from './context/ThemeContext';
import { tryRefresh } from './utils/api';
import { api } from './utils/api';
import './styles/theme.css';
import './App.css';

function ProtectedRoute({ user, children }) {
  if (user === null) return <Navigate to="/login" replace />; // not logged in
  if (user === undefined) return null; // still checking
  return children;
}

function App() {
  const [user, setUser] = useState(undefined); // undefined = checking, null = not logged in, string = username

  useEffect(() => {
   tryRefresh()
         .then(response => {
           if (response) {
             // if response is an object, grab .username; otherwise just use the string
                       setUser(typeof response === 'string' ? response : response.username);
           } else {
             setUser(null);
           }
         })
      .catch(() => setUser(null));
  }, []);

  function handleLogin(username) {
    setUser(username);
  }

  function handleLogout() {
    setUser(null);
  }

  return (
    <ThemeProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={
            user ? <Navigate to="/" replace /> : // already logged in, skip login page
            <Login onLogin={handleLogin} />
          } />
          <Route path="/" element={
            <ProtectedRoute user={user}>
              <Home onLogout={handleLogout} user={user} />
            </ProtectedRoute>
          } />
          <Route path="/collection" element={
            <ProtectedRoute user={user}>
              <Collection />
            </ProtectedRoute>
          } />
          <Route path="/add" element={
            <ProtectedRoute user={user}>
              <Add />
            </ProtectedRoute>
          } />
          <Route path="/edit/:id" element={
            <ProtectedRoute user={user}>
              <Edit />
            </ProtectedRoute>
          } />
          <Route path="/layer/:id" element={
            <ProtectedRoute user={user}>
              <Layer />
            </ProtectedRoute>
          } />
          <Route path="/filter" element={
            <ProtectedRoute user={user}>
              <Filter />
            </ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;