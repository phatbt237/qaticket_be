package com.qms.qms.controller;

import com.qms.qms.dto.CursorPageResponse;
import com.qms.qms.dto.ticket.QaTicketRequest;
import com.qms.qms.dto.ticket.QaTicketResponse;
import com.qms.qms.dto.ticket.QaTicketSummaryResponse;
import com.qms.qms.entity.enums.TicketStatus;
import com.qms.qms.security.StaffPrincipal;
import com.qms.qms.service.QaTicketService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/qa-tickets")
public class QaTicketController {

    private final QaTicketService qaTicketService;

    public QaTicketController(QaTicketService qaTicketService) {
        this.qaTicketService = qaTicketService;
    }

    @PostMapping
    public ResponseEntity<QaTicketResponse> create(@Valid @RequestBody QaTicketRequest request) {
        QaTicketResponse created = qaTicketService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public QaTicketResponse update(@PathVariable Long id, @Valid @RequestBody QaTicketRequest request) {
        return qaTicketService.update(id, request);
    }

    @GetMapping("/{id}")
    public QaTicketResponse getById(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        return qaTicketService.getById(id, principal.getStaff());
    }

    /**
     * Cursor (keyset) pagination ordered by id DESC (newest first): pass the previous
     * response's {@code nextCursor} back in as {@code cursor} to fetch the next page.
     * Omit {@code cursor} to get the first page.
     */
    @GetMapping
    public CursorPageResponse<QaTicketSummaryResponse> list(
            @RequestParam(required = false) Long factoryId,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Boolean exported,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @AuthenticationPrincipal StaffPrincipal principal) {
        return qaTicketService.list(factoryId, lineId, staffId, status, exported, dateFrom, dateTo, cursor, size,
                principal.getStaff());
    }

    @PatchMapping("/{id}/export")
    public QaTicketResponse markExported(@PathVariable Long id) {
        return qaTicketService.markExported(id);
    }

    @PatchMapping("/{id}/unexport")
    public QaTicketResponse unmarkExported(@PathVariable Long id) {
        return qaTicketService.unmarkExported(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        qaTicketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
