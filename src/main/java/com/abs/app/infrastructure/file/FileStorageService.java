package com.abs.app.infrastructure.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileStorageService {
    private static final String AVATAR_UPLOAD_DIR = System.getProperty("user.dir") + "/images/uploads/avatars/";
    private static final String AVATAR_PUBLIC_PATH = "/images/uploads/avatars/";

    private static final String PRODUCT_IMAGE_UPLOAD_DIR = System.getProperty("user.dir") + "/images/uploads/products/";
    private static final String PRODUCT_IMAGE_PUBLIC_PATH = "/images/uploads/products/";

    private static final String REVIEW_IMAGE_UPLOAD_DIR = System.getProperty("user.dir") + "/images/uploads/reviews/";
    private static final String REVIEW_IMAGE_PUBLIC_PATH = "/images/uploads/reviews/";

    private static final String SELLER_IMAGE_UPLOAD_DIR = System.getProperty("user.dir") + "/images/uploads/sellers/";
    private static final String SELLER_IMAGE_PUBLIC_PATH = "/images/uploads/sellers/";

    public String storeAvatar(MultipartFile file, String userId) {
        return storeImage(file, userId, AVATAR_UPLOAD_DIR, AVATAR_PUBLIC_PATH);
    }

    public String storeProduct(MultipartFile file, String headString) {
        return storeImage(file, headString, PRODUCT_IMAGE_UPLOAD_DIR, PRODUCT_IMAGE_PUBLIC_PATH);
    }

    public String storeReview(MultipartFile file, String headString) {
        return storeImage(file, headString, REVIEW_IMAGE_UPLOAD_DIR, REVIEW_IMAGE_PUBLIC_PATH);
    }

    public String storeSeller(MultipartFile file, String headString) {
        return storeImage(file, headString, SELLER_IMAGE_UPLOAD_DIR, SELLER_IMAGE_PUBLIC_PATH);
    }


    private String storeImage(MultipartFile file, String headString, String path, String publicPath) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Ảnh không hợp lệ");
        }

        try {
            Path uploadPath = Paths.get(path);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || !originalFileName.contains(".")) {
                throw new RuntimeException("Tên file không hợp lệ");
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
            List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".gif");
            if (!allowedExtensions.contains(extension)) {
                throw new RuntimeException("Định dạng ảnh không được hỗ trợ");
            }
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                throw new RuntimeException("Ảnh vượt quá kích thước tối đa cho phép (5MB)");
            }

            String filename = headString + "_" + System.currentTimeMillis() + extension;
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());
            return publicPath + filename;

        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu ảnh", e);
        }
    }
}
