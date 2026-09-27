package com.api.Citaya.repositories;

import com.api.Citaya.models.CitaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Repositorio de citas, extiende de JpaRepository para heredar los métodos CRUD básicos para manejar
// las citas registradas en la base de datos, se encarga de interactuar con la base de datos para realizar las operaciones CRUD
@Repository
public interface ICitaRepository extends JpaRepository<CitaModel, Integer> {

    // Método para buscar citas por estado, devuelve una lista de citas que coinciden con el estado especificado
    List<CitaModel> findByEstado(CitaModel.Estado estado);

    // Método para buscar citas por id de médico y fecha, devuelve una lista de citas que coinciden con el id del
    //  médico y la fecha especificada
    @Query("SELECT c FROM CitaModel c WHERE c.id_medico = :idMedico AND c.fecha = :fecha")
    List<CitaModel> findByIdMedicoAndFecha(@Param("idMedico") Integer idMedico, @Param("fecha") LocalDate fecha);

    // metodo para buscar citas por id de médico y estado, devuelve una lista de citas que coinciden con el id del
    //  médico y el estado especificado
    @Query("SELECT c FROM CitaModel c WHERE c.id_medico = :idMedico AND c.estado = :estado")
    List<CitaModel> findByIdMedicoAndEstado(@Param("idMedico") Integer idMedico, @Param("estado") CitaModel.Estado estado);

}