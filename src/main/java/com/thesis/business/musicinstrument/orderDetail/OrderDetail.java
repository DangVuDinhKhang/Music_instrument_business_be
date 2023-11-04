package com.thesis.business.musicinstrument.orderDetail;

import com.thesis.business.musicinstrument.order.CustomerOrder;

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
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total")
    private Long total;

    @ManyToOne
    @JoinColumn(name = "customerOrder_id")
    private CustomerOrder customerOrder;

}
