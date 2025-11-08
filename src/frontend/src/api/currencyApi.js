import axios from 'axios';

// Base URL for Currency Service
const CURRENCY_SERVICE_URL = process.env.REACT_APP_CURRENCY_SERVICE_URL || 'http://localhost:9558';

/**
 * Convert currency
 * @param {string} from Source currency code (e.g., 'USD')
 * @param {string} to Target currency code (e.g., 'EUR')
 * @param {number} amount Amount to convert
 * @returns {Promise<Object>} Conversion result
 */
export const convertCurrency = async (from, to, amount) => {
  try {
    const response = await axios.get(`${CURRENCY_SERVICE_URL}/api/v1/convert`, {
      params: { from, to, amount }
    });
    return response.data;
  } catch (error) {
    console.error(`Error converting ${amount} ${from} to ${to}:`, error);
    throw error;
  }
};

/**
 * Get list of supported currencies
 * @returns {Promise<Array>} List of supported currency codes
 */
export const getSupportedCurrencies = async () => {
  try {
    const response = await axios.get(`${CURRENCY_SERVICE_URL}/api/v1/currencies`);
    return response.data;
  } catch (error) {
    console.error('Error fetching supported currencies:', error);
    throw error;
  }
};


export default {
  convertCurrency,
  getSupportedCurrencies
};