package com.thesis.business.musicinstrument.import_order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportOrderProduct {

    private Long id;
    
    private Integer quantity;

    private Integer price;

}
