import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { productApi } from '../api';
import { useSession } from '../contexts/SessionContext';
import { useCurrencyConverter } from '../hooks/useCurrencyConverter';
import { formatMoney } from '../utils/formatters';
import './HomePage.css';

const HomePage = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { currency } = useSession();
  
  // Use the currency converter hook
  const { 
    convertPrice, 
    loading: conversionLoading, 
    error: conversionError,
    rates
  } = useCurrencyConverter(currency);

  // Debug log when currency or rates change
  useEffect(() => {
    console.log('Current currency:', currency);
    console.log('Current rates:', rates);
  }, [currency, rates]);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        const data = await productApi.getProducts();
        
        console.log('Products data:', data); // Debug log
        
        // Check if data is an object with a products property
        if (data && data.products && Array.isArray(data.products)) {
          setProducts(data.products);
        } else if (Array.isArray(data)) {
          // If data is already an array, use it directly
          setProducts(data);
        } else {
          console.error('Unexpected response format:', data);
          setError('Received unexpected data format from the server');
        }
      } catch (err) {
        console.error('Failed to fetch products:', err);
        setError('Failed to load products. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  return (
    <main role="main" className="home">
      <div className="home-mobile-hero-banner d-lg-none"></div>

      <div className="container-fluid">
        <div className="row">
          <div className="col-12 col-lg-12 px-10-percent">
            <div className="row hot-products-row px-xl-6">
              <div className="col-12 d-flex justify-content-between align-items-center">
                <h3>Hot Products</h3>
                {conversionLoading && (
                  <small className="text-muted">Converting prices...</small>
                )}
                {conversionError && (
                  <small className="text-danger">{conversionError}</small>
                )}
              </div>

              {loading ? (
                <div className="col-12 text-center py-5">
                  <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                  </div>
                </div>
              ) : error ? (
                <div className="col-12 text-center py-5">
                  <div className="alert alert-danger" role="alert">
                    {error}
                  </div>
                </div>
              ) : (
                products.map((product) => {
                  // Get the converted price
                  let convertedValue = null;
                  
                  if (currency !== 'USD' && product.priceUsd) {
                    convertedValue = convertPrice(product.priceUsd);
                    // Debug log for each product conversion
                    console.log(
                      `Converting ${product.name} price: `,
                      product.priceUsd, 
                      'to', 
                      currency, 
                      'result:', 
                      convertedValue
                    );
                  }
                  
                  return (
                    <div key={product.id} className="col-md-4 hot-product-card">
                      <Link to={`/product/${product.id}`}>
                        <img loading="lazy" src={product.picture} alt={product.name} />
                        <div className="hot-product-card-img-overlay"></div>
                      </Link>
                      <div>
                        <div className="hot-product-card-name">{product.name}</div>
                        <div className="hot-product-card-price">
                          {formatMoney(product.priceUsd, currency, convertedValue)}
                        </div>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </div>
        </div>
      </div>
    </main>
  );
};

export default HomePage;