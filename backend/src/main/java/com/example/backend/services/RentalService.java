package com.example.backend.services;

import com.example.backend.models.Rental;
import com.example.backend.repositories.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository repository;

    public Rental create(Rental rental) {
        return repository.save(rental);
    }

    public List<Rental> getAll() {
        return repository.findAll();
    }

    public Rental getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
    }

    public Rental update(Integer id, Rental rental) {
        rental.setId(id);
        return repository.save(rental);
    }
}
