import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { apiConfig } from './api';

// Initialize API configuration
apiConfig.setupApiConfig();

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);