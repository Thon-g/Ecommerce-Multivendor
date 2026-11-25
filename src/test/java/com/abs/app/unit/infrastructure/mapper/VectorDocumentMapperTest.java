package com.abs.app.unit.infrastructure.mapper;

import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.ProductSku;
import com.abs.app.domain.entity.Seller;
import com.abs.app.infrastructure.mapper.VectorDocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VectorDocumentMapperTest {

    private VectorDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VectorDocumentMapper();
    }

    @Test
    @DisplayName("Convert active Product with SKUs into Spring AI Document")
    void testToProductDocument() {
        Category category = new Category();
        category.setCategoryId("cat-men-fashion");
        category.setName("Thời trang nam");

        Seller seller = new Seller();
        seller.setSellerId("seller-123");
        seller.setShopName("Nam Fashion Shop");

        ProductSku sku1 = new ProductSku();
        sku1.setColor("Trắng");
        sku1.setSize("L");
        sku1.setQuantity(15);

        ProductSku sku2 = new ProductSku();
        sku2.setColor("Đen");
        sku2.setSize("M");
        sku2.setQuantity(20);

        Product product = new Product();
        product.setId("prod-polo-01");
        product.setTitle("Áo Polo Nam Trắng Cotton");
        product.setDescription("<p>Áo polo nam <b>chất liệu cotton</b> thoáng mát.</p>");
        product.setSellingPrice(250000);
        product.setMrpPrice(350000);
        product.setDiscountPercent(28);
        product.setAverageRating(4.8);
        product.setNumRatings(120);
        product.setCategory(category);
        product.setSeller(seller);
        product.setSkus(List.of(sku1, sku2));

        Document doc = mapper.toProductDocument(product);

        assertThat(doc).isNotNull();
        assertThat(doc.getId()).isEqualTo("doc_prod_prod-polo-01");
        assertThat(doc.getContent()).contains("Áo Polo Nam Trắng Cotton");
        assertThat(doc.getContent()).contains("250000 VNĐ");
        assertThat(doc.getContent()).contains("Nam Fashion Shop");
        assertThat(doc.getContent()).contains("Màu: Trắng, Size: L, Còn: 15");
        assertThat(doc.getContent()).doesNotContain("<p>").doesNotContain("<b>");

        assertThat(doc.getMetadata()).containsEntry(VectorDocumentMapper.META_DOC_TYPE, VectorDocumentMapper.DOC_TYPE_PRODUCT);
        assertThat(doc.getMetadata()).containsEntry(VectorDocumentMapper.META_PRODUCT_ID, "prod-polo-01");
        assertThat(doc.getMetadata()).containsEntry(VectorDocumentMapper.META_SELLING_PRICE, 250000);
        assertThat(doc.getMetadata()).containsEntry(VectorDocumentMapper.META_SHOP_NAME, "Nam Fashion Shop");
        assertThat(doc.getMetadata()).containsEntry(VectorDocumentMapper.META_IN_STOCK, true);
    }

    @Test
    @DisplayName("Convert Category into Spring AI Document")
    void testToCategoryDocument() {
        Category parent = new Category();
        parent.setCategoryId("parent-fashion");
        parent.setName("Thời trang");

        Category category = new Category();
        category.setCategoryId("cat-men-fashion");
        category.setName("Thời trang nam");
        category.setLevel(2);
        category.setParentCategory(parent);

        Document doc = mapper.toCategoryDocument(category);

        assertThat(doc).isNotNull();
        assertThat(doc.getId()).isEqualTo("doc_cat_cat-men-fashion");
        assertThat(doc.getContent()).contains("Danh mục: Thời trang nam");
        assertThat(doc.getContent()).contains("Thuộc danh mục cha: Thời trang");

        assertThat(doc.getMetadata()).containsEntry(VectorDocumentMapper.META_DOC_TYPE, VectorDocumentMapper.DOC_TYPE_CATEGORY);
        assertThat(doc.getMetadata()).containsEntry(VectorDocumentMapper.META_CATEGORY_ID, "cat-men-fashion");
        assertThat(doc.getMetadata()).containsEntry(VectorDocumentMapper.META_LEVEL, 2);
    }

    @Test
    @DisplayName("stripHtml removes all HTML tags and normalizes spaces")
    void testStripHtml() {
        String html = "<div><h1>Tiêu đề</h1> <p>Nội dung <b>đậm</b> và    nhiều   khoảng trắng</p></div>";
        String clean = mapper.stripHtml(html);
        assertThat(clean).isEqualTo("Tiêu đề Nội dung đậm và nhiều khoảng trắng");
    }
}
