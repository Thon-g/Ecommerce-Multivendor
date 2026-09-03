# Quản Lý Trạng Thái Gian Hàng (Seller Status Workflow)

Tài liệu này định nghĩa chặt chẽ vòng đời và các quy tắc chuyển đổi trạng thái của `SellerStatus` trong hệ thống. Việc tuân thủ nghiêm ngặt luồng trạng thái này giúp đảm bảo tính nhất quán của dữ liệu (đơn hàng, doanh thu, sản phẩm) của Seller, tránh làm đứt gãy luồng nghiệp vụ.

## 1. Các Trạng Thái Hợp Lệ (`SellerStatus`)
- `PENDING_VERIFICATION`: Chờ Admin phê duyệt (trạng thái mặc định khi mới đăng ký).
- `ACTIVE`: Đang hoạt động bình thường, được phép bán hàng.
- `SUSPENDED`: Bị đình chỉ tạm thời (do vi phạm nhẹ hoặc bị Admin khóa tạm để kiểm tra).
- `DEACTIVATED`: Vô hiệu hóa (thường do Seller tự khóa).
- `CLOSED`: Đóng cửa vĩnh viễn (Seller hủy gian hàng).
- `BANNED`: Bị Admin cấm vĩnh viễn (vi phạm nặng).

## 2. Quy Tắc Chuyển Đổi Trạng Thái (State Transitions)

Để tránh phá vỡ logic hệ thống khi Seller đang có đơn hàng, sản phẩm hay doanh thu, Admin **không được phép cập nhật trạng thái tùy tiện**. Các luồng chuyển đổi phải tuân theo quy tắc sau:

### Từ `PENDING_VERIFICATION`:
✅ **Được phép chuyển sang:**
- `ACTIVE` (Phê duyệt hồ sơ thành công).
- `BANNED` / `REJECTED` (Từ chối hồ sơ, cấm không cho đăng ký lại).

❌ **KHÔNG được phép chuyển sang:** `SUSPENDED`, `CLOSED` (Vì chưa từng hoạt động).

### Từ `ACTIVE`:
✅ **Được phép chuyển sang:**
- `SUSPENDED` (Admin đình chỉ tạm thời).
- `BANNED` (Admin cấm vĩnh viễn).
- `DEACTIVATED` / `CLOSED` (Do Seller tự thực hiện).

❌ **KHÔNG được phép chuyển sang:** 
- `PENDING_VERIFICATION`: Tuyệt đối **không đưa một Seller đang hoạt động về lại trạng thái chờ duyệt**, vì Seller này đã có thể có Product, Order đang xử lý. Nếu thông tin thay đổi cần kiểm duyệt lại, có thể đưa vào trạng thái `SUSPENDED` để ngưng bán, hoặc có cờ `isProfileUpdatePending` chứ không sửa trạng thái chính của toàn hệ thống.

### Từ `SUSPENDED`:
✅ **Được phép chuyển sang:**
- `ACTIVE` (Admin gỡ đình chỉ).
- `BANNED` (Quyết định cấm vĩnh viễn sau quá trình điều tra).

❌ **KHÔNG được phép chuyển sang:** `PENDING_VERIFICATION`.

### Từ `BANNED` / `CLOSED`:
- Thường là **Trạng Thái Cuối Cùng (Terminal States)**. Rất hạn chế (hoặc không cho phép) mở lại trừ khi có luồng khiếu nại rất đặc thù.

## 3. Ràng Buộc Trong Mã Nguồn (Code Level)
Trong class `UpdateSellerAccountStatusCommandHandler`, cần phải bổ sung logic kiểm tra trạng thái hiện tại (Current Status) trước khi cho phép cập nhật sang trạng thái mới (New Status). Nếu vi phạm luồng trên, hệ thống phải ném ra Exception (ví dụ: `IllegalStateException` hoặc trả về lỗi `INVALID_STATUS_TRANSITION`) để chặn đứng hành động này.
