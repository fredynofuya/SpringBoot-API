package com.api.Citaya.repositories;

import com.api.Citaya.models.CitaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio de citas, extiende de JpaRepository para heredar los métodos CRUD básicos para manejar
// las citas registradas en la base de datos, se encarga de interactuar con la base de datos para realizar las operaciones CRUD
@Repository
public interface ICitaRepository extends JpaRepository<CitaModel, Integer> {
}
