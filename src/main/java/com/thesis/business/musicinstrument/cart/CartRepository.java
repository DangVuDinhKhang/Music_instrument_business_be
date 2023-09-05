package com.thesis.business.musicinstrument.cart;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CartRepository implements PanacheRepository<Cart>{
    
}
