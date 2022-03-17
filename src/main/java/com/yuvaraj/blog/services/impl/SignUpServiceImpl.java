package com.yuvaraj.blog.services.impl;

import com.yuvaraj.blog.exceptions.signup.CustomerAlreadyExistException;
import com.yuvaraj.blog.helpers.ErrorCode;
import com.yuvaraj.blog.models.controllers.v1.signup.PostSignUpRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.PostSignUpResponse;
import com.yuvaraj.blog.models.db.AuthorityEntity;
import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.services.AuthorityService;
import com.yuvaraj.blog.services.CustomerService;
import com.yuvaraj.blog.services.SignUpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yuvaraj.blog.helpers.DateHelper.convertDateForEndResult;

@Service
@Slf4j
@AllArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final CustomerService customerService;
    private final AuthorityService authorityService;

    @Override
    public PostSignUpResponse processPostSignUp(PostSignUpRequest postSignUpRequest) throws CustomerAlreadyExistException {
        checkIfUserAlreadyExist(postSignUpRequest.getEmailAddress());
        CustomerEntity customerEntity = getAnyExistingRecordIfAvailable(postSignUpRequest.getEmailAddress());
        if(null != customerEntity){
            if(customerEntity.getStatus().equals(CustomerEntity.Status.VERIFICATION_PENDING.getStatus())){
                //TODO: Verification part
                log.info("Verification Needed");
            }
            customerEntity.setStatus(CustomerEntity.Status.INITIATED.getStatus());
            customerEntity = customerService.update(customerEntity);
            return buildPostSignUpResponse(customerEntity);
        }
        customerEntity = initiateCustomerRecord(postSignUpRequest);
        return buildPostSignUpResponse(customerEntity);
    }

    private CustomerEntity initiateCustomerRecord(PostSignUpRequest postSignUpRequest) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setType(CustomerEntity.Type.CUSTOMER.getType());
        customerEntity.setSubtype(CustomerEntity.SubType.NA.getSubType());
        customerEntity.setEmail(postSignUpRequest.getEmailAddress());
        customerEntity.setAuthorityEntity(authorityService.getById(AuthorityEntity.Role.ROLE_CUSTOMER.getId()));
        customerEntity.setStatus(CustomerEntity.Status.INITIATED.getStatus());
        return customerService.save(customerEntity);
    }

    private PostSignUpResponse buildPostSignUpResponse(CustomerEntity customerEntity) {
        PostSignUpResponse postSignUpResponse = new PostSignUpResponse();
        postSignUpResponse.setCustomerId(customerEntity.getId());
        postSignUpResponse.setDateCreated(convertDateForEndResult(customerEntity.getCreatedDate()));
        postSignUpResponse.setDateUpdated(convertDateForEndResult(customerEntity.getUpdatedDate()));
        return postSignUpResponse;
    }

    private CustomerEntity getAnyExistingRecordIfAvailable(String emailAddress) {
        return customerService.findByEmailTypeSubtypeAndStatuses(
                emailAddress,
                CustomerEntity.Type.CUSTOMER.getType(),
                CustomerEntity.SubType.NA.getSubType(),
                List.of(
                        CustomerEntity.Status.INITIATED.getStatus(),
                        CustomerEntity.Status.SUBMITTED.getStatus(),
                        CustomerEntity.Status.CONFIRMED.getStatus(),
                        CustomerEntity.Status.VERIFICATION_PENDING.getStatus()
                )
        );
    }

    private void checkIfUserAlreadyExist(String emailAddress) throws CustomerAlreadyExistException {
        CustomerEntity customerEntity = customerService.findByEmailTypeSubtypeAndStatuses(
                emailAddress,
                CustomerEntity.Type.CUSTOMER.getType(),
                CustomerEntity.SubType.NA.getSubType(),
                List.of(CustomerEntity.Status.SUCCESS.getStatus())
                );
        if(null != customerEntity){
            log.info("User already exist emailAddress={}, type={}, subType={}, status={}",
                    emailAddress,
                    CustomerEntity.Type.CUSTOMER.getType(),
                    CustomerEntity.SubType.NA.getSubType(),
                    List.of(CustomerEntity.Status.SUCCESS.getStatus())
                    );
            throw new CustomerAlreadyExistException("User already exit", ErrorCode.CUSTOMER_ALREADY_EXIST);
        }
    }
}
