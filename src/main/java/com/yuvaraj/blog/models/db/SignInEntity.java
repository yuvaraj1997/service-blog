package com.yuvaraj.blog.models.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "SignInEntity")
@Table(name = "sign_in_tab")
@Access(value = AccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInEntity implements Serializable {

    public static final int MAX_SIGN_SESSION = 5;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "sit_id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sit_customer_id", referencedColumnName = "ct_id")
    private CustomerEntity customerEntity;

    @Column(name = "sit_refresh_token_generation_time", nullable = false)
    private long refreshTokenGenerationTime;

    @Column(name = "sit_device_name")
    private String deviceName;

    @Column(name = "sit_device_type", nullable = false)
    private String deviceType;

    @Column(name = "sit_device_subtype")
    private String deviceSubType;

    @Column(name = "sit_ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "sit_sign_in_date", nullable = false)
    private Date signInDate;

    @Column(name = "sit_sign_out_date")
    private Date signOutDate;

    @Column(name = "sit_status", nullable = false)
    private String status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sit_record_create_date", nullable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sit_record_update_date", nullable = false)
    private Date updatedDate;


    @Getter
    @AllArgsConstructor
    public enum Status {
        ACTIVE("ACTIVE"),
        FORCED_SIGN_OUT("FORCED_SIGN_OUT"),
        EXPIRED("EXPIRED");

        private final String status;
    }

}
