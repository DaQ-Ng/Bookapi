package com.dat.bookapi.order;

import com.dat.bookapi.book.Book;
import com.dat.bookapi.book.BookRepository;
import com.dat.bookapi.user.User;
import com.dat.bookapi.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class OrderService{
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, BookRepository bookRepository, UserRepository userRepository){
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    private User getUserById(int id){
        return userRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    private Book getBookById(int id){
        return bookRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found."));
    }

    public Order placeOrder(OrderRequest request){
        User buyer = getUserById(request.getBuyerId());
        Book book = getBookById(request.getBookId());

        if(!book.isAvailable()){
           throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already sold.");
        }
        else{
            book.setAvailable(false);
        }

        bookRepository.save(book);
        return orderRepository.save(new Order(buyer, book, book.getPrice()));
    }
}