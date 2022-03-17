package com.yuvaraj.blog.services;

import com.yuvaraj.blog.exceptions.signup.CustomerAlreadyExistException;
import com.yuvaraj.blog.models.controllers.v1.signup.PostSignUpRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.PostSignUpResponse;
import com.yuvaraj.blog.models.db.AuthorityEntity;

/**
 *
 */
public interface AuthorityService {

    AuthorityEntity getById(Long id);
}
