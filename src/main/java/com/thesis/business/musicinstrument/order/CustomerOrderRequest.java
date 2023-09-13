package com.thesis.business.musicinstrument.order;

import java.time.LocalDate;
import java.util.List;

import com.aayushatharva.brotli4j.common.annotations.Local;
import com.thesis.business.musicinstrument.account.Account;
import com.thesis.business.musicinstrument.payment.Payment;
import com.thesis.business.musicinstrument.product.ProductInCartDTO;

import jakarta.json.bind.annotation.JsonbDateFormat;
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
