package com.dat.bookapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}