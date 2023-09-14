package com.thesis.business.musicinstrument.import_order;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ImportOrderRepository implements PanacheRepository<ImportOrder>{
    
}
