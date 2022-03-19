package com.yuvaraj.blog.models.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static com.yuvaraj.blog.helpers.DateHelper.*;

@Entity(name = "VerificationCodeEntity")
@Table(name = "verification_code_tab")
@Access(value = AccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class VerificationCodeEntity implements Serializable {

    //TODO; Environemtn variables
    public static final int MAX_RETRIES = 3;
    public static final int RESEND_REQUEST_AFTER_CERTAIN_MINUTES = 3;
    public static final int REQUEST_UNLOCK_IN_MINUTES = 4;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "vct_id", nullable = false)
    private String id;

    @Column(name = "vct_identifier", nullable = false, updatable = false)
    private String identifier;

    @Column(name = "vct_type", nullable = false, updatable = false)
    private String type;

    @Column(name = "vct_code", updatable = false)
    private String code;

    @Column(name = "vct_expiry_date", updatable = false)
    private Date expiryDate;

    @Column(name = "vct_resend_retries", nullable = false, updatable = false)
    private int resendRetries;

    @Column(name = "vct_verified_date")
    private Date verifiedDate;

    @Column(name = "vct_status", nullable = false)
    private String status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vct_record_create_date")
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vct_record_update_date")
    private Date updatedDate;

    public boolean isExpired() {
        return new Date().after(expiryDate);
    }

    public boolean isMaxReached() {
        int futureResendRetries = this.resendRetries + 1;
        boolean check = (this.resendRetries + 1) > MAX_RETRIES;
        log.info("[{}]: Is Max Reached currentResendRetries={}, futureResendRetries={}, check={}", this.getIdentifier(), this.resendRetries, futureResendRetries, check);
        return check;
    }

    public int getNextResendRetriesCount() {
        if (isMaxReached()) {
            log.info("[{}]: Resetting to 1 since request lock is unlocked", this.getIdentifier());
            return 1;
        }
        return this.resendRetries + 1;
    }

    public boolean isRequestUnlocked() {
        Date nowDate = nowDate();
        Date timeToUnlock = dateAddMinutes(this.createdDate, REQUEST_UNLOCK_IN_MINUTES);
        boolean isRequestUnlocked = nowDate.after(timeToUnlock);
        log.info("[{}]: Still have {} minutes to unlock", this.getIdentifier(), getMinutesLeft(nowDate, timeToUnlock));
        return isRequestUnlocked;
    }

    public boolean isItEligibleToResendVerification() {
        Date nowDate = nowDate();
        Date timeToUnlock = dateAddMinutes(this.createdDate, RESEND_REQUEST_AFTER_CERTAIN_MINUTES);
        boolean isItEligibleToResendVerificationCheck = nowDate.after(timeToUnlock);
        log.info("[{}]: Still have {} minutes to resend", this.getIdentifier(), getMinutesLeft(nowDate, timeToUnlock));
        return isItEligibleToResendVerificationCheck;
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        SIGN_UP_ACTIVATION("SIGN_UP_ACTIVATION");

        private final String type;
    }

    //todo: implement scheduler to expire

    @Getter
    @AllArgsConstructor
    public enum Status {
        PENDING("PENDING"),
        USER_REQUESTED_AGAIN("USER_REQUESTED_AGAIN"),
        VERIFIED("VERIFIED"),
        EXPIRED("EXPIRED");

        private final String status;
    }

}
