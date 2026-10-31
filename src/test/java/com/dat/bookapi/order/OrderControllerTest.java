package com.dat.bookapi.order;

import com.dat.bookapi.book.Book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@WebMvcTest(OrderController.class)
class OrderControllerTest{
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrderService orderService;

    @Test
    void postOrderLostRaceReturn409() throws Exception{
        when(orderService.placeOrder(any())).thenThrow(new ObjectOptimisticLockingFailureException(Book.class, 6));

        String json = """
            {"buyerId": 2, "bookId": 6}
        """;

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isConflict());
    }
}