package com.abs.app.application.admin.sellermanager.query;

import com.abs.app.domain.entity.enums.SellerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllSellersQuery {
    private SellerStatus status;
}
