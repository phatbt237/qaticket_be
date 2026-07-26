package com.qms.qms.dto.master;

import com.qms.qms.entity.DefectItem;

public record DefectItemResponse(
        Long id,
        Long defectId,
        String code,
        String nameEn,
        String nameVi,
        boolean allowMinor,
        boolean allowMajor
) {
    public static DefectItemResponse from(DefectItem item) {
        return new DefectItemResponse(
                item.getId(),
                item.getDefect().getId(),
                item.getCode(),
                item.getNameEn(),
                item.getNameVi(),
                item.isAllowMinor(),
                item.isAllowMajor()
        );
    }
}
