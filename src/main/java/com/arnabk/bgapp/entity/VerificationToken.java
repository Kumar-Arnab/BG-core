package com.arnabk.bgapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "verification_session")
@Data
@NoArgsConstructor
public class VerificationToken {

    // setting expiration time to 10 minutes
    private static final int EXPIRATION_TIME = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Column(name = "expiration_time", columnDefinition = "datetime")
    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "access_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_VERIFY_TOKEN"))
    private User user;

    public VerificationToken(User user, String token) {
        super();
        this.user = user;
        this.token = token;
        this.expirationTime = calculateExpirationDate();
    }

    public VerificationToken(String token) {
        super();
        this.token = token;
        this.expirationTime = calculateExpirationDate();
    }

    private Date calculateExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        // adding 10 to the current time ( Current time in Minute + 10) and return the new time as date.getTime()
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);

        return new Date(calendar.getTime().getTime());
    }
}
