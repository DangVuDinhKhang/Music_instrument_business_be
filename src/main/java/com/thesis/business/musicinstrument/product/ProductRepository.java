package com.thesis.business.musicinstrument.product;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ProductRepository implements PanacheRepository<Product> {
    
}
