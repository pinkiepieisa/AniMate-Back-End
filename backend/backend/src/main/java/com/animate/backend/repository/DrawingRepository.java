package com.animate.backend.repository;

import com.animate.backend.model.Drawing;
import com.animate.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DrawingRepository extends JpaRepository<Drawing, UUID> {

    // Lista todos os desenhos de um usuário
    List<Drawing> findByOwnerOrderByUpdatedAtDesc(User owner);

    // Busca um desenho pelo id e dono (garante que o usuário só acessa o próprio)
    Optional<Drawing> findByIdAndOwner(UUID id, User owner);
}
