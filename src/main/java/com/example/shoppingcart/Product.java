package com.example.shoppingcart;

public record Product(String name, int quantity) {
    public static Product empty(){
        return new Product(null,0);
    }
    public boolean isEmpty(){
        return name == null && quantity == 0;
    }
}
