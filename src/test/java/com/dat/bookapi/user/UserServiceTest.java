package com.dat.bookapi.user;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

class UserServiceTest{

    @Test
    void getByEmailReturnsThatUser(){
        UserRepository repo = mock(UserRepository.class);
        User tam = new User("Tam", "tam@test.com", "hash");
        when(repo.findByEmailIgnoreCase("tam@test.com")).thenReturn(Optional.of(tam));
        UserService service = new UserService(repo, mock(PasswordEncoder.class));

        assertEquals(tam, service.getByEmail("tam@test.com"));
    }

    @Test
    void getByEmailUnknownThrows401(){
        UserRepository repo = mock(UserRepository.class);
        when(repo.findByEmailIgnoreCase("ghost@test.com")).thenReturn(Optional.empty());
        UserService service = new UserService(repo, mock(PasswordEncoder.class));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.getByEmail("ghost@test.com"));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
