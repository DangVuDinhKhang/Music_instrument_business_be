package com.thesis.business.musicinstrument.order;

import java.util.List;

import com.thesis.business.musicinstrument.account.Account;
import com.thesis.business.musicinstrument.payment.Payment;
import com.thesis.business.musicinstrument.product.ProductInCartDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderRequest {

    private String phone;

    private String address;
    
    private Long total;

    private String note;

    private Payment payment;

    private Account account;

    private List<ProductInCartDTO> productsInCartDTO;
}
