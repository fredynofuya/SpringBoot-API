package com.api.Citaya.repositories;

import com.api.Citaya.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepository extends JpaRepository<UsuarioModel, Integer> {
}