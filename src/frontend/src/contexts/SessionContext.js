import React, { createContext, useState, useEffect } from 'react';
import { v4 as uuidv4 } from 'uuid';

// Create context
export const SessionContext = createContext();

// Create provider component
export const SessionProvider = ({ children }) => {
  const [sessionId, setSessionId] = useState('');
  const [currency, setCurrency] = useState('USD');
  const [cartSize, setCartSize] = useState(0);

  // Initialize session ID on component mount
  useEffect(() => {
    // Check if session ID already exists in localStorage
    let existingSessionId = localStorage.getItem('sessionId');
    
    if (!existingSessionId) {
      // Generate a new session ID if none exists
      existingSessionId = uuidv4();
      localStorage.setItem('sessionId', existingSessionId);
    }
    
    setSessionId(existingSessionId);
  }, []);

  // Context value
  const contextValue = {
    sessionId,
    currency,
    setCurrency,
    cartSize,
    setCartSize
  };

  return (
    <SessionContext.Provider value={contextValue}>
      {children}
    </SessionContext.Provider>
  );
};

// Custom hook for using session context
export const useSession = () => {
  const context = React.useContext(SessionContext);
  
  if (context === undefined) {
    throw new Error('useSession must be used within a SessionProvider');
  }
  
  return context;
};