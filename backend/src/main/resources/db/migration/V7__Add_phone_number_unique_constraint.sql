-- Thêm unique constraint cho SoDienThoai trong bảng USER
-- Trước khi thêm constraint, cần đảm bảo không có dữ liệu trùng lặp

-- Tạm thời đặt NULL cho các số điện thoại trùng lặp 
-- (giữ lại giá trị cho bản ghi có UserID nhỏ nhất)
UPDATE USER u1
JOIN (
    SELECT SoDienThoai, MIN(UserID) AS MinUserID
    FROM USER
    WHERE SoDienThoai IS NOT NULL
    GROUP BY SoDienThoai
    HAVING COUNT(*) > 1
) u2 ON u1.SoDienThoai = u2.SoDienThoai AND u1.UserID <> u2.MinUserID
SET u1.SoDienThoai = NULL
WHERE u1.SoDienThoai IS NOT NULL;

-- Thêm unique constraint cho trường SoDienThoai
ALTER TABLE USER
ADD CONSTRAINT UK_USER_SoDienThoai UNIQUE (SoDienThoai);

-- Cập nhật thông báo
INSERT INTO NOTIFICATION (UserID, Content, Type, Status, CreatedDate)
VALUES (1, 'Đã cập nhật cơ sở dữ liệu: Số điện thoại người dùng giờ đây phải là duy nhất trong hệ thống.', 'system', 'unread', NOW()); 