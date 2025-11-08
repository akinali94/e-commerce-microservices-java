import { useState, useEffect } from 'react';
import { currencyApi } from '../api';

export const useCurrencyConverter = (selectedCurrency) => {
  const [rates, setRates] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Update rates when currency changes
  useEffect(() => {
    const fetchRates = async () => {
      // Only fetch rates if not USD
      if (selectedCurrency === 'USD') {
        setRates({});
        return;
      }

      setLoading(true);
      try {
        // Get the conversion rate for 1 USD
        const response = await currencyApi.convertCurrency('USD', selectedCurrency, 1);
        console.log('Currency conversion response:', response); // Debug log
        
        if (response && typeof response.result === 'number') {
          setRates({
            [selectedCurrency]: response.result
          });
          setError(null);
        } else {
          console.error('Invalid currency conversion response:', response);
          setError('Currency conversion failed');
        }
      } catch (error) {
        console.error('Failed to fetch currency conversion rate:', error);
        setError('Currency conversion request failed');
      } finally {
        setLoading(false);
      }
    };

    fetchRates();
  }, [selectedCurrency]);

  // Simple function to convert price
  const convertPrice = (priceInUsd) => {
    // If price is null or undefined, return null
    if (!priceInUsd) return null;

    // If currency is USD or no rate, return null to use the original price
    if (selectedCurrency === 'USD' || !rates[selectedCurrency]) {
      return null;
    }

    try {
      // Get the conversion rate
      const rate = rates[selectedCurrency];

      // Calculate the value in USD
      const units = priceInUsd.units || 0;
      const nanos = priceInUsd.nanos || 0;
      const valueInUsd = units + (nanos / 1000000000);

      // Apply the conversion rate
      const convertedValue = valueInUsd * rate;
      
      // Ensure we return a valid number
      return typeof convertedValue === 'number' && !isNaN(convertedValue) 
        ? convertedValue 
        : null;
    } catch (e) {
      console.error('Error converting price:', e);
      return null; // Return null on error to use original price
    }
  };

  return {
    convertPrice,
    loading,
    error,
    rates
  };
};