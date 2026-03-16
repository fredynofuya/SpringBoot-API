package com.api.Citaya.repositories;

import com.api.Citaya.models.MedicoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio de médicos, extiende de JpaRepository para heredar los métodos CRUD básicos para manejar
// los médicos registrados en la base de datos, se encarga de interactuar con la base de datos para
// realizar las operaciones CRUD
@Repository
public interface IMedicoRepository extends JpaRepository <MedicoModel, Integer> {
}
