package com.thesis.business.musicinstrument.payment;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class PaymentRepository implements PanacheRepository<Payment> {
    
}
