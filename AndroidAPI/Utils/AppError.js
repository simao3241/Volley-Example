class AppError extends Error {
  constructor(message, statusCode) {
    super(message);
    this.error = {
      status: `${statusCode}`.startsWith('4') ? 'fail' : 'error',
      statusCode: statusCode,
      message: message,
      isOperational: true,
    }

    Error.captureStackTrace(this, this.constructor);
  }
}

module.exports = AppError;