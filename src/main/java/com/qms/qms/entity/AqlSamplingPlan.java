package com.qms.qms.entity;

import com.qms.qms.entity.enums.AqlLevel;
import com.qms.qms.entity.enums.AqlLevelConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "aql_sampling_plan")
@Getter
@Setter
@NoArgsConstructor
public class AqlSamplingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = AqlLevelConverter.class)
    @Column(name = "aql_level", nullable = false, length = 10)
    private AqlLevel aqlLevel;

    @Column(name = "qty_min", nullable = false)
    private Integer qtyMin;

    @Column(name = "qty_max", nullable = false)
    private Integer qtyMax;

    @Column(name = "sampling_size", nullable = false)
    private Integer samplingSize;

    @Column(name = "major_accept", nullable = false)
    private Integer majorAccept;

    @Column(name = "major_reject", nullable = false)
    private Integer majorReject;

    @Column(name = "minor_accept", nullable = false)
    private Integer minorAccept;

    @Column(name = "minor_reject", nullable = false)
    private Integer minorReject;
}
