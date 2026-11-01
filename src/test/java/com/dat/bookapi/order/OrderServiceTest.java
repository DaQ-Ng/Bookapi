package com.dat.bookapi.order;

import com.dat.bookapi.book.Book;
import com.dat.bookapi.book.BookRepository;
import com.dat.bookapi.user.User;
import com.dat.bookapi.user.UserRepository;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

class OrderServiceTest{

    @Test
    void placeOrderUnknownEmailThrows401(){
        OrderRepository orderRepo = mock(OrderRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        when(userRepo.findByEmailIgnoreCase("ghost@test.com")).thenReturn(Optional.empty()); // empty box
        OrderService service = new OrderService(orderRepo, mock(BookRepository.class), userRepo);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.placeOrder(new OrderRequest(1), "ghost@test.com"));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        verify(orderRepo, never()).save(any(Order.class));
    }

    @Test
    void placeOrderOwnBookThrows403(){
        OrderRepository orderRepo = mock(OrderRepository.class);
        BookRepository bookRepo = mock(BookRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        User tam = new User("Tam", "tam@test.com", "hash");
        Book book = new Book("Title", "Author", 2020, 9.99, true);
        book.setSeller(tam); // Tam owns this book
        when(userRepo.findByEmailIgnoreCase("tam@test.com")).thenReturn(Optional.of(tam));
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        OrderService service = new OrderService(orderRepo, bookRepo, userRepo);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.placeOrder(new OrderRequest(1), "tam@test.com"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertEquals(true, book.isAvailable()); // still for sale
        verify(orderRepo, never()).save(any(Order.class));
    }

    @Test
    void placeOrderBuyerComesFromEmail(){
        OrderRepository orderRepo = mock(OrderRepository.class);
        BookRepository bookRepo = mock(BookRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        User tam = new User("Tam", "tam@test.com", "hash");
        User minh = new User("Minh", "minh@test.com", "hash");
        Book book = new Book("Title", "Author", 2020, 9.99, true);
        book.setSeller(tam);
        when(userRepo.findByEmailIgnoreCase("minh@test.com")).thenReturn(Optional.of(minh));
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        when(orderRepo.save(any(Order.class))).thenAnswer(call -> call.getArgument(0)); // save hands back the order it was given
        OrderService service = new OrderService(orderRepo, bookRepo, userRepo);

        Order result = service.placeOrder(new OrderRequest(1), "minh@test.com");

        assertEquals(minh, result.getBuyer());
        assertEquals(9.99, result.getPrice(), 0.001);
        assertEquals(false, book.isAvailable());
    }

    @Test
    void placeOrderBookWithoutSellerDoesNotCrash(){
        OrderRepository orderRepo = mock(OrderRepository.class);
        BookRepository bookRepo = mock(BookRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        User minh = new User("Minh", "minh@test.com", "hash");
        Book oldBook = new Book("Dune", "Herbert", 1965, 9.99, true); // seller = null, like books made before auth
        when(userRepo.findByEmailIgnoreCase("minh@test.com")).thenReturn(Optional.of(minh));
        when(bookRepo.findById(6)).thenReturn(Optional.of(oldBook));
        when(orderRepo.save(any(Order.class))).thenAnswer(call -> call.getArgument(0));
        OrderService service = new OrderService(orderRepo, bookRepo, userRepo);

        Order result = service.placeOrder(new OrderRequest(6), "minh@test.com");

        assertEquals(minh, result.getBuyer());
    }
}
