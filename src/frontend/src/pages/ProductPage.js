import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productApi, cartApi, recommendationApi, adApi } from '../api';
import { useSession } from '../contexts/SessionContext';
import { useCurrencyConverter } from '../hooks/useCurrencyConverter';
import { formatMoney } from '../utils/formatters';
import RecommendationSection from '../components/RecommendationSection';
import './ProductPage.css';

const ProductPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { sessionId, currency, setCartSize } = useSession();
  
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [recommendations, setRecommendations] = useState([]);
  const [ad, setAd] = useState(null);
  
  // Use the currency converter hook
  const { convertPrice, loading: conversionLoading } = useCurrencyConverter(currency);

  // Fetch product data
  useEffect(() => {
    const fetchProductData = async () => {
      try {
        setLoading(true);
        
        // Fetch product details
        const productData = await productApi.getProduct(id);
        setProduct(productData);
        
        // Fetch recommendations based on product
        try {
          const recommendationResponse = await recommendationApi.getRecommendations(
            sessionId, 
            [id]
          );

        // If we have productIds in the response
        if (recommendationResponse && recommendationResponse.productIds && 
            recommendationResponse.productIds.length > 0) {
            
          // Fetch details for each recommended product
          const recommendedProductsDetails = await Promise.all(
            recommendationResponse.productIds.map(async (productId) => {
              try {
                return await productApi.getProduct(productId);
              } catch (error) {
                console.error(`Failed to fetch details for product ${productId}:`, error);
                return null;
              }
            })
          );

          // Filter out any null values (failed fetches)
          const validRecommendations = recommendedProductsDetails.filter(product => product !== null);
          
          setRecommendations(validRecommendations);
        } else {
          setRecommendations([]);
        }
      } catch (recError) {
        console.error('Failed to fetch recommendations:', recError);
        setRecommendations([]);
      }
        
        // Fetch an ad related to the product categories
        try {
          const categories = productData.categories || [];
          const adResponse = await adApi.getAdsByContextKeys(categories);
          // Check if the response has an 'ads' array and it contains at least one item
          if (adResponse && adResponse.ads && adResponse.ads.length > 0) {
            // Pick a random ad from the results
            const randomIndex = Math.floor(Math.random() * adResponse.ads.length);
            setAd(adResponse.ads[randomIndex]);
          }
        } catch (adError) {
          console.error('Failed to fetch ad:', adError);
          // Don't set error state for ads since it's not critical
        }
        
        setError(null);
      } catch (err) {
        console.error('Failed to fetch product:', err);
        setError('Failed to load product details. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchProductData();
  }, [id, sessionId]);

  const handleQuantityChange = (e) => {
    setQuantity(parseInt(e.target.value, 10));
  };

  const handleAddToCart = async (e) => {
    e.preventDefault();
    
    try {
      await cartApi.addItemToCart(sessionId, id, quantity);

      const response = await cartApi.getCart(sessionId);
      const cartData = response.data;
      
      // Use the totalQuantity provided by the backend
      if (cartData && typeof cartData.totalQuantity === 'number') {
        setCartSize(cartData.totalQuantity);
      } else {
        // Fallback to 0 if we can't determine the cart size
        setCartSize(0);
      }
      
      // Navigate to cart page
      navigate('/cart');
    } catch (err) {
      console.error('Failed to add item to cart:', err);
      alert('Failed to add item to cart. Please try again.');
    }
  };

  if (loading) {
    return (
      <main role="main" className="container py-5 text-center">
        <div className="spinner-border text-primary" role="status">
          <span className="sr-only">Loading...</span>
        </div>
      </main>
    );
  }

  if (error) {
    return (
      <main role="main" className="container py-5">
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      </main>
    );
  }

  if (!product) {
    return (
      <main role="main" className="container py-5">
        <div className="alert alert-warning" role="alert">
          Product not found.
        </div>
      </main>
    );
  }
  
  // Get the converted price if needed
  const convertedValue = currency !== 'USD' 
    ? convertPrice(product.priceUsd)
    : null;

  return (
    <main role="main">
      <div className="h-product container">
        <div className="row">
          <div className="col-md-6">
            <img 
              className="product-image" 
              alt={product.name} 
              src={product.picture} 
            />
          </div>
          <div className="product-info col-md-5">
            <div className="product-wrapper">
              <h2>{product.name}</h2>
              <p className="product-price">
                {formatMoney(product.priceUsd, currency, convertedValue)}
                {conversionLoading && (
                  <small className="ms-2 text-muted">
                    <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                    <span className="visually-hidden">Converting...</span>
                  </small>
                )}
              </p>
              <p>{product.description}</p>

              {/* Optional packaging info would go here */}

              <form onSubmit={handleAddToCart}>
                <div className="product-quantity-dropdown">
                  <select 
                    name="quantity" 
                    id="quantity"
                    value={quantity}
                    onChange={handleQuantityChange}
                  >
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                    <option value="10">10</option>
                  </select>
                  <img src="/static/icons/Hipster_DownArrow.svg" alt="" />
                </div>
                <button type="submit" className="cymbal-button-primary">
                  Add To Cart
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
      
      {recommendations && recommendations.length > 0 && (
        <RecommendationSection recommendations={recommendations} />
      )}
      
      {ad && (
        <div className="ad">
          <div className="container py-3 px-lg-5 py-lg-5">
            <div role="alert">
              <strong>Ad</strong>
              <a 
                href={ad.redirectUrl} 
                rel="nofollow noopener noreferrer" 
                target="_blank"
              >
                {ad.text}
              </a>
            </div>
          </div>
        </div>
      )}
    </main>
  );
};

export default ProductPage;