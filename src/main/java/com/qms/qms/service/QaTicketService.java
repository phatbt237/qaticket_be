package com.qms.qms.service;

import com.qms.qms.dto.CursorPageResponse;
import com.qms.qms.dto.ticket.*;
import com.qms.qms.entity.*;
import com.qms.qms.entity.enums.TicketStatus;
import com.qms.qms.exception.InvalidTicketStateException;
import com.qms.qms.exception.ResourceNotFoundException;
import com.qms.qms.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class QaTicketService {

    private final QaTicketRepository qaTicketRepository;
    private final StaffRepository staffRepository;
    private final FactoryRepository factoryRepository;
    private final LineRepository lineRepository;
    private final ProductionGroupRepository productionGroupRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CustomerRepository customerRepository;
    private final GarmentTypeRepository garmentTypeRepository;
    private final GarmentLocationRepository garmentLocationRepository;
    private final DefectRepository defectRepository;
    private final TicketCodeGenerator ticketCodeGenerator;

    public QaTicketService(QaTicketRepository qaTicketRepository,
                            StaffRepository staffRepository,
                            FactoryRepository factoryRepository,
                            LineRepository lineRepository,
                            ProductionGroupRepository productionGroupRepository,
                            PurchaseOrderRepository purchaseOrderRepository,
                            CustomerRepository customerRepository,
                            GarmentTypeRepository garmentTypeRepository,
                            GarmentLocationRepository garmentLocationRepository,
                            DefectRepository defectRepository,
                            TicketCodeGenerator ticketCodeGenerator) {
        this.qaTicketRepository = qaTicketRepository;
        this.staffRepository = staffRepository;
        this.factoryRepository = factoryRepository;
        this.lineRepository = lineRepository;
        this.productionGroupRepository = productionGroupRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.customerRepository = customerRepository;
        this.garmentTypeRepository = garmentTypeRepository;
        this.garmentLocationRepository = garmentLocationRepository;
        this.defectRepository = defectRepository;
        this.ticketCodeGenerator = ticketCodeGenerator;
    }

    public QaTicketResponse create(QaTicketRequest request) {
        QaTicket ticket = new QaTicket();
        ticket.setTicketCode(ticketCodeGenerator.generate(qaTicketRepository.nextTicketSequence()));
        applyScalarFields(ticket, request);
        rebuildDefects(ticket, request);

        QaTicket saved = qaTicketRepository.save(ticket);
        return QaTicketResponse.from(saved);
    }

    public QaTicketResponse update(Long id, QaTicketRequest request) {
        QaTicket ticket = qaTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QA ticket not found: " + id));

        applyScalarFields(ticket, request);
        rebuildDefects(ticket, request);

        QaTicket saved = qaTicketRepository.save(ticket);
        return QaTicketResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public QaTicketResponse getById(Long id) {
        QaTicket ticket = qaTicketRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QA ticket not found: " + id));
        return QaTicketResponse.from(ticket);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<QaTicketSummaryResponse> list(Long factoryId, Long lineId, Long staffId, TicketStatus status,
                                                              Boolean exported, LocalDate dateFrom, LocalDate dateTo,
                                                              Long cursor, int size) {
        Specification<QaTicket> spec = Specification.allOf(
                QaTicketSpecifications.factoryId(factoryId),
                QaTicketSpecifications.lineId(lineId),
                QaTicketSpecifications.staffId(staffId),
                QaTicketSpecifications.status(status),
                QaTicketSpecifications.exported(exported),
                QaTicketSpecifications.dateFrom(dateFrom),
                QaTicketSpecifications.dateTo(dateTo),
                QaTicketSpecifications.idBefore(cursor)
        );
        // Keyset pagination ordered by id DESC: always fetches page 0 (LIMIT only, no OFFSET),
        // so performance stays constant regardless of how deep the client pages.
        var pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "id"));
        List<QaTicket> rows = qaTicketRepository.findAll(spec, pageable).getContent();

        boolean hasNext = rows.size() > size;
        List<QaTicket> page = hasNext ? rows.subList(0, size) : rows;
        Long nextCursor = hasNext ? page.get(page.size() - 1).getId() : null;

        return new CursorPageResponse<>(page.stream().map(QaTicketSummaryResponse::from).toList(), nextCursor, hasNext);
    }

    public void delete(Long id) {
        QaTicket ticket = qaTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QA ticket not found: " + id));
        if (ticket.getStatus() != TicketStatus.DRAFT) {
            throw new InvalidTicketStateException("Only DRAFT tickets can be deleted: " + id);
        }
        qaTicketRepository.delete(ticket);
    }

    public QaTicketResponse markExported(Long id) {
        QaTicket ticket = qaTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QA ticket not found: " + id));
        ticket.setExported(true);
        ticket.setExportedAt(LocalDateTime.now());
        return QaTicketResponse.from(qaTicketRepository.save(ticket));
    }

    public QaTicketResponse unmarkExported(Long id) {
        QaTicket ticket = qaTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QA ticket not found: " + id));
        ticket.setExported(false);
        ticket.setExportedAt(null);
        return QaTicketResponse.from(qaTicketRepository.save(ticket));
    }

    private void applyScalarFields(QaTicket ticket, QaTicketRequest request) {
        ticket.setStaff(getRef(staffRepository, request.staffId(), "Staff"));
        ticket.setFactory(getRef(factoryRepository, request.factoryId(), "Factory"));
        ticket.setLine(getRef(lineRepository, request.lineId(), "Line"));
        ticket.setGroup(request.groupId() != null ? getRef(productionGroupRepository, request.groupId(), "ProductionGroup") : null);
        ticket.setPurchaseOrder(request.poId() != null ? getRef(purchaseOrderRepository, request.poId(), "PurchaseOrder") : null);
        ticket.setCustomer(getRef(customerRepository, request.customerId(), "Customer"));
        ticket.setGarmentType(getRef(garmentTypeRepository, request.garmentTypeId(), "GarmentType"));
        ticket.setInspectionStage(request.inspectionStage());
        ticket.setInspectedQty(request.inspectedQty());
        ticket.setStatus(request.status());
    }

    private void rebuildDefects(QaTicket ticket, QaTicketRequest request) {
        ticket.getDefects().clear();
        if (request.defects() == null) {
            return;
        }
        for (QaTicketDefectRequest defectReq : request.defects()) {
            QaTicketDefect defect = new QaTicketDefect();
            defect.setDefect(getRef(defectRepository, defectReq.defectId(), "Defect"));
            defect.setNote(defectReq.note());
            ticket.addDefect(defect);

            if (defectReq.locations() != null) {
                for (QaTicketDefectLocationRequest locReq : defectReq.locations()) {
                    QaTicketDefectLocation location = new QaTicketDefectLocation();
                    location.setGarmentLocation(locReq.garmentLocationId() != null
                            ? getRef(garmentLocationRepository, locReq.garmentLocationId(), "GarmentLocation")
                            : null);
                    location.setLocationText(locReq.locationText());
                    location.setQuantity(locReq.quantity());
                    defect.addLocation(location);

                    if (locReq.images() != null) {
                        for (String imageUrl : locReq.images()) {
                            QaTicketDefectImage image = new QaTicketDefectImage();
                            image.setImageUrl(imageUrl);
                            location.addImage(image);
                        }
                    }
                }
            }
        }
    }

    private <T, ID> T getRef(org.springframework.data.repository.CrudRepository<T, ID> repository, ID id, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName + " not found: " + id));
    }
}
