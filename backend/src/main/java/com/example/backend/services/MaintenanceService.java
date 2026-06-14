package com.example.backend.services;

import com.example.backend.models.Maintenance;
import com.example.backend.repositories.MaintenanceRepository;
import com.example.backend.repositories.KartRepository;
import com.example.backend.repositories.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository repository;
    private final KartRepository kartRepository;
    private final TrackRepository trackRepository;

    @Transactional
    public Maintenance create(Maintenance maintenance) {
        if ("kart".equalsIgnoreCase(maintenance.getType()) && maintenance.getKart() != null) {
            kartRepository.save(maintenance.getKart());
        }

        else if ("track".equalsIgnoreCase(maintenance.getType()) && maintenance.getTrack() != null) {
            trackRepository.save(maintenance.getTrack());
        }

        return repository.save(maintenance);
    }

    public List<Maintenance> getAll() {
        return repository.findAll();
    }

    public Maintenance getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));
    }

    @Transactional
    public Maintenance update(Integer id, Maintenance maintenance) {
        maintenance.setId(id);

        if ("kart".equalsIgnoreCase(maintenance.getType()) && maintenance.getKart() != null) {
            kartRepository.save(maintenance.getKart());
        }
        else if ("track".equalsIgnoreCase(maintenance.getType()) && maintenance.getTrack() != null) {
            trackRepository.save(maintenance.getTrack());
        }

        return repository.save(maintenance);
    }
}