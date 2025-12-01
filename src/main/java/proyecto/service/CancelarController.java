package proyecto.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CancelarController {

    private UserService service;

    public CancelarController(UserService service) {
        this.service = service;
    }

    /** 
     * Devuelve todas las actividades que se pueden cancelar:
     * - No deben estar cerradas ni canceladas.
     * - Su fecha de inicio debe ser posterior a la fecha actual.
     */
    public List<Map<String, Object>> listarActividadesCancelables() {
        // Traemos todas las actividades
        String sql = "SELECT * FROM Actividad";
        List<Map<String, Object>> todas = service.getDb().executeQueryMap(sql);

        List<Map<String, Object>> cancelables = new ArrayList<>();
        LocalDate hoy = service.getFechaHoy(); // obtenemos la fecha actual del UserService

        for (Map<String, Object> act : todas) {
            String estado = service.obtenerEstadoActividad(act);

            // Intentamos obtener la fecha de inicio
            LocalDate fechaInicio = null;
            try {
                Object valor = act.get("fecha_inicio");
                if (valor != null) {
                    fechaInicio = LocalDate.parse(valor.toString());
                }
            } catch (Exception e) {
                // Si hay error de formato, la actividad se ignora
                continue;
            }

            // Solo a�adimos las que se pueden cancelar
            if (fechaInicio != null 
                && hoy.isBefore(fechaInicio) 
                && !estado.equals("Cerrada") 
                && !estado.equals("Cancelada")) {
                cancelables.add(act);
            }
        }

        return cancelables;
    }

    public void cancelarActividad(int idActividad) {
        // Marcar la actividad como cancelada
        String sqlUpdate = "UPDATE Actividad SET isCancelada = 1 WHERE id_actividad = ?";
        service.getDb().executeUpdate(sqlUpdate, idActividad);

        // Obtener la fecha de inicio de la actividad
        String sqlFecha = "SELECT fecha_inicio FROM Actividad WHERE id_actividad = ?";
        List<Map<String, Object>> res = service.getDb().executeQueryMap(sqlFecha, idActividad);
        LocalDate fechaActividad = null;
        if (!res.isEmpty() && res.get(0).get("fecha_inicio") != null) {
            fechaActividad = LocalDate.parse(res.get(0).get("fecha_inicio").toString());
        } else {
            fechaActividad = service.getFechaHoy(); // fallback por seguridad
        }

        // Obtener las matr�culas asociadas
        String sqlMatriculas = "SELECT * FROM Matricula WHERE id_actividad = ?";
        List<Map<String, Object>> matriculas = service.getDb().executeQueryMap(sqlMatriculas, idActividad);

        for (Map<String, Object> m : matriculas) {
            int idMatricula = (int) m.get("id_matricula");
            int idAlumno = (int) m.get("id_alumno");

            Object montoObj = m.get("monto_pagado");
            double montoPagado = 0.0;

            if (montoObj instanceof Number) {
                montoPagado = ((Number) montoObj).doubleValue();
            } else if (montoObj != null) {
                montoPagado = Double.parseDouble(montoObj.toString());
            }

            // Calcular devoluci�n y registrar
            double montoDevuelto = service.calcularMontoDevolucion(fechaActividad, montoPagado, idMatricula);
            service.registrarDevolucion(idMatricula, idAlumno, idActividad, montoDevuelto,false);
        }
    }
    
    

    /** Comprueba si la actividad se puede cancelar seg�n la fecha actual */
    public boolean sePuedeCancelar(LocalDate fechaHoy, LocalDate fechaInicio) {
        return fechaHoy.isBefore(fechaInicio);
    }

	public String obtenerEstadoActividad(Map<String, Object> act) {
		
		return service.obtenerEstadoActividad(act);
	}

	public List<Map<String, Object>> listarDevolucionesPendientes() {
		// TODO Auto-generated method stub
		return null;
	}
}
