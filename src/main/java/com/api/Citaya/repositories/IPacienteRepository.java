package com.api.Citaya.repositories;

import com.api.Citaya.models.PacienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio de pacientes, extiende de JpaRepository para heredar los métodos CRUD básicos para manejar
// los pacientes registrados en la base de datos, se encarga de interactuar con la base de datos para
// realizar las operaciones CRUD
@Repository
public interface IPacienteRepository extends JpaRepository <PacienteModel, Integer> {
    Optional<PacienteModel> findByDocumento(String documento);
}
