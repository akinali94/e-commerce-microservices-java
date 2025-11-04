import productApi from './productApi';
import cartApi from './cartApi';
import checkoutApi from './checkoutApi';
import currencyApi from './currencyApi';
import adApi from './adApi';
import recommendationApi from './recommendationApi';
import shippingApi from './shippingApi';
import apiConfig from './apiConfig';


// Initialize API configuration
apiConfig.setupApiConfig();

// Export all API modules
export {
  productApi,
  cartApi,
  checkoutApi,
  currencyApi,
  adApi,
  recommendationApi,
  shippingApi,
  apiConfig
};

// Default export for easier importing
export default {
  product: productApi,
  cart: cartApi,
  checkout: checkoutApi,
  currency: currencyApi,
  ad: adApi,
  recommendation: recommendationApi,
  shipping: shippingApi,
  config: apiConfig
};