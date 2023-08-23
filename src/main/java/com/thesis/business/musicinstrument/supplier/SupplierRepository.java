package com.thesis.business.musicinstrument.supplier;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class SupplierRepository implements PanacheRepository<Supplier> {
    
}
