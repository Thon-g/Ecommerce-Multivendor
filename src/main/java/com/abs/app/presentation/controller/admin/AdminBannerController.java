package com.abs.app.presentation.controller.admin;

import com.abs.app.application.admin.banner.command.CreateHomeCategoryCommand;
import com.abs.app.application.admin.banner.command.CreateHomeCategoryCommandHandler;
import com.abs.app.application.admin.banner.command.DeleteHomeCategoryCommand;
import com.abs.app.application.admin.banner.command.DeleteHomeCategoryCommandHandler;
import com.abs.app.application.admin.banner.command.UpdateHomeCategoryCommand;
import com.abs.app.application.admin.banner.command.UpdateHomeCategoryCommandHandler;
import com.abs.app.application.admin.banner.dto.CreateHomeCategoryRequestDto;
import com.abs.app.application.admin.banner.dto.HomeCategoryResponseDto;
import com.abs.app.application.admin.banner.dto.UpdateHomeCategoryRequestDto;
import com.abs.app.application.admin.banner.query.GetHomeCategoriesQuery;
import com.abs.app.application.admin.banner.query.GetHomeCategoriesQueryHandler;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/home-categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBannerController {

    private final CreateHomeCategoryCommandHandler createHomeCategoryCommandHandler;
    private final UpdateHomeCategoryCommandHandler updateHomeCategoryCommandHandler;
    private final DeleteHomeCategoryCommandHandler deleteHomeCategoryCommandHandler;
    private final GetHomeCategoriesQueryHandler getHomeCategoriesQueryHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HomeCategoryResponseDto>>> getHomeCategories(
            @RequestParam(required = false) HomeCategorySection section) {
        List<HomeCategoryResponseDto> response = getHomeCategoriesQueryHandler.handle(new GetHomeCategoriesQuery(section));
        return ResponseEntity.ok(new ApiResponse<>(true, BannerConstant.HOME_CATEGORY_FETCHED_SUCCESS, response));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<HomeCategoryResponseDto>> createHomeCategory(
            @Valid @ModelAttribute CreateHomeCategoryRequestDto request) {
        CreateHomeCategoryCommand command = new CreateHomeCategoryCommand(
                request.getName(),
                request.getImage(),
                request.getCategoryId(),
                request.getSection()
        );
        HomeCategoryResponseDto response = createHomeCategoryCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, BannerConstant.HOME_CATEGORY_CREATED_SUCCESS, response));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<HomeCategoryResponseDto>> updateHomeCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateHomeCategoryRequestDto request) {
        UpdateHomeCategoryCommand command = new UpdateHomeCategoryCommand(
                id,
                request.getName(),
                request.getImage(),
                request.getCategoryId(),
                request.getSection()
        );
        HomeCategoryResponseDto response = updateHomeCategoryCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, BannerConstant.HOME_CATEGORY_UPDATED_SUCCESS, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHomeCategory(@PathVariable Long id) {
        deleteHomeCategoryCommandHandler.handle(new DeleteHomeCategoryCommand(id));
        return ResponseEntity.ok(new ApiResponse<>(true, BannerConstant.HOME_CATEGORY_DELETED_SUCCESS, null));
    }
}
