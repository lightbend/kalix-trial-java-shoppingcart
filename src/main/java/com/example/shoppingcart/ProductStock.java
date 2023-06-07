package com.example.shoppingcart;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ProductStock(Integer quantity){
    @JsonIgnore
    public static ProductStock empty(){
        return new ProductStock(null);
    }
    public boolean isEmpty(){
        return quantity == null;
    }
}
