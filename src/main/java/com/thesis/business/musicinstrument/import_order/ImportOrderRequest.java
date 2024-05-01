package com.thesis.business.musicinstrument.import_order;

import java.util.List;

import com.thesis.business.musicinstrument.account.Account;
import com.thesis.business.musicinstrument.supplier.Supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportOrderRequest {
    
    private Long total;

    private Supplier supplier;

    private Account account;

    private List<ImportOrderProduct> products;
}
