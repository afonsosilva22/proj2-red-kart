package com.example.backend.services;

import com.example.backend.models.Race;
import com.example.backend.models.RaceKart;
import com.example.backend.repositories.RaceRepository;
import com.example.backend.repositories.KartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceService {

    private final RaceRepository repository;
    private final KartRepository kartRepository;

    @Transactional
    public Race create(Race race) {
        if (race.getRaceKarts() != null) {
            for (RaceKart rk : race.getRaceKarts()) {
                if (rk.getKart() != null) {
                    kartRepository.save(rk.getKart());
                }
            }
        }

        return repository.save(race);
    }

    public List<Race> getAll() {
        return repository.findAll();
    }

    public Race getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Race not found"));
    }
}