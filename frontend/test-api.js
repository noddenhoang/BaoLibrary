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
    
    const loginData = {
      taiKhoan: 'admin',
      matKhau: 'admin123'
    };
    
    console.log('Dữ liệu yêu cầu:', loginData);
    
    const response = await api.post('/auth/login', loginData);
    
    console.log('Phản hồi đăng nhập:', response.data);
    
    // Lưu token vào localStorage nếu đăng nhập thành công
    if (response.data && response.data.token) {
      console.log('Đăng nhập thành công! Token:', response.data.token);
      
      // Thiết lập token cho các yêu cầu tiếp theo
      api.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
      
      // Thử gọi một API được bảo vệ để kiểm tra token
      console.log('Đang gọi API profile để kiểm tra token...');
      const profileResponse = await api.get('/auth/profile');
      console.log('Dữ liệu profile:', profileResponse.data);
    }
    
    return response.data;
  } catch (error) {
    console.error('Lỗi đăng nhập:');
    
    if (error.response) {
      // Yêu cầu đã được gửi và máy chủ phản hồi với mã trạng thái
      // không nằm trong khoảng 2xx
      console.error('Trạng thái:', error.response.status);
      console.error('Dữ liệu:', error.response.data);
      console.error('Headers:', error.response.headers);
      
      if (error.response.status === 401) {
        console.error('Thông tin đăng nhập không chính xác hoặc tài khoản không tồn tại.');
      } else if (error.response.status === 403) {
        console.error('Không có quyền truy cập.');
      }
    } else if (error.request) {
      // Yêu cầu đã được gửi nhưng không nhận được phản hồi
      console.error('Không nhận được phản hồi từ máy chủ. Vui lòng kiểm tra:');
      console.error('1. Backend server đã khởi chạy chưa?');
      console.error('2. API endpoint có chính xác không?');
      console.error('3. Có vấn đề với CORS không?');
    } else {
      // Đã xảy ra lỗi khi thiết lập yêu cầu
      console.error('Lỗi cài đặt yêu cầu:', error.message);
    }
    
    console.error('Cấu hình lỗi:', error.config);
    return null;
  }
}

// Thực hiện kiểm tra
console.log('Bắt đầu kiểm tra kết nối API...');
testLogin()
  .then(result => {
    console.log('Kiểm tra hoàn tất:', result ? 'THÀNH CÔNG' : 'THẤT BẠI');
  })
  .catch(err => {
    console.error('Lỗi không xác định trong quá trình kiểm tra:', err);
  });