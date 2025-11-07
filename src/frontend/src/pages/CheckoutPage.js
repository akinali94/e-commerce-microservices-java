import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSession } from '../contexts/SessionContext';
import { cartApi, checkoutApi } from '../api';
import { formatMoney } from '../utils/formatters';
import './CheckoutPage.css';

const CheckoutPage = () => {
  const navigate = useNavigate();
  const { sessionId, currency, setCartSize } = useSession();
  
  const [formData, setFormData] = useState({
    email: '',
    streetAddress: '',
    zipCode: '',
    city: '',
    state: '',
    country: '',
    creditCardNumber: '',
    creditCardCvv: '',
    creditCardExpirationMonth: 1,
    creditCardExpirationYear: new Date().getFullYear() + 1
  });
  
  const [orderSummary, setOrderSummary] = useState({
    items: [],
    totalItems: 0,
    subtotal: { units: 0, nanos: 0 },
    shipping: { units: 0, nanos: 0 },
    total: { units: 0, nanos: 0 }
  });
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [processingOrder, setProcessingOrder] = useState(false);
  
  // Generate expiration year options (current year + 10 years)
  const currentYear = new Date().getFullYear();
  const expirationYears = Array.from(
    { length: 10 }, 
    (_, i) => currentYear + i
  );
  
  // Fetch cart data on component mount
  React.useEffect(() => {
    const fetchCartData = async () => {
      try {
        setLoading(true);
        
        // Get cart items
        const cartItems = await cartApi.getCart(sessionId);
        
        if (!cartItems || cartItems.length === 0) {
          // If cart is empty, redirect to cart page
          navigate('/cart');
          return;
        }
        
        // Calculate subtotal
        let subtotal = { units: 0, nanos: 0 };
        let totalItems = 0;
        
        cartItems.forEach(item => {
          totalItems += item.quantity;
          
          if (item.cost) {
            // Calculate cost for this item based on quantity
            const itemTotal = {
              units: item.cost.units * item.quantity,
              nanos: item.cost.nanos * item.quantity
            };
            
            // Handle nanos overflow
            if (itemTotal.nanos >= 1000000000) {
              itemTotal.units += Math.floor(itemTotal.nanos / 1000000000);
              itemTotal.nanos %= 1000000000;
            }
            
            // Add to subtotal
            subtotal.units += itemTotal.units;
            subtotal.nanos += itemTotal.nanos;
            
            // Handle nanos overflow in subtotal
            if (subtotal.nanos >= 1000000000) {
              subtotal.units += Math.floor(subtotal.nanos / 1000000000);
              subtotal.nanos %= 1000000000;
            }
          }
        });
        
        // Get shipping estimate
        // For demo purposes, use flat rate shipping
        const shipping = { units: 5, nanos: 990000000 }; // $5.99
        
        // Calculate total
        const total = {
          units: subtotal.units + shipping.units,
          nanos: subtotal.nanos + shipping.nanos
        };
        
        // Handle nanos overflow in total
        if (total.nanos >= 1000000000) {
          total.units += Math.floor(total.nanos / 1000000000);
          total.nanos %= 1000000000;
        }
        
        // Update order summary
        setOrderSummary({
          items: cartItems,
          totalItems,
          subtotal,
          shipping,
          total
        });
        
        setError(null);
      } catch (err) {
        console.error('Error fetching cart data:', err);
        setError('Failed to load your cart information. Please try again.');
      } finally {
        setLoading(false);
      }
    };
    
    fetchCartData();
  }, [sessionId, navigate]);
  
  // Handle form input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };
  
  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (processingOrder) return;
    
    try {
      setProcessingOrder(true);
      
      // Prepare order data
      const orderData = {
        userId: sessionId,
        userCurrency: currency,
        address: {
          streetAddress: formData.streetAddress,
          city: formData.city,
          state: formData.state,
          country: formData.country,
          zipCode: formData.zipCode
        },
        email: formData.email,
        creditCard: {
          creditCardNumber: formData.creditCardNumber,
          creditCardCvv: formData.creditCardCvv,
          creditCardExpirationYear: parseInt(formData.creditCardExpirationYear, 10),
          creditCardExpirationMonth: parseInt(formData.creditCardExpirationMonth, 10)
        },
        items: orderSummary.items.map(item => ({
          productId: item.productId,
          quantity: item.quantity
        }))
      };
      
      // Place order
      const orderResult = await checkoutApi.placeOrder(orderData);
      
      // Clear cart after successful checkout
      await cartApi.emptyCart(sessionId);
      setCartSize(0);
      
      // Navigate to order confirmation page with order data
      navigate('/order', { 
        state: { 
          order: orderResult 
        }
      });
      
    } catch (err) {
      console.error('Order placement failed:', err);
      setError('Failed to process your order. Please check your information and try again.');
    } finally {
      setProcessingOrder(false);
    }
  };
  
  if (loading) {
    return (
      <main className="checkout-page">
        <div className="container">
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-3">Loading checkout information...</p>
          </div>
        </div>
      </main>
    );
  }
  
  return (
    <main className="checkout-page">
      <div className="container">
        <h2 className="text-center mb-4">Checkout</h2>
        
        {error && (
          <div className="alert alert-danger" role="alert">
            {error}
          </div>
        )}
        
        <div className="row">
          {/* Order Summary */}
          <div className="col-md-4 order-md-2 mb-4">
            <div className="checkout-summary">
              <h4 className="d-flex justify-content-between align-items-center mb-3">
                <span>Order Summary</span>
                <span className="badge bg-primary rounded-pill">{orderSummary.totalItems}</span>
              </h4>
              
              <ul className="list-group mb-3">
                {orderSummary.items.map((item) => (
                  <li key={item.productId} className="list-group-item d-flex justify-content-between lh-sm">
                    <div>
                      <h6 className="my-0">{item.name} × {item.quantity}</h6>
                      <small className="text-muted">{item.description ? item.description.substring(0, 50) + '...' : 'No description'}</small>
                    </div>
                    <span className="text-muted">{formatMoney(item.cost, currency)}</span>
                  </li>
                ))}
                
                <li className="list-group-item d-flex justify-content-between">
                  <span>Subtotal</span>
                  <strong>{formatMoney(orderSummary.subtotal, currency)}</strong>
                </li>
                
                <li className="list-group-item d-flex justify-content-between">
                  <span>Shipping</span>
                  <strong>{formatMoney(orderSummary.shipping, currency)}</strong>
                </li>
                
                <li className="list-group-item d-flex justify-content-between bg-light">
                  <span className="text-success">Total</span>
                  <strong className="text-success">{formatMoney(orderSummary.total, currency)}</strong>
                </li>
              </ul>
            </div>
          </div>
          
          {/* Checkout Form */}
          <div className="col-md-8 order-md-1">
            <form className="checkout-form" onSubmit={handleSubmit}>
              <h4 className="mb-3">Contact Information</h4>
              <div className="mb-3">
                <label htmlFor="email" className="form-label">Email</label>
                <input 
                  type="email" 
                  className="form-control" 
                  id="email" 
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  placeholder="you@example.com"
                  required
                />
              </div>
              
              <h4 className="mb-3">Shipping Address</h4>
              <div className="mb-3">
                <label htmlFor="streetAddress" className="form-label">Street Address</label>
                <input 
                  type="text" 
                  className="form-control" 
                  id="streetAddress" 
                  name="streetAddress"
                  value={formData.streetAddress}
                  onChange={handleInputChange}
                  placeholder="1234 Main St"
                  required
                />
              </div>
              
              <div className="row">
                <div className="col-md-5 mb-3">
                  <label htmlFor="city" className="form-label">City</label>
                  <input 
                    type="text" 
                    className="form-control" 
                    id="city" 
                    name="city"
                    value={formData.city}
                    onChange={handleInputChange}
                    placeholder="City"
                    required
                  />
                </div>
                
                <div className="col-md-4 mb-3">
                  <label htmlFor="state" className="form-label">State</label>
                  <input 
                    type="text" 
                    className="form-control" 
                    id="state" 
                    name="state"
                    value={formData.state}
                    onChange={handleInputChange}
                    placeholder="State"
                    required
                  />
                </div>
                
                <div className="col-md-3 mb-3">
                  <label htmlFor="zipCode" className="form-label">Zip Code</label>
                  <input 
                    type="text" 
                    className="form-control" 
                    id="zipCode" 
                    name="zipCode"
                    value={formData.zipCode}
                    onChange={handleInputChange}
                    placeholder="12345"
                    required
                  />
                </div>
              </div>
              
              <div className="mb-3">
                <label htmlFor="country" className="form-label">Country</label>
                <input 
                  type="text" 
                  className="form-control" 
                  id="country" 
                  name="country"
                  value={formData.country}
                  onChange={handleInputChange}
                  placeholder="Country"
                  required
                />
              </div>
              
              <h4 className="mb-3">Payment Information</h4>
              <div className="mb-3">
                <label htmlFor="creditCardNumber" className="form-label">Credit Card Number</label>
                <input 
                  type="text" 
                  className="form-control" 
                  id="creditCardNumber" 
                  name="creditCardNumber"
                  value={formData.creditCardNumber}
                  onChange={handleInputChange}
                  placeholder="1234 5678 9012 3456"
                  required
                />
              </div>
              
              <div className="row">
                <div className="col-md-4 mb-3">
                  <label htmlFor="creditCardExpirationMonth" className="form-label">Month</label>
                  <select 
                    className="form-select" 
                    id="creditCardExpirationMonth"
                    name="creditCardExpirationMonth"
                    value={formData.creditCardExpirationMonth}
                    onChange={handleInputChange}
                    required
                  >
                    {Array.from({ length: 12 }, (_, i) => i + 1).map(month => (
                      <option key={month} value={month}>
                        {month < 10 ? `0${month}` : month}
                      </option>
                    ))}
                  </select>
                </div>
                
                <div className="col-md-4 mb-3">
                  <label htmlFor="creditCardExpirationYear" className="form-label">Year</label>
                  <select 
                    className="form-select" 
                    id="creditCardExpirationYear"
                    name="creditCardExpirationYear"
                    value={formData.creditCardExpirationYear}
                    onChange={handleInputChange}
                    required
                  >
                    {expirationYears.map(year => (
                      <option key={year} value={year}>{year}</option>
                    ))}
                  </select>
                </div>
                
                <div className="col-md-4 mb-3">
                  <label htmlFor="creditCardCvv" className="form-label">CVV</label>
                  <input 
                    type="text" 
                    className="form-control" 
                    id="creditCardCvv" 
                    name="creditCardCvv"
                    value={formData.creditCardCvv}
                    onChange={handleInputChange}
                    placeholder="123"
                    required
                  />
                </div>
              </div>
              
              <button 
                className="w-100 btn btn-primary btn-lg mt-4"
                type="submit"
                disabled={processingOrder}
              >
                {processingOrder ? 'Processing...' : 'Place Order'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </main>
  );
};

export default CheckoutPage;