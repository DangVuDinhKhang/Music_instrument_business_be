package com.thesis.business.musicinstrument.product;

import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.category.CategoryService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject 
    CategoryService categoryService;

    @Transactional
    public Long add(Product product) {

        if(categoryService.findById(product.getCategory().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Category does not exist");

        productRepository.persist(product);
        return product.getId();
    }

    public List<Product> findAll() {

        return productRepository.listAll();
    }

    @Transactional
    public void updateById(Long id, Product product) {

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
    public void deleteById(Long id){
        
        if(productRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
    }

}
