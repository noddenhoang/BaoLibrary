import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

async function testApi() {
  try {
    console.log('Testing API connection...');
    
    // Test a public API endpoint (books or categories)
    console.log('Testing Books API...');
    const booksResponse = await axios.get(`${API_URL}/books`);
    console.log('Books API Response Status:', booksResponse.status);
    console.log('Books API Response Data Sample:', 
      booksResponse.data.content ? 
      `Found ${booksResponse.data.totalElements} books` : 
      'No books found but endpoint is accessible');
    
    // Try categories endpoint as well
    console.log('\nTesting Categories API...');
    const categoriesResponse = await axios.get(`${API_URL}/categories`);
    console.log('Categories API Response Status:', categoriesResponse.status);
    console.log('Categories Data:', categoriesResponse.data.length > 0 ? 
      `Found ${categoriesResponse.data.length} categories` : 
      'No categories found but endpoint is accessible');
    
    console.log('\nAPI connection successful!');
  } catch (error) {
    console.error('API connection failed:', error.message);
    
    // Enhanced error logging
    if (error.response) {
      // The server responded with a status code outside the 2xx range
      console.error('Error Status:', error.response.status);
      console.error('Error Headers:', error.response.headers);
      console.error('Error Data:', error.response.data);
    } else if (error.request) {
      // The request was made but no response was received
      console.error('No response received. The server might be down or network issues');
      console.error('Request details:', error.request);
    } else {
      // Something happened in setting up the request
      console.error('Error setting up request:', error.message);
    }
    
    console.error('\nTroubleshooting tips:');
    console.error('1. Make sure your backend server is running at http://localhost:8080');
    console.error('2. Check if CORS is properly configured on the backend');
    console.error('3. Some endpoints might require authentication');
  }
}

testApi();