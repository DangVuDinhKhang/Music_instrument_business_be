package com.thesis.business.musicinstrument.cart_product;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CartProductRepository implements PanacheRepository<CartProduct>{
    
}
