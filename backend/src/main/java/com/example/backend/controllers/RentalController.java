package com.example.backend.controllers;

import com.example.backend.models.Rental;
import com.example.backend.services.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
@CrossOrigin(origins ="*")
public class RentalController {

    private final RentalService service;

    @PostMapping("/create")
    public ResponseEntity<Rental> create(@RequestBody Rental rental) {
        Rental saved = service.create(rental);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<Rental>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Rental> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Rental> update(@PathVariable Integer id, @RequestBody Rental rental) {
        return ResponseEntity.ok(service.update(id, rental));
    }
}
