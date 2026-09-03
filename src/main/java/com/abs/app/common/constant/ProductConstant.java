package com.abs.app.common.constant;

public class ProductConstant {
    private ProductConstant() {
    }

    // Success messages
    public static final String PRODUCT_CREATED_SUCCESS = "Tạo sản phẩm thành công.";
    public static final String PRODUCT_UPDATED_SUCCESS = "Cập nhật sản phẩm thành công.";
    public static final String PRODUCT_DELETED_SUCCESS = "Xóa sản phẩm thành công.";
    public static final String PRODUCTS_FETCHED_SUCCESS = "Lấy danh sách sản phẩm thành công.";
    public static final String PRODUCT_FETCHED_SUCCESS = "Lấy thông tin sản phẩm thành công.";

    // Error messages
    public static final String PRODUCT_NOT_FOUND = "Không tìm thấy sản phẩm.";
    public static final String PRODUCT_FORBIDDEN = "Bạn không có quyền chỉnh sửa/xóa sản phẩm của người khác.";

    // Validation messages
    public static final String TITLE_REQUIRED = "Tên sản phẩm không được để trống.";
    public static final String DESCRIPTION_REQUIRED = "Mô tả sản phẩm không được để trống.";
    public static final String MRP_PRICE_REQUIRED = "Giá gốc không được để trống.";
    public static final String SELLING_PRICE_REQUIRED = "Giá bán không được để trống.";
    public static final String PRICE_MIN_INVALID = "Giá sản phẩm không được nhỏ hơn 0.";
    public static final String QUANTITY_REQUIRED = "Số lượng không được để trống.";
    public static final String QUANTITY_MIN_INVALID = "Số lượng không được nhỏ hơn 0.";
    public static final String CATEGORY_REQUIRED = "Danh mục sản phẩm không được để trống.";
}
