// Import modules
const express = require('express');

// Import controller
const productsControllers = require('./../controllers/productsController');

// Create router
const router = express.Router();

// Set parameters checker
router.param('id', productsControllers.checkId);

// Set routes
router
  .route('/')
  .post(productsControllers.addProduct)
  .get(productsControllers.getProducts);

router
  .route('/:id')
  .get(productsControllers.getProduct)
  .put(productsControllers.updateProduct)
  .delete(productsControllers.deleteProduct);

router
  .route('/:id/enable')
  .post(productsControllers.enableProduct);

router
  .route('/:id/disable')
  .post(productsControllers.disableProduct);

// Export router
module.exports = router;