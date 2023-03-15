package com.yuvaraj.blog.models.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "authority_tab")
@Access(value = AccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "at_id")
    private Long id;

    @Column(name = "at_name", nullable = false)
    private String name;

    @Column(name = "at_role", nullable = false)
    private String role;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "at_record_create_date", nullable = false, updatable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "at_record_update_date", nullable = false)
    private Date updatedDate;

    @Getter
    @AllArgsConstructor
    public enum Role {
        ROLE_CUSTOMER(1, "ROLE_CUSTOMER"),
        ROLE_SUPER_ADMIN(2, "ROLE_SUPER_ADMIN");

        private final long id;
        private final String role;
    }
}
