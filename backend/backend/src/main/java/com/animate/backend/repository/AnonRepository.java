package com.animate.backend.repository;

import com.animate.backend.model.AnonToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnonRepository extends CrudRepository<AnonToken, UUID> {

    Optional<AnonToken> findByAnonToken(String anonToken);

}