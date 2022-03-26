package com.yuvaraj.blog.services.impl;

import com.google.common.base.Preconditions;
import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.models.db.PasswordEntity;
import com.yuvaraj.blog.repositories.PasswordRepository;
import com.yuvaraj.blog.services.CustomerService;
import com.yuvaraj.blog.services.PasswordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final PasswordRepository passwordRepository;
    private final CustomerService customerService;

    @Override
    public void upsertPassword(String password, String customerId) {
        Preconditions.checkNotNull(password, "password cannot be null");
        Preconditions.checkNotNull(password, "customerId cannot be null");
        CustomerEntity customerEntity = customerService.findById(customerId);
        Preconditions.checkNotNull(customerEntity, "customerEntity could not be found customerId = " + customerId);
        PasswordEntity passwordEntity = getByCustomerEntity(customerEntity);
        if (null != passwordEntity) {
            passwordEntity.setPassword(password);
            passwordRepository.save(passwordEntity);
            return;
        }
        passwordEntity = new PasswordEntity();
        passwordEntity.setPassword(password);
        passwordEntity.setCustomerEntity(customerEntity);
        passwordEntity.setStatus(PasswordEntity.Status.ACTIVE.getStatus());
        passwordRepository.save(passwordEntity);
    }

    @Override
    public PasswordEntity getByCustomerEntity(CustomerEntity customerEntity) {
        return passwordRepository.findByCustomerEntity(customerEntity);
    }
}
