package com.thesis.business.musicinstrument.rating;

import java.time.LocalDate;
import java.util.List;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.account.AccountService;
import com.thesis.business.musicinstrument.order.CustomerOrderService;
import com.thesis.business.musicinstrument.orderDetail.OrderDetail;
import com.thesis.business.musicinstrument.orderDetail.OrderDetailService;
import com.thesis.business.musicinstrument.product.ProductService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class RatingService {

    @Inject
    RatingRepository ratingRepository;

    @Inject
    ProductService productService;

    @Inject
    AccountService accountService;

    @Inject
    CustomerOrderService customerOrderService;

    @Inject 
    OrderDetailService orderDetailService;

    @Transactional
    public Long add(Rating rating, String username, String role) {

        if(rating.getStar() < 0 || rating.getStar() > 5)
            throw new MusicInstrumentException(Response.Status.BAD_REQUEST, "Star must greater than 0 and lower than 5");
        if(accountService.findById(rating.getAccount().getId(), username, role) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");
        if(productService.findById(rating.getProduct().getId()) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");
        OrderDetail orderDetail = orderDetailService.findByProductIdAndListOfOrder(rating.getProduct().getId(), 
            customerOrderService.findByAccountId(rating.getAccount().getId()));
        if(orderDetail == null || orderDetail.getCustomerOrder().getStatus() != 2)
            throw new MusicInstrumentException(Response.Status.BAD_REQUEST, "You must purchase this product before rating");

        rating.setDate(LocalDate.now());
        ratingRepository.persist(rating);
        return rating.getId();
    }

    public List<Rating> findByProductId(Long productId) {

        if(productService.findById(productId) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");

        return ratingRepository.list("product.id", productId);
    }

    public List<Rating> findByAccountId(Long accountId, String username, String role) {

        if(accountService.findById(accountId, username, role) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");

        return ratingRepository.list("account.id", accountId);
    }
    

    public Rating findById(Long id){
        return ratingRepository.findById(id);
    }


    @Transactional
    public void deleteById(Long id){
        
        if(ratingRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Rating does not exist");
    }

}
