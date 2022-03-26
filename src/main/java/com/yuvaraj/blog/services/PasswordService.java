package com.yuvaraj.blog.services;

import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.models.db.PasswordEntity;

/**
 *
 */
public interface PasswordService {

    /**
     * @param password   String request
     * @param customerId String request
     */
    void upsertPassword(String password, String customerId);


    /**
     * @param customerEntity Object request
     * @return PasswordEntity
     */
    PasswordEntity getByCustomerEntity(CustomerEntity customerEntity);
}
