package com.animate.backend.repository;

import com.animate.backend.model.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TokenRepository
        extends CrudRepository<Token, Integer> {

    Optional<Token> findByToken(String Token);
}
