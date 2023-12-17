// Import app
const app = require('./app');

// Declare port
const port = process.env.PORT || 30110;

//Start Server
app.listen(port, () => {
  console.log(`App is listening on port ${port} and running in ${process.env.NODE_ENV} mode`);
});