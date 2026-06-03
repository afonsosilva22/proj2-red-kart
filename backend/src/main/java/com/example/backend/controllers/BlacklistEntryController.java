package com.example.backend.controllers;

import com.example.backend.models.BlacklistEntry;
import com.example.backend.services.BlacklistEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blacklist-entries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BlacklistEntryController {

    private final BlacklistEntryService service;

    @PostMapping("/create")
    public ResponseEntity<BlacklistEntry> create(@RequestBody BlacklistEntry blacklistEntry) {
        BlacklistEntry saved = service.create(blacklistEntry);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<BlacklistEntry>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<BlacklistEntry> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/reinstate/{customerId}")
    public ResponseEntity<Void> reinstate(@PathVariable Integer customerId) {
        service.reinstateCustomer(customerId);
        return ResponseEntity.ok().build();
    }
}
