import axios from 'axios';

// Base URL for Checkout Service
const CHECKOUT_SERVICE_URL = process.env.REACT_APP_CHECKOUT_SERVICE_URL || 'http://localhost:9557';

/**
 * Place an order
 * @param {Object} orderData Order details including shipping address, items, etc.
 * @returns {Promise<Object>} Order confirmation
 */
export const placeOrder = async (orderData) => {
  try {
    const response = await axios.post(`${CHECKOUT_SERVICE_URL}/api/v1/checkout/orders`, orderData);
    return response.data;
  } catch (error) {
    console.error('Error placing order:', error);
    throw error;
  }
};


export default {
  placeOrder
};