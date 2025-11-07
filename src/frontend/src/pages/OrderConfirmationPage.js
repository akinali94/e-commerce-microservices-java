import React from 'react';
import { Link, useLocation, Navigate } from 'react-router-dom';
import { useSession } from '../contexts/SessionContext';
import { useCurrencyConverter } from '../hooks/useCurrencyConverter';
import RecommendationSection from '../components/RecommendationSection';
import { formatMoney } from '../utils/formatters';
import { currencyApi } from '../api';
import './OrderConfirmationPage.css';

const OrderConfirmationPage = () => {
  const location = useLocation();
  const { currency } = useSession();

  const { convertPrice, loading: conversionLoading } = useCurrencyConverter(currency);


  
  // Check if order data exists in location state
  // This would come from the redirect after successful checkout
  const { order, recommendations } = location.state || {};
  
  // If no order data, redirect to home
  if (!order) {
    return <Navigate to="/" replace />;
  }

  const convertedTotal = currency !== 'USD' && order.totalCost 
    ? convertPrice(order.totalCost) 
    : null;

  return (
    <main role="main" className="order">
      <section className="container order-complete-section">
        <div className="row">
          <div className="col-12 text-center">
            <h3>Your order is complete!</h3>
          </div>
          <div className="col-12 text-center">
            <p>We've sent you a confirmation email.</p>
          </div>
        </div>
        <div className="row border-bottom-solid padding-y-24">
          <div className="col-6 pl-md-0">
            Confirmation #
          </div>
          <div className="col-6 pr-md-0 text-right">
            {order.orderId}
          </div>
        </div>
        <div className="row border-bottom-solid padding-y-24">
          <div className="col-6 pl-md-0">
            Tracking #
          </div>
          <div className="col-6 pr-md-0 text-right">
            {order.shippingTrackingId || 'Pending'}
          </div>
        </div>
        <div className="row padding-y-24">
          <div className="col-6 pl-md-0">
            Total Paid
          </div>
          <div className="col-6 pr-md-0 text-right">
            {formatMoney(order.totalCost, currency, convertedTotal)}
            {conversionLoading && (
              <small className="ms-2 text-muted">
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                <span className="visually-hidden">Converting...</span>
              </small>
            )}
          </div>
        </div>
        <div className="row">
          <div className="col-12 text-center">
            <Link className="cymbal-button-primary" to="/" role="button">
              Continue Shopping
            </Link>
          </div>
        </div>
      </section>

      {recommendations && recommendations.length > 0 && (
        <RecommendationSection recommendations={recommendations} />
      )}
    </main>
  );
};

export default OrderConfirmationPage;