exports.getTimeStampToDB = (timestamp) => {
  // Convert Unix timestamp to JavaScript Date object
  const dateObj = new Date(timestamp);
  // Format the date as "YYYY-MM-DD HH:mm:ss"
  return dateObj.toISOString().slice(0, 19).replace('T', ' ');
}

exports.getUnixTimestamp = (timestamp) => {
  // Convert date string to JavaScript Date object
  const dateObj = new Date(timestamp);

  // Get the Unix timestamp in milliseconds
  return dateObj.getTime();
}