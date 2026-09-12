package com.abs.app.domain.service;

import com.abs.app.common.constant.RoleConstant;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Role;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.RoleUser;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.repository.RoleRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public Seller updateSellerStatus(String sellerId, SellerStatus newStatus) {
        Seller seller = sellerRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        validateStatusTransition(seller.getStatus(), newStatus);
        
        seller.setStatus(newStatus);
        seller = sellerRepository.save(seller);

        synchronizeUserRoles(seller.getUser(), newStatus);

        return seller;
    }

    private void validateStatusTransition(SellerStatus currentStatus, SellerStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
        }

        boolean isValid = false;
        switch (currentStatus) {
            case PENDING_VERIFICATION:
                if (newStatus == SellerStatus.ACTIVE || newStatus == SellerStatus.BANNED) {
                    isValid = true;
                }
                break;
            case ACTIVE:
                if (newStatus == SellerStatus.SUSPENDED || newStatus == SellerStatus.BANNED 
                        || newStatus == SellerStatus.DEACTIVATED || newStatus == SellerStatus.CLOSED) {
                    isValid = true;
                }
                break;
            case SUSPENDED:
                if (newStatus == SellerStatus.ACTIVE || newStatus == SellerStatus.BANNED) {
                    isValid = true;
                }
                break;
            case BANNED:
                if (newStatus == SellerStatus.ACTIVE) {
                    isValid = true;
                }
                break;
            case DEACTIVATED:
            case CLOSED:
                // Terminal states
                break;
        }

        if (!isValid) {
            throw new BusinessException(
                    String.format(SellerConstant.INVALID_STATUS_TRANSITION, currentStatus, newStatus)
            );
        }
    }

    private void synchronizeUserRoles(User user, SellerStatus newStatus) {
        boolean hasSellerRole = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleUser.SELLER);

        Role sellerRole = roleRepository.findByRoleName(RoleUser.SELLER)
                .orElseThrow(() -> new ResourceNotFoundException(RoleConstant.ROLE_NOT_EXIST));

        boolean roleChanged = false;

        if (newStatus == SellerStatus.ACTIVE) {
            if (!hasSellerRole) {
                user.getRoles().add(sellerRole);
                roleChanged = true;
            }
        } else if (newStatus == SellerStatus.SUSPENDED || newStatus == SellerStatus.BANNED 
                || newStatus == SellerStatus.DEACTIVATED || newStatus == SellerStatus.CLOSED) {
            if (hasSellerRole) {
                user.getRoles().removeIf(role -> role.getRoleName() == RoleUser.SELLER);
                roleChanged = true;
            }
        }

        if (roleChanged) {
            userRepository.save(user);
        }
    }
}
