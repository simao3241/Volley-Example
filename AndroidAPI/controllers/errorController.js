const AppError = require('./../Utils/AppError');

const handleCastErrorDB = (err) => {
  const message = `Invalid ${err.path}: ${err.value}.`;
  return new AppError(message, 400);
};

const handleQueryCommandsDB = (err) => {
  const message = `Comando query inválido: ${err.message}.`;
  return new AppError(message, 400);
};

const handleDuplicateFieldsDB = (err) => {
  //const value = err.errmsg.match(/(["'])(\\?.)*?\1/)[0];
  const value = err.keyValue.name;
  const message = `Valor duplicado: ${value}. Por favor use outro valor!`;
  return new AppError(message, 400);
};

const handleValidationErrorDB = (err) => {
  // const errors = Object.values(err.errors).map(el => el.message);
  // const message = `Invalid input data. ${errors.join('. ')}`;
  return new AppError(err.message, 400);
};

const handleJWTError = (err) =>
  new AppError('Token inválido! Por favor loge novamente.', 401);

const handleJWTExpiredError = (err) =>
  new AppError('O seu token expirou! Por favor loge novamente.', 401);

const handleFileSizeError = (err) =>
  new AppError(`O tamanho do ficheiro é demasiado grande. O tamanho máximo é ${process.env.MAX_FILE_UPLOAD} bytes!`, 400);

const handleFileCountError = (err) =>
  new AppError(`O número de ficheiros é demasiado grande. O número máximo de ficheiros é ${process.env.MAX_FILE_COUNT}!`, 400);

const sendErrorDev = (err, res) => {
  res.status(err.statusCode).json({
    meta: res.locals.meta,
    error: {
      status: err.status,
      message: err.message,
      error: err,
      stack: err.stack
    }
  });
};

const sendErrorProd = (err, res) => {
  // Operational, trusted error: send message to client
  // console.log(err);
  if (err.error?.isOperational) {
    res.status(err.error.statusCode).json({
      meta: res.locals.meta,
      error: err.error
    });
  } else { // Programming or other unknown error: don't leak error details
    // 1) Log error
    console.error('ERROR 💥', err);
    // 2) Send generic message
    res.status(500).json({
      meta: res.locals.meta,
      data: {
        status: 'error',
        message: 'Something went very wrong!'
      }
    });
  }
};


module.exports = (err, req, res, next) => {
  //console.log(err.stack);
  err.statusCode = err.statusCode || 500;
  err.status = err.status || 'error';

  if (process.env.NODE_ENV === 'DEV') {
    sendErrorDev(err, res);
  } else if (process.env.NODE_ENV === 'PRODUCTION') {
    const meta = res.locals.meta;
    let error = { ...err, message: err.message };
    if (error.name === 'CastError') error = handleCastErrorDB(error);
    if (error.code === '42703') error = handleQueryCommandsDB(error);
    if (error.code === 11000) error = handleDuplicateFieldsDB(error);
    if (error.code === '23502') error = handleValidationErrorDB(error);
    if (error.name === 'JsonWebTokenError') error = handleJWTError(error);
    if (error.name === 'TokenExpiredError') error = handleJWTExpiredError(error);
    if (error.code === 'LIMIT_FILE_SIZE') error = handleFileSizeError(error);
    if (error.code === 'LIMIT_FILE_COUNT') error = handleFileCountError(error);
    sendErrorProd(error, res);
  }
  next();
};