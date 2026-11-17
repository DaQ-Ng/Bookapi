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
import java.util.List;
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

    @Test
    void getMyOrdersAsksRepoForThisEmailOnly(){
        OrderRepository orderRepo = mock(OrderRepository.class);
        User minh = new User("Minh", "minh@test.com", "hash");
        Order order = new Order(minh, new Book("Title", "Author", 2020, 9.99, false), 9.99);
        when(orderRepo.findByBuyerEmailIgnoreCaseOrderByCreatedAtDesc("minh@test.com")).thenReturn(List.of(order));
        OrderService service = new OrderService(orderRepo, mock(BookRepository.class), mock(UserRepository.class));

        List<Order> result = service.getMyOrders("minh@test.com");

        assertEquals(List.of(order), result);
    }

    @Test
    void getMyOrdersNoOrdersReturnsEmptyList(){
        OrderRepository orderRepo = mock(OrderRepository.class);
        when(orderRepo.findByBuyerEmailIgnoreCaseOrderByCreatedAtDesc("tam@test.com")).thenReturn(List.of());
        OrderService service = new OrderService(orderRepo, mock(BookRepository.class), mock(UserRepository.class));

        List<Order> result = service.getMyOrders("tam@test.com");

        assertEquals(0, result.size()); // empty list, not null → controller sends 200 []
    }
}
