package com.api.Citaya.services;

import com.api.Citaya.models.CitaModel;
import com.api.Citaya.models.NotificacionModel.Tipo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotificacionServiceTest {

    private static final LocalDate DIA = LocalDate.of(2026, 10, 20);
    private static final LocalTime NUEVE = LocalTime.of(9, 0);
    private static final LocalTime DIEZ = LocalTime.of(10, 0);

    private static NotificacionService.EstadoCita antes(CitaModel.Estado e, int medico, LocalTime hora) {
        return new NotificacionService.EstadoCita(e, medico, DIA, hora);
    }

    private static CitaModel despues(CitaModel.Estado e, int medico, LocalTime hora) {
        CitaModel c = new CitaModel();
        c.setEstado(e);
        c.setId_medico(medico);
        c.setFecha(DIA);
        c.setHora(hora);
        return c;
    }

    @Test
    void solicitudNuevaNotificaSolicitudRecibida() {
        assertEquals(Tipo.SOLICITUD_RECIBIDA, NotificacionService.determinarTipo(null, new CitaModel()));
    }

    @Test
    void citaNuevaConfirmadaNotificaConfirmada() {
        assertEquals(Tipo.CONFIRMADA,
                NotificacionService.determinarTipo(null, despues(CitaModel.Estado.CONFIRMADA, 1, NUEVE)));
    }

    @Test
    void solicitadaAConfirmadaNotificaConfirmada() {
        assertEquals(Tipo.CONFIRMADA, NotificacionService.determinarTipo(
                antes(CitaModel.Estado.SOLICITADA, 1, NUEVE), despues(CitaModel.Estado.CONFIRMADA, 1, NUEVE)));
    }

    @Test
    void canceladaAConfirmadaNotificaConfirmada() {
        assertEquals(Tipo.CONFIRMADA, NotificacionService.determinarTipo(
                antes(CitaModel.Estado.CANCELADA, 1, NUEVE), despues(CitaModel.Estado.CONFIRMADA, 1, NUEVE)));
    }

    @Test
    void cambioDeHoraEnConfirmadaNotificaReprogramada() {
        assertEquals(Tipo.REPROGRAMADA, NotificacionService.determinarTipo(
                antes(CitaModel.Estado.CONFIRMADA, 1, NUEVE), despues(CitaModel.Estado.CONFIRMADA, 1, DIEZ)));
    }

    @Test
    void cambioDeMedicoEnConfirmadaNotificaReprogramada() {
        assertEquals(Tipo.REPROGRAMADA, NotificacionService.determinarTipo(
                antes(CitaModel.Estado.CONFIRMADA, 1, NUEVE), despues(CitaModel.Estado.CONFIRMADA, 2, NUEVE)));
    }

    @Test
    void guardarSinCambiosNoNotifica() {
        assertNull(NotificacionService.determinarTipo(
                antes(CitaModel.Estado.CONFIRMADA, 1, NUEVE), despues(CitaModel.Estado.CONFIRMADA, 1, NUEVE)));
    }

    @Test
    void revertirDeAtendidaAConfirmadaNoNotifica() {
        assertNull(NotificacionService.determinarTipo(
                antes(CitaModel.Estado.ATENDIDA, 1, NUEVE), despues(CitaModel.Estado.CONFIRMADA, 1, NUEVE)));
    }

    @Test
    void marcarAtendidaNoNotifica() {
        assertNull(NotificacionService.determinarTipo(
                antes(CitaModel.Estado.CONFIRMADA, 1, NUEVE), despues(CitaModel.Estado.ATENDIDA, 1, NUEVE)));
    }

    @Test
    void cancelarNotificaCancelada() {
        assertEquals(Tipo.CANCELADA, NotificacionService.determinarTipo(
                antes(CitaModel.Estado.CONFIRMADA, 1, NUEVE), despues(CitaModel.Estado.CANCELADA, 1, NUEVE)));
    }

    @Test
    void cancelarUnaYaCanceladaNoNotificaDeNuevo() {
        assertNull(NotificacionService.determinarTipo(
                antes(CitaModel.Estado.CANCELADA, 1, NUEVE), despues(CitaModel.Estado.CANCELADA, 1, NUEVE)));
    }
}