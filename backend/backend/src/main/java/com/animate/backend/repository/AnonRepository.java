package com.animate.backend.repository;

import com.animate.backend.model.AnonToken;
import com.animate.backend.model.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnonRepository extends CrudRepository<AnonToken, Integer> {

    Optional<AnonToken> findByAnonToken(String AnonToken);
}
