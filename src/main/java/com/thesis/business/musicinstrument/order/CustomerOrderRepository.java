package com.thesis.business.musicinstrument.order;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CustomerOrderRepository implements PanacheRepository<CustomerOrder> {
    
}
