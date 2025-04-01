-- Thêm chi nhánh thứ 2
INSERT INTO BRANCH (BranchID, TenChiNhanh, DiaChi, SoDienThoai)
VALUES (2, 'Chi nhánh thứ 2', '6 Trần Phú, Thạch Thang, Hải Châu, Đà Nẵng', '0123456789');

-- Cập nhật INVENTORY cho chi nhánh mới
INSERT INTO INVENTORY (BranchID, BookID, TongSoBan, SoLuongHienCo)
SELECT 2, BookID, SoLuong, SoLuong
FROM BOOK;

-- Cập nhật trigger để xử lý thêm sách mới cho cả hai chi nhánh
DELIMITER //

DROP TRIGGER IF EXISTS after_book_insert//
CREATE TRIGGER after_book_insert 
AFTER INSERT ON BOOK
FOR EACH ROW 
BEGIN
    -- Thêm vào chi nhánh 1
    INSERT INTO INVENTORY (BranchID, BookID, TongSoBan, SoLuongHienCo)
    VALUES (1, NEW.BookID, NEW.SoLuong, NEW.SoLuong);
    
    -- Thêm vào chi nhánh 2
    INSERT INTO INVENTORY (BranchID, BookID, TongSoBan, SoLuongHienCo)
    VALUES (2, NEW.BookID, NEW.SoLuong, NEW.SoLuong);
END//

DROP TRIGGER IF EXISTS after_book_update//
CREATE TRIGGER after_book_update
AFTER UPDATE ON BOOK
FOR EACH ROW
BEGIN
    IF OLD.SoLuong <> NEW.SoLuong THEN
        -- Cập nhật chi nhánh 1
        UPDATE INVENTORY
        SET TongSoBan = NEW.SoLuong, 
            SoLuongHienCo = SoLuongHienCo + (NEW.SoLuong - OLD.SoLuong)
        WHERE BookID = NEW.BookID AND BranchID = 1;
        
        -- Cập nhật chi nhánh 2
        UPDATE INVENTORY
        SET TongSoBan = NEW.SoLuong, 
            SoLuongHienCo = SoLuongHienCo + (NEW.SoLuong - OLD.SoLuong)
        WHERE BookID = NEW.BookID AND BranchID = 2;
    END IF;
END//

-- Cập nhật các function để hỗ trợ cả hai chi nhánh
DROP FUNCTION IF EXISTS decrease_book_quantity//
CREATE FUNCTION decrease_book_quantity(book_id INT, amount INT, branch_id INT)
RETURNS BOOLEAN
DETERMINISTIC
MODIFIES SQL DATA
BEGIN
    DECLARE current_quantity INT;
    DECLARE success BOOLEAN DEFAULT FALSE;
    
    -- Kiểm tra số lượng có sẵn tại chi nhánh cụ thể
    SELECT SoLuongHienCo INTO current_quantity 
    FROM INVENTORY 
    WHERE BookID = book_id AND BranchID = branch_id;
    
    IF current_quantity >= amount THEN
        -- Giảm số lượng tại chi nhánh cụ thể
        UPDATE INVENTORY 
        SET SoLuongHienCo = SoLuongHienCo - amount 
        WHERE BookID = book_id AND BranchID = branch_id;
        
        -- Giảm tổng số lượng trong Book
        UPDATE BOOK 
        SET SoLuong = SoLuong - amount 
        WHERE BookID = book_id;
        
        SET success = TRUE;
    END IF;
    
    RETURN success;
END//

DROP FUNCTION IF EXISTS increase_book_quantity//
CREATE FUNCTION increase_book_quantity(book_id INT, amount INT, branch_id INT)
RETURNS BOOLEAN
DETERMINISTIC
MODIFIES SQL DATA
BEGIN
    -- Tăng số lượng tại chi nhánh cụ thể
    UPDATE INVENTORY 
    SET SoLuongHienCo = SoLuongHienCo + amount 
    WHERE BookID = book_id AND BranchID = branch_id;
    
    -- Tăng tổng số lượng trong Book
    UPDATE BOOK 
    SET SoLuong = SoLuong + amount 
    WHERE BookID = book_id;
    
    RETURN TRUE;
END//

DELIMITER ; 

-- Thêm user admin nếu chưa tồn tại và thiết lập vai trò admin
INSERT INTO USER (UserID, HoTen, TaiKhoan, MatKhau, Email, SoDienThoai, TrangThai, Role)
SELECT 1, 'Admin', 'admin', '$2a$12$5xq6lM6p7mohAW/KQZonUuSXTpDWyIfonfR6f98BIhf2XhiruohdO', 'admin@gmail.com', '0123456789', 'active', 'admin'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM USER WHERE UserID = 1);


