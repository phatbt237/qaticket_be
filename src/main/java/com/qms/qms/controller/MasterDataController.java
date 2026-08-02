package com.qms.qms.controller;

import com.qms.qms.dto.CursorPageResponse;
import com.qms.qms.dto.master.*;
import com.qms.qms.entity.Staff;
import com.qms.qms.repository.*;
import com.qms.qms.service.StaffSpecifications;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/master")
public class MasterDataController {

    private final StaffRepository staffRepository;
    private final FactoryRepository factoryRepository;
    private final LineRepository lineRepository;
    private final ProductionGroupRepository productionGroupRepository;
    private final GarmentTypeRepository garmentTypeRepository;
    private final GarmentLocationRepository garmentLocationRepository;
    private final DefectRepository defectRepository;
    private final DefectItemRepository defectItemRepository;

    public MasterDataController(StaffRepository staffRepository,
                                 FactoryRepository factoryRepository,
                                 LineRepository lineRepository,
                                 ProductionGroupRepository productionGroupRepository,
                                 GarmentTypeRepository garmentTypeRepository,
                                 GarmentLocationRepository garmentLocationRepository,
                                 DefectRepository defectRepository,
                                 DefectItemRepository defectItemRepository) {
        this.staffRepository = staffRepository;
        this.factoryRepository = factoryRepository;
        this.lineRepository = lineRepository;
        this.productionGroupRepository = productionGroupRepository;
        this.garmentTypeRepository = garmentTypeRepository;
        this.garmentLocationRepository = garmentLocationRepository;
        this.defectRepository = defectRepository;
        this.defectItemRepository = defectItemRepository;
    }

    /**
     * Cursor (keyset) pagination ordered by id DESC (newest first): pass the previous
     * response's {@code nextCursor} back in as {@code cursor} to fetch the next page.
     * Omit {@code cursor} to get the first page. {@code name} filters by fullName (contains, case-insensitive).
     */
    @GetMapping("/staff")
    public CursorPageResponse<StaffResponse> staff(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        Specification<Staff> spec = Specification.allOf(
                StaffSpecifications.fullNameContains(name),
                StaffSpecifications.idBefore(cursor)
        );
        var pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "id"));
        List<Staff> rows = staffRepository.findAll(spec, pageable).getContent();

        boolean hasNext = rows.size() > size;
        List<Staff> page = hasNext ? rows.subList(0, size) : rows;
        Long nextCursor = hasNext ? page.get(page.size() - 1).getId() : null;

        return new CursorPageResponse<>(page.stream().map(StaffResponse::from).toList(), nextCursor, hasNext);
    }

    @Cacheable("factories")
    @GetMapping("/factories")
    public List<FactoryResponse> factories() {
        return factoryRepository.findAll().stream().map(FactoryResponse::from).toList();
    }

    @Cacheable("lines")
    @GetMapping("/lines")
    public List<LineResponse> lines(@RequestParam(required = false) Long factoryId) {
        var lines = factoryId != null ? lineRepository.findByFactoryId(factoryId) : lineRepository.findAll();
        return lines.stream().map(LineResponse::from).toList();
    }

    @Cacheable("groups")
    @GetMapping("/groups")
    public List<ProductionGroupResponse> groups(@RequestParam(required = false) Long lineId) {
        var groups = lineId != null ? productionGroupRepository.findByLineId(lineId) : productionGroupRepository.findAll();
        return groups.stream().map(ProductionGroupResponse::from).toList();
    }

    @Cacheable("garmentTypes")
    @GetMapping("/garment-types")
    public List<GarmentTypeResponse> garmentTypes() {
        return garmentTypeRepository.findAll().stream().map(GarmentTypeResponse::from).toList();
    }

    @Cacheable("garmentLocations")
    @GetMapping("/garment-locations")
    public List<GarmentLocationResponse> garmentLocations(@RequestParam(required = false) Long garmentTypeId) {
        var locations = garmentTypeId != null
                ? garmentLocationRepository.findByGarmentTypeId(garmentTypeId)
                : garmentLocationRepository.findAll();
        return locations.stream().map(GarmentLocationResponse::from).toList();
    }

    @Cacheable("defects")
    @GetMapping("/defects")
    public List<DefectResponse> defects() {
        return defectRepository.findAll().stream().map(DefectResponse::from).toList();
    }

    @Cacheable("defectItems")
    @GetMapping("/defect-items")
    public List<DefectItemResponse> defectItems(@RequestParam(required = false) Long defectId) {
        var items = defectId != null ? defectItemRepository.findByDefectId(defectId) : defectItemRepository.findAll();
        return items.stream().map(DefectItemResponse::from).toList();
    }

}
