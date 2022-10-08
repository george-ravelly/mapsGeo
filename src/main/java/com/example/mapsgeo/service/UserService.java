package com.example.mapsgeo.service;

import com.example.mapsgeo.document.User;
import com.example.mapsgeo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public Mono<User> save(User user) {
        return repository.save(user);
    }

    public Mono<User> findById(String id) {
        return repository.findById(id);
    }
}
