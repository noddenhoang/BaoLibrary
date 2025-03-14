// Simple test script to test API connection
import axios from 'axios';

// Configure axios
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Test login request
async function testLogin() {
  try {
    console.log('Sending login request...');
    console.log('Request data:', {
      taiKhoan: 'admin',
      matKhau: 'admin123'
    });
    
    const response = await api.post('/auth/login', {
      taiKhoan: 'admin',
      matKhau: 'admin123'
    });
    
    console.log('Login response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Login error:');
    
    if (error.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
      console.error('Headers:', error.response.headers);
    } else if (error.request) {
      // The request was made but no response was received
      console.error('No response received:', error.request);
    } else {
      // Something happened in setting up the request that triggered an Error
      console.error('Request setup error:', error.message);
    }
    
    console.error('Error config:', error.config);
    return null;
  }
}

// Execute the test
testLogin()
  .then(result => {
    console.log('Test completed:', result ? 'SUCCESS' : 'FAILED');
  })
  .catch(err => {
    console.error('Test error:', err);
  }); 