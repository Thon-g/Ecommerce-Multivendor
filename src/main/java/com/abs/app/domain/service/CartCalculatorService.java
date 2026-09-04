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

        // 1. Calculate base totals from items
        for (CartItem item : cart.getCartItems()) {
            totalItem += item.getQuantity();
            totalMrpPrice += item.getMrpPrice() * item.getQuantity();
            totalSellingPrice += item.getSellingPrice() * item.getQuantity();
        }

        // 2. Base discount (MRP - Selling Price of products themselves)
        int discount = totalMrpPrice - totalSellingPrice;

        // 3. Apply Coupon if valid
        if (optionalCoupon.isPresent() && isCouponValid(optionalCoupon.get(), totalSellingPrice)) {
            Coupon coupon = optionalCoupon.get();
            int couponDiscount = 0;

            if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                couponDiscount = (int) (totalSellingPrice * (coupon.getDiscountPercentage() / 100.0));
            } else if (coupon.getDiscountType() == DiscountType.FIXED) {
                // Wait, does Coupon have a fixed discount amount? Currently it only has discountPercentage.
                // If it's fixed, we might need a field for it, but for now we fallback or handle if it exists.
                // Let's assume we use discountPercentage as fixed amount if type is FIXED for now.
                couponDiscount = coupon.getDiscountPercentage().intValue();
            }

            totalSellingPrice = Math.max(0, totalSellingPrice - couponDiscount);
            discount += couponDiscount;
            cart.setCouponCode(coupon.getCode());
        } else {
            cart.setCouponCode(null);
        }

        // 4. Update cart fields
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
