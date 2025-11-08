import React, { useState, useRef, useEffect } from 'react';
import { useSession } from '../contexts/SessionContext';
import { productApi } from '../api';
import './AssistantPage.css';

const AssistantPage = () => {
  const { sessionId } = useSession();
  const [messages, setMessages] = useState([
    {
      type: 'bot',
      text: "Hi, I'm the Demo Microservice assistant. I can help you with your shopping experience."
    },
    {
      type: 'bot',
      text: "What can I help you with? P.s: I am not working right now."
    }
  ]);
  const [inputText, setInputText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [uploadedImage, setUploadedImage] = useState(null);
  const messagesEndRef = useRef(null);
  
  // Auto-scroll to bottom of messages
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };
  
  useEffect(() => {
    scrollToBottom();
  }, [messages]);
  
  const handleInputChange = (e) => {
    setInputText(e.target.value);
  };
  
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };
  
  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    
    if (!file) return;
    
    const reader = new FileReader();
    reader.onloadend = () => {
      setUploadedImage(reader.result);
    };
    reader.readAsDataURL(file);
  };
  
  const handleSend = async () => {
    if ((!inputText || !inputText.trim()) && !uploadedImage) return;
    
    // Add user message to chat
    const userMessage = {
      type: 'user',
      text: inputText,
      image: uploadedImage
    };
    
    setMessages(prev => [...prev, userMessage]);
    
    // Clear input and image
    setInputText('');
    setUploadedImage(null);
    
    // Add bot "typing" message
    setIsLoading(true);
    
    try {
      // In a real app, we would call the shopping assistant service here
      // For this demo, we'll simulate a response with a product recommendation
      
      // Simulate API call delay
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Get some product to recommend
      let recommendedProducts = [];
      try {
        const products = await productApi.getProducts();
        // Pick 2 random products to recommend
        if (products && products.length > 0) {
          const shuffled = [...products].sort(() => 0.5 - Math.random());
          recommendedProducts = shuffled.slice(0, 2);
        }
      } catch (error) {
        console.error('Failed to fetch products for recommendations:', error);
      }
      
      // Simulate bot response
      const botResponse = {
        type: 'bot',
        text: generateBotResponse(inputText, uploadedImage !== null),
        products: recommendedProducts
      };
      
      setMessages(prev => [...prev, botResponse]);
    } catch (error) {
      console.error('Error in assistant conversation:', error);
      
      // Add error message
      setMessages(prev => [
        ...prev, 
        { 
          type: 'bot', 
          text: "I'm sorry, I'm having trouble processing your request right now. Please try again later."
        }
      ]);
    } finally {
      setIsLoading(false);
    }
  };
  
  // Simple bot response generator based on user input
  const generateBotResponse = (input, hasImage) => {
    const inputLower = input.toLowerCase();
    
    if (hasImage) {
      return "Thanks for sharing that image! Based on what I see, you might be interested in these products:";
    }
    
    if (inputLower.includes('recommend') || inputLower.includes('suggest')) {
      return "I'd be happy to recommend some products! Here are some items you might like:";
    }
    
    if (inputLower.includes('discount') || inputLower.includes('sale') || inputLower.includes('coupon')) {
      return "We currently don't have any active sales, but I can show you some of our popular items:";
    }
    
    if (inputLower.includes('hello') || inputLower.includes('hi') || inputLower.includes('hey')) {
      return "Hello there! How can I help with your shopping today? Here are some items you might be interested in:";
    }
    
    return "I think you might be interested in these products:";
  };
  
  return (
    <main role="main">
      <div className="container">
        <div className="row">
          <div className="col-md-12">
            <div id="chat-modal" className="chat-modal">
              <div id="bot-messages" className="bot-messages">
                {messages.map((message, index) => (
                  <div key={index}>
                    {message.type === 'bot' ? (
                      <p className="bot-message">
                        <span className="bot-message-text">{message.text}</span>
                        
                        {/* Product recommendations */}
                        {message.products && message.products.length > 0 && (
                          <div className="bot-products">
                            {message.products.map((product) => (
                              <a 
                                key={product.id}
                                className="bot-product"
                                href={`/product/${product.id}`}
                              >
                                <img 
                                  src={product.picture} 
                                  alt={product.name}
                                  className="bot-product-img" 
                                  onError={(e) => { e.target.style.display = 'none'; }}
                                />
                                <div className="bot-product-description">
                                  <strong>{product.name}</strong>
                                  <br />
                                  {product.description && product.description.length > 350
                                    ? `${product.description.substring(0, 330)}...`
                                    : product.description}
                                </div>
                              </a>
                            ))}
                          </div>
                        )}
                      </p>
                    ) : (
                      <>
                        <p className="user-message">
                          <span className="user-message-text">{message.text}</span>
                        </p>
                        {message.image && (
                          <div className="user-image-div">
                            <img 
                              src={message.image} 
                              alt="User uploaded" 
                              className="user-image" 
                              onError={(e) => { e.target.style.display = 'none'; }}
                            />
                          </div>
                        )}
                      </>
                    )}
                  </div>
                ))}
                
                {isLoading && (
                  <p className="bot-message bot-message-loading">
                    <span className="bot-message-text">
                      <span className="typing-indicator">
                        <span></span>
                        <span></span>
                        <span></span>
                      </span>
                    </span>
                  </p>
                )}
                
                <div ref={messagesEndRef} />
              </div>
              
              <div className="bot-input">
                <input 
                  id="bot-input-text" 
                  type="text" 
                  className="bot-input-text" 
                  placeholder="Recommend me items..." 
                  value={inputText}
                  onChange={handleInputChange}
                  onKeyPress={handleKeyPress}
                  disabled={isLoading}
                />
                
                <label className="bot-input-file-button">
                  <input 
                    type="file" 
                    accept="image/*" 
                    onChange={handleImageUpload} 
                    disabled={isLoading}
                  />
                  <span>{uploadedImage ? '✓' : '📷'}</span>
                </label>
                
                <button 
                  id="bot-input-button" 
                  className="bot-input-button"
                  onClick={handleSend}
                  disabled={isLoading}
                >
                  Send
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
};

export default AssistantPage;