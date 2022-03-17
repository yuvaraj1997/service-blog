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

@Entity
@Table(name = "customer_tab")
@Access(value=AccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEntity implements Serializable {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid2")
    @Column(name = "ct_id", nullable = false)
    private String id;

    @Column(name = "ct_type", nullable = false)
    private String type;

    @Column(name = "ct_subtype", nullable = false)
    private String subtype;

    @Column(name = "ct_preferred_name")
    private String preferredName;

    @Column(name = "ct_full_name")
    private String fullName;

    @Column(name = "ct_email")
    private String email;

    @Column(name = "ct_msisdn")
    private String msisdn;

    @Column(name = "ct_customer_created_date")
    private Date customerCreatedDate;

    @Column(name = "ct_merchant_created_date")
    private Date merchantCreatedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ct_authority_id")
    private AuthorityEntity authorityEntity;

    @Column(name = "ct_status")
    private String status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ct_record_create_date")
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ct_record_update_date")
    private Date updatedDate;

    @Getter
    @AllArgsConstructor
    public enum Type {
        CUSTOMER("CUSTOMER");

        private final String type;
    }

    @Getter
    @AllArgsConstructor
    public enum SubType {
        NA("NA");

        private final String subType;
    }


    @Getter
    @AllArgsConstructor
    public enum Status {
        INITIATED("INITIATED"),
        SUBMITTED("SUBMITTED"),
        CONFIRMED("CONFIRMED"),
        VERIFICATION_PENDING("VERIFICATION_PENDING"),
        SUCCESS("SUCCESS");

        private final String status;
    }

}
