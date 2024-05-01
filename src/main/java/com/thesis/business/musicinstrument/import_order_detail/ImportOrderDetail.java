package com.thesis.business.musicinstrument.import_order_detail;

import com.thesis.business.musicinstrument.import_order.ImportOrder;
import com.thesis.business.musicinstrument.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ImportOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "sold_quantity")
    private Integer soldQuantity;

    @Column(name = "price")
    private Integer price;

    @Column(name = "total")
    private Long total;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "importOder_id")
    private ImportOrder importOrder;
}
