package com.thesis.business.musicinstrument.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequest {

    private Long productId;
    
    private Long cartId;
}
