package com.qms.qms.dto;

import java.util.List;

public record CursorPageResponse<T>(List<T> items, Long nextCursor, boolean hasNext) {
}
