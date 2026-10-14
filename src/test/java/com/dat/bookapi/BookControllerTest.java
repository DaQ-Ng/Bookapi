package com.dat.bookapi;

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

@WebMvcTest(BookController.class)
class BookControllerTest{
    @Autowired
    MockMvc mockMvc; //Spring hands you the fake HTTP client;

    @MockitoBean
    BookService bookService; //mock service, injected into the controller

    @Test
    void getMissingReturn404() throws Exception{
        // 1) rule: the service returns null for id 99
        when(bookService.getBookById(99)).thenReturn(null);

        mockMvc.perform(get("/books/99")).andExpect(status().isNotFound()); 
    }

    @Test
    void postBlankTitleReturn400() throws Exception{
        // """ == test block
        String json ="""
            {"title": "", "author": "A", "year": 2020, "price": 9.99, "available": true}
        """;

        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());

        verify(bookService,never()).addBook(any());
    }
}