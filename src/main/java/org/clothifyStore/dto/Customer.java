package org.clothifyStore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Customer {
    private String id;
    private String name;
    private String address;
    private LocalDate dob;
    private String phoneNumber;
    private String gender;
    private String email;
    private String password;
    private boolean newsLetter;
}
