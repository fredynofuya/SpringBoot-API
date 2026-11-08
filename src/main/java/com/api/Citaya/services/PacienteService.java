//package com.api.Citaya.services;
//
//import com.api.Citaya.models.PacienteModel;
//import com.api.Citaya.repositories.IPacienteRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Optional;
//// Servicio para manejar la lógica de negocio relacionada con los pacientes, se encarga de interactuar con el repositorio de pacientes para realizar las operaciones CRUD
//@Service
//public class PacienteService {
//
//    @Autowired
//    // Inyección de dependencias del repositorio de pacientes
//    IPacienteRepository pacienteRepository;
//
//    //Muestra todos los pacientes registrados en la base de datos
//    public ArrayList<PacienteModel> getPacientes() {
//        return (ArrayList<PacienteModel>) pacienteRepository.findAll();
//    }
//
//    //Muestra un paciente por id, devuelve un Optional<PacienteModel> para manejar el caso en el que no se encuentre el paciente
//    public Optional<PacienteModel> getById(int id) {
//        return pacienteRepository.findById(id);
//    }
//
//    //Muestra un paciente por documento, devuelve un Optional<PacienteModel> para manejar el caso en el que no se encuentre el paciente
//    public Optional<PacienteModel> getByDocumento(String documento) {
//        return pacienteRepository.findByDocumento(documento);
//    }
//
//    //Crea un nuevo paciente, devuelve el paciente creado
//    public PacienteModel postPaciente(PacienteModel paciente) {
//        boolean acepta = Boolean.TRUE.equals(paciente.getAcepta_notificaciones());
//        paciente.setAcepta_notificaciones(acepta);
//        paciente.setFecha_consentimiento(acepta ? LocalDateTime.now() : null);
//        return pacienteRepository.save(paciente);
//    }
//
//    //Elimina un paciente por id, devuelve un booleano indicando si se eliminó o no el paciente
//    public Boolean deletePaciente(int id) {
//        try {
//            pacienteRepository.deleteById(id);
//            return true;
//        } catch (Exception err) {
//            return false;
//        }
//    }
//
//    //Actualiza un paciente por id, devuelve el paciente actualizado
//    public PacienteModel putPaciente(PacienteModel request, int id) {
//        //Obtenemos el paciente por id
//        PacienteModel pacienteModel = pacienteRepository.findById(id).get();
//        //Actualizamos los campos del paciente, si el campo del request es null, se mantiene el valor original del paciente
//        pacienteModel.setNombre(request.getNombre() != null ? request.getNombre() : pacienteModel.getNombre());
//        pacienteModel.setEmail(request.getEmail() != null ? request.getEmail() : pacienteModel.getEmail());
//        pacienteModel.setTipo_documento(request.getTipo_documento() != null ? request.getTipo_documento() : pacienteModel.getTipo_documento());
//        pacienteModel.setDocumento(request.getDocumento() != null ? request.getDocumento() : pacienteModel.getDocumento());
//        pacienteModel.setTelefono(request.getTelefono() != null ? request.getTelefono() : pacienteModel.getTelefono());
//        pacienteModel.setEps(request.getEps() != null ? request.getEps() : pacienteModel.getEps());
//        //El campo fecha_registro se actualiza con la fecha actual, ya que se asume que el paciente se actualiza en el momento de la actualización
//        Boolean acepta = request.getAcepta_notificaciones();
//        if (acepta != null && !acepta.equals(pacienteModel.getAcepta_notificaciones())) {
//            pacienteModel.setAcepta_notificaciones(acepta);
//            pacienteModel.setFecha_consentimiento(acepta ? LocalDateTime.now() : null);
//        }
//
//        pacienteModel.setFecha_actualizacion(LocalDateTime.now());
//        //Guardamos el paciente actualizado en la base de datos y lo devolvemos
//        return pacienteRepository.save(pacienteModel);
//    }
//}


