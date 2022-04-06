package com.yuvaraj.blog.models.inbuiltClass;

import com.yuvaraj.blog.models.signIn.SignInRequest;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUser extends User {

    private String customerId;
    private SignInRequest signInRequest;
    private String ipAddress;
    private String deviceName;
    private String deviceType;
    private String deviceSubtype;

    public CustomUser(String customerId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.customerId = customerId;
    }

    public CustomUser(String customerId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.customerId = customerId;
    }

    public CustomUser(String customerId, String ipAddress, String deviceName, String deviceType, String deviceSubtype, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.customerId = customerId;
        this.ipAddress = ipAddress;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceSubtype = deviceSubtype;
    }

    public CustomUser(SignInRequest signInRequest, String customerId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.signInRequest = signInRequest;
        this.customerId = customerId;
    }
}
