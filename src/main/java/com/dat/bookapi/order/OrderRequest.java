package com.dat.bookapi;
import jakarta.validation.constraints.NotNull;

public class OrderRequest{
    @NotNull
    private Integer buyerId;
    @NotNull
    private Integer bookId;

    public OrderRequest(){}

    OrderRequest(Integer buyerId, Integer bookId){
        setBuyerId(buyerId);
        setBookId(bookId);
    }

    public void setBuyerId(Integer buyerId){
        this.buyerId = buyerId;
    }

    public void setBookId(Integer bookId){
        this.bookId = bookId;
    }

    public Integer getBuyerId(){
        return buyerId;
    }

    public Integer getBookId(){
        return bookId;
    }
}