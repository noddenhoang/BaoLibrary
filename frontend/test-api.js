// Script đơn giản để kiểm tra kết nối API
import axios from 'axios';

// Cấu hình axios
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Kiểm tra yêu cầu đăng nhập
async function testLogin() {
  try {
    console.log('Đang gửi yêu cầu đăng nhập...');
    console.log('Dữ liệu yêu cầu:', {
      taiKhoan: 'admin',
      matKhau: 'admin123'
    });
    
    const response = await api.post('/auth/login', {
      taiKhoan: 'admin',
      matKhau: 'admin123'
    });
    
    console.log('Phản hồi đăng nhập:', response.data);
    return response.data;
  } catch (error) {
    console.error('Lỗi đăng nhập:');
    
    if (error.response) {
      // Yêu cầu đã được gửi và máy chủ phản hồi với mã trạng thái
      // không nằm trong khoảng 2xx
      console.error('Trạng thái:', error.response.status);
      console.error('Dữ liệu:', error.response.data);
      console.error('Headers:', error.response.headers);
    } else if (error.request) {
      // Yêu cầu đã được gửi nhưng không nhận được phản hồi
      console.error('Không nhận được phản hồi:', error.request);
    } else {
      // Đã xảy ra lỗi khi thiết lập yêu cầu
      console.error('Lỗi cài đặt yêu cầu:', error.message);
    }
    
    console.error('Cấu hình lỗi:', error.config);
    return null;
  }
}

// Thực hiện kiểm tra
testLogin()
  .then(result => {
    console.log('Kiểm tra hoàn tất:', result ? 'THÀNH CÔNG' : 'THẤT BẠI');
  })
  .catch(err => {
    console.error('Lỗi kiểm tra:', err);
  });