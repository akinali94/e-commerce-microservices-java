import axios from 'axios';

/**
 * Configure global axios settings and interceptors for all API requests
 * This should be called once when your application initializes
 */
export const setupApiConfig = () => {
  // Add a request interceptor to handle authentication and common headers
  axios.interceptors.request.use(
    (config) => {
      // Get authentication token from local storage or session storage if needed
      const token = localStorage.getItem('auth_token');
      
      // Add common headers
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      
      config.headers['Content-Type'] = 'application/json';
      
      // Add correlation ID for request tracing
      config.headers['X-Correlation-ID'] = generateCorrelationId();
      
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );
  
  // Add a response interceptor to handle errors consistently
  axios.interceptors.response.use(
    (response) => {
      return response;
    },
    (error) => {
      handleApiError(error);
      return Promise.reject(error);
    }
  );
  
  // Set global timeout for all requests
  axios.defaults.timeout = 10000; // 10 seconds
};

/**
 * Generate a unique correlation ID for request tracing
 * @returns {string} Correlation ID
 */
const generateCorrelationId = () => {
  return 'xxxx-xxxx-xxxx-xxxx'.replace(/[x]/g, () => {
    return Math.floor(Math.random() * 16).toString(16);
  });
};

/**
 * Handle API errors in a consistent way
 * @param {Error} error Axios error object
 */
const handleApiError = (error) => {
  if (error.response) {
    // The request was made and the server responded with a status code outside the 2xx range
    console.error('API Error Response:', {
      status: error.response.status,
      data: error.response.data,
      headers: error.response.headers
    });
    
    // Handle specific HTTP status codes
    switch (error.response.status) {
      case 401: // Unauthorized
        // Redirect to login or refresh token
        console.error('Authentication error. You may need to log in again.');
        // Optional: window.location.href = '/login';
        break;
      case 403: // Forbidden
        console.error('You do not have permission to access this resource.');
        break;
      case 404: // Not Found
        console.error('The requested resource was not found.');
        break;
      case 500: // Server Error
        console.error('An internal server error occurred.');
        break;
      default:
        console.error(`Unexpected error occurred (HTTP ${error.response.status})`);
    }
  } else if (error.request) {
    // The request was made but no response was received
    console.error('No response received from server:', error.request);
    console.error('Network error. Please check your connection and try again.');
  } else {
    // Something happened in setting up the request
    console.error('Error setting up request:', error.message);
  }
};

/**
 * Format URL with query parameters
 * @param {string} baseUrl Base URL
 * @param {Object} params Query parameters
 * @returns {string} Formatted URL
 */
export const formatUrlWithParams = (baseUrl, params = {}) => {
  const url = new URL(baseUrl);
  Object.keys(params).forEach(key => {
    if (params[key] !== undefined && params[key] !== null) {
      url.searchParams.append(key, params[key]);
    }
  });
  return url.toString();
};

export default {
  setupApiConfig,
  formatUrlWithParams
};