package com.animate.backend.repository;

import com.animate.backend.model.LoggedUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<LoggedUser, Integer> {



    Optional<LoggedUser> findByEmail(String email);



}
