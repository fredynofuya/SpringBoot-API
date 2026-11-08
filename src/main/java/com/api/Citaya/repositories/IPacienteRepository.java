
package com.api.Citaya.repositories;

import com.api.Citaya.models.PacienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPacienteRepository extends JpaRepository<PacienteModel, Integer> {

    Optional<PacienteModel> findByDocumento(String documento);

    // Obtener únicamente pacientes activos
    List<PacienteModel> findByActivoTrue();

    // Buscar un paciente por ID únicamente si está activo
    Optional<PacienteModel> findByIdAndActivoTrue(int id);
}

