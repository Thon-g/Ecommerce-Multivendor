package com.abs.app.presentation.controller.publicapi;

import com.abs.app.application.admin.banner.dto.HomeDataResponseDto;
import com.abs.app.application.publicapi.home.query.GetHomeDataQuery;
import com.abs.app.application.publicapi.home.query.GetHomeDataQueryHandler;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/home")
@RequiredArgsConstructor
public class HomeController {

    private final GetHomeDataQueryHandler getHomeDataQueryHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<HomeDataResponseDto>> getHomeData() {
        HomeDataResponseDto response = getHomeDataQueryHandler.handle(new GetHomeDataQuery());
        return ResponseEntity.ok(new ApiResponse<>(true, BannerConstant.HOME_DATA_FETCHED_SUCCESS, response));
    }
}

