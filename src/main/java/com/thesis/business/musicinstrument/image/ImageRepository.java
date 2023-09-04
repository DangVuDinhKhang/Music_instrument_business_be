package com.thesis.business.musicinstrument.image;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ImageRepository implements PanacheRepository<Image> {
    
}
