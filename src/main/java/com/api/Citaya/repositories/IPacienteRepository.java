package com.api.Citaya.repositories;

import com.api.Citaya.models.PacienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPacienteRepository extends JpaRepository <PacienteModel, Integer> {
}
