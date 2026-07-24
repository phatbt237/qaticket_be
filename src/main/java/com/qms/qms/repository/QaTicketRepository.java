package com.qms.qms.repository;

import com.qms.qms.entity.QaTicket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QaTicketRepository extends JpaRepository<QaTicket, Long>, JpaSpecificationExecutor<QaTicket> {

    @EntityGraph(attributePaths = {
            "staff", "factory", "line", "group", "purchaseOrder", "customer", "garmentType",
            "defects", "defects.defect", "defects.locations", "defects.locations.garmentLocation",
            "defects.locations.images"
    })
    Optional<QaTicket> findWithDetailsById(Long id);

    @Query(value = "SELECT nextval('qa_ticket_seq')", nativeQuery = true)
    long nextTicketSequence();
}
