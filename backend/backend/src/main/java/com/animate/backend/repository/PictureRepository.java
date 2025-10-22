package com.animate.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.animate.backend.model.ProfilePic;
import com.animate.backend.model.User;

public interface PictureRepository extends JpaRepository<ProfilePic, Long>{

    Optional<ProfilePic> findByUser(User user);
    
}
