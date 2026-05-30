package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.common.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;
    private String phoneNumber;
    private String emergencyContact;
    private String comments;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean active;

    public static Patient create(
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender,
            String address,
            String phoneNumber,
            String emergencyContact,
            String comments
    ) {
        return Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .gender(gender)
                .address(address)
                .phoneNumber(phoneNumber)
                .emergencyContact(emergencyContact)
                .comments(comments)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    public void updateName(String firstName, String lastName) {
        if (firstName != null) {
            this.firstName = firstName;
        }

        if (lastName != null) {
            this.lastName = lastName;
        }
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void archive() {
        this.active = false;
    }
}
