package com.qms.qms.dto.master;

import com.qms.qms.entity.Defect;

public record DefectResponse(Long id, String code, String nameEn, String nameVi) {
    public static DefectResponse from(Defect d) {
        return new DefectResponse(d.getId(), d.getCode(), d.getNameEn(), d.getNameVi());
    }
}
