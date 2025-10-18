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
    public void eliminarTodosLosDatos() {
        try {
            // 1️⃣ Eliminar los registros de las tablas en orden para respetar claves foráneas
            db.executeUpdate("DELETE FROM Matricula");
            db.executeUpdate("DELETE FROM Actividad");
            db.executeUpdate("DELETE FROM Alumno");
            db.executeUpdate("DELETE FROM Profesor");
            db.executeUpdate("DELETE FROM Administrador");

            // 2️⃣ Reiniciar los contadores AUTOINCREMENT
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Matricula'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Actividad'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Alumno'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Profesor'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Administrador'");

            JOptionPane.showMessageDialog(null,
                    "Todos los datos han sido eliminados y los IDs reiniciados.",
                    "Operación completada", JOptionPane.INFORMATION_MESSAGE);

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
	            throw new ApplicationException("Formato de fecha invalido. Usa el formato dd/MM/yyyy");
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
            System.out.println("Inicio inscripción: " + act.get("inicio_inscripcion"));
            System.out.println("Fin inscripción: " + act.get("fin_inscripcion"));
            System.out.println("Fecha actividad: " + act.get("fecha"));
            System.out.println("Cuota: " + act.get("cuota"));
            System.out.println("Remuneración: " + act.get("remuneracion"));
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
                System.out.println(" No hay matrículas registradas en la base de datos.");
                return;
            }

            System.out.println("===== LISTADO COMPLETO DE MATRÍCULAS =====\n");
            for (Map<String, Object> m : matriculas) {
                System.out.println("ID Matrícula: " + m.get("id_matricula"));
                System.out.println("Alumno: " + m.get("alumno_nombre"));
                System.out.println("Email: " + m.get("alumno_email"));
                System.out.println("Teléfono: " + m.get("alumno_telefono"));
                System.out.println("Actividad: " + m.get("actividad_nombre"));
                System.out.println("Fecha actividad: " + m.get("actividad_fecha"));
                System.out.println("Espacio: " + m.get("actividad_espacio"));
                System.out.println("Cuota: " + m.get("actividad_cuota"));
                System.out.println("Fecha matrícula: " + m.get("fecha_matricula"));
                System.out.println("Monto pagado: " + m.get("monto_pagado"));
                System.out.println("¿Está pagado?: " + (((Integer)m.get("esta_pagado")) == 1 ? "Sí" : "No"));
                System.out.println("----------------------------------------\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Error al consultar las matrículas.");
        }
    }

    
 // Calcula el estado de la actividad
    public String obtenerEstadoActividad(Map<String,Object> act) {
        try {
            java.time.LocalDate hoy = java.time.LocalDate.now();
            java.time.LocalDate inicio = java.time.LocalDate.parse((String) act.get("inicio_inscripcion"));
            java.time.LocalDate fin = java.time.LocalDate.parse((String) act.get("fin_inscripcion"));
            java.time.LocalDate fecha = java.time.LocalDate.parse((String) act.get("fecha"));

            if (hoy.isBefore(inicio)) return "Planificada";
            if (!hoy.isBefore(inicio) && !hoy.isAfter(fin)) return "En periodo de inscripcion";
            if (hoy.isAfter(fin) && hoy.isBefore(fecha)) return "Inscripcion cerrada";
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
        	    "AND m.esta_pagado = 0", // 👈 Solo matrículas impagas
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
        double gastosConfirmados = gastosEstimados; // asumimos que siempre se confirma remuneraci�n

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
            // 1️⃣ Obtener todos los alumnos
            List<Map<String, Object>> alumnos = db.executeQueryMap("SELECT * FROM Alumno ORDER BY nombre");

            if (alumnos.isEmpty()) {
                System.out.println("No hay alumnos registrados en la base de datos.");
                return;
            }

            System.out.println("===== LISTADO DE ALUMNOS Y SUS MATRÍCULAS =====\n");

            for (Map<String, Object> alumno : alumnos) {
                int idAlumno = (int) alumno.get("id_alumno");

                System.out.println(" Alumno: " + alumno.get("nombre") + " " + alumno.get("apellido"));
                System.out.println("   ID: " + idAlumno);
                System.out.println("   Email: " + alumno.get("email"));
                System.out.println("   Teléfono: " + alumno.get("telefono"));
                
                Object internoValue = alumno.get("es_interno");
                boolean esInterno = false;
                if (internoValue instanceof Boolean) {
                    esInterno = (Boolean) internoValue;
                } else if (internoValue instanceof Integer) {
                    esInterno = ((Integer) internoValue) == 1;
                }

                System.out.println("   Es interno: " + (esInterno ? "Sí" : "No"));
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
                    System.out.println("     No tiene matrículas registradas.\n");
                    continue;
                }

                System.out.println("    Matrículas:");
                for (Map<String, Object> m : matriculas) {
                    System.out.println("   - ID Matrícula: " + m.get("id_matricula"));
                    System.out.println("     Actividad: " + m.get("actividad_nombre"));
                    System.out.println("     Fecha actividad: " + m.get("actividad_fecha"));
                    System.out.println("     Espacio: " + m.get("actividad_espacio"));
                    System.out.println("     Cuota: " + m.get("actividad_cuota"));
                    System.out.println("     Fecha matrícula: " + m.get("fecha_matricula"));
                    System.out.println("     Monto pagado: " + m.get("monto_pagado"));
                    System.out.println("     ¿Está pagado?: " + ((Integer) m.get("esta_pagado") == 1 ? "Sí" : "No"));
                    System.out.println("     -------------------------------");
                }

                System.out.println("\n==============================================\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al consultar los alumnos y sus matrículas.",
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
                System.out.println("Remuneración: " + a.get("remuneracion"));
                System.out.println("Espacio: " + a.get("espacio"));
                System.out.println("Fecha: " + a.get("fecha"));
                System.out.println("Hora inicio: " + a.get("hora_inicio"));
                System.out.println("Hora fin: " + a.get("hora_fin"));
                System.out.println("Inicio inscripción: " + a.get("inicio_inscripcion"));
                System.out.println("Fin inscripción: " + a.get("fin_inscripcion"));
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
            System.out.println("Inicio inscripción: " + act.get("inicio_inscripcion"));
            System.out.println("Fin inscripción: " + act.get("fin_inscripcion"));
            System.out.println("Fecha actividad: " + act.get("fecha"));
            System.out.println("Cuota: " + act.get("cuota"));
            System.out.println("Remuneración: " + act.get("remuneracion"));
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
            System.out.println("   Cuota: " + cuota + " €");
            System.out.println("   Periodo inscripción: " + inicio + " → " + fin);
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

                    System.out.println("   ───────────────────────────────");
                    System.out.println("   Alumno: " + ins.get("alumno"));
                    System.out.println("   Fecha inscripción: " + fechaMatricula.format(formato));
                    System.out.println("   Último día permitido para pago: " + fechaMaxPago.format(formato));
                    System.out.println("   Monto pendiente: " + ins.get("cuota") + " €");
                    System.out.println("   Periodo de pago de la actividad: " 
                            + ins.get("inicio_inscripcion") + " → " + ins.get("fin_inscripcion"));
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
                        "No se encontró la matrícula especificada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            String fechaStr = (String) result.get(0).get("fecha_matricula");
            return LocalDate.parse(fechaStr);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al obtener la fecha de matrícula.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    public boolean registrarPago(int idMatricula, double montoPagado, LocalDate fechaPago) {
        try {
            // Obtener datos de la matrícula y actividad
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
                        "No se encontró la matrícula o la actividad asociada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Map<String, Object> info = datos.get(0);
            int idActividad = ((Number) info.get("id_actividad")).intValue();
            double cuota = ((Number) info.get("cuota")).doubleValue();
            int totalPlazas = ((Number) info.get("total_plazas")).intValue();
            int plazasOcupadas = ((Number) info.get("plazas_ocupadas")).intValue();

            // Validar monto
            if (Math.abs(montoPagado - cuota) > 0.01) {
                JOptionPane.showMessageDialog(null,
                        "La cantidad pagada debe coincidir con la cuota del curso (" + cuota + " €).",
                        "Monto incorrecto", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Registrar pago
            db.executeUpdate("""
                UPDATE Matricula
                SET monto_pagado = ?, esta_pagado = 1
                WHERE id_matricula = ?
            """, montoPagado, idMatricula);

            // Recalcular plazas ocupadas después del pago
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

    
}
    
