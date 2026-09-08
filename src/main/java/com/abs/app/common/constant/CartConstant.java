package com.abs.app.common.constant;

public class CartConstant {
    public static final String CART_NOT_FOUND = "Không tìm thấy giỏ hàng.";
    public static final String CART_EMPTY = "Giỏ hàng đang trống, không thể áp dụng mã giảm giá.";
    public static final String CART_ITEM_NOT_FOUND = "Không tìm thấy sản phẩm trong giỏ hàng.";
    public static final String CART_ITEM_NOT_BELONG_TO_USER = "Sản phẩm trong giỏ hàng không thuộc về người dùng này.";

    public static final String COUPON_NOT_FOUND = "Mã giảm giá không tồn tại.";
    public static final String COUPON_EXPIRED = "Mã giảm giá đã hết hiệu lực.";
    public static final String COUPON_NOT_IN_VALID_PERIOD = "Mã giảm giá không nằm trong thời gian sử dụng.";
    public static final String COUPON_ALREADY_USED = "Bạn đã sử dụng mã giảm giá này rồi.";

    public static final String CANNOT_ADD_OWN_PRODUCT = "Bạn không thể thêm sản phẩm của chính mình vào giỏ hàng.";

    // API Success Messages
    public static final String FETCH_CART_SUCCESS = "Lấy thông tin giỏ hàng thành công.";
    public static final String ADD_ITEM_SUCCESS = "Thêm sản phẩm vào giỏ hàng thành công.";
    public static final String UPDATE_ITEM_SUCCESS = "Cập nhật giỏ hàng thành công.";
    public static final String REMOVE_ITEM_SUCCESS = "Xóa sản phẩm khỏi giỏ hàng thành công.";
    public static final String APPLY_COUPON_SUCCESS = "Áp dụng mã giảm giá thành công.";
    public static final String REMOVE_COUPON_SUCCESS = "Gỡ mã giảm giá thành công.";

    // Validation Messages
    public static final String PRODUCT_ID_REQUIRED = "Mã sản phẩm không được để trống.";
    public static final String SIZE_REQUIRED = "Kích thước không được để trống.";
    public static final String QUANTITY_REQUIRED = "Số lượng không được để trống.";
    public static final String QUANTITY_MIN = "Số lượng phải lớn hơn hoặc bằng 1.";
    public static final String OUT_OF_STOCK = "Vượt quá số lượng tồn kho.";
    public static final String COUPON_CODE_REQUIRED = "Mã giảm giá không được để trống.";
}
