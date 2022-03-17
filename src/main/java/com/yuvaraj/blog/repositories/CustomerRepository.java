package com.yuvaraj.blog.repositories;

import com.yuvaraj.blog.models.db.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

    @Query("SELECT c FROM CustomerEntity c WHERE c.email = ?1 and c.status = ?2")
    CustomerEntity findByEmailAndStatus(String email, String status);

    @Query("SELECT c FROM CustomerEntity c WHERE c.email = ?1 and c.type = ?2 and c.subtype = ?3 and c.status in ?4")
    CustomerEntity findByEmailTypeSubtypeAndStatuses(String email, String type, String subtype, List<String> status);
}
