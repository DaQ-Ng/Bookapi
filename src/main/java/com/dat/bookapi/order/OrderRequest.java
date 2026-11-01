package com.dat.bookapi.order;
import jakarta.validation.constraints.NotNull;

public class OrderRequest{
    @NotNull
    private Integer bookId;

    public OrderRequest(){}

    public OrderRequest(Integer bookId){
        setBookId(bookId);
    }

    public void setBookId(Integer bookId){
        this.bookId = bookId;
    }

    public Integer getBookId(){
        return bookId;
    }
}
