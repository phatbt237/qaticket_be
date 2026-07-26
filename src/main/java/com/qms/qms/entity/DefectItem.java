package com.qms.qms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "defect_item")
@Getter
@Setter
@NoArgsConstructor
public class DefectItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defect_id", nullable = false)
    private Defect defect;

    @Column(unique = true, length = 50)
    private String code;

    @Column(name = "name_en", length = 150)
    private String nameEn;

    @Column(name = "name_vi", nullable = false, length = 150)
    private String nameVi;

    @Column(name = "allow_minor", nullable = false)
    private boolean allowMinor = true;

    @Column(name = "allow_major", nullable = false)
    private boolean allowMajor = true;
}
