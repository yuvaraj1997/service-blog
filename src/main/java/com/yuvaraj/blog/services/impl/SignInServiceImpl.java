package com.yuvaraj.blog.services.impl;

import com.google.common.base.Preconditions;
import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.models.db.PasswordEntity;
import com.yuvaraj.blog.services.CustomerService;
import com.yuvaraj.blog.services.PasswordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class SignInServiceImpl implements UserDetailsService {

    private final CustomerService customerService;
    private final PasswordService passwordService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("[{}]: Load User By Username: Attempting to login", email);
        CustomerEntity customerEntity = customerService.findByEmail(email);
        if (null == customerEntity) {
            log.info("[{}]:Load User By Username: Customer Not Found", email);
            throw new UsernameNotFoundException("Customer not found");
        }
        if (customerEntity.getStatus().equals(CustomerEntity.Status.SUCCESS.getStatus())) {
            PasswordEntity passwordEntity = passwordService.getByCustomerEntity(customerEntity);
            Preconditions.checkNotNull(passwordEntity, "Password table not found = " + email);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(customerEntity.getAuthorityEntity().getRole()));
            log.info("[{}]:Load User By Username: Success", email);
            //TODO: Inbuilt User got many implementations please try to take advantage of that
            return new User(customerEntity.getEmail(), passwordEntity.getPassword(), authorities);
        }
        //TODO: Handle properly
        log.info("[{}]:Load User By Username: Customer is not in {} status, userCurrentStatus={}", email, CustomerEntity.Status.SUCCESS.getStatus(), customerEntity.getStatus());
        throw new UsernameNotFoundException(" Customer is not in allowed status");
    }
}
