/**
 * SNI Direct IP Connection Test Script
 *
 * This script demonstrates how to make HTTPS requests using:
 * - Direct IP connection (bypassing DNS)
 * - SNI (Server Name Indication) with domain name
 * - Custom headers for API authentication
 */

const https = require('https');

const options = {
  host: '104.18.31.39', // Direct IP connection
  // host: '216.19.4.106',
  port: 443,
  path: '/wallet/v1/account/validate-address?networkId=btc--0&accountAddress=bc1qezh467l5gwkk72v2dx6yj488hlpad8d34u6z2j',
  method: 'GET',
  servername: 'wallet.onekeytest.com', // CRITICAL: SNI must use domain name for TLS handshake
  headers: {
    'Host': 'wallet.onekeytest.com',
    'X-Onekey-Request-ID': 'cc740bab-7cbb-412f-9d9a-1d7b515f601d',
    'X-Onekey-Request-Currency': 'usd',
    'X-Onekey-Request-Locale': 'zh-cn',
    'X-Onekey-Request-Theme': 'light',
    'X-Onekey-Request-Platform': 'android-apk',
    'X-Onekey-Request-Version': '5.16.0',
    'X-Onekey-Request-Build-Number': '2000000000',
    'X-Onekey-Request-Token': 'eyJhbGciOi...', // Truncated token for security
    'X-Onekey-Request-Currency-Value': '1.0',
    'X-Onekey-Instance-Id': '67848a28-b89c-4e0b-8c0f-b87824480d6a',
    'x-onekey-wallet-type': 'hd',
    'x-onekey-hide-asset-details': 'false',
  },
};

console.log('🚀 Starting SNI test...');
console.log(`📡 Connecting to: ${options.host}:${options.port}`);
console.log(`🔐 SNI servername: ${options.servername}`);
console.log('');

const req = https.request(options, (res) => {
  console.log('✅ Connection successful!');
  console.log(`📊 Status Code: ${res.statusCode}`);
  console.log('📋 Response Headers:', JSON.stringify(res.headers, null, 2));
  console.log('');

  res.setEncoding('utf8');
  let body = '';

  res.on('data', (chunk) => {
    body += chunk;
  });

  res.on('end', () => {
    console.log('📦 Response Body:');
    try {
      const parsed = JSON.parse(body);
      console.log(JSON.stringify(parsed, null, 2));
    } catch (e) {
      console.log(body);
    }
  });
});

req.on('error', (e) => {
  console.error('❌ Request failed:', e.message);
  console.error('💡 Possible causes:');
  console.error('   - Network connectivity issues');
  console.error('   - Invalid IP address or port');
  console.error('   - SNI configuration mismatch');
  console.error('   - Certificate validation failure');
});

req.end();
