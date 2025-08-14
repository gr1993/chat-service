package com.example.chat_webflux.repository;

import com.example.chat_webflux.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    void save_findById_성공() {
        // given
        String userId = "abc";
        User user = new User(userId);

        // when
        userRepository.save(user).block();

        // then
        Mono<User> savedUser = userRepository.findById(userId);
        StepVerifier.create(savedUser)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void delete_성공() {
        // given
        String userId = "abc";
        User user = new User(userId);
        userRepository.save(user).block();

        // when
        userRepository.delete(userId).block();

        // then
        Mono<User> savedUser = userRepository.findById(userId);
        StepVerifier.create(savedUser)
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    void findAll_성공() {
        // given
        userRepository.save(new User("abc")).block();
        userRepository.save(new User("def")).block();

        // when
        Flux<User> users = userRepository.findAll();

        // then
        StepVerifier.create(users)
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

}
