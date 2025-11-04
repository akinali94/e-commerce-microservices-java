import axios from 'axios';

// Base URL for Ad Service
const AD_SERVICE_URL = process.env.REACT_APP_AD_SERVICE_URL || 'http://localhost:9555/api/v1';

/**
 * Get all available ad categories
 * @returns {Promise<Array>} List of categories
 */
export const getCategories = async () => {
  try {
    const response = await axios.get(`${AD_SERVICE_URL}/ads/categories`);
    return response.data;
  } catch (error) {
    console.error('Error fetching ad categories:', error);
    throw error;
  }
};

/**
 * Get random ads
 * @param {number} count Number of ads to retrieve (default: 1)
 * @returns {Promise<Array>} List of random ads
 */
export const getRandomAds = async (count = 1) => {
  try {
    const response = await axios.get(`${AD_SERVICE_URL}/ads/random`, {
      params: { count }
    });
    return response.data;
  } catch (error) {
    console.error(`Error fetching ${count} random ads:`, error);
    throw error;
  }
};

/**
 * Get ads by context keys using GET
 * @param {Array<string>} contextKeys Context keys for ad targeting
 * @returns {Promise<Array>} List of ads matching the context
 */
export const getAdsByContextKeys = async (contextKeys) => {
  try {
    const response = await axios.get(`${AD_SERVICE_URL}/ads`, {
      params: { contextKeys: contextKeys.join(',') }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching ads by context keys:', error);
    throw error;
  }
};

/**
 * Get ads by context keys using POST
 * @param {Array<string>} contextKeys Context keys for ad targeting
 * @returns {Promise<Array>} List of ads matching the context
 */
export const postAdsByContextKeys = async (contextKeys) => {
  try {
    const response = await axios.post(`${AD_SERVICE_URL}/ads`, { contextKeys });
    return response.data;
  } catch (error) {
    console.error('Error posting for ads by context keys:', error);
    throw error;
  }
};

export default {
  getCategories,
  getRandomAds,
  getAdsByContextKeys,
  postAdsByContextKeys,
};