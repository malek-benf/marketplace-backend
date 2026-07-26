package com.nahla.marketplace.dto.response;

import java.util.List;
public record CountedListResponse<T>(long count, List<T> data) {

    public static <T> CountedListResponse<T> of(List<T> data) {
        return new CountedListResponse<>(data.size(), data);
    }
}
