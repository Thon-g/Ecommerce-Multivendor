package com.abs.app.common.util;

import java.util.Optional;

import org.springframework.util.StringUtils;

public class EnumUtil {
    private EnumUtil() {
    }

    public static <E extends Enum<E>> Optional<E> parse(Class<E> enumType, String value) {
        if (!StringUtils.hasText(value)) {
            return Optional.empty();
        }

        try {
            return Optional.of(Enum.valueOf(enumType, value.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static boolean isInvalidEnumValue(String value, Optional<? extends Enum<?>> parsedValue) {
        return StringUtils.hasText(value) && parsedValue.isEmpty();
    }
}
