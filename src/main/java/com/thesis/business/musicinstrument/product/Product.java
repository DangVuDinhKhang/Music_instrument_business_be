package com.thesis.business.musicinstrument.product;

import com.thesis.business.musicinstrument.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", columnDefinition="TEXT")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    //@ManyToMany(mappedBy = "products")
    //@JsonbTransient
    //private Set<Cart> carts;

    public Product(Long id){
        this.id = id;
    }

    public Product(String name, String description, Integer price, Integer quantity, Category category){
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    // public Long getId() {
    //     return id;
    // }

    // public void setId(Long id) {
    //     this.id = id;
    // }

    // public String getName() {
    //     return name;
    // }

    // public void setName(String name) {
    //     this.name = name;
    // }

    // public String getDescription() {
    //     return description;
    // }

    // public void setDescription(String description) {
    //     this.description = description;
    // }

    // public Integer getPrice() {
    //     return price;
    // }

    // public void setPrice(Integer price) {
    //     this.price = price;
    // }

    // public Integer getAmount() {
    //     return amount;
    // }

    // public void setAmount(Integer amount) {
    //     this.amount = amount;
    // }

    // public Category getCategory() {
    //     return category;
    // }

    // public void setCategory(Category category) {
    //     this.category = category;
    // }

    // public Set<Cart> getCarts() {
    //     return carts;
    // }

    // public void setCarts(Set<Cart> carts) {
    //     this.carts = carts;
    // }

    

}
