package com.thesis.business.musicinstrument.product;

import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Transactional
    Long add(Product product) {

        productRepository.persist(product);
        return product.getId();
    }

    List<Product> findAll() {

        return productRepository.listAll();
    }

    @Transactional
    void updateById(Long id, Product product) {

        Product productInDB = productRepository.findById(id);
        if(productInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");

        productInDB.setName(product.getName());
        productInDB.setDescription(product.getDescription());
        productInDB.setPrice(product.getPrice());
        productInDB.setAmount(product.getAmount());
        productRepository.persist(productInDB);
    }

    @Transactional
    void deleteById(Long id){
        
        if(productRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
    }

}
