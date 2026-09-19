package com.trixi.demo.controller;

import com.trixi.demo.entities.Municipality;
import com.trixi.demo.repository.MunicipalityRepository;
import com.trixi.demo.service.XMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/municipalities")
public class DataController
{
    private final XMLService xmlService;
    @Autowired
    public DataController(XMLService xmlService) {
        this.xmlService = xmlService;
    }

    @GetMapping
    public Page<Municipality> getData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return xmlService.getMunicipalitiesPage(page, size);
    }

    @PostMapping("/load")
    public ResponseEntity<LoadResponse> loadData(@RequestBody LoadRequest request) {
        try {
            this.xmlService.AsyncDownloadAndSaveFromURL(request.url()).join();
            return ResponseEntity.ok(new LoadResponse(true, "XML data was loaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoadResponse(false, "Failed to load XML data: " + e.getMessage()));
        }
    }
}
