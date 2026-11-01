package com.abs.app.application.admin.usermanager.query;

import com.abs.app.application.admin.usermanager.dto.UserResponseDto;
import com.abs.app.common.response.PageResponse;
import com.abs.app.common.util.PaginationUtil;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.mapper.UserAdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllUsersQueryHandler {

    private final UserRepository userRepository;

    public PageResponse<UserResponseDto> handle(GetAllUsersQuery query) {
        Pageable pageable = PaginationUtil.createPageable(
                query.getPage(),
                query.getSize(),
                Sort.by("createdAt").descending()
        );

        Page<User> page = userRepository.search(query.getKeyword(), query.getStatus(), pageable);

        return PaginationUtil.toPageResponse(
                page,
                UserAdminMapper::toUserResponseDto,
                query.getPage(),
                query.getSize()
        );
    }
}
