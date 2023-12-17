//Import Modules
//https://www.npmjs.com/package/jsonwebtoken - JWT
const express = require('express');
const bodyParser = require('body-parser');
const morgan = require('morgan');
const dotenv = require('dotenv');
const rateLimit = require('express-rate-limit');
const helmet = require('helmet');
const xss = require('xss-clean');
const hpp = require('hpp');
const cors = require('cors');

// Load environment variables from .env file
dotenv.config();

// Import Error Class
const AppError = require('./Utils/AppError');

// Import Error Controller
const globalErrorHandler = require('./controllers/errorController');

// Import Routes
const productsRouteV1 = require('./routes/productsRoute');

// Create Express App
const app = express();

// CORS Options
const corsOptions = {
  //origin: 'http://example.com', // Restrict access to a specific origin
  methods: 'GET,POST,PUT,DELETE', // Allow only specified HTTP methods
  allowedHeaders: 'Content-Type,Authorization', // Allow specific headers
};

// Enable CORS
app.use(cors(corsOptions));

// DATA Sanitization against site script XSS
app.use(xss());

// Prevent parameter pollution
app.use(hpp());
app.use(
  hpp({
    whitelist:
      [
        'price',
        'ratingsQuantity',
        'ratingsAverage',
        'duration',
        'difficulty',
        'maxGroupSize'
      ]
  }));

// Limit size of download and upload files
app.use(express.json({ limit: "10kb" }));

// Secure HTTP Headers
app.use(helmet());

// Define limit request 
const limiter = rateLimit({
  max: 100,
  windowMs: 60 * 60 * 1000,
  message: 'Too many requests from this IP, please try again in an hour!'
});

// Set limit request
app.use('/api', limiter);

//Set properties
app.use(bodyParser.json({ extended: true }));
if (process.env.NODE_ENV === 'DEV') {
  app.use(morgan('dev'));
}

// API Routes Function Definitions - Version 1
const apiAlive = (req, res) => {
  res.json({ message: 'Android API V1 is alive!' });
};

// Prepare API Response - Version 1
app.use((req, res, next) => {
  res.locals.meta = {
    isLogged: false,
    language: 'pt',
    timestamp: Date.now(),
    version: '1.0.0',
  }
  // console.log(res.locals.meta)
  
  next();
});

// API Routers - Version 1
const apiRouterV1 = express.Router();
app.use('/api/v1', apiRouterV1);
app.use('/api/v1/products/', productsRouteV1);

// API Routes - Version 1
apiRouterV1.get('/healtcheck', apiAlive);

// Global Error Handling - Version 1
app.all('*', (req, res, next) => {
  next(new AppError(`Impossível encontrar a rota ${req.originalUrl} neste servidor!`, 404));
});

app.use(globalErrorHandler);

// Export App
module.exports = app;