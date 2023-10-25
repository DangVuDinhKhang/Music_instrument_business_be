package com.thesis.business.musicinstrument.orderDetail;

import com.thesis.business.musicinstrument.order.CustomerOrder;
import com.thesis.business.musicinstrument.product.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    
    private Long id;

    private Integer quantity;

    private Long total;

    private CustomerOrder customerOrder;

    private Product product;
}
