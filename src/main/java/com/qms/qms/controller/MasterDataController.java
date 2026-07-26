package com.qms.qms.controller;

import com.qms.qms.dto.master.*;
import com.qms.qms.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/master")
public class MasterDataController {

    private final StaffRepository staffRepository;
    private final FactoryRepository factoryRepository;
    private final LineRepository lineRepository;
    private final ProductionGroupRepository productionGroupRepository;
    private final CustomerRepository customerRepository;
    private final GarmentTypeRepository garmentTypeRepository;
    private final GarmentLocationRepository garmentLocationRepository;
    private final DefectRepository defectRepository;
    private final DefectItemRepository defectItemRepository;

    public MasterDataController(StaffRepository staffRepository,
                                 FactoryRepository factoryRepository,
                                 LineRepository lineRepository,
                                 ProductionGroupRepository productionGroupRepository,
                                 CustomerRepository customerRepository,
                                 GarmentTypeRepository garmentTypeRepository,
                                 GarmentLocationRepository garmentLocationRepository,
                                 DefectRepository defectRepository,
                                 DefectItemRepository defectItemRepository) {
        this.staffRepository = staffRepository;
        this.factoryRepository = factoryRepository;
        this.lineRepository = lineRepository;
        this.productionGroupRepository = productionGroupRepository;
        this.customerRepository = customerRepository;
        this.garmentTypeRepository = garmentTypeRepository;
        this.garmentLocationRepository = garmentLocationRepository;
        this.defectRepository = defectRepository;
        this.defectItemRepository = defectItemRepository;
    }

    @GetMapping("/staff")
    public List<StaffResponse> staff() {
        return staffRepository.findAll().stream().map(StaffResponse::from).toList();
    }

    @GetMapping("/factories")
    public List<FactoryResponse> factories() {
        return factoryRepository.findAll().stream().map(FactoryResponse::from).toList();
    }

    @GetMapping("/lines")
    public List<LineResponse> lines(@RequestParam(required = false) Long factoryId) {
        var lines = factoryId != null ? lineRepository.findByFactoryId(factoryId) : lineRepository.findAll();
        return lines.stream().map(LineResponse::from).toList();
    }

    @GetMapping("/groups")
    public List<ProductionGroupResponse> groups(@RequestParam(required = false) Long lineId) {
        var groups = lineId != null ? productionGroupRepository.findByLineId(lineId) : productionGroupRepository.findAll();
        return groups.stream().map(ProductionGroupResponse::from).toList();
    }

    @GetMapping("/customers")
    public List<CustomerResponse> customers() {
        return customerRepository.findAll().stream().map(CustomerResponse::from).toList();
    }

    @GetMapping("/garment-types")
    public List<GarmentTypeResponse> garmentTypes() {
        return garmentTypeRepository.findAll().stream().map(GarmentTypeResponse::from).toList();
    }

    @GetMapping("/garment-locations")
    public List<GarmentLocationResponse> garmentLocations(@RequestParam(required = false) Long garmentTypeId) {
        var locations = garmentTypeId != null
                ? garmentLocationRepository.findByGarmentTypeId(garmentTypeId)
                : garmentLocationRepository.findAll();
        return locations.stream().map(GarmentLocationResponse::from).toList();
    }

    @GetMapping("/defects")
    public List<DefectResponse> defects() {
        return defectRepository.findAll().stream().map(DefectResponse::from).toList();
    }

    @GetMapping("/defect-items")
    public List<DefectItemResponse> defectItems(@RequestParam(required = false) Long defectId) {
        var items = defectId != null ? defectItemRepository.findByDefectId(defectId) : defectItemRepository.findAll();
        return items.stream().map(DefectItemResponse::from).toList();
    }
}
