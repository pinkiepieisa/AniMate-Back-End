package com.animate.backend.repository;

import com.animate.backend.model.AnonToken;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ATokenRepository extends CrudRepository<AnonToken, Integer> {

    Optional<AnonToken> findByAnonToken(String AnonToken);
}
