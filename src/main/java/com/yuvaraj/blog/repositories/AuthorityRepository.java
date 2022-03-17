package com.yuvaraj.blog.repositories;

import com.yuvaraj.blog.models.db.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {
}
