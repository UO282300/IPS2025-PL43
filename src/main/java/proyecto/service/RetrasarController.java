package proyecto.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RetrasarController {

    private UserService service;

    public RetrasarController(UserService service) {
        this.service = service;
    }

    /** 
     * Devuelve todas las actividades que se pueden retrasar:
     * - No deben estar cerradas ni canceladas.
     * - Su fecha de inicio debe ser posterior a la fecha actual.
     */
    public List<Map<String, Object>> listarActividadesRetrasables() {
        String sql = "SELECT * FROM Actividad";
        List<Map<String, Object>> todas = service.getDb().executeQueryMap(sql);

        List<Map<String, Object>> retrasables = new ArrayList<>();
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

            if (!estado.equals("Cerrada") && !estado.equals("Cancelada")&& !estado.equals("En Curso")&& !estado.equals("Finalizada")) {
                retrasables.add(act);
            }
        }

        return retrasables;
    }

    /**
     * Retrasa la actividad y actualiza las fechas según el formulario.
     * Si un valor es nulo, se mantiene la fecha original.
     * Además marca todas las matrículas como isRetrasada = true.
     */
    public void retrasarActividad(int idActividad, LocalDate nuevaFinInscripcion,
                                  LocalDate nuevaFechaInicio, LocalDate nuevaFechaFin) {

        // Primero obtenemos la actividad original
        String sqlSelect = "SELECT fin_inscripcion, fecha_inicio, fecha_fin FROM Actividad WHERE id_actividad = ?";
        List<Map<String, Object>> res = service.getDb().executeQueryMap(sqlSelect, idActividad);

        if (res.isEmpty()) return;

        LocalDate finInscripcion = (nuevaFinInscripcion != null) ? nuevaFinInscripcion
                : LocalDate.parse(res.get(0).get("fin_inscripcion").toString());
        LocalDate fechaInicio = (nuevaFechaInicio != null) ? nuevaFechaInicio
                : LocalDate.parse(res.get(0).get("fecha_inicio").toString());
        LocalDate fechaFin = (nuevaFechaFin != null) ? nuevaFechaFin
                : LocalDate.parse(res.get(0).get("fecha_fin").toString());

        // Actualizamos la actividad
        String sqlUpdate = "UPDATE Actividad SET fin_inscripcion = ?, fecha_inicio = ?, fecha_fin = ? WHERE id_actividad = ?";
        service.getDb().executeUpdate(sqlUpdate, finInscripcion.toString(), fechaInicio.toString(), fechaFin.toString(), idActividad);

        // Marcamos todas las matrículas como retrasadas
        String sqlMatriculas = "UPDATE Matricula SET isRetrasada = 1 WHERE id_actividad = ?";
        service.getDb().executeUpdate(sqlMatriculas, idActividad);
    }

	public String obtenerEstadoActividad(Map<String, Object> act) {
		
		return service.obtenerEstadoActividad(act);
	}
}
