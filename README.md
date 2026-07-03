# Bookstore

Hệ thống quản lý nhà sách viết bằng Java + Maven, chạy giao diện console, kết nối MySQL qua JDBC.

## Mô Tả

Project này hiện tập trung vào các luồng chính của một nhà sách: quản lý sách, kho hàng, bán hàng, combo/voucher, thuê sách, tài khoản người dùng và thống kê báo cáo.

## Công Nghệ

- Java
- Maven
- JDBC
- MySQL
- Console UI

## Chức Năng Đã Có

### 1. Đăng nhập, đăng ký và quản lý tài khoản
- Đăng nhập và đăng ký tài khoản.
- Xem danh sách tài khoản.
- Cập nhật vai trò người dùng.
- Mật khẩu được lưu dạng hash.

### 2. Quản lý sách
- Xem danh sách sách.
- Tìm kiếm và gợi ý sách.
- Thêm sách mới.
- Chỉnh sửa sách.
- Ẩn sách.
- Đánh dấu sách lỗi.

### 3. Quản lý kho hàng
- Lập phiếu nhập.
- Lập phiếu xuất.

### 4. Quản lý giảm giá
- Xem danh sách combo.
- Tạo combo.
- Hủy combo.
- Tạo voucher.
- Áp dụng voucher khi đặt hàng.

### 5. Giỏ hàng và hóa đơn
- Đặt hàng.
- Xác nhận thanh toán.
- Hủy đơn và hoàn tiền.
- Tìm kiếm hóa đơn.

### 6. Quản lý thuê sách
- Thuê sách.
- Trả sách.
- Tìm kiếm phiếu thuê.
- Cảnh báo quá hạn trả sách.

### 7. Thống kê và báo cáo
- Báo cáo doanh thu theo khoảng thời gian.
- Thống kê sản phẩm bán chạy.
- Báo cáo tồn kho / sách sắp hết.
- Vẽ biểu đồ bán chạy dạng text.
- Xuất báo cáo ra file CSV.

## Chức Năng Còn Thiếu Hoặc Mới Ở Mức Một Phần

### 1. Email và thông báo tự động
- Sơ đồ drawio có các luồng gửi mail, xác nhận đơn, thông báo sale, nhắc quá hạn, xác thực quên mật khẩu.
- Trong code hiện tại chưa có service gửi email hoặc hệ thống notification thật sự.
- Một số mục chỉ mới là dòng thông báo trên console.

### 2. Quản lý thành viên / tích điểm
- CSDL có bảng `member`, nhưng chưa có model, DAO, service và menu riêng cho tích điểm, hạng thành viên hay quy đổi điểm.

### 3. Quản lý chi tiết kho hàng
- Có phiếu nhập/xuất ở mức tạo phiếu.
- Chưa thấy luồng duyệt, xem chi tiết, sửa, xóa hoặc hủy phiếu đầy đủ như sơ đồ mô tả.

### 4. Quản lý giảm giá nâng cao
- Combo và voucher đã có, nhưng chưa có màn hình quản lý chi tiết đầy đủ như chỉnh sửa, xem chi tiết từng combo, hay các luồng xử lý nâng cao khác.

### 5. Tìm kiếm và gợi ý nâng cao
- Chức năng tìm kiếm sách đã có.
- Phần gợi ý hiện chỉ ở mức đơn giản, chưa có gợi ý theo lịch sử xem hoặc các cơ chế cá nhân hóa sâu như sơ đồ thiết kế.

### 6. Phân quyền thực thi
- Vai trò người dùng có lưu trong hệ thống.
- Tuy nhiên chưa thấy cơ chế chặn menu / chặn chức năng theo vai trò một cách chặt chẽ.

### 7. Giỏ hàng lưu bền vững
- Luồng đặt hàng đang chạy theo hướng xử lý trong bộ nhớ và tạo đơn.
- Chưa có entity / DAO riêng cho giỏ hàng được lưu lâu dài trước khi checkout.

## Cấu Trúc Chính

- `src/main/java/com/bookstore/controller`: lớp điều khiển thao tác người dùng.
- `src/main/java/com/bookstore/dao`: lớp truy cập dữ liệu.
- `src/main/java/com/bookstore/model`: các entity.
- `src/main/java/com/bookstore/service`: logic nghiệp vụ.
- `src/main/java/com/bookstore/view`: giao diện console.
- `src/main/resources/schema.sql`: cấu trúc CSDL.

