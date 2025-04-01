-- Thêm trường số lượng vào bảng BOOK
ALTER TABLE BOOK ADD COLUMN SoLuong INT DEFAULT 0;

-- Cập nhật dữ liệu số lượng sách từ bảng INVENTORY sang BOOK
UPDATE BOOK b 
SET b.SoLuong = (
    SELECT SUM(i.SoLuongHienCo) 
    FROM INVENTORY i 
    WHERE i.BookID = b.BookID
    GROUP BY i.BookID
);

-- Tạo bảng BRANCH mặc định (nếu chưa có)
INSERT INTO BRANCH (BranchID, TenChiNhanh, DiaChi, SoDienThoai)
SELECT 1, 'Chi nhánh chính', '50 Phan Thanh, Thạc Gián, Thanh Khê, Đà Nẵng', '0123456789'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM BRANCH WHERE BranchID = 1);

-- Cập nhật tất cả các khóa ngoại tham chiếu đến bảng BRANCH
UPDATE LOAN SET BranchID = 1 WHERE BranchID IS NOT NULL;
UPDATE INCIDENTREPORT SET BranchID = 1 WHERE BranchID IS NOT NULL;
UPDATE RESERVATION SET BranchID = 1 WHERE BranchID IS NOT NULL;

-- Cập nhật BOOK_INVENTORY bằng cách tạo một trigger
-- Tạo trigger cho hành động INSERT trên bảng BOOK
DELIMITER //
CREATE TRIGGER after_book_insert 
AFTER INSERT ON BOOK
FOR EACH ROW 
BEGIN
    INSERT INTO INVENTORY (BranchID, BookID, TongSoBan, SoLuongHienCo)
    VALUES (1, NEW.BookID, NEW.SoLuong, NEW.SoLuong);
END//

-- Tạo trigger cho hành động UPDATE trên bảng BOOK
CREATE TRIGGER after_book_update
AFTER UPDATE ON BOOK
FOR EACH ROW
BEGIN
    IF OLD.SoLuong <> NEW.SoLuong THEN
        UPDATE INVENTORY
        SET TongSoBan = NEW.SoLuong, 
            SoLuongHienCo = SoLuongHienCo + (NEW.SoLuong - OLD.SoLuong)
        WHERE BookID = NEW.BookID AND BranchID = 1;
    END IF;
END//
DELIMITER ;

-- Thêm function mới để xử lý mượn/trả sách
DELIMITER //
CREATE FUNCTION decrease_book_quantity(book_id INT, amount INT) 
RETURNS BOOLEAN
DETERMINISTIC
MODIFIES SQL DATA
BEGIN
    DECLARE current_quantity INT;
    DECLARE success BOOLEAN DEFAULT FALSE;
    
    SELECT SoLuong INTO current_quantity FROM BOOK WHERE BookID = book_id;
    
    IF current_quantity >= amount THEN
        UPDATE BOOK SET SoLuong = SoLuong - amount WHERE BookID = book_id;
        UPDATE INVENTORY SET SoLuongHienCo = SoLuongHienCo - amount WHERE BookID = book_id AND BranchID = 1;
        SET success = TRUE;
    END IF;
    
    RETURN success;
END//

CREATE FUNCTION increase_book_quantity(book_id INT, amount INT) 
RETURNS BOOLEAN
DETERMINISTIC
MODIFIES SQL DATA
BEGIN
    UPDATE BOOK SET SoLuong = SoLuong + amount WHERE BookID = book_id;
    UPDATE INVENTORY SET SoLuongHienCo = SoLuongHienCo + amount WHERE BookID = book_id AND BranchID = 1;
    RETURN TRUE;
END//
DELIMITER ; 