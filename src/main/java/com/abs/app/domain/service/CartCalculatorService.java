package com.abs.app.domain.service;

import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.CartItem;
import com.abs.app.domain.entity.Coupon;
import com.abs.app.domain.entity.enums.CouponStatus;
import com.abs.app.domain.entity.enums.DiscountType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CartCalculatorService {

    public void recalculateCart(Cart cart, Optional<Coupon> optionalCoupon) {
        int totalItem = 0;
        int totalMrpPrice = 0;
        int totalSellingPrice = 0;

        for (CartItem item : cart.getCartItems()) {
            totalItem += item.getQuantity();
            totalMrpPrice += item.getMrpPrice() * item.getQuantity();
            totalSellingPrice += item.getSellingPrice() * item.getQuantity();
        }

        int discount = totalMrpPrice - totalSellingPrice;

        if (optionalCoupon.isPresent() && isCouponValid(optionalCoupon.get(), totalSellingPrice)) {
            Coupon coupon = optionalCoupon.get();
            int couponDiscount = 0;

            if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                couponDiscount = (int) (totalSellingPrice * (coupon.getDiscountPercentage() / 100.0));
            } else if (coupon.getDiscountType() == DiscountType.FIXED) {
                couponDiscount = coupon.getDiscountPercentage().intValue();
            }

            totalSellingPrice = Math.max(0, totalSellingPrice - couponDiscount);
            discount += couponDiscount;
            cart.setCouponCode(coupon.getCode());
        } else {
            cart.setCouponCode(null);
        }

        cart.setTotalItem(totalItem);
        cart.setTotalMrpPrice(totalMrpPrice);
        cart.setTotalSellingPrice((double) totalSellingPrice);
        cart.setDiscount(discount);
    }

    private boolean isCouponValid(Coupon coupon, int totalSellingPrice) {
        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            return false;
        }

        LocalDate now = LocalDate.now();
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            return false;
        }

        if (coupon.getMinimumOrderValue() != null && totalSellingPrice < coupon.getMinimumOrderValue()) {
            return false;
        }

        return true;
    }
}
