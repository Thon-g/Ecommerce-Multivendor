package com.abs.app.presentation.controller;

import com.abs.app.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping
    public ApiResponse HomeControllerHandler() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Hello world");
        return apiResponse;
    }
}
