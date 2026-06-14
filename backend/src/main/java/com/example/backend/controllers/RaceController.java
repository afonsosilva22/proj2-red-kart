package com.example.backend.controllers;

import com.example.backend.models.Race;
import com.example.backend.services.RaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/races")
@RequiredArgsConstructor
@CrossOrigin(origins ="*")
public class RaceController {

    private final RaceService service;

    @PostMapping("/create")
    public ResponseEntity<Race> create(@RequestBody Race race) {
        if (race.getRaceKarts() != null) {
            race.getRaceKarts().forEach(rk -> {
                rk.setRace(race);

                // Re-link the Laps to the RaceKart
                if (rk.getLaps() != null && !rk.getLaps().isEmpty()) {
                    System.out.println("SUCCESS: Received " + rk.getLaps().size() + " laps for Kart #" + rk.getKart().getId());
                    rk.getLaps().forEach(lap -> lap.setRaceKart(rk));
                } else {
                    System.out.println("WARNING: 0 Laps received for Kart #" + rk.getKart().getId());
                }
            });
        }

        if (race.getRaceEquipments() != null) {
            race.getRaceEquipments().forEach(re -> re.setRace(race));
        }

        Race saved = service.create(race);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<Race>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Race> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }
}
