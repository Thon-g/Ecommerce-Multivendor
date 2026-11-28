package com.abs.app.unit.infrastructure.ai;

import com.abs.app.domain.model.AiChatResponse;
import com.abs.app.infrastructure.ai.AiResponseParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiResponseParserTest {

    @Test
    @DisplayName("Parse raw output containing all tags successfully")
    void testParse_WithAllTags() {
        String raw = """
                <response>
                Chào bạn! Mình tìm thấy 2 mẫu áo polo nam màu trắng rất đẹp nè:
                </response>
                <recommended_products>
                PROD_001, PROD_002
                </recommended_products>
                <recommended_categories>
                CAT_MEN_FASHION
                </recommended_categories>
                <quick_replies>
                Áo polo trắng, Quần tây nam, Chính sách đổi trả
                </quick_replies>
                """;

        AiChatResponse response = AiResponseParser.parse(raw);

        assertThat(response.getText()).isEqualTo("Chào bạn! Mình tìm thấy 2 mẫu áo polo nam màu trắng rất đẹp nè:");
        assertThat(response.getRecommendedProductIds()).containsExactly("PROD_001", "PROD_002");
        assertThat(response.getRecommendedCategoryIds()).containsExactly("CAT_MEN_FASHION");
        assertThat(response.getQuickReplies()).containsExactly("Áo polo trắng", "Quần tây nam", "Chính sách đổi trả");
    }

    @Test
    @DisplayName("Parse raw output without response tag strips metadata tags from text")
    void testParse_WithoutResponseTag_StripsOtherTags() {
        String raw = """
                Dạ đây là các mẫu áo đang giảm giá sâu hôm nay ạ!
                <recommended_products>PROD_003</recommended_products>
                <quick_replies>Xem thêm</quick_replies>
                """;

        AiChatResponse response = AiResponseParser.parse(raw);

        assertThat(response.getText()).isEqualTo("Dạ đây là các mẫu áo đang giảm giá sâu hôm nay ạ!");
        assertThat(response.getRecommendedProductIds()).containsExactly("PROD_003");
        assertThat(response.getRecommendedCategoryIds()).isEmpty();
        assertThat(response.getQuickReplies()).containsExactly("Xem thêm");
    }

    @Test
    @DisplayName("Parse empty or null raw output returns safe defaults")
    void testParse_EmptyOrNull() {
        AiChatResponse nullResponse = AiResponseParser.parse(null);
        assertThat(nullResponse.getText()).isEmpty();
        assertThat(nullResponse.getRecommendedProductIds()).isEmpty();

        AiChatResponse blankResponse = AiResponseParser.parse("   ");
        assertThat(blankResponse.getText()).isEmpty();
        assertThat(blankResponse.getRecommendedProductIds()).isEmpty();
    }

    @Test
    @DisplayName("Parse NONE inside product tag treats as empty list")
    void testParse_WithNoneValue() {
        String raw = """
                <response>Hiện sàn chưa có sản phẩm này ạ.</response>
                <recommended_products>NONE</recommended_products>
                <recommended_categories>NONE</recommended_categories>
                """;

        AiChatResponse response = AiResponseParser.parse(raw);

        assertThat(response.getText()).isEqualTo("Hiện sàn chưa có sản phẩm này ạ.");
        assertThat(response.getRecommendedProductIds()).isEmpty();
        assertThat(response.getRecommendedCategoryIds()).isEmpty();
    }

    @Test
    @DisplayName("Parse JSON-like bracketed arrays and quoted strings cleanly")
    void testParse_BracketedAndQuotedArrays() {
        String raw = """
                <response>Đây là sản phẩm phù hợp.</response>
                <recommended_products>["PROD_001", "PROD_002"]</recommended_products>
                <recommended_categories>[]</recommended_categories>
                <quick_replies>["Có size L không?", "Xem màu trắng"]</quick_replies>
                """;

        AiChatResponse response = AiResponseParser.parse(raw);

        assertThat(response.getText()).isEqualTo("Đây là sản phẩm phù hợp.");
        assertThat(response.getRecommendedProductIds()).containsExactly("PROD_001", "PROD_002");
        assertThat(response.getRecommendedCategoryIds()).isEmpty();
        assertThat(response.getQuickReplies()).containsExactly("Có size L không?", "Xem màu trắng");
    }
}
