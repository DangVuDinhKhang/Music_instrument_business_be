package com.thesis.business.musicinstrument.order_receipt_link;

import java.util.List;

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

    public OrderReceiptLink findOneByOrderDetailId(Long orderDetailId){
        return orderReceiptLinkRepository.find("orderDetail.id", orderDetailId).firstResult();
    }

    public List<OrderReceiptLink> findAllByOrderDetailId(Long orderDetailId){
        return orderReceiptLinkRepository.find("orderDetail.id", orderDetailId).list();
    }

    public OrderReceiptLink findOneByImportOrderDetailId(Long importOrderDetailId){
        return orderReceiptLinkRepository.find("importOrderDetail.id", importOrderDetailId).firstResult();
    }


    public List<OrderReceiptLink> findAllByImportOrderDetailId(Long importOrderDetailId){
        return orderReceiptLinkRepository.find("importOrderDetail.id", importOrderDetailId).list();
    }

    public void deleteByOrderDetailId(Long orderDetailId) {
        
        orderReceiptLinkRepository.delete("orderDetail.id", orderDetailId);
    }

}
