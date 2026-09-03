package com.abs.app.common.constant;

public class CategoryConstant {
    private CategoryConstant() {
    }

    public static final String CATEGORY_NOT_FOUND = "Không tìm thấy danh mục.";
    public static final String CATEGORY_ID_ALREADY_EXISTS = "Mã danh mục đã tồn tại.";
    public static final String CATEGORY_HAS_CHILDREN = "Không thể xóa danh mục vì vẫn còn danh mục con.";
    public static final String CATEGORY_CREATED_SUCCESS = "Tạo danh mục thành công.";
    public static final String CATEGORY_UPDATED_SUCCESS = "Cập nhật danh mục thành công.";
    public static final String CATEGORY_DELETED_SUCCESS = "Xóa danh mục thành công.";
    public static final String CATEGORIES_FETCHED_SUCCESS = "Lấy danh sách danh mục thành công.";
    public static final String CATEGORY_FETCHED_SUCCESS = "Lấy thông tin danh mục thành công.";

    // Validation messages
    public static final String CATEGORY_NAME_REQUIRED = "Tên danh mục không được để trống.";
    public static final String CATEGORY_ID_REQUIRED = "Mã danh mục (slug) không được để trống.";
    public static final String CATEGORY_LEVEL_REQUIRED = "Cấp độ phân cấp không được để trống.";
}
