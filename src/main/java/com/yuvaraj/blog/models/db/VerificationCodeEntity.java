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

import static com.yuvaraj.blog.helpers.DateHelpers.*;

@Entity(name = "VerificationCodeEntity")
@Table(name = "verification_code_tab")
@Access(value = AccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class VerificationCodeEntity implements Serializable {

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

    //Defaylt Value
    @Transient
    private int maxRetries = 3;
    @Transient
    private int resendRequestAfterCertainSeconds = 3 * 60;
    @Transient
    private int requestUnlockInSeconds = 3 * 60;

    public boolean isExpired() {
        return new Date().after(expiryDate);
    }

    public boolean isMaxReached() {
        int futureResendRetries = this.resendRetries + 1;
        boolean check = (this.resendRetries + 1) > this.maxRetries;
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
        Date timeToUnlock = dateAddSeconds(this.createdDate, this.requestUnlockInSeconds);
        boolean isRequestUnlocked = nowDate.after(timeToUnlock);
        log.info("[{}]: Still have {} minutes to unlock", this.getIdentifier(), getMinutesLeft(nowDate, timeToUnlock));
        return isRequestUnlocked;
    }

    public boolean isItEligibleToResendVerification() {
        Date nowDate = nowDate();
        Date timeToUnlock = dateAddSeconds(this.createdDate, resendRequestAfterCertainSeconds);
        boolean isItEligibleToResendVerificationCheck = nowDate.after(timeToUnlock);
        log.info("[{}]: Still have {} minutes to resend", this.getIdentifier(), getMinutesLeft(nowDate, timeToUnlock));
        return isItEligibleToResendVerificationCheck;
    }

    public void initializeVariables(int maxRetries, int resendRequestAfterCertainSeconds, int requestUnlockInSeconds) {
        this.maxRetries = maxRetries;
        this.resendRequestAfterCertainSeconds = resendRequestAfterCertainSeconds;
        this.requestUnlockInSeconds = requestUnlockInSeconds;
    }

    public Date calculateExpiryDate() {
        return nowDateAddSeconds(this.resendRequestAfterCertainSeconds);
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        SIGN_UP_ACTIVATION("SIGN_UP_ACTIVATION"),
        FORGOT_PASSWORD("FORGOT_PASSWORD");

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
