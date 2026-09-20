# Ecommerce Multivendor - Coding Standards & Guidelines

Tài liệu này định nghĩa các quy tắc lập trình bắt buộc phải tuân thủ nghiêm ngặt trong dự án này. 
**Mục tiêu:** Đảm bảo code sạch, kiến trúc đồng nhất, và tránh lặp lại những lỗi cơ bản.

## 1. Nguyên Tắc KHÔNG Magic String (No Magic Strings)
- **TUYỆT ĐỐI KHÔNG** được hardcode chuỗi string trực tiếp trong code (Controller, Service, Handler, Exception, DTO Validation).
- Tất cả các chuỗi thông báo (Success messages, Error messages, Validation messages) đều phải được định nghĩa dưới dạng `public static final String` trong các class `Constant` tương ứng.
- Vị trí đặt file Constant: `src/main/java/com/abs/app/common/constant/` (VD: `CartConstant.java`, `ProductConstant.java`).

## 2. Kiến Trúc Hexagonal (Ports & Adapters)
- Dự án áp dụng kiến trúc Hexagonal.
- Các class cài đặt (Implementation) của Repository interface (nằm ở tầng Domain) **BẮT BUỘC** phải được đặt vào package `adapter`.
- Vị trí đúng: `src/main/java/com/abs/app/infrastructure/persistence/adapter/` (VD: `CartRepositoryImpl.java`, không bao giờ được vứt bên ngoài `persistence`).

## 3. Quy Tắc Về Entity (JPA & Hibernate)
- Tránh tuyệt đối lỗi `StackOverflowError` sinh ra do quan hệ 2 chiều (Bidirectional Relationships) khi dùng Lombok.
- **BẮT BUỘC:** 
  - Khai báo class Entity với `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`.
  - Chỉ áp dụng `@EqualsAndHashCode.Include` duy nhất trên trường Khóa chính (`@Id`).
  - Không bao giờ được dùng `@EqualsAndHashCode` mặc định (không tham số) trên Entity.

## 4. Quản Lý Giao Dịch (Transactions)
- Các tác vụ Query (chỉ đọc, ví dụ: `GetCartQueryHandler`, `GET` API): Phải được đánh dấu `@Transactional(readOnly = true)` để tối ưu hóa hiệu năng Database.
- Các tác vụ Command (ghi, sửa, xóa, ví dụ: `AddToCartCommandHandler`, `POST`, `PUT`, `DELETE` API): Phải đánh dấu `@Transactional`.

## 5. Quy Chuẩn Git & Nhánh (Branching)
- **Tên Nhánh:** Tên nhánh phải phản ánh đúng mục đích (VD: `feature/cart-api`, `fix/entity-stackoverflow`).
- **Commit Message:** Sử dụng Conventional Commits ngắn gọn, súc tích (VD: `feat(cart-api): add apply coupon endpoint`, `fix(entity): resolve StackOverflowError`). Không ghi dông dài lan man.
- **Sạch Sẽ:** Kiểm tra kỹ `git status` trước khi commit. TUYỆT ĐỐI không commit rác, thư mục test dư thừa (như thư mục `images/` ở root), phải đưa vào `.gitignore` hoặc `git rm -r --cached`.
- Sửa lỗi toàn cục (bug chung) không được dồn vào nhánh tính năng riêng lẻ mà phải tách nhánh fix riêng biệt.

## 6. Logic Nghiệp Vụ Chú Ý
- **Phòng chống trục lợi:** Trong một nền tảng Multivendor, luôn luôn phải kiểm tra chéo: Người bán (Seller) KHÔNG BAO GIỜ được phép thêm vào giỏ hàng hoặc mua chính sản phẩm do mình đăng bán. Guard Clause chặn việc này phải luôn được đặt ở mọi điểm bắt đầu mua hàng.

## 7. Domain Services cho Logic Lặp Lại (DRY)
- Các đoạn logic nghiệp vụ lặp đi lặp lại nhiều lần giữa các Handler (ví dụ: kiểm tra tồn tại và khởi tạo tự động Wishlist/Cart cho User) **BẮT BUỘC** phải được tách ra thành một Service riêng.
- Vị trí đặt: `src/main/java/com/abs/app/domain/service/` (VD: `CartService.java`).
- Không được phép copy-paste cùng một đoạn logic giống hệt nhau vào nhiều file Handler/Query khác nhau.
