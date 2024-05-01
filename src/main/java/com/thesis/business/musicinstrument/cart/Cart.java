package com.thesis.business.musicinstrument.cart;

import com.thesis.business.musicinstrument.account.Account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    // @ManyToMany
    // @JoinTable(
    //     name = "cart_product",
    //     joinColumns = @JoinColumn(name = "cart_id"),
    //     inverseJoinColumns = @JoinColumn(name = "product_id")
    // )
    // @JsonbTransient
    // private Set<Product> products;

    // public void calculateAmount() {
    //     if (products != null && !products.isEmpty()) {
    //         int totalAmount = products.size();
    //         this.amount = totalAmount;
    //     } 
    //     else {
    //         this.amount = 0;
    //     }
    // }

    

}
