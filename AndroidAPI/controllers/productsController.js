// Import PostgreSQL connection
const pool = require('./../database/connection');

// Async Catcher
const catchAsync = require('./../Utils/catchAsync');

// App Error Handler
const AppError = require('./../Utils/AppError');

// Import Timestamp Manager
const TimestampManager = require('./../Utils/TimestampManager');

// Checker for id in request params
exports.checkId = (req, res, next, value) => {
  // console.log("id: " + value);
  if (!req.params.id || !(req.params.id > 0)) {
    return next(new AppError('Id inválido! Verifique o parâmetro!', 404));
  }
  next();
};

// Update function
const updateProduct = (options, id) => {
  // console.log("options: " + JSON.stringify(options));
  let query = 'UPDATE public.products SET ';
  let values = [];

  // Remove empty options from array
  options = options.filter(option => option[1] !== '');

  // Remove options with invalid values
  options = options.filter(async option => {
    if (option[0] === 'name') {
      option[2] = "="
      option[3] = ""

      if (option[1].length > 0) {
        return option;
      }
    } else if (option[0] === 'description') {
      option[2] = "="
      option[3] = ""

      if (option[1].length > 0) {
        return option;
      }
    } else if (option[0] === 'price') {
      option[2] = "="
      option[3] = "::MONEY"

      // Parse price to double value
      option[1] = parseFloat(option[1]);

      if (option[1] < 0.01 || option[1] > 9999.99) {
        return option;
      }
    } else if (option[0] === 'actual_stock') {
      option[2] = "="
      option[3] = ""

      // Parse actual stock to int value
      option[1] = parseInt(option[1]);

      if (option[1] < 1 || option[1] > 9999) {
        return option;
      }
    } else if (option[0] === 'enabled') {
      option[2] = '='
      option[3] = ""

      if (option[1].length === true || option[1].length === false) {
        return option;
      }
    }
  });

  if (options.length === 0) {
    return { query: '', values: [] };
  }
  // Get updated_at date timestamp
  const updated_at = TimestampManager.getTimeStampToDB(Date.now() + (1 * 60 * 60 * 1000));
  // Add updated_at to options
  options.push(['updated_at', updated_at, '=', '']);

  // Add options to query
  options.forEach((option, index) => {
    if (index === 0) {
      query += option[0] + ' ' + option[2] + ' $' + (index + 1) + option[3];
    } else {
      query += ', ' + option[0] + ' ' + option[2] + ' $' + (index + 1) + option[3];
    }
    values.push(option[1]);
  });
  query += ' WHERE id_product = $' + (options.length + 1);
  values.push(id);
  // console.log("query: " + query);
  // console.log("values: " + values);
  return { query, values };
};

// Products Routes Function Definitions - Version 1
exports.addProduct = catchAsync(async (req, res, next) => {
  // console.log(req.body)
  // return res.status(200).json({ message: 'Product added' });
  const data = req.body;
  if (!data.name || !data.description ||
    !data.price || !data.actual_stock) {
    return next(new AppError('Por favor preencha todos os parametros válidos!', 403));
  }

  // Parse price to double value
  data.price = parseFloat(data.price);
  // console.log(req.body.price);

  // Parse actual stock to int value
  data.actual_stock = parseInt(data.actual_stock);

  // Check if name is valid
  if (data.name.length < 3 || data.name.length > 30) {
    return next(new AppError('Nome inválido! O nome deve ter entre 3 e 30 caracteres!', 403));
  }

  // Check if description is valid
  if (data.description.length < 3 || data.description.length > 500) {
    return next(new AppError('Descrição inválida! A descrição deve ter entre 3 e 500 caracteres!', 403));
  }

  // Check if price is valid
  if (data.price < 0.01 || data.price > 9999.99) {
    return next(new AppError('Preço inválido! O preço deve estar entre 0.01 e 9999.99!', 403));
  }

  // Check if stock is valid
  if (data.actual_stock < 1 || data.actual_stock > 9999) {
    return next(new AppError('Stock inválido! O stock deve estar entre 1 e 9999!', 403));
  }

  // Get added_at date and updated_at date
  const added_at = TimestampManager.getTimeStampToDB(Date.now() + (1 * 60 * 60 * 1000));
  const updated_at = TimestampManager.getTimeStampToDB(Date.now() + (1 * 60 * 60 * 1000));

  // Parse price to string value
  req.body.price = req.body.price.toString();

  // Add product to database
  const newProduct = await pool.query(
    'INSERT INTO public.products (name, description, price, actual_stock, enabled, added_at, updated_at)' +
    ' VALUES ($1, $2, $3::MONEY, $4, $5, $6, $7) RETURNING *',
    [
      req.body.name,
      req.body.description,
      req.body.price,
      req.body.actual_stock,
      true, // enabled
      added_at,
      updated_at
    ]
  );

  // Return product
  res.json({
    meta: res.locals.meta,
    data: {
      message: 'Produto adicionado!',
      product: newProduct.rows[0]
    }
  });
});

