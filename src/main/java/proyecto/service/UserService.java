package proyecto.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import proyecto.model.Database;
import proyecto.model.entity.Actividad;
import proyecto.model.entity.Alumno;
import proyecto.model.entity.Factura;
import proyecto.model.entity.FechaFiltrado;
import proyecto.util.ApplicationException;
import proyecto.util.MensajeError;

public class UserService {
	private Database db;
	private Alumno a = new Alumno();
	private Actividad ac;
	private LocalDate fechaHoy;
	private FechaFiltrado fechaFiltrado;
	private int idAlumnoCancel;
	private int idAlumnoInscrip;

    public int getIdAlumnoCancel() {
		return idAlumnoCancel;
	}

	public void setIdAlumnoCancel(int idAlumnoCancel) {
		this.idAlumnoCancel = idAlumnoCancel;
	}

	

    public int getIdAlumnoInscrip() {
		return idAlumnoInscrip;
	}

	public void setIdAlumnoInscrip(int idAlumnoInscrip) {
		this.idAlumnoInscrip = idAlumnoInscrip;
	}

	public UserService() {
        this.db = new Database();
        //crearDataBase();
        //cargarDataBase();
    }
    public void eliminarTodosLosDatos() {
        try {
            // 1Ã¯Â¸ï¿½Ã¢Æ’Â£ Eliminar los registros de las tablas en orden para respetar claves forÃƒÂ¡neas
            db.executeUpdate("DELETE FROM Matricula");
            db.executeUpdate("DELETE FROM Actividad");
            db.executeUpdate("DELETE FROM Alumno");
            db.executeUpdate("DELETE FROM Profesor");
            db.executeUpdate("DELETE FROM Administrador");

            // 2Ã¯Â¸ï¿½Ã¢Æ’Â£ Reiniciar los contadores AUTOINCREMENT
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Matricula'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Actividad'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Alumno'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Profesor'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Administrador'");

            JOptionPane.showMessageDialog(null,
                    "Todos los datos han sido eliminados y los IDs reiniciados.",
                    "OperaciÃƒÂ³n completada", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al eliminar los datos de la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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
	            throw new ApplicationException("Formato de fecha invalido. Usa el formato dd/MM/yyyy");
	        }
	        
	        if (fechaHoy.isAfter(inicioIns) || fechaHoy.isAfter(fechaActividad)) {
	        	System.out.println("fecha hoy: "+ fechaHoy + " fecha inicio insc actividad: "+ inicioIns.toString() + " fecha fin insc: "+ finIns.toString() + " fecha actividad: " + fechaActividad.toString());
	        	JOptionPane.showMessageDialog(null,
	        			"No puedes iniciar una actividad antes de la fecha de hoy", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }
	        if (inicioIns.isAfter(finIns)) {
	        	JOptionPane.showMessageDialog(null,
	        			"La fecha de inicio de inscripciÃ¯Â¿Â½n no puede ser posterior a la de cierre", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }
	        if (fechaActividad.isBefore(finIns)) {
	        	JOptionPane.showMessageDialog(null,
	        			"Formato de fecha invÃ¯Â¿Â½lido. Usa el formato yyyy-MM-dd", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	            return false;
	        }
	        
	        if (fechaHoy.isAfter(inicioIns) || fechaHoy.isAfter(fechaActividad)) {
	        	System.out.println("fecha hoy: "+ fechaHoy + " fecha inicio insc actividad: "+ inicioIns.toString() + " fecha fin insc: "+ finIns.toString() + " fecha actividad: " + fechaActividad.toString());
	        	JOptionPane.showMessageDialog(null,
	        			"No puedes iniciar una actividad antes de la fecha de hoy", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }
	        if (inicioIns.isAfter(finIns)) {
	        	JOptionPane.showMessageDialog(null,
	        			"La fecha de inicio de inscripciÃ¯Â¿Â½n no puede ser posterior a la de cierre", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }
	        if (fechaActividad.isBefore(finIns)) {
	        	JOptionPane.showMessageDialog(null,
	        			"La fecha de la actividad debe ser posterior al fin de inscripciÃ¯Â¿Â½n", 
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
	        			"Formato de hora invÃ¯Â¿Â½lido. Usa HH:mm", 
		                "Error al registrar actividad",
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }	        
	        
	        if (horaI.isAfter(horaF)) {
	        	JOptionPane.showMessageDialog(null,
	        			"La hora inicial no puede ser despuÃ¯Â¿Â½s de la final", 
	        			"Formato de hora invÃ¯Â¿Â½lido. Usa HH:mm", 
		                JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }	        
	        
	        if (horaI.isAfter(horaF)) {
	        	JOptionPane.showMessageDialog(null,
	        			"La hora inicial no puede ser despuÃ¯Â¿Â½s de la final", 
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
	             hora_inicio, hora_fin, inicio_inscripcion, fin_inscripcion, cuota, es_gratuita, isClosed)
	            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	            """,
	            nombre, objetivos, contenidos, idProfesor, remuneracionNum, espacio,
	            plazasN, fechaActividad.toString(), horaI, horaF,
	            inicioIns.toString(), finIns.toString(),
	            cuotaNum, esGratuita ? 1 : 0, 0
	        );

	        JOptionPane.showMessageDialog(null,
	                "Actividad registrada correctamente.",
	                "Registro completado", JOptionPane.INFORMATION_MESSAGE);
	        
	        return true;

	    } catch (ApplicationException ex) {
	    	JOptionPane.showMessageDialog(null,
	    			(ex.getMessage() != null) ? ex.getMessage() : "Error registrando la actividad",
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
            throw new ApplicationException("Formato de fecha invÃ¯Â¿Â½lido. Usa el formato yyyy-MM-dd");
        }
        
        this.fechaHoy = fechaL;
	}

    public List<Map<String, Object>> listarActividades() {
        List<Map<String,Object>> actividades = db.executeQueryMap(
            "SELECT id_actividad, nombre, inicio_inscripcion, fin_inscripcion, fecha, " +
            "cuota, remuneracion, isClosed " +
            "FROM Actividad ORDER BY fecha"
        );

        for (Map<String,Object> act : actividades) {
            act.put("estado", obtenerEstadoActividad(act));
        }
        return actividades;
    }
    
    public List<Map<String, Object>> imprimirActividades() {
        List<Map<String,Object>> actividades = db.executeQueryMap(
            "SELECT id_actividad, nombre, inicio_inscripcion, fin_inscripcion, fecha, " +
            "cuota, remuneracion " +
            "FROM Actividad ORDER BY fecha"
        );

        for (Map<String,Object> act : actividades) {
            String estado = obtenerEstadoActividad(act);
            act.put("estado", estado);

            System.out.println("===== Actividad =====");
            System.out.println("ID: " + act.get("id_actividad"));
            System.out.println("Nombre: " + act.get("nombre"));
            System.out.println("Inicio inscripciÃƒÂ³n: " + act.get("inicio_inscripcion"));
            System.out.println("Fin inscripciÃƒÂ³n: " + act.get("fin_inscripcion"));
            System.out.println("Fecha actividad: " + act.get("fecha"));
            System.out.println("Cuota: " + act.get("cuota"));
            System.out.println("RemuneraciÃƒÂ³n: " + act.get("remuneracion"));
            System.out.println("Estado: " + estado);
            System.out.println("====================\n");
        }
        return actividades;
    }
    
    public void verTodasLasMatriculas() {
        try {
            List<Map<String, Object>> matriculas = db.executeQueryMap(
                "SELECT m.id_matricula, m.fecha_matricula, m.monto_pagado, m.esta_pagado, " +
                "a.nombre AS actividad_nombre, a.fecha AS actividad_fecha, a.espacio AS actividad_espacio, a.cuota AS actividad_cuota, " +
                "al.nombre || ' ' || al.apellido AS alumno_nombre, al.email AS alumno_email, al.telefono AS alumno_telefono " +
                "FROM Matricula m " +
                "JOIN Actividad a ON m.id_actividad = a.id_actividad " +
                "JOIN Alumno al ON m.id_alumno = al.id_alumno " +
                "ORDER BY m.id_matricula"
            );

            if (matriculas.isEmpty()) {
                System.out.println(" No hay matrÃƒÂ­culas registradas en la base de datos.");
                return;
            }

            System.out.println("===== LISTADO COMPLETO DE MATRÃƒï¿½CULAS =====\n");
            for (Map<String, Object> m : matriculas) {
                System.out.println("ID MatrÃƒÂ­cula: " + m.get("id_matricula"));
                System.out.println("Alumno: " + m.get("alumno_nombre"));
                System.out.println("Email: " + m.get("alumno_email"));
                System.out.println("TelÃƒÂ©fono: " + m.get("alumno_telefono"));
                System.out.println("Actividad: " + m.get("actividad_nombre"));
                System.out.println("Fecha actividad: " + m.get("actividad_fecha"));
                System.out.println("Espacio: " + m.get("actividad_espacio"));
                System.out.println("Cuota: " + m.get("actividad_cuota"));
                System.out.println("Fecha matrÃƒÂ­cula: " + m.get("fecha_matricula"));
                System.out.println("Monto pagado: " + m.get("monto_pagado"));
                System.out.println("Ã‚Â¿EstÃƒÂ¡ pagado?: " + (((Integer)m.get("esta_pagado")) == 1 ? "SÃƒÂ­" : "No"));
                System.out.println("----------------------------------------\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Error al consultar las matrÃƒÂ­culas.");
        }
    }

    
 // Calcula el estado de la actividad
    public String obtenerEstadoActividad(Map<String,Object> act) {
        try {
        	Object closed = act.get("isClosed");
            if (closed.equals(1)) {
                return "Cerrada";
            }
            LocalDate hoy = fechaHoy;
            LocalDate inicio = parseFecha((String) act.get("inicio_inscripcion"));
            LocalDate fin = parseFecha((String) act.get("fin_inscripcion"));
            LocalDate fecha = parseFecha((String) act.get("fecha"));

            if (hoy.isBefore(inicio)) return "Planificada";
            if (!hoy.isBefore(inicio) && !hoy.isAfter(fin)) return "En periodo de inscripcion";
            if (hoy.isAfter(fin) && hoy.isBefore(fecha)) return "Inscripcion cerrada";
            if (!hoy.isBefore(fecha)) return "Finalizada";
            if(hoy.isEqual(fecha)) return "En curso";
        } catch (Exception e) {
            return "Estado desconocido";
        }
        return "Estado desconocido";
    }
    
    private LocalDate parseFecha(String fechaStr) {
        if (fechaStr == null) return null;
        fechaStr = fechaStr.trim();
        if (fechaStr.contains("T")) {
            fechaStr = fechaStr.split("T")[0];
        }
        return LocalDate.parse(fechaStr);
    }
    
 // Obtiene todos los detalles de una actividad
    public Map<String, Object> getActividadDetalles(int idActividad) {
        Map<String,Object> resultado = new HashMap<>();

        // Datos bÃ¯Â¿Â½sicos de la actividad
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
        List<Map<String, Object>> inscripciones = db.executeQueryMap(
        	    "SELECT m.id_matricula, " +
        	    "al.nombre || ' ' || al.apellido AS nombre_alumno, " +
        	    "m.fecha_matricula, " +
        	    "CASE " +
        	    "   WHEN m.esta_pagado = 1 THEN 'Cobrada' " +
        	    "   ELSE 'Pendiente' " +
        	    "END AS estado " +
        	    "FROM Matricula m " +
        	    "JOIN Alumno al ON m.id_alumno = al.id_alumno " +
        	    "WHERE m.id_actividad = ? " +
        	    "AND (m.isCancelada IS NULL OR m.isCancelada = 0)", // âœ… solo no canceladas
        	    idActividad
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
        double gastosConfirmados = gastosEstimados; // asumimos que siempre se confirma remuneraciÃ¯Â¿Â½n

        resultado.put("ingresos_estimados", ingresosEstimados);
        resultado.put("ingresos_confirmados", ingresosConfirmados);
        resultado.put("gastos_estimados", gastosEstimados);
        resultado.put("gastos_confirmados", gastosConfirmados);

        return resultado;
    }
    public Integer getIdAlumnoPorEmail(String email) {
        List<Map<String,Object>> result = db.executeQueryMap(
            "SELECT id_alumno FROM Alumno WHERE email = ?", email
        );
        if (result.isEmpty()) return null;
        return (Integer) result.get(0).get("id_alumno");
    }

    public Integer getIdMatricula(int idAlumno) {
        List<Map<String,Object>> result = db.executeQueryMap(
            "SELECT id_matricula FROM Matricula WHERE id_alumno = ? LIMIT 1", idAlumno
        );
        if (result.isEmpty()) return null;
        return (Integer) result.get(0).get("id_matricula");
    }
	
       public void verTodosLosAlumnosConMatriculas() {
        try {
            // 1Ã¯Â¸ï¿½Ã¢Æ’Â£ Obtener todos los alumnos
            List<Map<String, Object>> alumnos = db.executeQueryMap("SELECT * FROM Alumno ORDER BY nombre");

            if (alumnos.isEmpty()) {
                System.out.println("No hay alumnos registrados en la base de datos.");
                return;
            }

            System.out.println("===== LISTADO DE ALUMNOS Y SUS MATRÃƒï¿½CULAS =====\n");

            for (Map<String, Object> alumno : alumnos) {
                int idAlumno = (int) alumno.get("id_alumno");

                System.out.println(" Alumno: " + alumno.get("nombre") + " " + alumno.get("apellido"));
                System.out.println("   ID: " + idAlumno);
                System.out.println("   Email: " + alumno.get("email"));
                System.out.println("   TelÃƒÂ©fono: " + alumno.get("telefono"));
                
                Object internoValue = alumno.get("es_interno");
                boolean esInterno = false;
                if (internoValue instanceof Boolean) {
                    esInterno = (Boolean) internoValue;
                } else if (internoValue instanceof Integer) {
                    esInterno = ((Integer) internoValue) == 1;
                }

                System.out.println("   Es interno: " + (esInterno ? "SÃƒÂ­" : "No"));
                System.out.println("------------------------------------------------");

                List<Map<String, Object>> matriculas = db.executeQueryMap("""
                    SELECT m.id_matricula, m.fecha_matricula, m.monto_pagado, m.esta_pagado,
                           a.nombre AS actividad_nombre, a.fecha AS actividad_fecha,
                           a.espacio AS actividad_espacio, a.cuota AS actividad_cuota
                    FROM Matricula m
                    JOIN Actividad a ON m.id_actividad = a.id_actividad
                    WHERE m.id_alumno = ?
                    ORDER BY m.fecha_matricula DESC
                """, idAlumno);

                if (matriculas.isEmpty()) {
                    System.out.println("     No tiene matrÃƒÂ­culas registradas.\n");
                    continue;
                }

                System.out.println("    MatrÃƒÂ­culas:");
                for (Map<String, Object> m : matriculas) {
                    System.out.println("   - ID MatrÃƒÂ­cula: " + m.get("id_matricula"));
                    System.out.println("     Actividad: " + m.get("actividad_nombre"));
                    System.out.println("     Fecha actividad: " + m.get("actividad_fecha"));
                    System.out.println("     Espacio: " + m.get("actividad_espacio"));
                    System.out.println("     Cuota: " + m.get("actividad_cuota"));
                    System.out.println("     Fecha matrÃƒÂ­cula: " + m.get("fecha_matricula"));
                    System.out.println("     Monto pagado: " + m.get("monto_pagado"));
                    System.out.println("     Ã‚Â¿EstÃƒÂ¡ pagado?: " + ((Integer) m.get("esta_pagado") == 1 ? "SÃƒÂ­" : "No"));
                    System.out.println("     -------------------------------");
                }

                System.out.println("\n==============================================\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al consultar los alumnos y sus matrÃƒÂ­culas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void verActividades() {
        try {
            List<Map<String, Object>> actividades = db.executeQueryMap("SELECT * FROM Actividad");

            if (actividades.isEmpty()) {
                System.out.println("  La tabla 'Actividad' existe, pero no contiene registros.");
                return;
            }

            System.out.println("===== LISTADO DE ACTIVIDADES =====");
            for (Map<String, Object> a : actividades) {
                System.out.println("ID Actividad: " + a.get("id_actividad"));
                System.out.println("Nombre: " + a.get("nombre"));
                System.out.println("Objetivos: " + a.get("objetivos"));
                System.out.println("Contenidos: " + a.get("contenidos"));
                System.out.println("RemuneraciÃƒÂ³n: " + a.get("remuneracion"));
                System.out.println("Espacio: " + a.get("espacio"));
                System.out.println("Fecha: " + a.get("fecha"));
                System.out.println("Hora inicio: " + a.get("hora_inicio"));
                System.out.println("Hora fin: " + a.get("hora_fin"));
                System.out.println("Inicio inscripciÃƒÂ³n: " + a.get("inicio_inscripcion"));
                System.out.println("Fin inscripciÃƒÂ³n: " + a.get("fin_inscripcion"));
                System.out.println("Cuota: " + a.get("cuota"));
                System.out.println("Es gratuita: " + a.get("es_gratuita"));
                System.out.println("ID Profesor: " + a.get("id_profesor"));
                System.out.println("-------------------------------------");
            }

        } catch (Exception e) {
            System.out.println(" Error al consultar la tabla 'Actividad': " + e.getMessage());
            System.out.println(" Es posible que la tabla no exista en la base de datos actual.");
        }
    }

    public List<Map<String, Object>> listarActividadesConPagosPendientes() {
        List<Map<String, Object>> actividades = db.executeQueryMap(
            """
            SELECT DISTINCT a.id_actividad, a.nombre, a.inicio_inscripcion, a.fin_inscripcion, a.fecha, a.cuota, a.remuneracion
            FROM Actividad a
            JOIN Matricula m ON a.id_actividad = m.id_actividad
            WHERE m.esta_pagado = 0
            ORDER BY a.fecha
            """
        );

        for (Map<String, Object> act : actividades) {
            act.put("estado", obtenerEstadoActividad(act));
        }

        return actividades;
    }
    
    public void imprimirActividadesConPagosPendientes() {
        List<Map<String,Object>> actividades = listarActividadesConPagosPendientes();
        
        if (actividades.isEmpty()) {
            System.out.println(" No hay actividades con pagos pendientes.");
            return;
        }

        for (Map<String,Object> act : actividades) {
            System.out.println("===== Actividad con pago pendiente =====");
            System.out.println("ID: " + act.get("id_actividad"));
            System.out.println("Nombre: " + act.get("nombre"));
            System.out.println("Inicio inscripciÃƒÂ³n: " + act.get("inicio_inscripcion"));
            System.out.println("Fin inscripciÃƒÂ³n: " + act.get("fin_inscripcion"));
            System.out.println("Fecha actividad: " + act.get("fecha"));
            System.out.println("Cuota: " + act.get("cuota"));
            System.out.println("RemuneraciÃƒÂ³n: " + act.get("remuneracion"));
            System.out.println("Estado: " + act.get("estado"));
            System.out.println("====================");
        }
    }
    
    public void imprimirPagosPendientesDetallado() {
        System.out.println("===== LISTADO DETALLADO DE PAGOS PENDIENTES =====");

        List<Map<String, Object>> actividades = listarActividadesConPagosPendientes();

        if (actividades.isEmpty()) {
            System.out.println(" No hay actividades con pagos pendientes.");
            return;
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Map<String, Object> act : actividades) {
            int idActividad = ((Number) act.get("id_actividad")).intValue();
            String nombreActividad = (String) act.get("nombre");
            double cuota = ((Number) act.get("cuota")).doubleValue();
            String inicio = (String) act.get("inicio_inscripcion");
            String fin = (String) act.get("fin_inscripcion");
            String fechaActividad = (String) act.get("fecha");

            System.out.println("\n ACTIVIDAD: " + nombreActividad.toUpperCase());
            System.out.println("   ID: " + idActividad);
            System.out.println("   Cuota: " + cuota + " Ã¢â€šÂ¬");
            System.out.println("   Periodo inscripciÃƒÂ³n: " + inicio + " Ã¢â€ â€™ " + fin);
            System.out.println("   Fecha actividad: " + fechaActividad);

            List<Map<String, Object>> inscripcionesPendientes = db.executeQueryMap("""
                SELECT 
                    m.id_matricula,
                    m.fecha_matricula,
                    al.nombre || ' ' || al.apellido AS alumno,
                    a.nombre AS actividad,
                    a.cuota AS cuota,
                    a.inicio_inscripcion,
                    a.fin_inscripcion
                FROM Matricula m
                JOIN Alumno al ON m.id_alumno = al.id_alumno
                JOIN Actividad a ON m.id_actividad = a.id_actividad
                WHERE m.esta_pagado = 0
                  AND a.id_actividad = ?
                ORDER BY m.fecha_matricula
            """, idActividad);

            if (inscripcionesPendientes.isEmpty()) {
                System.out.println("   Todos los alumnos han pagado.");
            } else {
                for (Map<String, Object> ins : inscripcionesPendientes) {
                    String fechaMatriculaStr = (String) ins.get("fecha_matricula");
                    LocalDate fechaMatricula = LocalDate.parse(fechaMatriculaStr);
                    LocalDate fechaMaxPago = fechaMatricula.plusDays(2);

                    System.out.println("   Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬");
                    System.out.println("   Alumno: " + ins.get("alumno"));
                    System.out.println("   Fecha inscripciÃƒÂ³n: " + fechaMatricula.format(formato));
                    System.out.println("   ÃƒÅ¡ltimo dÃƒÂ­a permitido para pago: " + fechaMaxPago.format(formato));
                    System.out.println("   Monto pendiente: " + ins.get("cuota") + " Ã¢â€šÂ¬");
                    System.out.println("   Periodo de pago de la actividad: " 
                            + ins.get("inicio_inscripcion") + " Ã¢â€ â€™ " + ins.get("fin_inscripcion"));
                }
            }

            System.out.println("--------------------------------------------------");
        }
    }

    
    public LocalDate getFechaMatricula(int idMatricula) {
        try {
            List<Map<String, Object>> result = db.executeQueryMap(
                    "SELECT fecha_matricula FROM Matricula WHERE id_matricula = ?",
                    idMatricula
            );

            if (result.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No se encontrÃƒÂ³ la matrÃƒÂ­cula especificada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            String fechaStr = (String) result.get(0).get("fecha_matricula");
            return LocalDate.parse(fechaStr);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al obtener la fecha de matrÃƒÂ­cula.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    public boolean registrarPago(int idMatricula, double montoPagado, LocalDate fechaPago) {
        try {
            // Obtener datos de la matrÃƒÂ­cula y actividad
            List<Map<String, Object>> datos = db.executeQueryMap("""
                SELECT a.id_actividad, a.cuota, a.total_plazas,
                       (SELECT COUNT(*) 
                        FROM Matricula 
                        WHERE id_actividad = a.id_actividad AND esta_pagado = 1) AS plazas_ocupadas
                FROM Matricula m
                JOIN Actividad a ON m.id_actividad = a.id_actividad
                WHERE m.id_matricula = ?
            """, idMatricula);

            if (datos.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No se encontrÃƒÂ³ la matrÃƒÂ­cula o la actividad asociada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Map<String, Object> info = datos.get(0);
            double cuota = ((Number) info.get("cuota")).doubleValue();
            int totalPlazas = ((Number) info.get("total_plazas")).intValue();
            int plazasOcupadas = ((Number) info.get("plazas_ocupadas")).intValue();

            // Validar monto
            if (Math.abs(montoPagado - cuota) > 0.01) {
                JOptionPane.showMessageDialog(null,
                        "La cantidad pagada debe coincidir con la cuota del curso (" + cuota + " Ã¢â€šÂ¬).",
                        "Monto incorrecto", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Registrar pago
            db.executeUpdate("""
                UPDATE Matricula
                SET monto_pagado = ?, esta_pagado = 1
                WHERE id_matricula = ?
            """, montoPagado, idMatricula);

            // Recalcular plazas ocupadas despuÃƒÂ©s del pago
            plazasOcupadas++; // ya se acaba de registrar este pago
            boolean hayPlazas = plazasOcupadas <= totalPlazas;

            return hayPlazas;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al registrar el pago en la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
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

	public boolean introduce(MensajeError msj) {
		if(!comprobarPlazos()) {
			msj.setMensaje("Fuera de plazo");
			System.out.println("Fuera de plazo");
			return false;
		}
		insertarAlumno();
		
		
		return insertarMatricula(msj);
		
	}

	private boolean comprobarPlazos() {
		LocalDate hoy = getFecha();
	    LocalDate inicio = ac.getInicio_insc();
	    LocalDate fin = ac.getFin_inscr();

	    // Devuelve true si hoy estÃ¯Â¿Â½ entre inicio y fin (inclusive)
	    return !hoy.isBefore(inicio) && !hoy.isAfter(fin);
	}

	private boolean insertarMatricula(MensajeError msj) {
		if(!comprobarPlazasActividad()) {
			msj.setMensaje("No hay plazas disponibles");
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
				    System.out.println("No se insertÃ¯Â¿Â½ al alumno.");
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
	
	
	public List<Factura> recuperarActividadesEnRango() {
	   
	    List<Factura> listaActividades = new ArrayList<>();
	    LocalDate fechaMax = LocalDate.of(fechaHoy.getYear(), 12, 31);
	    LocalDate fechaMin = LocalDate.of(fechaHoy.getYear(), 1, 1);
	    String sql = "SELECT * FROM Actividad WHERE fecha >= ? AND fecha <= ? ORDER BY fecha ASC";
	    
	    List<Map<String, Object>> resultados = db.executeQueryMap(sql, fechaMin.toString(),fechaMax.toString());

	    for (Map<String, Object> fila : resultados) {
	        Factura f = new Factura((int)fila.get("total_plazas"));
	        f.setId_actividad((int) fila.get("id_actividad"));
	        f.setNombre((String) fila.get("nombre"));
	        f.setGastos(((Number) fila.get("remuneracion")).doubleValue());
	        f.setIngresos(((Number) fila.get("cuota")).doubleValue());
	        f.setFecha(LocalDate.parse((String) fila.get("fecha")));
	        f.setEstimado(((Number) fila.get("cuota")).doubleValue());
	        f.setBalance(((Number) fila.get("cuota")).doubleValue(),(int)fila.get("total_plazas")-recuperarPlazasLibres(f.getId_actividad()));
	        
	        f.setEstado((String)getActividadDetalles(f.getId_actividad()).get("estado"));
	        System.out.println("Estado "+ f.getEstado());

	        listaActividades.add(f);
	    }
	    
	    return listaActividades;
	}
	
	public List<Factura> recuperarActividadesEnRango(LocalDate min, LocalDate max) {
		   
	    List<Factura> listaActividades = new ArrayList<>();
	    String sql = "SELECT * FROM Actividad WHERE fecha >= ? AND fecha <= ? ORDER BY fecha ASC";
	    
	    List<Map<String, Object>> resultados = db.executeQueryMap(sql, min.toString(),max.toString());

	    for (Map<String, Object> fila : resultados) {
	        Factura f = new Factura((int)fila.get("total_plazas"));
	        f.setId_actividad((int) fila.get("id_actividad"));
	        f.setNombre((String) fila.get("nombre"));
	        f.setGastos(((Number) fila.get("remuneracion")).doubleValue());
	        f.setIngresos(((Number) fila.get("cuota")).doubleValue());
	        f.setFecha(LocalDate.parse((String) fila.get("fecha")));
	        f.setEstimado(((Number) fila.get("cuota")).doubleValue());
	        f.setBalance(((Number) fila.get("cuota")).doubleValue(),(int)fila.get("total_plazas")-recuperarPlazasLibres(f.getId_actividad()));
	        
	        f.setEstado((String)getActividadDetalles(f.getId_actividad()).get("estado"));
	        System.out.println("Estado "+ f.getEstado());

	        listaActividades.add(f);
	    }
	    
	    return listaActividades;
	}
	
	
	private int recuperarPlazasLibres(int id) {
		

		 Map<String, Object> resultados = getActividadDetalles(id);
		 if (resultados.isEmpty()) {
			
			 return Integer.MAX_VALUE;
		 }
		 Number plazasObj = (Number) resultados.get("plazas_disponibles");
		 int plazas_libres = plazasObj.intValue();
		 return plazas_libres;
   
	}
	
	public List<Factura> recuperaAcabadas(){
		List<Factura> lista= recuperarActividadesEnRango();
		List<Factura> acabadas = new ArrayList<>();
		for(Factura f: lista) {
			if(f.estaCerrada()) {
				acabadas.add(f);
			}
		}
		System.out.println("Acbadas: "+ acabadas.size());
		return acabadas;
	}
	
	
	public List<Factura> recuperaSinAcabar(){
		List<Factura> lista= recuperarActividadesEnRango();
		List<Factura> sinAcabar = new ArrayList<>();
		for(Factura f: lista) {
			if(f.estaAbierta()) {
				sinAcabar.add(f);
			}
		}
		System.out.println("Sin acabar = " + sinAcabar.size());
		return sinAcabar;
	}
	
	public String getFacturasTextoAcabadas() {
	    List<Factura> lista = recuperaAcabadas();
	    StringBuilder sb = new StringBuilder();

	    for (Factura f : lista) {
	        sb.append(f);
	    }

	    return sb.toString();
	}
	
	public String getFacturasTextoSinAcabar() {
	    List<Factura> lista = recuperaSinAcabar();
	    StringBuilder sb = new StringBuilder();

	    for (Factura f : lista) {
	    	sb.append(f.toStringSin());
	    }

	    return sb.toString();
	}

	public void setFechaFiltrado(String fechaIn, String fechaFin) {
		fechaFiltrado = new FechaFiltrado(fechaIn, fechaFin,fechaHoy);
		
	}

	public boolean compruebaFormatoFecha(String fecha) {
		if(fecha==null) {
			return false;
		}
		try {
	        LocalDate.parse(fecha);
	        return true;
	    } catch (DateTimeParseException e) {
	        return false;
	    }
        
	}

	public String getFacturasTextoAcabadasEnRango(LocalDate inicio, LocalDate fin) {
		LocalDate fechaInicio;
		LocalDate fechaFin;
		if(inicio==null && fin==null) {
			fechaInicio = LocalDate.of(fechaHoy.getYear(), 1, 1);
			fechaFin = LocalDate.of(fechaHoy.getYear(), 12, 31);
		}else {
			fechaInicio = (inicio != null) ? inicio : LocalDate.of(fechaHoy.getYear(), 1, 1);
		    fechaFin = (fin != null) ? fin : LocalDate.of(fechaHoy.getYear(), 12, 31);
		}
	    
	    List<Factura> lista = recuperaAcabadasEnRango(fechaInicio, fechaFin);

	    
	    StringBuilder sb = new StringBuilder();
	    for (Factura f : lista) {
	        sb.append(f);
	    }

	    return sb.toString();
	}

	private List<Factura> recuperaAcabadasEnRango(LocalDate fechaIn, LocalDate fechaFin) {
		List<Factura> lista= recuperarActividadesEnRango(fechaIn,fechaFin);
		List<Factura> acabadas = new ArrayList<>();
		for(Factura f: lista) {
			if(f.estaCerrada()) {
				acabadas.add(f);
			}
		}
		System.out.println("Acbadas: "+ acabadas.size());
		return acabadas;
	}

	public String getFacturasTextoSinAcabarEnRango(LocalDate inicio, LocalDate fin) {
	    LocalDate fechaInicio = (inicio != null) ? inicio : LocalDate.of(fechaHoy.getYear(), 1, 1);
	    LocalDate fechaFin = (fin != null) ? fin : LocalDate.of(fechaHoy.getYear(), 12, 31);
	    List<Factura> lista = recuperaSinAcabarEnRango(fechaInicio, fechaFin);

	    StringBuilder sb = new StringBuilder();
	    for (Factura f : lista) {
	        sb.append(f.toStringSin());
	    }
	    return sb.toString();
	}

	private List<Factura> recuperaSinAcabarEnRango(LocalDate fechaIn, LocalDate fechaFin) {
		List<Factura> lista= recuperarActividadesEnRango(fechaIn,fechaFin);
		List<Factura> acabadas = new ArrayList<>();
		for(Factura f: lista) {
			if(f.estaAbierta()) {
				acabadas.add(f);
			}
		}
		System.out.println("Acbadas: "+ acabadas.size());
		return acabadas;
	}
	
	//Metodos Pagos profesores
	
	public List<Map<String, Object>> listarActividadesConProfesoresConPagosPendientes() {
	    List<Map<String, Object>> actividades = db.executeQueryMap(
	        """
	        SELECT a.id_actividad, a.nombre, p.id_profesor, p.nombre AS profesor_nombre, p.apellido AS profesor_apellido
	        FROM Actividad a
	        JOIN Profesor p ON a.id_profesor = p.id_profesor
	        WHERE NOT EXISTS (
	            SELECT 1
	            FROM PagoProfesor pp
	            WHERE pp.id_profesor = p.id_profesor
	              AND pp.id_actividad = a.id_actividad
	              AND pp.estado_pago = 'Pagado'
	        )
	        ORDER BY a.fecha
	        """
	    );

	    return actividades;
	}



	
	public Map<String, Object> obtenerDatosProfesorPorActividad(int idActividad) {
	    List<Map<String, Object>> resultados = db.executeQueryMap(
	        """
	        SELECT DISTINCT p.id_profesor,
	               p.nombre AS profesor_nombre,
	               p.apellido AS profesor_apellido,
	               f.emisor_nombre AS profesor_nif,
	               f.emisor_direccion AS profesor_direccion
	        FROM Profesor p
	        JOIN Actividad a ON a.id_profesor = p.id_profesor
	        LEFT JOIN FacturaP f ON f.id_profesor = p.id_profesor
	        WHERE a.id_actividad = ?
	        ORDER BY f.fecha_factura DESC
	        LIMIT 1
	        """,
	        idActividad
	    );

	    if (resultados.isEmpty()) {
	        return null;
	    }

	    return resultados.get(0);
	}

	
	public double obtenerRemuneracionActividad(int idActividad) {
	    List<Map<String, Object>> resultados = db.executeQueryMap(
	        "SELECT remuneracion FROM Actividad WHERE id_actividad = ?",
	        idActividad
	    );
	    if (resultados.isEmpty()) {
	        return 0;
	    }
	    return ((Number) resultados.get(0).get("remuneracion")).doubleValue();
	}
	
	public LocalDate getFechaHoy() {
	    return fechaHoy;
	}
	
	public int obtenerIdFactura(int idProfesor, int idActividad) {
	    List<Map<String, Object>> facturas = db.executeQueryMap(
	        "SELECT id_factura FROM FacturaP WHERE id_profesor = ? AND id_actividad = ?",
	        idProfesor,
	        idActividad
	    );

	    if (facturas.isEmpty()) {
	        throw new RuntimeException("No existe factura para este profesor y actividad.");
	    }

	    return ((Number) facturas.get(0).get("id_factura")).intValue();
	}
	
	public void registrarPagoProfesor(int idProfesor, int idFactura, int idActividad, String fechaPago, double cantidad) {
	    db.executeUpdate(
	        "INSERT INTO PagoProfesor (id_profesor, id_factura, id_actividad, fecha_pago, cantidad, estado_pago) VALUES (?, ?, ?, ?, ?, ?)",
	        idProfesor, idFactura, idActividad, fechaPago, cantidad, "Pagado"
	    );
	}
	public void imprimirPagosProfesor() {
	    List<Map<String, Object>> pagos = db.executeQueryMap("SELECT * FROM PagoProfesor");

	    System.out.println("=== Tabla PagoProfesor ===");
	    for (Map<String, Object> p : pagos) {
	        System.out.println(
	            "id_pago=" + p.get("id_pago") +
	            ", id_profesor=" + p.get("id_profesor") +
	            ", id_factura=" + p.get("id_factura") +
	            ", fecha_pago=" + p.get("fecha_pago") +
	            ", cantidad=" + p.get("cantidad") +
	            ", estado_pago=" + p.get("estado_pago")
	        );
	    }
	    System.out.println("==========================");
	}
	
	public boolean actividadConMovimientosAlumnos(int idActividad) {
	    String sql = "SELECT COUNT(*) AS total FROM Matricula WHERE id_actividad = ? AND esta_pagado = 0";
	    List<Map<String, Object>> result = db.executeQueryMap(sql, idActividad);
	    int pendientes = ((Number) result.get(0).get("total")).intValue();
	    return pendientes > 0;
	}
	
	public boolean actividadConMovimientosProfesores(int idActividad) {
	    String sql = "SELECT COUNT(*) AS total FROM PagoProfesor WHERE id_actividad = ?";
	    List<Map<String, Object>> result = db.executeQueryMap(sql, idActividad);
	    int pagos = ((Number) result.get(0).get("total")).intValue();
	    return pagos > 0;
	}
	
	public boolean cerrarActividad(int idActividad) {
	    try {
	        String sql = "UPDATE Actividad SET isClosed = 1 WHERE id_actividad = ?";
	        db.executeUpdate(sql, idActividad);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	// Lista las actividades filtradas por estado
    public List<Map<String, Object>> listarActividadesPorEstado(String estado) {
        String sql = """
            SELECT id_actividad, nombre, inicio_inscripcion, fin_inscripcion, fecha, 
                   cuota, remuneracion
            FROM Actividad
            ORDER BY fecha
        """;

        // Obtenemos todas las actividades
        List<Map<String, Object>> actividades = db.executeQueryMap(sql);

        // Filtramos las que coincidan con el estado solicitado
        java.util.List<Map<String, Object>> filtradas = new java.util.ArrayList<>();
        for (Map<String, Object> act : actividades) {
            String estadoActual = obtenerEstadoActividad(act);
            if (estadoActual.equalsIgnoreCase(estado)) {
                act.put("estado", estadoActual);
                filtradas.add(act);
            }
        }

        return filtradas;
    }
    
    public List<Map<String, Object>> listarAlumnos() {
        return db.executeQueryMap("SELECT id_alumno, nombre, apellido FROM Alumno ORDER BY nombre");
    }

    public List<Map<String, Object>> listarMatriculasPorAlumno(int idAlumno) {
        String sql = """
            SELECT m.id_matricula, m.id_actividad, a.nombre AS actividad, a.fecha, 
                   m.monto_pagado, m.isCancelada
            FROM Matricula m 
            JOIN Actividad a ON m.id_actividad = a.id_actividad
            WHERE m.id_alumno = ? AND m.isCancelada = 0
            ORDER BY a.fecha
            """;
        return db.executeQueryMap(sql, idAlumno);
    }

    public double calcularMontoDevolucion(LocalDate fechaActividad, double montoPagado) {
        long diasFaltan = java.time.temporal.ChronoUnit.DAYS.between(fechaHoy, fechaActividad);
        if (diasFaltan >= 7) return montoPagado;
        else if (diasFaltan >= 3) return montoPagado * 0.5;
        else return 0;
    }

    public void registrarDevolucion(int idMatricula, int idAlumno, int idActividad, double montoDevuelto) {
        LocalDate hoy = fechaHoy != null ? fechaHoy : LocalDate.now();

        db.executeUpdate("""
            INSERT INTO Devoluciones (id_matricula, id_alumno, id_actividad, fecha_solicitada, fecha_enviada, monto_devuelto)
            VALUES (?, ?, ?, ?, ?, ?)
            """, idMatricula, idAlumno, idActividad, hoy.toString(), hoy.toString(), montoDevuelto);

        db.executeUpdate("UPDATE Matricula SET isCancelada = 1 WHERE id_matricula = ?", idMatricula);
    }
    
    

    public int obtenerIdActividadPorMatricula(int idMatricula) {
        String sql = "SELECT id_actividad FROM Matricula WHERE id_matricula = ?";
        List<Map<String, Object>> result = db.executeQueryMap(sql, idMatricula);
        return result.isEmpty() ? 0 : (int) result.get(0).get("id_actividad");
    }
    
    public Alumno getInformacionAlumno() {
    	if(idAlumnoInscrip==0) return null;
    	else {
    		Alumno a = getAlumnoById();
    		return a;
    	}
    }
    
    private Alumno getAlumnoById() {
    	Alumno a = new Alumno();
    	List<Map<String, Object>> consulta = db.executeQueryMap("SELECT nombre, apellido, email, telefono, es_interno FROM Alumno where id_alumno = " + getIdAlumnoInscrip());
    	Map<String, Object> row = consulta.get(0);
    	a.setNombre((String)row.get("nombre"));
    	a.setApellidos((String)row.get("apellido"));
    	a.setCorreo((String)row.get("email"));
    	a.setNumeroTf((String)row.get("telefono"));
    	a.setPerteneceDB((int)row.get("es_interno"));
    	return a;
    }

	public FechaFiltrado getFechaFiltrado() {
		return fechaFiltrado;
	}

}