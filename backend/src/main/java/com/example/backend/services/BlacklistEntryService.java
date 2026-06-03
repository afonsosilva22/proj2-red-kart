package com.example.backend.services;

import com.example.backend.models.BlacklistEntry;
import com.example.backend.models.Customer;
import com.example.backend.repositories.BlacklistEntryRepository;
import com.example.backend.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlacklistEntryService {

    private final BlacklistEntryRepository repository;
    private final CustomerRepository customerRepository;

    @Transactional
    public BlacklistEntry create(BlacklistEntry blacklistEntry) {

        if (blacklistEntry.getExitDate() == null) {

            Integer customerId = blacklistEntry.getCustomer().getId();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

            customer.setStatus("suspended");

            customerRepository.save(customer);

            blacklistEntry.setCustomer(customer);
        }

        return repository.save(blacklistEntry);
    }

    public List<BlacklistEntry> getAll() {
        return repository.findAll();
    }

    public BlacklistEntry getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("BlacklistEntry not found"));
    }

    @Transactional
    public void reinstateCustomer(Integer customerId) {
        BlacklistEntry activeEntry = repository.findAll().stream()
                .filter(entry -> entry.getCustomer().getId().equals(customerId) && entry.getExitDate() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active blacklist entry found for this customer"));

        activeEntry.setExitDate(LocalDate.now());
        repository.save(activeEntry);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setStatus("active");
        customerRepository.save(customer);
    }
}
