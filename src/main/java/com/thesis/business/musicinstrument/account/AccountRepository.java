package com.thesis.business.musicinstrument.account;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AccountRepository implements PanacheRepository<Account>{
    
}
