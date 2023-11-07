package com.thesis.business.musicinstrument.rating;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thesis.business.musicinstrument.MusicInstrumentException;
import com.thesis.business.musicinstrument.account.AccountService;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetail;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetailService;
import com.thesis.business.musicinstrument.order.CustomerOrder;
import com.thesis.business.musicinstrument.order.CustomerOrderService;
import com.thesis.business.musicinstrument.orderDetail.OrderDetail;
import com.thesis.business.musicinstrument.orderDetail.OrderDetailService;
import com.thesis.business.musicinstrument.order_receipt_link.OrderReceiptLink;
import com.thesis.business.musicinstrument.order_receipt_link.OrderReceiptLinkService;
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

    @Inject
    OrderReceiptLinkService orderReceiptLinkService;

    @Inject
    ImportOrderDetailService importOrderDetailService;

    @Transactional
    public Long add(Rating rating, String username, String role) {

        if(rating.getStar() < 0 || rating.getStar() > 5)
            throw new MusicInstrumentException(Response.Status.BAD_REQUEST, "Star must greater than 0 and lower than 5");
        if(accountService.findById(rating.getAccount().getId(), username, role) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");

        OrderDetail orderDetail = orderDetailService.findById(rating.getOrderDetail().getId());
        CustomerOrder customerOrder = customerOrderService.findById(orderDetail.getCustomerOrder().getId());
        if(customerOrder.getStatus() != 2)
            throw new MusicInstrumentException(Response.Status.BAD_REQUEST, "You must purchase this product before rating");

        rating.setDate(LocalDate.now());
        ratingRepository.persist(rating);
        return rating.getId();
    }

    public List<Rating> findByProductId(Long productId) {

        List<Rating> ratings = new ArrayList<>();

        if(productService.findById(productId) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Product does not exist");

        List<ImportOrderDetail> importOrderDetails = importOrderDetailService.findByProductId(productId);
        List<OrderReceiptLink> orderReceiptLinks = new ArrayList<>();
        for(ImportOrderDetail importOrderDetail : importOrderDetails){
            List<OrderReceiptLink> tempOrderReceiptLinks = orderReceiptLinkService.findAllByImportOrderDetailId(importOrderDetail.getId());
            for(int i = 0; i < tempOrderReceiptLinks.size(); i++){
                orderReceiptLinks.add(tempOrderReceiptLinks.get(i));
            }
        }

        Set<Long> tempSet = new HashSet<>();
        List<OrderReceiptLink> newOrderReceiptLinks = new ArrayList<>();
        for (OrderReceiptLink orderReceiptLink : orderReceiptLinks) {
            if (tempSet.add(orderReceiptLink.getOrderDetail().getId())) {
                newOrderReceiptLinks.add(orderReceiptLink);
            }
        }

        for(OrderReceiptLink newOrderReceiptLink : newOrderReceiptLinks){
            OrderDetail orderDetail = orderDetailService.findById(newOrderReceiptLink.getOrderDetail().getId());
            CustomerOrder customerOrder = customerOrderService.findById(orderDetail.getCustomerOrder().getId());
            if(customerOrder.getStatus() == 2){
                Rating rating = ratingRepository.find("orderDetail.id", orderDetail.getId()).firstResult();
                if(rating != null){
                    ratings.add(rating);
                }
            }
        }
        

        return ratings;
    }

    public List<Rating> findByAccountId(Long accountId, String username, String role) {

        if(accountService.findById(accountId, username, role) == null)
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Account does not exist");

        return ratingRepository.list("account.id", accountId);
    }
    

    public Rating findById(Long id){
        return ratingRepository.findById(id);
    }

    public Long statistic(){
        return ratingRepository.count();
    }


    @Transactional
    public void deleteById(Long id){
        
        if(ratingRepository.deleteById(id))
            return;
        else
            throw new MusicInstrumentException(Response.Status.NOT_FOUND, "Rating does not exist");
    }

}
