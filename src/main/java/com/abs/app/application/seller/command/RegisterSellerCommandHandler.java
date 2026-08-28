package com.abs.app.application.seller.command;

import com.abs.app.infrastructure.mapper.SellerMapper;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.util.GenerateIdUtil;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterSellerCommandHandler {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final SellerMapper sellerMapper;

    @Transactional
    public String handle(RegisterSellerCommand command) {
        String userId = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng."));

        if (sellerRepository.existsByUserId(userId)) {
            throw new BusinessException(SellerConstant.SELLER_ALREADY_EXISTS);
        }

        String sellerId = GenerateIdUtil.GenerateId("SHOP", 8);
        Seller seller = sellerMapper.toSeller(command, user, sellerId);

        sellerRepository.save(seller);

        return SellerConstant.SELLER_REGISTER_SUCCESS;
    }
}
