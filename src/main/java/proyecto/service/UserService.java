package proyecto.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import proyecto.model.Database;
import proyecto.model.entity.Actividad;
import proyecto.model.entity.Alumno;
import proyecto.util.ApplicationException;

public class UserService {
	private Database db;
	private Alumno a = new Alumno();
	private Actividad ac;
	private LocalDate fechaHoy;

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
			String espacio, String fecha ,String horaInicio, String horaFinal, String inscripcionI,
			String inscripcionF, String cuota, String objetivos, String contenidos, String plazas, boolean esGratuita) {
		
		try {
	        if (nombre == null || nombre.isBlank() || fecha == null || plazas == null
	        	|| fecha.isBlank() || espacio == null || espacio.isBlank()) {
	            JOptionPane.showMessageDialog(null,
	                    "Debe completar al menos: nombre, fecha, espacio y plazas.",
	                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
	            return false;
	        }
	        
	        int plazasN;
	        try {
	        	plazasN = Integer.parseInt(plazas);
	        } catch (Exception e) {
	        	JOptionPane.showMessageDialog(null,
	        			"Formato de plazas invalido, usa un decimal", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }

	        LocalDate fechaActividad;
	        LocalDate inicioIns;
	        LocalDate finIns;
	        
	        try {
		        fechaActividad = LocalDate.parse(fecha);
		        inicioIns = LocalDate.parse(inscripcionI);
		        finIns = LocalDate.parse(inscripcionF);
	        } catch (DateTimeParseException e) {
	        	JOptionPane.showMessageDialog(null,
	        			"Formato de fecha inválido. Usa el formato yyyy-MM-dd", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	            return false;
	        }
	        
	        if (fechaHoy.isAfter(inicioIns) || fechaHoy.isAfter(fechaActividad)) {
	        	JOptionPane.showMessageDialog(null,
	        			"No puedes iniciar una actividad antes de la fecha de hoy", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }
	        if (inicioIns.isAfter(finIns)) {
	        	JOptionPane.showMessageDialog(null,
	        			"La fecha de inicio de inscripción no puede ser posterior a la de cierre", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }
	        if (fechaActividad.isBefore(finIns)) {
	        	JOptionPane.showMessageDialog(null,
	        			"La fecha de la actividad debe ser posterior al fin de inscripción", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }

	        LocalTime horaI;
	        LocalTime horaF;
	        try {
	        	horaI = LocalTime.parse(horaInicio);
	        	horaF = LocalTime.parse(horaFinal);
	        } catch (DateTimeParseException e) {
	        	JOptionPane.showMessageDialog(null,
	        			"Formato de hora inválido. Usa HH:mm", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }	        
	        
	        if (horaI.isAfter(horaF)) {
	        	JOptionPane.showMessageDialog(null,
	        			"La hora inicial no puede ser después de la final", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }
	        
	        double remuneracionNum = Double.parseDouble(remuneracion);
	        double cuotaNum = Double.parseDouble(cuota);
	        
	        if (esGratuita) {
	        	cuotaNum = 0;
	        }
	        
	        if (plazasN < 0 || remuneracionNum < 0 || cuotaNum < 0) {
	        	JOptionPane.showMessageDialog(null,
	        			"No se pueden rellenar campos con decimales negativos", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }

	        db.executeUpdate("""
	            INSERT INTO Actividad
	            (nombre, objetivos, contenidos, id_profesor, remuneracion, espacio, total_plazas, fecha,
	             hora_inicio, hora_fin, inicio_inscripcion, fin_inscripcion, cuota, es_gratuita)
	            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	            """,
	            nombre, objetivos, contenidos, idProfesor, remuneracionNum, espacio,
	            plazasN, fechaActividad.toString(), horaI, horaF,
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
	                "Registro fallido", JOptionPane.ERROR_MESSAGE);
	        throw new ApplicationException("Error al cargar la actividad");
	    }
	}
	
	public List<Map<String, Object>> listarProfesores() {
	    return db.executeQueryMap("SELECT id_profesor, nombre, apellido FROM Profesor ORDER BY nombre");
	}
	
	
	public LocalDate getFecha() {
		return fechaHoy;
	}

	public void setFecha(LocalDate fecha) {
		this.fechaHoy = fecha;
	}
	
	public void setFecha(String fechaS) {
		LocalDate fechaL;
		
        try {
	        fechaL = LocalDate.parse(fechaS);
        } catch (DateTimeParseException e) {
            throw new ApplicationException("Formato de fecha inválido. Usa el formato yyyy-MM-dd");
        }
        
        this.fechaHoy = fechaL;
	}

	// Método para listar actividades con datos básicos y estado
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
            if (!hoy.isBefore(inicio) && !hoy.isAfter(fin)) return "En periodo de inscripción";
            if (hoy.isAfter(fin) && hoy.isBefore(fecha)) return "Inscripción cerrada";
            if (hoy.isAfter(fecha)) return "Cerrada (completada)";
        } catch (Exception e) {
            return "Estado desconocido";
        }
        return "Estado desconocido";
    }
    
 // Obtiene todos los detalles de una actividad
    public Map<String, Object> getActividadDetalles(int idActividad) {
        Map<String,Object> resultado = new HashMap<>();

        // Datos básicos de la actividad
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
        double gastosConfirmados = gastosEstimados; // asumimos que siempre se confirma remuneración

        resultado.put("ingresos_estimados", ingresosEstimados);
        resultado.put("ingresos_confirmados", ingresosConfirmados);
        resultado.put("gastos_estimados", gastosEstimados);
        resultado.put("gastos_confirmados", gastosConfirmados);

        return resultado;
    }

	public void guardarNombre(String text) {
		a.setNombre(text);
		
	}

	public void guardarApellidos(String text) {
		a.setApellidos(text);
		
	}

	public void guardarTf(String text) {
		a.setNumeroTf(text);
		
	}

	public void guardarCorreo(String text) {
		a.setCorreo(text);
		
	}

	public void guardarPertenece(boolean selected) {
		a.setPertenece(selected);
		
	}

	public boolean checkear() {
		return a.validar();
		
	}

	public boolean introduce() {
		if(!comprobarPlazos()) {
			System.out.println("Fuera de plazo");
			return false;
		}
		insertarAlumno();
		
		
		return insertarMatricula();
		
	}

	private boolean comprobarPlazos() {
		LocalDate hoy = getFecha();
	    LocalDate inicio = ac.getInicio_insc();
	    LocalDate fin = ac.getFin_inscr();

	    // Devuelve true si hoy está entre inicio y fin (inclusive)
	    return !hoy.isBefore(inicio) && !hoy.isAfter(fin);
	}

	private boolean insertarMatricula() {
		if(!comprobarPlazasActividad()) {
			System.out.println("No hay plazas");
			return false;
		}
		
		else {
			insertaMatricula();
			List<Map<String, Object>> resultado = db.executeQueryMap(
				    "SELECT * FROM Matricula WHERE id_alumno = ? AND id_actividad = ?",
				    a.getIdAlumno(), ac.getId_Actividad()
				);
			
				if (!resultado.isEmpty()) {
				    System.out.println(" Alumno insertado correctamente: " + resultado.get(0));
				} else {
				    System.out.println("No se insertó al alumno.");
				}
			a=new Alumno();
			ac=null;
			return true;
		}
		
	}
	
	
	public List<Actividad> recuperarActividades(){
		List<Actividad> listaActividades = new ArrayList<>();
		LocalDate hoy = fechaHoy;
	    String fechaFiltro = hoy.toString();
	    String fechaMax = hoy.plusYears(1).toString();
	    
	    String sql = "SELECT * FROM Actividad WHERE fecha >= '" + fechaFiltro + "' AND fecha <= '" + fechaMax + "' ORDER BY fecha ASC";
	    List<Map<String, Object>> resultados = db.executeQueryMap(sql);

	    for (Map<String, Object> fila : resultados) {
	        Actividad act = new Actividad();
	        act.setId_Actividad((int) fila.get("id_actividad"));
	        act.setNombre((String) fila.get("nombre"));
	        act.setObjetivos((String) fila.get("objetivos"));
	        act.setContenidos((String) fila.get("contenidos"));
	        act.setId_profesor((int) fila.get("id_profesor"));
	        act.setRemuneracion(((Number) fila.get("remuneracion")).doubleValue());
	        act.setEspacio((String) fila.get("espacio"));
	        act.setFecha(LocalDate.parse((String) fila.get("fecha"))); 
	        act.setHoraInicio((String) fila.get("hora_inicio"));
	        act.setHoraFin((String) fila.get("hora_fin"));
	        act.setInicio_insc(LocalDate.parse((String) fila.get("inicio_inscripcion")));
	        act.setFin_inscr(LocalDate.parse((String) fila.get("fin_inscripcion")));
	        act.setCuota(((Number) fila.get("cuota")).doubleValue());
	        act.setEs_gratuita(((Number) fila.get("es_gratuita")).intValue() == 1);
	        act.setPlazas((Number) fila.get("total_plazas"));

	        listaActividades.add(act);
	    }

	    return listaActividades;
	}
	
	
	
	
	private boolean comprobarPlazasActividad() {
		

		 Map<String, Object> resultados = getActividadDetalles(ac.getId_Actividad());
		 if (resultados.isEmpty()) {
			
			 return false;
		 }
		 Number plazasObj = (Number) resultados.get("plazas_disponibles");
		 int plazas_libres = plazasObj.intValue();
		 return plazas_libres > 0;
    
	}

	private void insertaMatricula() {
		db.executeUpdate(
	            "INSERT INTO Matricula (id_alumno, id_actividad, esta_pagado, monto_pagado, fecha_matricula) " +
	            "VALUES (?, ?, ?, ?, ?)",
	            a.getIdAlumno(),
	            ac.getId_Actividad(),
	            false,
	            0.0,
	            fechaHoy
	        );
				
	}

	private void insertarAlumno() {
		String correoBuscado = (String) a.getCorreo();
		List<Map<String, Object>> resultados = db.executeQueryMap(
		    "SELECT * FROM Alumno WHERE email = ?", correoBuscado 
		);

		if (resultados.isEmpty()) {
			db.executeUpdate(
			        "INSERT INTO Alumno (nombre, apellido, email, telefono, es_interno) VALUES (?, ?, ?, ?, ?)",
			        a.getNombre(), a.getApellido(), a.getCorreo(), a.getTelefono(), a.pertenece()
			    );
			  List<Map<String, Object>> nuevo = db.executeQueryMap(
			            "SELECT id_alumno FROM Alumno WHERE email = ?", correoBuscado
			        );
			        if (!nuevo.isEmpty()) {
			            Number id = (Number) nuevo.get(0).get("id_alumno");
			            a.setId(id.intValue());
			        }
			    } else {
			        Number id = (Number) resultados.get(0).get("id_alumno");
			        a.setId(id.intValue());
			    
			    }
	}
	

	public void selectActividad(Actividad selec) {
		this.ac = selec;
		
	}

	public Object getAct() {
		return ac;
	}
}