package com.yuvaraj.blog.repositories;

import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.models.db.PasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PasswordRepository extends JpaRepository<PasswordEntity, Long> {

    @Query("SELECT p FROM PasswordEntity p WHERE p.customerEntity = ?1")
    PasswordEntity findByCustomerEntity(CustomerEntity customerEntity);
}
