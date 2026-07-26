package com.nahla.marketplace.dto.response;
import java.util.List;
public record PagedResponse<T>(List<T> data, long total, int limit, int offset) {
}