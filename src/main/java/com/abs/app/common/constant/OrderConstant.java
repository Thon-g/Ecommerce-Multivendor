package com.abs.app.common.constant;

public class OrderConstant {
    public static final String ORDER_NOT_FOUND = "Không tìm thấy đơn hàng.";
    public static final String OUT_OF_STOCK = "Sản phẩm '%s' đã hết hàng hoặc số lượng yêu cầu vượt quá tồn kho hiện tại.";
    public static final String CART_EMPTY = "Giỏ hàng của bạn đang trống, không thể thanh toán.";
    public static final String CHECKOUT_SUCCESS = "Đặt hàng thành công.";
    public static final String INVALID_ADDRESS = "Địa chỉ giao hàng không hợp lệ.";
    public static final String SALT = "ORD";
    public static final int LIMIT = 10;
    public static final String GET_ORDERS_SUCCESS = "Lấy danh sách đơn hàng thành công.";
    public static final String GET_SELLER_ORDERS_SUCCESS = "Lấy danh sách đơn hàng cho người bán thành công.";
    public static final String USER_NOT_SELLER = "Tài khoản của bạn chưa được liên kết với bất kỳ gian hàng nào.";
    public static final String ORDER_ACCESS_DENIED = "Bạn không có quyền thao tác trên đơn hàng này.";
    public static final String ORDER_UPDATE_CANCELLED = "Không thể cập nhật trạng thái của đơn hàng đã hủy.";
    public static final String ORDER_CANCEL_SHIPPED = "Không thể hủy đơn hàng đã được giao hoặc đang vận chuyển.";
    public static final String ORDER_ALREADY_CANCELLED = "Đơn hàng này đã bị hủy từ trước.";
    public static final String SKU_NOT_FOUND_FOR_ORDER = "Không tìm thấy thông tin sản phẩm trong kho để hoàn trả.";
    public static final String UPDATE_ORDER_STATUS_SUCCESS = "Cập nhật trạng thái đơn hàng thành công.";
    public static final String CANCEL_ORDER_SUCCESS = "Hủy đơn hàng thành công.";
    public static final String CANCEL_REASON_REQUIRED = "Vui lòng nhập lý do hủy đơn hàng.";
    public static final String ORDER_STATUS_REQUIRED = "Vui lòng chọn trạng thái đơn hàng muốn cập nhật.";
    public static final String ADDRESS_NOT_NULL = "Địa chỉ giao hàng không được để trống.";
}
