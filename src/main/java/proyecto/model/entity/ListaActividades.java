package proyecto.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import proyecto.model.Database;

public class ListaActividades {
	
	List<Actividad> lista = new ArrayList<>();
	
	
	public List<Actividad> getActividades(LocalDate fechaHoy,Database db) {
		lista = new ArrayList<>();
		LocalDate hoy = fechaHoy;
	    String fechaFiltro = hoy.toString();
	    String fechaMax = hoy.plusYears(1).toString();
	    
	    String sql = "SELECT * FROM Actividad WHERE fecha_inicio >= '" + fechaFiltro + "' AND fecha_inicio <= '" + fechaMax + "' ORDER BY fecha_inicio ASC";
	    List<Map<String, Object>> resultados = db.executeQueryMap(sql);

	    for (Map<String, Object> fila : resultados) {
	        Actividad act = new Actividad();
	        act.setId_Actividad((int) fila.get("id_actividad"));
	        act.setNombre((String) fila.get("nombre"));
	        act.setObjetivos((String) fila.get("objetivos"));
	        act.setContenidos((String) fila.get("contenidos"));
	        act.setEspacio((String) fila.get("espacio"));
	        act.setFechaInicio(LocalDate.parse((String) fila.get("fecha_inicio")));
	        act.setFechaFin(LocalDate.parse((String) fila.get("fecha_fin")));
	        act.setInicio_insc(LocalDate.parse((String) fila.get("inicio_inscripcion")));
	        act.setFin_inscr(LocalDate.parse((String) fila.get("fin_inscripcion")));
	        act.setCuota(fila.get("cuota") != null ? ((Number) fila.get("cuota")).doubleValue() : 0.0);
	        act.setEs_gratuita(((Number) fila.get("es_gratuita")).intValue() == 1);
	        act.setPlazas(fila.get("total_plazas") != null ? ((Number) fila.get("total_plazas")).intValue() : 0);
	        act.setClosed(((Number) fila.get("isClosed")).intValue() == 1);
	        System.out.println("ACTIVIDAD AHORA");

	        lista.add(act);
	    }

	    return lista;
	}
	
	
	public Actividad getActividad(int fila) {
		return lista.get(fila);
	}
	
	
	

}
