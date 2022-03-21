package com.yuvaraj.blog.repositories;

import com.yuvaraj.blog.models.db.VerificationCodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface VerificationCodeRepository extends JpaRepository<VerificationCodeEntity, String> {

    @Query(value = "SELECT vct FROM VerificationCodeEntity vct WHERE vct.identifier = ?1 and vct.type = ?2")
    Page<VerificationCodeEntity> findLatestByIdentifierAndType(String identifier, String type, Pageable pageable);

    @Query(value = "SELECT vct FROM VerificationCodeEntity vct WHERE vct.id = ?1 and vct.identifier = ?2")
    VerificationCodeEntity findByIdAndIdentifier(String id, String identifier);
}