//package com.api.Citaya.services;
//
//import com.api.Citaya.models.PacienteModel;
//import com.api.Citaya.repositories.IPacienteRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class PacienteService {
//
//    @Autowired
//    private IPacienteRepository pacienteRepository;
//
//    // Obtener únicamente los pacientes activos
//    public List<PacienteModel> getPacientes() {
//        return pacienteRepository.findByActivoTrue();
//    }
//
//    // Obtener un paciente por ID, siempre que esté activo
//    public Optional<PacienteModel> getById(int id) {
//        return pacienteRepository.findByIdAndActivoTrue(id);
//    }
//
//    // Buscar paciente por documento
//    // Se consulta también a los pacientes inactivos para evitar
//    // crear registros duplicados.
//    public Optional<PacienteModel> getByDocumento(String documento) {
//        return pacienteRepository.findByDocumento(documento);
//    }
//
//    // Crear un nuevo paciente
//    public PacienteModel postPaciente(PacienteModel paciente) {
//
//        boolean acepta = Boolean.TRUE.equals(
//                paciente.getAcepta_notificaciones()
//        );
//
//        paciente.setAcepta_notificaciones(acepta);
//        paciente.setFecha_consentimiento(
//                acepta ? LocalDateTime.now() : null
//        );
//
//        // Todo paciente nuevo comienza activo
//        paciente.setActivo(true);
//
//        return pacienteRepository.save(paciente);
//    }
//
//    // Eliminación lógica de paciente
//    public Boolean deletePaciente(int id) {
//
//        Optional<PacienteModel> pacienteOpt =
//                pacienteRepository.findById(id);
//
//        if (pacienteOpt.isEmpty()) {
//            return false;
//        }
//
//        PacienteModel paciente = pacienteOpt.get();
//
//        // Si ya está inactivo, no es necesario modificarlo
//        if (Boolean.FALSE.equals(paciente.getActivo())) {
//            return true;
//        }
//
//        // Cambiar su estado sin eliminarlo de la BD
//        paciente.setActivo(false);
//        paciente.setFecha_actualizacion(LocalDateTime.now());
//
//        pacienteRepository.save(paciente);
//
//        return true;
//    }
//
//    // Actualizar un paciente por ID
//    public PacienteModel putPaciente(PacienteModel request, int id) {
//
//        PacienteModel pacienteModel = pacienteRepository
//                .findByIdAndActivoTrue(id)
//                .orElseThrow(() ->
//                        new RuntimeException("Paciente no encontrado o inactivo")
//                );
//
//        pacienteModel.setNombre(
//                request.getNombre() != null
//                        ? request.getNombre()
//                        : pacienteModel.getNombre()
//        );
//
//        pacienteModel.setEmail(
//                request.getEmail() != null
//                        ? request.getEmail()
//                        : pacienteModel.getEmail()
//        );
//
//        pacienteModel.setTipo_documento(
//                request.getTipo_documento() != null
//                        ? request.getTipo_documento()
//                        : pacienteModel.getTipo_documento()
//        );
//
//        pacienteModel.setDocumento(
//                request.getDocumento() != null
//                        ? request.getDocumento()
//                        : pacienteModel.getDocumento()
//        );
//
//        pacienteModel.setTelefono(
//                request.getTelefono() != null
//                        ? request.getTelefono()
//                        : pacienteModel.getTelefono()
//        );
//
//        pacienteModel.setEps(
//                request.getEps() != null
//                        ? request.getEps()
//                        : pacienteModel.getEps()
//        );
//
//        Boolean acepta = request.getAcepta_notificaciones();
//
//        if (acepta != null &&
//                !acepta.equals(pacienteModel.getAcepta_notificaciones())) {
//
//            pacienteModel.setAcepta_notificaciones(acepta);
//
//            pacienteModel.setFecha_consentimiento(
//                    acepta ? LocalDateTime.now() : null
//            );
//        }
//
//        pacienteModel.setFecha_actualizacion(LocalDateTime.now());
//
//        return pacienteRepository.save(pacienteModel);
//    }
//}

