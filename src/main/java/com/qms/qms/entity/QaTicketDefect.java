package com.qms.qms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "qa_ticket_defect")
@Getter
@Setter
@NoArgsConstructor
public class QaTicketDefect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_ticket_id", nullable = false)
    private QaTicket qaTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defect_id", nullable = false)
    private Defect defect;

    @Column(length = 255)
    private String note;

    @OneToMany(mappedBy = "qaTicketDefect", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QaTicketDefectLocation> locations = new LinkedHashSet<>();

    public void addLocation(QaTicketDefectLocation location) {
        locations.add(location);
        location.setQaTicketDefect(this);
    }
}
