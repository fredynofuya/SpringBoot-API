package com.api.Citaya.repositories;

import com.api.Citaya.models.MedicoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio de médicos, extiende de JpaRepository para heredar los métodos CRUD básicos para manejar
// los médicos registrados en la base de datos, se encarga de interactuar con la base de datos para
// realizar las operaciones CRUD
@Repository
public interface IMedicoRepository extends JpaRepository <MedicoModel, Integer> {
    // Query personalizada para buscar un médico por su id de usuario, devuelve un Optional que puede
    // contener el médico encontrado o estar vacío si no se encuentra ningún médico con ese id de usuario
    @Query("SELECT m FROM MedicoModel m WHERE m.id_usuario = :idUsuario")
    Optional<MedicoModel> buscarPorUsuario(@Param("idUsuario") int idUsuario);

}
