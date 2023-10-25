package com.thesis.business.musicinstrument.order_receipt_link;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class OrderReceiptLinkService {
    
    @Inject
    OrderReceiptLinkRepository orderReceiptLinkRepository;

    @Transactional
    public Long add(OrderReceiptLink orderReceiptLink){

        orderReceiptLinkRepository.persist(orderReceiptLink);
        return orderReceiptLink.getId();
    }

    public OrderReceiptLink findByOrderDetailId(Long orderDetailId){
        return orderReceiptLinkRepository.find("orderDetail.id", orderDetailId).firstResult();
    }

}
