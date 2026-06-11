package com.example.backend.controllers;

import com.example.backend.models.Maintenance;
import com.example.backend.services.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
@CrossOrigin(origins ="*")
public class MaintenanceController {

    private final MaintenanceService service;

    @PostMapping("/create")
    public ResponseEntity<Maintenance> create(@RequestBody Maintenance maintenance) {
        Maintenance saved = service.create(maintenance);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<Maintenance>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Maintenance> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Maintenance> update(@PathVariable Integer id, @RequestBody Maintenance maintenance) {
        return ResponseEntity.ok(service.update(id, maintenance));
    }
}
