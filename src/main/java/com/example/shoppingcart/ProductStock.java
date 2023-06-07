package com.example.shoppingcart;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ProductStock(Integer quantity){
    public static ProductStock empty(){
        return new ProductStock(null);
    }
    @JsonIgnore
    public boolean isEmpty(){
        return quantity == null;
    }
}
