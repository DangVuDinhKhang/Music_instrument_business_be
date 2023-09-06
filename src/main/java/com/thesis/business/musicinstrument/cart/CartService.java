package com.thesis.business.musicinstrument.cart;

import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.account.Account;
import com.thesis.business.musicinstrument.account.AccountService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class CartService {

    @Inject
    CartRepository cartRepository;
    
    @Inject
    AccountService accountService;

    @Transactional
    public Cart add(Account account) {

        if(accountService.findById(account.getId(), account.getUsername(), account.getRole()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");

        Cart cart = new Cart();
        cart.setAccount(account);
        
        cartRepository.persist(cart);
        return cart;
    }

    public List<Cart> findAll() {

        return cartRepository.listAll();
    }

    public Cart findById(Long id){

        Cart cart = cartRepository.findById(id);
        if(cart == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Cart does not exist");
        return cart;
    }

    public Cart findByAccountId(Long accountId){

        return cartRepository.find("account.id", accountId).singleResult();
    }

    @Transactional
    public void update(Cart cart){
        cartRepository.persist(cart);
    }

    public void deleteByID(Long id){
        
        if(cartRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Cart does not exist");
    }

    // public Category findById(Long id){
    //     return categoryRepository.findById(id);
    // }

    // @Transactional
    // public void updateById(Long id, Category category) {

    //     Category categoryInDB = categoryRepository.findById(id);
    //     if(categoryInDB == null)
    //         throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Category does not exist");

    //     categoryInDB.setName(category.getName());
    //     categoryRepository.persist(categoryInDB);
    // }

    // @Transactional
    // public void deleteById(Long id){
        
    //     if(categoryRepository.deleteById(id))
    //         return;
    //     else
    //         throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Category does not exist");
    // }

}
