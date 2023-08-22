package com.thesis.business.musicinstrument.category;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CategoryRepository implements PanacheRepository<Category> {
    
}
