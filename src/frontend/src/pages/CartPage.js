import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { cartApi, recommendationApi, shippingApi, productApi, checkoutApi } from '../api';
import { useSession } from '../contexts/SessionContext';
import { useCurrencyConverter } from '../hooks/useCurrencyConverter';
import { formatMoney } from '../utils/formatters';
import RecommendationSection from '../components/RecommendationSection';
import './CartPage.css';

const CartPage = () => {
  const { sessionId, currency, setCartSize } = useSession();
  const navigate = useNavigate();
  
  const [items, setItems] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [shippingCost, setShippingCost] = useState({ units: 0, nanos: 0 });
  const [totalCost, setTotalCost] = useState({ units: 0, nanos: 0 });
  const [isSubmitting, setIsSubmitting] = useState(false);

  
  // Use the currency converter hook
  const { convertPrice, loading: conversionLoading } = useCurrencyConverter(currency);
  
  // Mock expiration years for form
  const currentYear = new Date().getFullYear();
  const expirationYears = Array.from(
    { length: 10 }, 
    (_, i) => currentYear + i
  );

  useEffect(() => {
    // Only fetch cart when sessionId is available
    if (!sessionId) {
      console.log("Waiting for session ID to be available...");
      return; // Exit early if sessionId is not available yet
    }
    
  const fetchCart = async () => {
    try {
      setLoading(true);
      
      // Fetch cart
      const response = await cartApi.getCart(sessionId);
      const cartData = response && response.success ? response.data : null;
      
      // Check if cart data exists and has items
      if (cartData && cartData.items && Array.isArray(cartData.items)) {
        // Use totalQuantity from the response
        if (typeof cartData.totalQuantity === 'number') {
          setCartSize(cartData.totalQuantity);
        }
        
        // If cart is not empty, process cart items and get recommendations
        if (cartData.items.length > 0) {
          // Get product IDs from cart
          const productIds = cartData.items.map(item => item.productId);
          
          // Fetch all product details in a single batch request
          const productsResponse = await productApi.getMultipleProducts(productIds);
          const products = productsResponse.products || [];
          
          // Create a map of products by ID for easier lookup
          const productMap = {};
          products.forEach(product => {
            productMap[product.id] = product;
          });
          
          // Combine cart items with product details
          const itemsWithDetails = cartData.items.map(item => {
            const product = productMap[item.productId];
            
            if (product) {
              return {
                ...item,
                cost: product.priceUsd,
                name: product.name,
                picture: product.picture
              };
            } else {
              // Fallback for products that weren't found
              return {
                ...item,
                cost: { units: 0, nanos: 0 },
                name: "Unknown Product",
                picture: "/static/img/products/placeholder.jpg"
              };
            }
          });
          
          // Update items with full details
          setItems(itemsWithDetails);
          
          // Fetch recommendations based on cart items
          try {
            const recommendationResponse = await recommendationApi.getRecommendations(
              sessionId, 
              productIds
            );
            
            // Process recommendations data
            if (recommendationResponse && recommendationResponse.productIds && 
                recommendationResponse.productIds.length > 0) {
              // Fetch details for all recommended products in a single batch request
              const recommendedProductsResponse = await productApi.getMultipleProducts(
                recommendationResponse.productIds
              );
              
              // Set recommendations from the batch response
              const recommendedProducts = recommendedProductsResponse.products || [];
              setRecommendations(recommendedProducts);
            } else {
              setRecommendations([]);
            }
          } catch (recError) {
            console.error('Failed to fetch recommendations:', recError);
            setRecommendations([]);
          }
          
          // Calculate shipping cost
          try {
            const quoteRequest = {
              address: null, // Default address for demo
              items: cartData.items
            };
            const quoteResponse = await shippingApi.getShippingQuote(quoteRequest);
            
            // Extract shipping cost
            const shippingCost = quoteResponse.costUsd || { units: 0, nanos: 0 };
            setShippingCost(shippingCost);
            
            // Calculate subtotal using the itemsWithDetails that now have cost information
            const itemsTotal = itemsWithDetails.reduce((total, item) => {
              const itemCost = item.cost || { units: 0, nanos: 0 };
              const itemTotal = {
                units: itemCost.units * item.quantity,
                nanos: itemCost.nanos * item.quantity
              };
              
              // Handle nanos overflow
              if (itemTotal.nanos >= 1000000000) {
                itemTotal.units += Math.floor(itemTotal.nanos / 1000000000);
                itemTotal.nanos %= 1000000000;
              }
              
              // Add to running total
              total.units += itemTotal.units;
              total.nanos += itemTotal.nanos;
              
              // Handle nanos overflow in total
              if (total.nanos >= 1000000000) {
                total.units += Math.floor(total.nanos / 1000000000);
                total.nanos %= 1000000000;
              }
              
              return total;
            }, { units: 0, nanos: 0 });
            
            // Add shipping to total
            const total = {
              units: itemsTotal.units + shippingCost.units,
              nanos: itemsTotal.nanos + shippingCost.nanos
            };
            
            // Handle nanos overflow
            if (total.nanos >= 1000000000) {
              total.units += Math.floor(total.nanos / 1000000000);
              total.nanos %= 1000000000;
            }
            
            setTotalCost(total);
            
          } catch (shipError) {
            console.error('Failed to fetch shipping quote:', shipError);
            setShippingCost({ units: 0, nanos: 0 });
            setTotalCost({ units: 0, nanos: 0 });
          }
        } else {
          // Empty cart - reset everything
          setItems([]);
          setRecommendations([]);
          setShippingCost({ units: 0, nanos: 0 });
          setTotalCost({ units: 0, nanos: 0 });
        }
      } else {
        // Empty or invalid cart - reset everything
        setItems([]);
        setCartSize(0);
        setRecommendations([]);
        setShippingCost({ units: 0, nanos: 0 });
        setTotalCost({ units: 0, nanos: 0 });
      }
      
      setError(null);
    } catch (err) {
      console.error('Failed to fetch cart:', err);
      setError('Failed to load your cart. Please try again later.');
      
      // Reset all state on error
      setItems([]);
      setCartSize(0);
      setRecommendations([]);
      setShippingCost({ units: 0, nanos: 0 });
      setTotalCost({ units: 0, nanos: 0 });
    } finally {
      setLoading(false);
    }
  };

    fetchCart();
  }, [sessionId, setCartSize]); // Add sessionId as a dependency

  const handleEmptyCart = async () => {
    try {
      await cartApi.emptyCart(sessionId);
      setItems([]);
      setCartSize(0);
      setShippingCost({ units: 0, nanos: 0 });
      setTotalCost({ units: 0, nanos: 0 });
    } catch (err) {
      console.error('Failed to empty cart:', err);
      alert('Failed to empty your cart. Please try again.');
    }
  };

  const handleCheckout = async (e) => {
    e.preventDefault();

    setIsSubmitting(true);


    try {

      // Gather form data
      const formData = new FormData(e.target);
      const checkoutData = {
        userId: sessionId,
        userCurrency: currency,
        address: {
          streetAddress: formData.get('street_address'),
          city: formData.get('city'),
          state: formData.get('state'),
          country: formData.get('country'),
          zipCode: formData.get('zip_code')
        },
        email: formData.get('email'),
        creditCard: {
          number: formData.get('credit_card_number'),
          cvv: formData.get('credit_card_cvv'),
          expirationMonth: parseInt(formData.get('credit_card_expiration_month'), 10),
          expirationYear: parseInt(formData.get('credit_card_expiration_year'), 10)
        },
        items: items.map(item => ({
          productId: item.productId,
          quantity: item.quantity
        }))
      };

      // Call the checkout API using our service
      const orderResponse = await checkoutApi.placeOrder(checkoutData);
      
      // Check if the response has the expected structure
      if (!orderResponse) {
        throw new Error(orderResponse?.message || 'Order placement failed');
      }
      
      // Extract the order data (assuming it's in the 'data' field like your other APIs)
      const orderData = orderResponse.order;
      
      // Clear cart after successful checkout
      await cartApi.emptyCart(sessionId);
      setCartSize(0);
      
      // Navigate to order confirmation page with order data
      navigate('/order', { 
        state: { 
          order: orderData 
        }
      });
    } catch(err) {
        console.error('Checkout failed:', err);
        
        const errorMessage = err.response?.data?.message || err.message || 'Checkout failed. Please try again.';
        alert(errorMessage);
    }
  }

  if (loading) {
    return (
      <main role="main" className="cart-sections">
        <div className="container py-5 text-center">
          <div className="spinner-border text-primary" role="status">
            <span className="sr-only">Loading...</span>
          </div>
        </div>
      </main>
    );
  }

  if (error) {
    return (
      <main role="main" className="cart-sections">
        <div className="container py-5">
          <div className="alert alert-danger" role="alert">
            {error}
          </div>
        </div>
      </main>
    );
  }

  // Empty cart view
  if (!items || items.length === 0) {
    return (
      <main role="main" className="cart-sections">
        <section className="empty-cart-section">
          <h3>Your shopping cart is empty!</h3>
          <p>Items you add to your shopping cart will appear here.</p>
          <Link className="cymbal-button-primary" to="/" role="button">
            Continue Shopping
          </Link>
        </section>
      </main>
    );
  }

  // Cart with items
  return (
    <main role="main" className="cart-sections">
      <section className="container">
        <div className="row">
          {/* Cart Summary Section */}
          <div className="col-lg-6 col-xl-5 offset-xl-1 cart-summary-section">
            <div className="row mb-3 py-2">
              <div className="col-4 pl-md-0">
                <h3>Cart ({items.reduce((total, item) => total + item.quantity, 0)})</h3>
              </div>
              <div className="col-8 pr-md-0 text-right">
                <button 
                  className="cymbal-button-secondary cart-summary-empty-cart-button"
                  onClick={handleEmptyCart}
                >
                  Empty Cart
                </button>
                <Link className="cymbal-button-primary" to="/" role="button">
                  Continue Shopping
                </Link>
              </div>
            </div>

            {/* Cart Items */}
            {items.map((item) => {
              const convertedCost = currency !== 'USD' && item.cost ? convertPrice(item.cost) : null;
              return (
                <div key={item.productId} className="row cart-summary-item-row">
                    <div className="col-md-4 pl-md-0">
                      <Link to={`/product/${item.productId}`}>
                        <img className="img-fluid" alt={item.name} src={item.picture} />
                      </Link>
                    </div>
                    <div className="col-md-8 pr-md-0">
                      <div className="row">
                        <div className="col">
                          <h4>{item.name}</h4>
                        </div>
                      </div>
                      <div className="row cart-summary-item-row-item-id-row">
                        <div className="col">
                          SKU #{item.productId}
                        </div>
                      </div>
                      <div className="row">
                        <div className="col">
                          Quantity: {item.quantity}
                        </div>
                        <div className="col pr-md-0 text-right">
                          <strong>
                            {formatMoney(item.cost, currency, convertedCost)}
                          </strong>
                        </div>
                      </div>
                    </div>
                  </div>
              );
            })}

            {/* Shipping and Total */}
            <div className="row cart-summary-shipping-row">
              <div className="col pl-md-0">Shipping</div>
              <div className="col pr-md-0 text-right">
                {formatMoney(shippingCost, currency, currency !== 'USD' ? convertPrice(shippingCost) : null)}
              </div>
            </div>

            <div className="row cart-summary-total-row">
              <div className="col pl-md-0">Total</div>
              <div className="col pr-md-0 text-right">
                {formatMoney(totalCost, currency, currency !== 'USD' ? convertPrice(totalCost) : null)}
              </div>
            </div>
          </div>

          {/* Checkout Form */}
          <div className="col-lg-5 offset-lg-1 col-xl-4">
            <form className="cart-checkout-form" onSubmit={handleCheckout}>
              <div className="row">
                <div className="col">
                  <h3>Shipping Address</h3>
                </div>
              </div>

              <div className="form-row">
                <div className="col cymbal-form-field">
                  <label htmlFor="email">E-mail Address</label>
                  <input 
                    type="email" 
                    id="email"
                    name="email" 
                    defaultValue="someone@example.com" 
                    required 
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="col cymbal-form-field">
                  <label htmlFor="street_address">Street Address</label>
                  <input 
                    type="text" 
                    name="street_address"
                    id="street_address" 
                    defaultValue="3400 Istanbul Street" 
                    required 
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="col cymbal-form-field">
                  <label htmlFor="zip_code">Zip Code</label>
                  <input 
                    type="text"
                    name="zip_code" 
                    id="zip_code" 
                    defaultValue="34116" 
                    required 
                    pattern="\d{4,5}" 
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="col cymbal-form-field">
                  <label htmlFor="city">City</label>
                  <input 
                    type="text" 
                    name="city" 
                    id="city"
                    defaultValue="Luxembourg City" 
                    required 
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="col-md-5 cymbal-form-field">
                  <label htmlFor="state">State</label>
                  <input 
                    type="text" 
                    name="state" 
                    id="state"
                    defaultValue="LC" 
                    required 
                  />
                </div>
                <div className="col-md-7 cymbal-form-field">
                  <label htmlFor="country">Country</label>
                  <input 
                    type="text" 
                    id="country"
                    placeholder="Country Name"
                    name="country" 
                    defaultValue="Luxembourg" 
                    required 
                  />
                </div>
              </div>

              <div className="row">
                <div className="col">
                  <h3 className="payment-method-heading">Payment Method</h3>
                </div>
              </div>

              <div className="form-row">
                <div className="col cymbal-form-field">
                  <label htmlFor="credit_card_number">Credit Card Number</label>
                  <input 
                    type="text" 
                    id="credit_card_number"
                    name="credit_card_number"
                    placeholder="0000000000000000"
                    defaultValue="4432801561520454"
                    required 
                    pattern="\d{16}" 
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="col-md-5 cymbal-form-field">
                  <label htmlFor="credit_card_expiration_month">Month</label>
                  <select 
                    name="credit_card_expiration_month" 
                    id="credit_card_expiration_month"
                    defaultValue="1"
                  >
                    <option value="1">January</option>
                    <option value="2">February</option>
                    <option value="3">March</option>
                    <option value="4">April</option>
                    <option value="5">May</option>
                    <option value="6">June</option>
                    <option value="7">July</option>
                    <option value="8">August</option>
                    <option value="9">September</option>
                    <option value="10">October</option>
                    <option value="11">November</option>
                    <option value="12">December</option>
                  </select>
                  <img src="/icons/Hipster_DownArrow.svg" alt="" className="cymbal-dropdown-chevron" />
                </div>
                <div className="col-md-4 cymbal-form-field">
                  <label htmlFor="credit_card_expiration_year">Year</label>
                  <select 
                    name="credit_card_expiration_year" 
                    id="credit_card_expiration_year"
                    defaultValue={expirationYears[1]}
                  >
                    {expirationYears.map((year, index) => (
                      <option key={year} value={year}>
                        {year}
                      </option>
                    ))}
                  </select>
                  <img src="/icons/Hipster_DownArrow.svg" alt="" className="cymbal-dropdown-chevron" />
                </div>
                <div className="col-md-3 cymbal-form-field">
                  <label htmlFor="credit_card_cvv">CVV</label>
                  <input 
                    type="password" 
                    id="credit_card_cvv"
                    name="credit_card_cvv" 
                    defaultValue="672" 
                    required 
                    pattern="\d{3}" 
                  />
                </div>
              </div>

              <div className="form-row justify-content-center">
                <div className="col text-center">
                  <button className="cymbal-button-primary" type="submit" disabled={isSubmitting}>
                    {isSubmitting ? 'Processing...' : 'Place Order'}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </section>

      {/* Recommendations Section */}
      {recommendations && recommendations.length > 0 && (
        <RecommendationSection recommendations={recommendations} />
      )}
    </main>
  );
};

export default CartPage;