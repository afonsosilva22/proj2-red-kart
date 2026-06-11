package com.example.backend.services;

import com.example.backend.models.Maintenance;
import com.example.backend.repositories.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository repository;

    public Maintenance create(Maintenance maintenance) {
        return repository.save(maintenance);
    }

    public List<Maintenance> getAll() {
        return repository.findAll();
    }

    public Maintenance getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));
    }

    public Maintenance update(Integer id, Maintenance maintenance) {
        maintenance.setId(id);
        return repository.save(maintenance);
    }
}