package com.api.Citaya.services;

import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.repositories.IPacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private IPacienteRepository pacienteRepository;

    @Autowired
    private CitaService citaService;

    // =========================================================
    // CONSULTAR PACIENTES
    // =========================================================

    // Obtener únicamente los pacientes activos.
    public List<PacienteModel> getPacientes() {
        return pacienteRepository.findByActivoTrue();
    }

    // Obtener un paciente por ID, siempre que esté activo.
    public Optional<PacienteModel> getById(int id) {
        return pacienteRepository.findByIdAndActivoTrue(id);
    }

    // Buscar paciente por documento.
    // Incluye pacientes inactivos para evitar registros duplicados.
    public Optional<PacienteModel> getByDocumento(String documento) {
        return pacienteRepository.findByDocumento(documento);
    }

    // =========================================================
    // CREAR PACIENTE
    // =========================================================

    public PacienteModel postPaciente(PacienteModel paciente) {

        boolean acepta = Boolean.TRUE.equals(
                paciente.getAcepta_notificaciones()
        );

        paciente.setAcepta_notificaciones(acepta);

        paciente.setFecha_consentimiento(
                acepta ? LocalDateTime.now() : null
        );

        // Todo paciente nuevo comienza activo.
        paciente.setActivo(true);

        return pacienteRepository.save(paciente);
    }

    // =========================================================
    // DESACTIVAR PACIENTE Y SUS CITAS
    // =========================================================

    @Transactional
    public Boolean deletePaciente(int id) {

        Optional<PacienteModel> pacienteOpt =
                pacienteRepository.findById(id);

        if (pacienteOpt.isEmpty()) {
            return false;
        }

        PacienteModel paciente = pacienteOpt.get();

        // Desactivar paciente si todavía está activo.
        if (!Boolean.FALSE.equals(paciente.getActivo())) {

            paciente.setActivo(false);
            paciente.setFecha_actualizacion(LocalDateTime.now());

            pacienteRepository.save(paciente);
        }

        // Desactivar todas las citas activas asociadas.
        // Ambas operaciones pertenecen a la misma transacción.
        citaService.desactivarCitasPorPaciente(id);

        return true;
    }

    // =========================================================
    // ACTUALIZAR PACIENTE
    // =========================================================

    @Transactional
    public PacienteModel putPaciente(
            PacienteModel request,
            int id) {

        PacienteModel pacienteModel = pacienteRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Paciente no encontrado o inactivo"
                        )
                );

        pacienteModel.setNombre(
                request.getNombre() != null
                        ? request.getNombre()
                        : pacienteModel.getNombre()
        );

        pacienteModel.setEmail(
                request.getEmail() != null
                        ? request.getEmail()
                        : pacienteModel.getEmail()
        );

        pacienteModel.setTipo_documento(
                request.getTipo_documento() != null
                        ? request.getTipo_documento()
                        : pacienteModel.getTipo_documento()
        );

        pacienteModel.setDocumento(
                request.getDocumento() != null
                        ? request.getDocumento()
                        : pacienteModel.getDocumento()
        );

        pacienteModel.setTelefono(
                request.getTelefono() != null
                        ? request.getTelefono()
                        : pacienteModel.getTelefono()
        );

        pacienteModel.setEps(
                request.getEps() != null
                        ? request.getEps()
                        : pacienteModel.getEps()
        );

        Boolean acepta = request.getAcepta_notificaciones();

        if (acepta != null &&
                !acepta.equals(pacienteModel.getAcepta_notificaciones())) {

            pacienteModel.setAcepta_notificaciones(acepta);

            pacienteModel.setFecha_consentimiento(
                    acepta ? LocalDateTime.now() : null
            );
        }

        pacienteModel.setFecha_actualizacion(LocalDateTime.now());

        return pacienteRepository.save(pacienteModel);
    }
}