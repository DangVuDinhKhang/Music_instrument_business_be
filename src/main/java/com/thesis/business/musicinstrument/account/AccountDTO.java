package com.thesis.business.musicinstrument.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {

    private Long id;

    private String username;
 
    private String address;
    
    private String phone;

    private String role;

    private String token;
}
