# Cymbal Shops Frontend

This project is a React-based frontend for the Cymbal Shops e-commerce demo application. It directly communicates with microservices via REST APIs.

## Features

- Product catalog browsing
- Shopping cart management
- Checkout process
- Currency conversion
- Product recommendations
- Ad display
- Shopping assistant/chatbot

## Prerequisites

- Node.js 16+ and npm

## Getting Started

1. Clone the repository
2. Install dependencies:
   ```
   npm install
   ```
3. Configure environment variables by creating a `.env` file in the root directory (see `.env.example`)
4. Start the development server:
   ```
   npm start
   ```
5. Open [http://localhost:3000](http://localhost:3000) to view it in your browser

## Environment Configuration

Copy `.env.example` to `.env` and adjust the settings:

```
# API Service URLs
REACT_APP_PRODUCT_SERVICE_URL=http://localhost:8080/api/v1
REACT_APP_CART_SERVICE_URL=http://localhost:8081/api/v1
REACT_APP_CHECKOUT_SERVICE_URL=http://localhost:8082/api/v1
REACT_APP_CURRENCY_SERVICE_URL=http://localhost:8083/api/v1
REACT_APP_AD_SERVICE_URL=http://localhost:8084/api/v1
REACT_APP_RECOMMENDATION_SERVICE_URL=http://localhost:8085/api/v1
REACT_APP_SHIPPING_SERVICE_URL=http://localhost:8086/api/v1

# Application Configuration
REACT_APP_FRONTEND_MESSAGE="Free shipping on orders over $75"
REACT_APP_CYMBAL_BRANDING=true
REACT_APP_ENABLE_ASSISTANT=true
REACT_APP_BASE_URL=""
```

## Project Structure

```
frontend/
├── public/               # Static assets
├── src/
│   ├── api/              # API service modules
│   ├── components/       # Reusable components
│   ├── contexts/         # React context providers
│   ├── pages/            # Page components
│   ├── utils/            # Utility functions
│   ├── App.js            # Main app component with routing
│   └── index.js          # Entry point
└── package.json
```

## Available Scripts

- `npm start` - Runs the app in development mode
- `npm test` - Launches the test runner
- `npm run build` - Builds the app for production
- `npm run eject` - Ejects from Create React App

## Dependencies

- React Router - For navigation
- Axios - For API calls
- Bootstrap - For styling

## Session Management

This is a demo application without user authentication. A session ID is generated automatically for each user and stored in localStorage to maintain cart state.

## License

Copyright 2024 - See LICENSE file for details