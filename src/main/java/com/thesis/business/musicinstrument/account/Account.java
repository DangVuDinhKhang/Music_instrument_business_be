package com.thesis.business.musicinstrument.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false, length = 10)
    private String role;

    @Column(name = "address", nullable = true, length = 255)
    private String address;

    @Column(name = "phone", nullable = true, length = 10)
    private String phone;

    @Column(name = "status", nullable = false)
    private Integer status;

    
}
