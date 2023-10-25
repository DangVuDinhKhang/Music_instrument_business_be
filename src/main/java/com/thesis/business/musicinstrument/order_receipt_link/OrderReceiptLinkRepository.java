package com.thesis.business.musicinstrument.order_receipt_link;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class OrderReceiptLinkRepository implements PanacheRepository<OrderReceiptLink> {
    
}
