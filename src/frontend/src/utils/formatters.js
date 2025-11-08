
/**
 * Format money value for display
 * @param {Object} priceUsd Price object from the product data
 * @param {string} currencyCode Current selected currency code
 * @param {number} convertedValue Optional converted value
 * @returns {string} Formatted price string
 */
export const formatMoney = (priceUsd, currencyCode = 'USD', convertedValue = null) => {
  if (!priceUsd) return '';
  
  // Currency symbols
  const currencySymbols = {
    'USD': '$',
    'EUR': '€',
    'CAD': 'C$',
    'JPY': '¥',
    'GBP': '£',
    'TRY': '₺'
  };
  
  const symbol = currencySymbols[currencyCode] || '$';
  
  // If we have a converted value and it's a valid number, use that
  if (convertedValue !== null && typeof convertedValue === 'number' && !isNaN(convertedValue)) {
    return `${symbol}${convertedValue.toFixed(2)}`;
  }
  
  // Otherwise format the original price
  const units = priceUsd.units || 0;
  const nanos = priceUsd.nanos || 0;
  
  // Format to 2 decimal places
  const nanosFormatted = String(nanos).padStart(9, '0').substring(0, 2);
  return `${symbol}${units}.${nanosFormatted}`;
};