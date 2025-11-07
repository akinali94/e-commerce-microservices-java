import React from 'react';
import { Link } from 'react-router-dom';
import './ErrorPage.css';

const ErrorPage = () => {
  return (
    <main role="main">
      <div className="py-5">
        <div className="container bg-light py-3 px-lg-5 py-lg-5 error-container">
          <h1>Uh, oh!</h1>
          <p>Something has gone wrong. The page you were looking for doesn't exist.</p>
          <p><strong>HTTP Status:</strong> 404 Not Found</p>
          
          <div className="text-center mt-4">
            <Link to="/" className="cymbal-button-primary">
              Return to Homepage
            </Link>
          </div>
        </div>
      </div>
    </main>
  );
};

export default ErrorPage;