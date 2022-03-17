package com.yuvaraj.blog.services.impl;

import com.yuvaraj.blog.models.db.AuthorityEntity;
import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.repositories.AuthorityRepository;
import com.yuvaraj.blog.repositories.CustomerRepository;
import com.yuvaraj.blog.services.AuthorityService;
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
public class AuthorityServiceImpl implements AuthorityService {

   private final AuthorityRepository authorityRepository;

   @Override
   public AuthorityEntity getById(Long id) {
      return authorityRepository.getById(id);
   }
}
