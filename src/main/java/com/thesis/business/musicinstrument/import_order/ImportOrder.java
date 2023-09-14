package com.thesis.business.musicinstrument.import_order;

import java.time.LocalDate;

import com.thesis.business.musicinstrument.account.Account;
import com.thesis.business.musicinstrument.payment.Payment;
import com.thesis.business.musicinstrument.supplier.Supplier;

import jakarta.json.bind.annotation.JsonbDateFormat;
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
public class ImportOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    @JsonbDateFormat(value = "dd-MM-yyyy")
    private LocalDate date;

    @Column(name = "total")
    private Long total;

    @Column(name = "status")
    private Integer status;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public ImportOrder(Long id){
        this.id = id;
    }
}   
