package com.thesis.business.musicinstrument.order_receipt_link;

import java.time.LocalDate;

import com.thesis.business.musicinstrument.account.Account;
import com.thesis.business.musicinstrument.import_order_detail.ImportOrderDetail;
import com.thesis.business.musicinstrument.orderDetail.OrderDetail;
import com.thesis.business.musicinstrument.payment.Payment;

import jakarta.json.bind.annotation.JsonbDateFormat;
//import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderReceiptLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_detail_id")
    private OrderDetail orderDetail;

    @ManyToOne
    @JoinColumn(name = "import_order_detail_id")
    private ImportOrderDetail importOrderDetail;

    @Column(name = "quantity")
    private Integer quantity;

    public OrderReceiptLink(OrderDetail orderDetail, ImportOrderDetail importOrderDetail, Integer quantity) {
        this.orderDetail = orderDetail;
        this.importOrderDetail = importOrderDetail;
        this.quantity = quantity;
    }

}
