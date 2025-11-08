import axios from 'axios';

// Base URL for Product Catalog Service
const PRODUCT_SERVICE_URL = process.env.REACT_APP_PRODUCT_SERVICE_URL || 'http://localhost:9561';

/**
 * Get all products from the catalog
 * @returns {Promise<Array>} List of products
 */
export const getProducts = async () => {
  try {
    const response = await axios.get(`${PRODUCT_SERVICE_URL}/api/v1/products`);
    // Check if response.data has a products property
    return response.data.products || response.data;
  } catch (error) {
    console.error('Error fetching products:', error);
    throw error;
  }
};

/**
 * Get a product by its ID
 * @param {string} id Product ID
 * @returns {Promise<Object>} Product details
 */
export const getProduct = async (id) => {
  try {
    const response = await axios.get(`${PRODUCT_SERVICE_URL}/api/v1/products/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching product ${id}:`, error);
    throw error;
  }
};

/**
 * Get multiple products by their IDs
 * @param {string[]} ids Array of product IDs to fetch
 * @returns {Promise<Object[]>} Array of product details
 */
export const getMultipleProducts = async (ids) => {
  try {
    // Validate input
    if (!ids || !Array.isArray(ids) || ids.length === 0) {
      console.warn('getProductsBatch called with invalid or empty ids array');
      return [];
    }

    // Call the batch endpoint with the array of product IDs in the request body
    const response = await axios.post(`${PRODUCT_SERVICE_URL}/api/v1/products/batch`, ids);
    
    // Return the data array from the response
    return response.data;
  } catch (error) {
    console.error(`Error fetching products batch:`, error);
    
    // For batch requests, we might want to return an empty array rather than throwing
    // This allows partial failure handling in the UI
    console.warn('Returning empty array due to batch request failure');
    return [];
  }
};


/**
 * Search products by query
 * @param {string} query Search query
 * @returns {Promise<Array>} List of products matching the search query
 */
export const searchProducts = async (query) => {
  try {
    const response = await axios.get(`${PRODUCT_SERVICE_URL}/api/v1/products/search`, {
      params: { q: query }
    });
    return response.data;
  } catch (error) {
    console.error('Error searching products:', error);
    throw error;
  }
};
;



export default {
  getProducts,
  getProduct,
  getMultipleProducts,
  searchProducts
};