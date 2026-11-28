package com.abs.app.infrastructure.ai;

import com.abs.app.domain.model.AiChatResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AiResponseParser {

    private static final Pattern RESPONSE_TAG_PATTERN = Pattern.compile("<response>(.*?)</response>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern PRODUCTS_TAG_PATTERN = Pattern.compile("<recommended_products>(.*?)</recommended_products>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern CATEGORIES_TAG_PATTERN = Pattern.compile("<recommended_categories>(.*?)</recommended_categories>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern QUICK_REPLIES_TAG_PATTERN = Pattern.compile("<quick_replies>(.*?)</quick_replies>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    private AiResponseParser() {
    }

    public static AiChatResponse parse(String rawOutput) {
        if (rawOutput == null || rawOutput.isBlank()) {
            return AiChatResponse.builder()
                    .text("")
                    .recommendedProductIds(new ArrayList<>())
                    .recommendedCategoryIds(new ArrayList<>())
                    .quickReplies(new ArrayList<>())
                    .build();
        }

        List<String> productIds = extractListFromTag(PRODUCTS_TAG_PATTERN, rawOutput);
        List<String> categoryIds = extractListFromTag(CATEGORIES_TAG_PATTERN, rawOutput);
        List<String> quickReplies = extractListFromTag(QUICK_REPLIES_TAG_PATTERN, rawOutput);

        String text;
        Matcher responseMatcher = RESPONSE_TAG_PATTERN.matcher(rawOutput);
        if (responseMatcher.find()) {
            text = responseMatcher.group(1).trim();
        } else {
            text = rawOutput
                    .replaceAll("(?is)<recommended_products>.*?</recommended_products>", "")
                    .replaceAll("(?is)<recommended_categories>.*?</recommended_categories>", "")
                    .replaceAll("(?is)<quick_replies>.*?</quick_replies>", "")
                    .trim();
        }

        return AiChatResponse.builder()
                .text(text)
                .recommendedProductIds(productIds)
                .recommendedCategoryIds(categoryIds)
                .quickReplies(quickReplies)
                .build();
    }

    private static List<String> extractListFromTag(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String inside = matcher.group(1).trim();
            if (inside.startsWith("[") && inside.endsWith("]")) {
                inside = inside.substring(1, inside.length() - 1).trim();
            }
            if (!inside.isBlank() && !inside.equalsIgnoreCase("NONE")) {
                return Arrays.stream(inside.split("[,;\\n]"))
                        .map(AiResponseParser::cleanToken)
                        .filter(s -> !s.isBlank() && !s.equalsIgnoreCase("NONE") && !s.equalsIgnoreCase("null"))
                        .distinct()
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private static String cleanToken(String raw) {
        String s = raw.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            if (s.length() >= 2) {
                s = s.substring(1, s.length() - 1).trim();
            }
        }
        return s;
    }
}
