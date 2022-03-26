package com.yuvaraj.blog.services.impl;

import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.repositories.CustomerRepository;
import com.yuvaraj.blog.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerEntity save(CustomerEntity customerEntity) {
        return customerRepository.save(customerEntity);
    }

    @Override
    public CustomerEntity update(CustomerEntity customerEntity) {
        return customerRepository.saveAndFlush(customerEntity);
    }

    @Override
    public CustomerEntity findById(String id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public CustomerEntity findByEmailTypeSubtypeAndStatuses(String email, String type, String subtype, List<String> status) {
        return customerRepository.findByEmailTypeSubtypeAndStatuses(email, type, subtype, status);
    }

    @Override
    public CustomerEntity findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}
