# Ứng dụng Quản lý Nội dung Văn bản

Ứng dụng web đơn giản để quản lý nội dung văn bản với khả năng định dạng in đậm và xuống dòng theo yêu cầu cụ thể.

## Mô tả

Ứng dụng này được thiết kế để:

1. Hiển thị nội dung từ file văn bản với định dạng đặc biệt
2. Định dạng chuỗi được bọc bởi `****` thành chữ in đậm
3. Chuyển đổi `\n` thành xuống dòng
4. Cho phép người dùng soạn thảo văn bản với định dạng in đậm và xuống dòng
5. Lưu văn bản với format tương tự để lưu vào cơ sở dữ liệu

## Cách sử dụng

### Cài đặt

1. Clone hoặc tải xuống mã nguồn
2. Đảm bảo bạn có [Node.js](https://nodejs.org/) được cài đặt

### Chạy ứng dụng

```bash
node server.js
```

Truy cập ứng dụng tại http://localhost:3000

### Tính năng

1. **Xem nội dung hiện tại**: Hiển thị nội dung từ file test.txt với định dạng chữ in đậm và xuống dòng
2. **Soạn thảo nội dung mới**: 
   - Nút "B" để in đậm văn bản được chọn
   - Nút "↵" để xuống dòng
3. **Kết quả định dạng**: Hiển thị kết quả sau khi chuyển đổi về định dạng lưu trữ (với **** và \n)
4. **Lưu nội dung**: Định dạng nội dung và tải xuống file văn bản

## Ứng dụng trong thực tế

Trong ứng dụng thực tế, thay vì tải xuống file văn bản, bạn sẽ:

1. Gửi nội dung đã định dạng đến máy chủ qua API
2. Lưu trữ nội dung trong cơ sở dữ liệu
3. Truy xuất và hiển thị nội dung khi cần thiết

## Cấu trúc mã nguồn

- `index.html`: Giao diện người dùng
- `styles.css`: Định dạng CSS
- `script.js`: Xử lý JavaScript cho định dạng văn bản
- `server.js`: Máy chủ Node.js đơn giản để phục vụ ứng dụng
- `test.txt`: File mẫu để hiển thị

## Giải thích kỹ thuật

- Định dạng văn bản từ người dùng:
  - Chuyển đổi `<strong>` và `<b>` thành `****`
  - Chuyển đổi `<br>` và `<div>` thành `\n`

- Hiển thị văn bản từ cơ sở dữ liệu:
  - Chuyển đổi `****` thành `<strong>`
  - Chuyển đổi `\n` thành `<br>` 