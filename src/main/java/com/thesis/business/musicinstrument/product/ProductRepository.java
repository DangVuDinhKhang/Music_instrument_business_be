package com.thesis.business.musicinstrument.product;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@RequestScoped
public class ProductRepository implements PanacheRepository<Product> {

    @Inject
    EntityManager entityManager;
    
    public List<Product> getProductsOrderedByName(Integer page, Integer pageSize) {
        return entityManager.createNativeQuery("SELECT * FROM product ORDER BY name COLLATE \"vi-VN-x-icu\" LIMIT " + pageSize + " OFFSET " + page, Product.class).getResultList();
    }
}
