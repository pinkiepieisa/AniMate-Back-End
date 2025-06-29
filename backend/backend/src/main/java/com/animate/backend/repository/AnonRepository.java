package com.animate.backend.repository;

import com.animate.backend.model.AnonToken;
import com.animate.backend.model.Token;
import com.animate.backend.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnonRepository extends CrudRepository<User, Integer> {

    Optional<User> findById(Integer id);


    Integer Id(Integer id);
}
