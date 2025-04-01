-- Thêm 2 manager users với mật khẩu mặc định là "12345678" (đã được mã hóa)
-- $2a$12$5xq6lM6p7mohAW/KQZonUuSXTpDWyIfonfR6f98BIhf2XhiruohdO là mã hóa của "12345678"

-- Kiểm tra và thêm Manager 1 (UserID = 2)
INSERT INTO USER (UserID, HoTen, TaiKhoan, MatKhau, Email, SoDienThoai, TrangThai, Role)
SELECT 2, 'Manager Chi Nhánh 1', 'manager1', '$2a$12$YgfZn7gAkXbBuVmzv1O09ecZib9j8nRsJH2SvlqD1VYfOCnVsyi5.', 'manager1@gmail.com', '0123456789', 'active', 'manager'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM USER WHERE UserID = 2);

-- Kiểm tra và thêm Manager 2 (UserID = 3)
INSERT INTO USER (UserID, HoTen, TaiKhoan, MatKhau, Email, SoDienThoai, TrangThai, Role)
SELECT 3, 'Manager Chi Nhánh 2', 'manager2', '$2a$12$YgfZn7gAkXbBuVmzv1O09ecZib9j8nRsJH2SvlqD1VYfOCnVsyi5.', 'manager2@gmail.com', '0987654321', 'active', 'manager'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM USER WHERE UserID = 3);

-- Cập nhật manager cho chi nhánh 1
UPDATE BRANCH
SET ManagerID = 2
WHERE BranchID = 1 AND (ManagerID IS NULL OR ManagerID <> 2);

-- Cập nhật manager cho chi nhánh 2
UPDATE BRANCH
SET ManagerID = 3
WHERE BranchID = 2 AND (ManagerID IS NULL OR ManagerID <> 3);

-- Thêm thông tin truy cập vào log để quản trị viên biết
INSERT INTO NOTIFICATION (UserID, Content, Type, Status, CreatedDate)
VALUES (1, 'Đã thêm 2 người quản lý mới cho các chi nhánh. Tài khoản: manager1 và manager2, mật khẩu mặc định: 12345678', 'system', 'unread', NOW()); 