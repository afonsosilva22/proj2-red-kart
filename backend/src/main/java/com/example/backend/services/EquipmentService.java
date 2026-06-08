package com.example.backend.services;

import com.example.backend.models.Equipment;
import com.example.backend.repositories.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository repository;

    public Equipment create(Equipment equipment) {
        return repository.save(equipment);
    }

    public List<Equipment> getAll() {
        return repository.findAll();
    }

    public Equipment getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
    }

    public Equipment update(Integer id, Equipment equipment) {
        equipment.setId(id);
        return repository.save(equipment);
    }
}