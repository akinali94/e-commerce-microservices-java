import axios from 'axios';

// Base URL for Shipping Service
const SHIPPING_SERVICE_URL = process.env.REACT_APP_SHIPPING_SERVICE_URL || 'http://localhost:9563/api/v1';

/**
 * Get shipping quote for items
 * @param {Object} quoteRequest Request containing address and items
 * @returns {Promise<Object>} Shipping quote with cost
 */
export const getShippingQuote = async (quoteRequest) => {
  try {
    const response = await axios.post(`${SHIPPING_SERVICE_URL}/shipping/quote`, quoteRequest);
    return response.data;
  } catch (error) {
    console.error('Error fetching shipping quote:', error);
    throw error;
  }
};

/**
 * Ship an order and get tracking information
 * @param {Object} shipRequest Request containing order details
 * @returns {Promise<Object>} Shipping confirmation with tracking ID
 */
export const shipOrder = async (shipRequest) => {
  try {
    const response = await axios.post(`${SHIPPING_SERVICE_URL}/shipping/ship`, shipRequest);
    return response.data;
  } catch (error) {
    console.error('Error shipping order:', error);
    throw error;
  }
};


export default {
  getShippingQuote,
  shipOrder
};