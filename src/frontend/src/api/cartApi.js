import axios from 'axios';

// Base URL for Cart Service
const CART_SERVICE_URL = process.env.REACT_APP_CART_SERVICE_URL || 'http://localhost:9556/api/v1';

/**
 * Get cart by user ID
 * @param {string} userId User ID
 * @returns {Promise<Object>} Cart details
 */
export const getCart = async (userId) => {
  try {
    if(!userId) {
      return [];
    }
    const response = await axios.get(`${CART_SERVICE_URL}/carts/${userId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching cart for user ${userId}:`, error);

    // Check for specific error responses
    if (error.response) {
      // Server responded with an error status
      if (error.response.status === 404) {
        console.log("Cart not found for user, returning empty cart");
        return []; // Return empty cart for 404 (Not Found)
      }
      if (error.response.status === 500) {
        console.log("Server error when fetching cart, returning empty cart");
        return []; // Return empty cart for 500 (Server Error)
      }
    }
    throw error;
  }
};

/**
 * Empty cart for a user
 * @param {string} userId User ID
 * @returns {Promise<Object>} Operation result
 */
export const emptyCart = async (userId) => {
  try {
    const response = await axios.delete(`${CART_SERVICE_URL}/carts/${userId}`);
    return response.data;
  } catch (error) {
    console.error(`Error emptying cart for user ${userId}:`, error);
    throw error;
  }
};

/**
 * Add item to cart
 * @param {string} userId User ID (session ID)
 * @param {string} productId Product ID to add
 * @param {number} quantity Quantity to add
 * @returns {Promise<Object>} Updated cart
 */
export const addItemToCart = async (userId, productId, quantity) => {
  try {
    if (!userId) {
      console.error('Error: User ID is required to add item to cart');
      throw new Error('User ID is required');
    }

    if (!productId) {
      console.error('Error: Product ID is required to add item to cart');
      throw new Error('Product ID is required');
    }

    if (!quantity || quantity <= 0) {
      console.error('Error: Quantity must be positive');
      throw new Error('Quantity must be positive');
    }
    
    // Use URL parameters for userId and query parameters for productId and quantity
    const response = await axios.post(
      `${CART_SERVICE_URL}/carts/${userId}/items`, 
      null,  // No request body
      {
        params: {
          productId,
          quantity
        }
      }
    );
    
    // The controller returns an ApiResponse wrapper, so we need to extract the data
    return response.data.data; // Assuming the actual cart is in the 'data' field
  } catch (error) {
    console.error('Error adding item to cart:', error);
    throw error;
  }
};

/**
 * Add item to cart using path variables
 * @param {string} userId User ID
 * @param {string} productId Product ID
 * @param {number} quantity Quantity to add
 * @returns {Promise<Object>} Updated cart
 */
export const addItemToCartPath = async (userId, productId, quantity) => {
  try {
    const response = await axios.post(
      `${CART_SERVICE_URL}/carts/${userId}/items/${productId}/quantity/${quantity}`
    );
    return response.data;
  } catch (error) {
    console.error(`Error adding product ${productId} to cart for user ${userId}:`, error);
    throw error;
  }
};


export default {
  getCart,
  emptyCart,
  addItemToCart,
  addItemToCartPath
};