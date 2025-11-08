import React from 'react';
import { Link } from 'react-router-dom';
import { useSession } from '../contexts/SessionContext';
import { currencyApi } from '../api';
import './Header.css';

const Header = () => {
  const { currency, setCurrency, cartSize } = useSession();
  const [currencies, setCurrencies] = React.useState(['USD', 'EUR', 'CAD', 'JPY', 'GBP', 'TRY']);
  const [frontendMessage, setFrontendMessage] = React.useState('');
  const [isCymbalBrand] = React.useState(true);
  const [showCurrencyDropdown, setShowCurrencyDropdown] = React.useState(false);

  // Fetch supported currencies on component mount
  React.useEffect(() => {
    const fetchCurrencies = async () => {
      try {
        const data = await currencyApi.getSupportedCurrencies();
        if (data && Array.isArray(data)) {
          setCurrencies(data);
        }
      } catch (error) {
        console.error('Error fetching currencies:', error);
      }
    };

    // Try to get any frontend message from environment or config
    const message = process.env.REACT_APP_FRONTEND_MESSAGE || '';
    setFrontendMessage(message);

    fetchCurrencies();
  }, []);

  const handleCurrencyChange = (newCurrency) => {
    setCurrency(newCurrency);
    setShowCurrencyDropdown(false);
  };

  return (
    <header>
      {frontendMessage && (
        <div className="navbar">
          <div className="container d-flex justify-content-center">
            <div className="h-free-shipping">{frontendMessage}</div>
          </div>
        </div>
      )}
      <div className="navbar sub-navbar">
        <div className="container d-flex justify-content-between">
          <Link to="/" className="navbar-brand d-flex align-items-center">
            {isCymbalBrand ? (
              <img 
                src="/static/icons/Cymbal_NavLogo.svg" 
                alt="Cymbal Shops" 
                className="top-left-logo-cymbal" 
              />
            ) : (
              <img 
                src="static/icons/Hipster_NavLogo.svg" 
                alt="Online Boutique" 
                className="top-left-logo" 
              />
            )}
          </Link>
          <div className="controls">
            <div className="h-controls">
              <div className="h-control">
                <span className="icon currency-icon">
                  {currency === 'USD' && '$'}
                  {currency === 'EUR' && '€'}
                  {currency === 'JPY' && '¥'}
                  {currency === 'GBP' && '£'}
                  {currency === 'TRY' && '₺'}
                  {currency === 'CAD' && '$'}
                </span>
                <div className="currency-selector">
                  <button 
                    className="currency-button"
                    onClick={() => setShowCurrencyDropdown(!showCurrencyDropdown)}
                  >
                    {currency}
                    <img 
                      src="/static/icons/Hipster_DownArrow.svg" 
                      alt="" 
                      className="icon arrow" 
                    />
                  </button>
                  {showCurrencyDropdown && (
                    <div className="currency-dropdown">
                      {currencies.map((curr) => (
                        <div 
                          key={curr} 
                          className="currency-item"
                          onClick={() => handleCurrencyChange(curr)}
                        >
                          {curr}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </div>

            <Link to="/assistant" className="cart-link">
              <img 
                src="/static/icons/Hipster_WandIcon.svg" 
                style={{ width: '22px', height: '22px' }} 
                alt="Assistant icon" 
                className="logo" 
                title="Assistant" 
              />
            </Link>

            <Link to="/cart" className="cart-link">
              <img 
                src="/static/icons/Hipster_CartIcon.svg"
                style={{ width: '25px', height: '25px' }} 
                alt="Cart icon" 
                className="logo" 
                title="Cart" 
              />
              {cartSize > 0 && (
                <span className="cart-size-circle">{cartSize}</span>
              )}
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;