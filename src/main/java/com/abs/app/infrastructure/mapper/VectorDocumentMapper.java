package com.abs.app.infrastructure.mapper;

import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VectorDocumentMapper {

    public static final String META_DOC_TYPE = "docType";
    public static final String META_PRODUCT_ID = "productId";
    public static final String META_TITLE = "title";
    public static final String META_SELLING_PRICE = "sellingPrice";
    public static final String META_MRP_PRICE = "mrpPrice";
    public static final String META_DISCOUNT_PERCENT = "discountPercent";
    public static final String META_AVERAGE_RATING = "averageRating";
    public static final String META_NUM_RATINGS = "numRatings";
    public static final String META_IN_STOCK = "inStock";
    public static final String META_CATEGORY_ID = "categoryId";
    public static final String META_CATEGORY_NAME = "categoryName";
    public static final String META_SELLER_ID = "sellerId";
    public static final String META_SHOP_NAME = "shopName";
    public static final String META_LEVEL = "level";
    public static final String META_PARENT_CATEGORY_ID = "parentCategoryId";

    public static final String DOC_TYPE_PRODUCT = "product";
    public static final String DOC_TYPE_CATEGORY = "category";

    public Document toProductDocument(Product product) {
        if (product == null) {
            return null;
        }

        String docId = "doc_prod_" + product.getId();
        String content = buildProductContent(product);
        Map<String, Object> metadata = buildProductMetadata(product);

        return new Document(docId, content, metadata);
    }

    public Document toCategoryDocument(Category category) {
        if (category == null) {
            return null;
        }

        String docId = "doc_cat_" + category.getCategoryId();
        String content = buildCategoryContent(category);
        Map<String, Object> metadata = buildCategoryMetadata(category);

        return new Document(docId, content, metadata);
    }

    private String buildProductContent(Product product) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sản phẩm: ").append(product.getTitle()).append("\n");
        sb.append("Mã sản phẩm: ").append(product.getId()).append("\n");

        if (product.getCategory() != null) {
            sb.append("Danh mục: ").append(product.getCategory().getName()).append("\n");
        }

        if (product.getSeller() != null) {
            sb.append("Gian hàng: ").append(product.getSeller().getShopName()).append("\n");
        }

        sb.append("Giá bán: ").append(product.getSellingPrice()).append(" VNĐ");
        if (product.getMrpPrice() != null) {
            sb.append(" (Giá gốc: ").append(product.getMrpPrice()).append(" VNĐ");
            if (product.getDiscountPercent() != null && product.getDiscountPercent() > 0) {
                sb.append(", Giảm: ").append(product.getDiscountPercent()).append("%");
            }
            sb.append(")");
        }
        sb.append("\n");

        if (product.getAverageRating() != null && product.getAverageRating() > 0) {
            sb.append("Đánh giá: ").append(product.getAverageRating()).append(" sao");
            if (product.getNumRatings() != null) {
                sb.append(" (").append(product.getNumRatings()).append(" lượt đánh giá)");
            }
            sb.append("\n");
        }

        if (product.getSkus() != null && !product.getSkus().isEmpty()) {
            List<String> skuDetails = product.getSkus().stream()
                    .filter(sku -> sku.getQuantity() != null && sku.getQuantity() > 0)
                    .map(sku -> {
                        List<String> parts = new ArrayList<>();
                        if (sku.getColor() != null && !sku.getColor().isBlank()) {
                            parts.add("Màu: " + sku.getColor());
                        }
                        if (sku.getSize() != null && !sku.getSize().isBlank()) {
                            parts.add("Size: " + sku.getSize());
                        }
                        parts.add("Còn: " + sku.getQuantity());
                        return String.join(", ", parts);
                    })
                    .toList();

            if (!skuDetails.isEmpty()) {
                sb.append("Phân loại hàng còn tồn: ").append(String.join("; ", skuDetails)).append("\n");
            }
        }

        if (product.getDescription() != null && !product.getDescription().isBlank()) {
            String cleanDescription = stripHtml(product.getDescription());
            if (cleanDescription.length() > 600) {
                cleanDescription = cleanDescription.substring(0, 600) + "...";
            }
            sb.append("Mô tả: ").append(cleanDescription).append("\n");
        }

        return sb.toString();
    }

    private Map<String, Object> buildProductMetadata(Product product) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(META_DOC_TYPE, DOC_TYPE_PRODUCT);
        metadata.put(META_PRODUCT_ID, product.getId());
        metadata.put(META_TITLE, product.getTitle());
        metadata.put(META_SELLING_PRICE, product.getSellingPrice() != null ? product.getSellingPrice() : 0);
        metadata.put(META_MRP_PRICE, product.getMrpPrice() != null ? product.getMrpPrice() : 0);
        metadata.put(META_DISCOUNT_PERCENT, product.getDiscountPercent() != null ? product.getDiscountPercent() : 0);
        metadata.put(META_AVERAGE_RATING, product.getAverageRating() != null ? product.getAverageRating() : 0.0);
        metadata.put(META_NUM_RATINGS, product.getNumRatings() != null ? product.getNumRatings() : 0);
        metadata.put(META_IN_STOCK, true);

        if (product.getCategory() != null) {
            metadata.put(META_CATEGORY_ID, product.getCategory().getCategoryId() != null ? product.getCategory().getCategoryId() : "");
            metadata.put(META_CATEGORY_NAME, product.getCategory().getName() != null ? product.getCategory().getName() : "");
        } else {
            metadata.put(META_CATEGORY_ID, "");
            metadata.put(META_CATEGORY_NAME, "");
        }

        if (product.getSeller() != null) {
            metadata.put(META_SELLER_ID, product.getSeller().getSellerId() != null ? product.getSeller().getSellerId() : "");
            metadata.put(META_SHOP_NAME, product.getSeller().getShopName() != null ? product.getSeller().getShopName() : "");
        } else {
            metadata.put(META_SELLER_ID, "");
            metadata.put(META_SHOP_NAME, "");
        }

        return metadata;
    }

    private String buildCategoryContent(Category category) {
        StringBuilder sb = new StringBuilder();
        sb.append("Danh mục: ").append(category.getName()).append("\n");
        sb.append("Mã danh mục: ").append(category.getCategoryId()).append("\n");
        if (category.getLevel() != null) {
            sb.append("Cấp độ: ").append(category.getLevel()).append("\n");
        }
        if (category.getParentCategory() != null) {
            sb.append("Thuộc danh mục cha: ").append(category.getParentCategory().getName()).append("\n");
        }
        sb.append("Mô tả: Ngành hàng ").append(category.getName()).append(" trên sàn thương mại điện tử.");
        return sb.toString();
    }

    private Map<String, Object> buildCategoryMetadata(Category category) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(META_DOC_TYPE, DOC_TYPE_CATEGORY);
        metadata.put(META_CATEGORY_ID, category.getCategoryId());
        metadata.put(META_CATEGORY_NAME, category.getName());
        metadata.put(META_LEVEL, category.getLevel() != null ? category.getLevel() : 1);
        if (category.getParentCategory() != null) {
            metadata.put(META_PARENT_CATEGORY_ID, category.getParentCategory().getCategoryId() != null ? category.getParentCategory().getCategoryId() : "");
        } else {
            metadata.put(META_PARENT_CATEGORY_ID, "");
        }
        return metadata;
    }

    public String stripHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<[^>]*>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
