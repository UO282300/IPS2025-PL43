package proyecto.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import proyecto.model.Database;
import proyecto.util.ApplicationException;

public class UserService {
	private Database db;

    public UserService() {
        this.db = new Database();
    }
    
    public void crearDataBase() {
    	db.createDatabase(false);
    }
    
    public void cargarDataBase() {
    	db.loadDatabase();
    }
    
    public void ejemploConsulta() {
	    List<Map<String, Object>> alumnos = db.executeQueryMap("SELECT * FROM Alumno");
	    for (Map<String, Object> alumno : alumnos) {
	        System.out.println(alumno);
	    }
	}

	public boolean cargarActividad(String nombre, int idProfesor, String remuneracion,
			String espacio, String fecha ,String horaI, String horaF, String inscripcionI,
			String inscripcionF, String cuota, String objetivos, String contenidos) {
		
		try {
	        if (nombre == null || nombre.isBlank() || fecha == null 
	        	|| fecha.isBlank() || espacio == null || espacio.isBlank()) {
	            JOptionPane.showMessageDialog(null,
	                    "Debe completar al menos: nombre, fecha y espacio.",
	                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
	            return false;
	        }

	        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        LocalDate fechaActividad;
	        LocalDate inicioIns;
	        LocalDate finIns;
	        
	        try {
		        fechaActividad = LocalDate.parse(fecha, df);
		        inicioIns = LocalDate.parse(inscripcionI, df);
		        finIns = LocalDate.parse(inscripcionF, df);
	        } catch (DateTimeParseException e) {
	            throw new ApplicationException("Formato de fecha inv�lido. Usa el formato dd/MM/yyyy");
	        }

	        double remuneracionNum = Double.parseDouble(remuneracion);
	        double cuotaNum = Double.parseDouble(cuota);
	        boolean esGratuita = false;

	        db.executeUpdate("""
	            INSERT INTO Actividad
	            (nombre, objetivos, contenidos, id_profesor, remuneracion, espacio, fecha,
	             hora_inicio, hora_fin, inicio_inscripcion, fin_inscripcion, cuota, es_gratuita)
	            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	            """,
	            nombre, objetivos, contenidos, idProfesor, remuneracionNum, espacio,
	            fechaActividad.toString(), horaI, horaF,
	            inicioIns.toString(), finIns.toString(),
	            cuotaNum, esGratuita ? 1 : 0
	        );

	        JOptionPane.showMessageDialog(null,
	                "Actividad registrada correctamente.",
	                "Registro completado", JOptionPane.INFORMATION_MESSAGE);
	        
	        return true;

	    } catch (ApplicationException ex) {
	    	JOptionPane.showMessageDialog(null,
	                "Error al registrar actividad",
	                "Registro falledio", JOptionPane.ERROR_MESSAGE);
	        throw new ApplicationException("Error al cargar la actividad");
	    }
	}
	
	public List<Map<String, Object>> listarProfesores() {
	    return db.executeQueryMap("SELECT id_profesor, nombre, apellido FROM Profesor ORDER BY nombre");
	}

	// M�todo para listar actividades con datos b�sicos y estado
    public List<Map<String, Object>> listarActividades() {
        List<Map<String,Object>> actividades = db.executeQueryMap(
            "SELECT id_actividad, nombre, inicio_inscripcion, fin_inscripcion, fecha, " +
            "cuota, remuneracion " +
            "FROM Actividad ORDER BY fecha"
        );

        for (Map<String,Object> act : actividades) {
            act.put("estado", obtenerEstadoActividad(act));
        }
        return actividades;
    }
    
 // Calcula el estado de la actividad
    public String obtenerEstadoActividad(Map<String,Object> act) {
        try {
            java.time.LocalDate hoy = java.time.LocalDate.now();
            java.time.LocalDate inicio = java.time.LocalDate.parse((String) act.get("inicio_inscripcion"));
            java.time.LocalDate fin = java.time.LocalDate.parse((String) act.get("fin_inscripcion"));
            java.time.LocalDate fecha = java.time.LocalDate.parse((String) act.get("fecha"));

            if (hoy.isBefore(inicio)) return "Planificada";
            if (!hoy.isBefore(inicio) && !hoy.isAfter(fin)) return "En periodo de inscripci�n";
            if (hoy.isAfter(fin) && hoy.isBefore(fecha)) return "Inscripci�n cerrada";
            if (hoy.isAfter(fecha)) return "Cerrada (completada)";
        } catch (Exception e) {
            return "Estado desconocido";
        }
        return "Estado desconocido";
    }
    
 // Obtiene todos los detalles de una actividad
    public Map<String, Object> getActividadDetalles(int idActividad) {
        Map<String,Object> resultado = new HashMap<>();

        // Datos b�sicos de la actividad
        List<Map<String,Object>> actividades = db.executeQueryMap(
            "SELECT * FROM Actividad WHERE id_actividad = ?", idActividad
        );
        if (actividades.isEmpty()) return null;
        Map<String,Object> act = actividades.get(0);
        resultado.putAll(act);
        resultado.put("estado", obtenerEstadoActividad(act));

        // Plazas Ocupadas, totales y disponibles
        int plazasOcupadas = db.executeQueryMap(
            "SELECT COUNT(*) as total FROM Matricula WHERE id_actividad = ?", idActividad
        ).get(0).get("total") == null ? 0 : ((Number)db.executeQueryMap(
            "SELECT COUNT(*) as total FROM Matricula WHERE id_actividad = ?", idActividad
        ).get(0).get("total")).intValue();
        
        int totalPlazas = ((Number) db.executeQueryMap("SELECT total_plazas as total FROM Actividad WHERE id_Actividad=?", idActividad).get(0).get("total")).intValue();


        int plazasDisponibles = totalPlazas - plazasOcupadas;
        resultado.put("plazas_disponibles", plazasDisponibles);

        // Inscripciones
        List<Map<String,Object>> inscripciones = db.executeQueryMap(
            "SELECT a.nombre || ' ' || a.apellido as nombre_alumno, m.fecha_matricula, " +
            "CASE WHEN m.esta_pagado=1 THEN 'Cobrada' WHEN m.monto_pagado>0 THEN 'Recibida' ELSE 'Cancelada' END as estado " +
            "FROM Matricula m JOIN Alumno a ON m.id_alumno = a.id_alumno " +
            "WHERE m.id_actividad = ?", idActividad
        );
        resultado.put("inscripciones", inscripciones);

        // Finanzas
        double ingresosConfirmados = inscripciones.stream()
                .filter(i -> "Cobrada".equals(i.get("estado")))
                .mapToDouble(i -> {
                    try { return Double.parseDouble(String.valueOf(act.get("cuota"))); }
                    catch(Exception e){ return 0; }
                }).sum();

        double ingresosEstimados = inscripciones.size() * Double.parseDouble(String.valueOf(act.get("cuota")));

        double gastosEstimados = act.get("remuneracion") != null ? Double.parseDouble(String.valueOf(act.get("remuneracion"))) : 0;
        double gastosConfirmados = gastosEstimados; // asumimos que siempre se confirma remuneraci�n

        resultado.put("ingresos_estimados", ingresosEstimados);
        resultado.put("ingresos_confirmados", ingresosConfirmados);
        resultado.put("gastos_estimados", gastosEstimados);
        resultado.put("gastos_confirmados", gastosConfirmados);

        return resultado;
    }
}
    
