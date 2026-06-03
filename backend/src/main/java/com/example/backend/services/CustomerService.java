package com.example.backend.services;

import com.example.backend.models.Customer;
import com.example.backend.repositories.CustomerRepository;
import com.example.backend.repositories.BlacklistEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final BlacklistEntryRepository blacklistRepository;

    public Customer create(Customer customer) {
        return repository.save(customer);
    }

    public List<Customer> getAll() {
        return repository.findAll();
    }

    public Customer getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer update(Integer id, Customer customer) {
        customer.setId(id);
        return repository.save(customer);
    }

    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Customer not found with ID: " + id);
        }

        blacklistRepository.findAll().stream()
                .filter(entry -> entry.getCustomer().getId().equals(id))
                .forEach(entry -> blacklistRepository.deleteById(entry.getId()));

        repository.deleteById(id);
    }
}
