package com.abs.app.common.constant;

public final class BannerConstant {
    private BannerConstant() {}

    // Success messages
    public static final String HOME_CATEGORY_CREATED_SUCCESS = "Tạo danh mục trang chủ thành công.";
    public static final String HOME_CATEGORY_UPDATED_SUCCESS = "Cập nhật danh mục trang chủ thành công.";
    public static final String HOME_CATEGORY_DELETED_SUCCESS = "Xóa danh mục trang chủ thành công.";
    public static final String HOME_CATEGORY_FETCHED_SUCCESS = "Lấy danh sách danh mục trang chủ thành công.";

    public static final String DEAL_CREATED_SUCCESS = "Tạo ưu đãi deal thành công.";
    public static final String DEAL_DELETED_SUCCESS = "Xóa ưu đãi deal thành công.";
    public static final String DEAL_FETCHED_SUCCESS = "Lấy danh sách ưu đãi deal thành công.";

    public static final String HOME_DATA_FETCHED_SUCCESS = "Lấy dữ liệu trang chủ thành công.";

    // Error messages
    public static final String HOME_CATEGORY_NOT_FOUND = "Không tìm thấy danh mục trang chủ.";
    public static final String DEAL_NOT_FOUND = "Không tìm thấy ưu đãi deal.";
    public static final String DEAL_ALREADY_EXISTS = "Ưu đãi deal cho danh mục này đã tồn tại.";
    public static final String INVALID_DISCOUNT_PERCENT = "Phần trăm giảm giá không hợp lệ (từ 1 đến 100%).";

    // Validation messages
    public static final String HOME_CATEGORY_NAME_REQUIRED = "Tên danh mục trang chủ không được để trống.";
    public static final String HOME_CATEGORY_NAME_SIZE_INVALID = "Tên danh mục trang chủ tối đa 100 ký tự.";
    public static final String CATEGORY_ID_REQUIRED = "Mã danh mục liên kết không được để trống.";
    public static final String SECTION_REQUIRED = "Vị trí hiển thị (section) không được để trống.";
    public static final String BANNER_IMAGE_REQUIRED = "Hình ảnh banner không được để trống.";

    public static final String DISCOUNT_REQUIRED = "Phần trăm giảm giá không được để trống.";
    public static final String DISCOUNT_MIN_INVALID = "Phần trăm giảm giá tối thiểu là 1%.";
    public static final String DISCOUNT_MAX_INVALID = "Phần trăm giảm giá tối đa là 100%.";
    public static final String HOME_CATEGORY_ID_REQUIRED = "Mã danh mục trang chủ (homeCategoryId) không được để trống.";

    public static final String BANNER_PREFIX = "banner_";
}
