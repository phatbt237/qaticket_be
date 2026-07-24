package com.qms.qms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "garment_location")
@Getter
@Setter
@NoArgsConstructor
public class GarmentLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garment_type_id", nullable = false)
    private GarmentType garmentType;

    @Column(nullable = false, length = 100)
    private String name;
}
