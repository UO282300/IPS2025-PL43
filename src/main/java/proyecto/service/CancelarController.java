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
        String sql = "SELECT * FROM Actividad";
        List<Map<String, Object>> todas = service.getDb().executeQueryMap(sql);

        List<Map<String, Object>> cancelables = new ArrayList<>();
        LocalDate hoy = service.getFechaHoy();

        for (Map<String, Object> act : todas) {
            String estado = service.obtenerEstadoActividad(act);

            LocalDate fechaInicio = null;
            try {
                Object valor = act.get("fecha_inicio");
                if (valor != null) {
                    fechaInicio = LocalDate.parse(valor.toString());
                }
            } catch (Exception e) {
                continue;
            }

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

        // Obtener las matriculas asociadas
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

            // Calcular devolucion y registrar
            double montoDevuelto = service.calcularMontoDevolucion(fechaActividad, montoPagado, idMatricula);
            service.registrarDevolucion(idMatricula, idAlumno, idActividad, montoDevuelto,false);
        }
    }
    
    public boolean sePuedeCancelar(LocalDate fechaHoy, LocalDate fechaInicio) {
        return fechaHoy.isBefore(fechaInicio);
    }

	public String obtenerEstadoActividad(Map<String, Object> act) {
		
		return service.obtenerEstadoActividad(act);
	}

	public List<Map<String, Object>> listarDevolucionesPendientes() {

	    String sql = """
	        SELECT 
	            a.nombre AS nombre,
	            a.apellido AS apellido,
	            act.nombre AS actividad,
	            m.id_matricula,
	            m.monto_inscripcion_cancelada,
	            m.monto_actividad_cancelada,
	            IFNULL((
	                SELECT SUM(d.monto_devuelto)
	                FROM Devoluciones d
	                WHERE d.id_matricula = m.id_matricula
	            ), 0) AS total_devuelto
	        FROM Matricula m
	        JOIN Alumno a ON a.id_alumno = m.id_alumno
	        JOIN Actividad act ON act.id_actividad = m.id_actividad
	        ORDER BY a.apellido, a.nombre;
	    """;

	    List<Map<String, Object>> lista = service.getDb().executeQueryMap(sql);
	    List<Map<String, Object>> resultado = new ArrayList<>();

	    for (Map<String, Object> row : lista) {

	        double ins = row.get("monto_inscripcion_cancelada") != null
	                     ? ((Number) row.get("monto_inscripcion_cancelada")).doubleValue()
	                     : 0.0;

	        double act = row.get("monto_actividad_cancelada") != null
	                     ? ((Number) row.get("monto_actividad_cancelada")).doubleValue()
	                     : 0.0;

	        double devuelto = row.get("total_devuelto") != null
	                         ? ((Number) row.get("total_devuelto")).doubleValue()
	                         : 0.0;

	        double pendiente = ins + act - devuelto;

	        if (pendiente <= 0.01) continue; // ignorar si ya está devuelto completamente

	        resultado.add(Map.of(
	            "nombre", row.get("nombre"),
	            "apellido", row.get("apellido"),
	            "actividad", row.get("actividad"),
	            "pendiente", pendiente
	        ));
	    }

	    return resultado;
	}

}
