package com.abs.app.common.util;

import com.abs.app.common.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

public class PaginationUtil {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private PaginationUtil() {
    }

    public static Pageable createPageable(int page, int pageSize) {
        return createPageable(page, pageSize, Sort.unsorted());
    }

    public static Pageable createPageable(int page, int pageSize, Sort sort) {
        return PageRequest.of(normalizePage(page) - 1, normalizePageSize(pageSize), sort);
    }

    public static <T, R> PageResponse<R> toPageResponse(
            Page<T> page,
            Function<T, R> mapper,
            int requestedPage,
            int requestedPageSize) {
        List<R> items = page.getContent().stream()
                .map(mapper)
                .toList();

        return new PageResponse<>(
                items,
                safeTotal(page.getTotalElements()),
                normalizePage(requestedPage),
                normalizePageSize(requestedPageSize));
    }

    public static <T> PageResponse<T> emptyResponse(int requestedPage, int requestedPageSize) {
        return new PageResponse<>(List.of(), 0, normalizePage(requestedPage), normalizePageSize(requestedPageSize));
    }

    public static int calculateTotalPages(int totalElements, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / pageSize);
    }

    public static boolean isValidPage(int page, int totalElements, int pageSize) {
        if (page < 1 || pageSize <= 0) {
            return false;
        }
        int totalPages = calculateTotalPages(totalElements, pageSize);
        return page <= totalPages;
    }

    private static int normalizePage(int page) {
        return page < DEFAULT_PAGE ? DEFAULT_PAGE : page;
    }

    private static int normalizePageSize(int pageSize) {
        if (pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private static int safeTotal(long totalElements) {
        return totalElements > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) totalElements;
    }
}
