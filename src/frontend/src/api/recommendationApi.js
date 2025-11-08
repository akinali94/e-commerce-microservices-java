import axios from 'axios';

// Base URL for Recommendation Service
const RECOMMENDATION_SERVICE_URL = process.env.REACT_APP_RECOMMENDATION_SERVICE_URL || 'http://localhost:9562';

/**
 * Get product recommendations based on user ID and product IDs
 * @param {string} userId User ID
 * @param {Array<string>} productIds Product IDs to base recommendations on (optional)
 * @returns {Promise<Array>} List of recommended products
 */
export const getRecommendations = async (userId, productIds = []) => {
  try {
    const response = await axios.post(`${RECOMMENDATION_SERVICE_URL}/api/v1/recommendations`, {
      userId,
      productIds
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching recommendations:', error);
    throw error;
  }
};


export default {
  getRecommendations
};