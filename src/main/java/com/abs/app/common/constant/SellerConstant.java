package com.abs.app.common.constant;

public class SellerConstant {

    private SellerConstant() {}

    public static final String SELLER_REGISTER_SUCCESS = "Đăng ký mở gian hàng thành công. Vui lòng chờ Admin phê duyệt.";
    public static final String SELLER_PROFILE_UPDATED = "Cập nhật thông tin gian hàng thành công.";
    public static final String SELLER_BANK_UPDATED = "Cập nhật thông tin ngân hàng thành công.";
    public static final String SELLER_ADDRESS_UPDATED = "Cập nhật địa chỉ lấy hàng thành công.";

    public static final String SELLER_ALREADY_EXISTS = "Bạn đã có gian hàng. Mỗi tài khoản chỉ được mở 1 gian hàng.";
    public static final String SELLER_NOT_FOUND = "Không tìm thấy gian hàng.";
    public static final String SELLER_NOT_ACTIVE = "Gian hàng của bạn chưa được kích hoạt hoặc đang bị đình chỉ.";
    public static final String SELLER_ACCESS_DENIED = "Bạn không có quyền thực hiện thao tác này trên gian hàng.";

    public static final String BUSINESS_NAME_REQUIRED = "Tên gian hàng không được để trống";
    public static final String BUSINESS_NAME_MAX_LENGTH = "Tên gian hàng tối đa 100 ký tự";
    public static final String BUSINESS_EMAIL_REQUIRED = "Email gian hàng không được để trống";
    public static final String BUSINESS_EMAIL_INVALID = "Email gian hàng không hợp lệ";
    public static final String BUSINESS_PHONE_REQUIRED = "Số điện thoại gian hàng không được để trống";
    public static final String BUSINESS_PHONE_INVALID = "Số điện thoại không hợp lệ";
    public static final String BUSINESS_ADDRESS_REQUIRED = "Địa chỉ gian hàng không được để trống";

    public static final String ACCOUNT_NAME_REQUIRED = "Tên tài khoản ngân hàng không được để trống";
    public static final String ACCOUNT_HOLDER_NAME_REQUIRED = "Tên chủ tài khoản không được để trống";
    public static final String IFSC_CODE_REQUIRED = "Mã IFSC/Swift không được để trống";

    public static final String PICKUP_NAME_REQUIRED = "Tên người nhận không được để trống";
    public static final String PICKUP_LOCALITY_REQUIRED = "Khu vực không được để trống";
    public static final String PICKUP_ADDRESS_REQUIRED = "Địa chỉ lấy hàng không được để trống";
    public static final String PICKUP_CITY_REQUIRED = "Thành phố không được để trống";
    public static final String PICKUP_STATE_REQUIRED = "Tỉnh/Thành phố không được để trống";
    public static final String PICKUP_PINCODE_REQUIRED = "Mã bưu chính không được để trống";
    public static final String PICKUP_PHONE_REQUIRED = "Số điện thoại lấy hàng không được để trống";
    public static final String PICKUP_PHONE_INVALID = "Số điện thoại lấy hàng không hợp lệ";
}
