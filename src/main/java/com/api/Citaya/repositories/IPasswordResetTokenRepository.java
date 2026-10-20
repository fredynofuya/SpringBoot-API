package com.api.Citaya.repositories;

import com.api.Citaya.models.PasswordResetTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenModel, Integer> {
    Optional<PasswordResetTokenModel> findByTokenHash(String tokenHash);
    List<PasswordResetTokenModel> findByUsuarioIdAndUsadoEnIsNull(int usuarioId);
}