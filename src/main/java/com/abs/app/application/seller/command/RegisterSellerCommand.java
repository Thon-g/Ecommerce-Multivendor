package com.abs.app.application.seller.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterSellerCommand {

    @NotBlank(message = "Tên gian hàng không được để trống")
    @Size(max = 100, message = "Tên gian hàng tối đa 100 ký tự")
    private String businessName;

    @NotBlank(message = "Email gian hàng không được để trống")
    @Email(message = "Email gian hàng không hợp lệ")
    private String businessEmail;

    @NotBlank(message = "Số điện thoại gian hàng không được để trống")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String businessPhone;

    @NotBlank(message = "Địa chỉ gian hàng không được để trống")
    private String businessAddress;

    @NotBlank(message = "Tên tài khoản ngân hàng không được để trống")
    private String accountName;

    @NotBlank(message = "Tên chủ tài khoản không được để trống")
    private String accountHolderName;

    @NotBlank(message = "Mã IFSC/Swift không được để trống")
    private String ifscCode;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String pickupName;

    @NotBlank(message = "Khu vực không được để trống")
    private String pickupLocality;

    @NotBlank(message = "Địa chỉ lấy hàng không được để trống")
    private String pickupAddress;

    @NotBlank(message = "Thành phố không được để trống")
    private String pickupCity;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String pickupState;

    @NotBlank(message = "Mã bưu chính không được để trống")
    private String pickupPinCode;

    @NotBlank(message = "Số điện thoại lấy hàng không được để trống")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String pickupPhone;

    private String gstin;
}
