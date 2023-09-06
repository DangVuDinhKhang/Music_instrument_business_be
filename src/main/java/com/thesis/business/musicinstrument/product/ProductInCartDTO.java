package com.thesis.business.musicinstrument.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInCartDTO {
    
    private Product product;

    private Integer quantity;
}
