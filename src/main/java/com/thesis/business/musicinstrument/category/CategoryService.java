package com.thesis.business.musicinstrument.category;

import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;

    @Transactional
    Long add(Category category) {

        categoryRepository.persist(category);
        return category.getId();
    }

    List<Category> findAll() {

        return categoryRepository.listAll();
    }

    @Transactional
    void updateById(Long id, Category category) {

        Category categoryInDB = categoryRepository.findById(id);
        if(categoryInDB == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Category does not exist");

        categoryInDB.setName(category.getName());
        categoryRepository.persist(categoryInDB);
    }

    @Transactional
    void deleteById(Long id){
        
        if(categoryRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Category does not exist");
    }

}
