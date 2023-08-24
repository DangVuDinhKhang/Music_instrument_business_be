package com.thesis.business.musicinstrument.rating;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class RatingRepository implements PanacheRepository<Rating> {
    
}