exports.updateProduct = catchAsync(async (req, res, next) => {
  // res.json({ message: 'Product updated' });
  const id_product = req.params.id;
  const data = req.body;
  // console.log("data: " + JSON.stringify(data));

  // Check if product exists
  const result = await pool.query('SELECT * FROM public.products WHERE id_product = $1', [id_product]);

  if (result.rows.length === 0) {
    // Product not found
    return next(new AppError('Produto não encontrado!', 404));
  }

  // Update product
  const { query, values } = updateProduct(Object.entries(data), id_product);

  if (query === '') {
    return next(new AppError('Nenhum parametro válido foi encontrado!', 403));
  }
  
  // Update product
  const updatedProduct = await pool.query(
    query + ' RETURNING *',
    values
  );

  // Return product
  res.json({
    meta: res.locals.meta,
    data: {
      message: 'Produto atualizado!',
      product: updatedProduct.rows[0]
    }
  });
});

exports.deleteProduct = catchAsync(async (req, res, next) => {
  // res.json({ message: 'Product deleted' });
  const id_product = req.params.id;

  // Check if user exists
  const result = await pool.query('SELECT * FROM public.products WHERE id_product = $1', [id_product]);
  if (result.rows.length === 0) {
    // User not found
    return next(new AppError('Usuário não encontrado!', 404));
  }

  await pool.query('DELETE FROM public.products WHERE id_product =$1;',
    [
      id_product
    ]);

  res.json({
    meta: res.locals.meta,
    data: {
      message: 'Produto excluido com sucesso!'
    }
  });
});

exports.getProduct = catchAsync(async (req, res, next) => {
  const id = req.params.id;

  const product = await pool.query(
    'SELECT * FROM public.products WHERE id_product = $1',
    [id]
  );

  if (product.rows.length === 0) {
    return next(new AppError('Produto não encontrado!', 404));
  }

  // Return product
  res.json({
    meta: res.locals.meta,
    data: {
      product: product.rows[0]
    }
  });
});
exports.getProducts = catchAsync(async (req, res, next) => {
  // console.log("query: " + JSON.stringify(req.query));
  // Get query
  const query = "SELECT * FROM public.products ORDER BY id_product ASC";

  pool.query(
    query,
    null,
    (error, result) => {
      if (error) {
        return next(error);
      } else {
        if (result.rows.length === 0) {
          return next(new AppError('Nenhum produto encontrado!', 404));
        }

        res.json({
          meta: res.locals.meta,
          data: {
            products: result.rows
          }
        });
      }
    }
  );
});
exports.enableProduct = catchAsync(async (req, res, next) => {
  const id = req.params.id;

  await pool.query(
    'UPDATE public.products SET enabled = true WHERE id_product = $1',
    [id]
  );

  const product = await pool.query(
    'SELECT * FROM public.products WHERE id_product = $1',
    [id]
  );

  res.json({
    meta: res.locals.meta,
    data: {
      message: 'Produto ativado!',
      product: product.rows[0]
    }
  });
});
exports.disableProduct = catchAsync(async (req, res, next) => {
  const id = req.params.id;

  await pool.query(
    'UPDATE public.products SET enabled = false WHERE id_product = $1',
    [id]
  );

  const product = await pool.query(
    'SELECT * FROM public.products WHERE id_product = $1',
    [id]
  );

  res.json({
    meta: res.locals.meta,
    data: {
      message: 'Produto desativado!',
      product: product.rows[0]
    }
  });
});