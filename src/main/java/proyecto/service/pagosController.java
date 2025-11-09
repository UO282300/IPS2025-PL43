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

public class pagosController {
	private Database db;
	private Alumno a = new Alumno();
	private Actividad ac;
	private LocalDate fechaHoy;
	private FechaFiltrado fechaFiltrado;
	private int idAlumnoCancel;
	private int idAlumnoInscrip;
	
	public pagosController (UserService us) {
		this.fechaHoy = us.getFechaHoy();
		this.db = new Database();
        crearDataBase();
        cargarDataBase();
	}
	
	public void crearDataBase() {
    	db.createDatabase(false);
    }
    
    public void cargarDataBase() {
    	db.loadDatabase();
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

        List<Map<String,Object>> actividades = db.executeQueryMap(
            "SELECT * FROM Actividad WHERE id_actividad = ?", idActividad
        );
        if (actividades.isEmpty()) return null;
        Map<String,Object> act = actividades.get(0);
        resultado.putAll(act);
        resultado.put("estado", obtenerEstadoActividad(act));

        // Plazas Ocupadas, totales y disponibles
        int plazasOcupadas = db.executeQueryMap(
        	    "SELECT COUNT(*) as total FROM Matricula WHERE id_actividad = ? AND esta_pagado = 1 AND (isCancelada IS NULL OR isCancelada = 0)",
        	    idActividad
        	).get(0).get("total") == null ? 0 : ((Number) db.executeQueryMap(
        	    "SELECT COUNT(*) as total FROM Matricula WHERE id_actividad = ? AND esta_pagado = 1 AND (isCancelada IS NULL OR isCancelada = 0)",
        	    idActividad
        	).get(0).get("total")).intValue();
        
        int totalPlazas = ((Number) db.executeQueryMap("SELECT total_plazas as total FROM Actividad WHERE id_Actividad=?", idActividad).get(0).get("total")).intValue();


        int plazasDisponibles = totalPlazas - plazasOcupadas;
        resultado.put("plazas_disponibles", plazasDisponibles);

        // Inscripciones
        List<Map<String, Object>> inscripciones = db.executeQueryMap(
        	    "SELECT m.id_matricula, " +
        	    "al.nombre, " +
        	    "al.apellido, " +
        	    "al.telefono, " +
        	    "al.nombre || ' ' || al.apellido AS nombre_alumno, " +
        	    "m.fecha_matricula, " +
        	    "CASE " +
        	    "   WHEN m.esta_pagado = 1 THEN 'Cobrada' " +
        	    "   ELSE 'Pendiente' " +
        	    "END AS estado " +
        	    "FROM Matricula m " +
        	    "JOIN Alumno al ON m.id_alumno = al.id_alumno " +
        	    "WHERE m.id_actividad = ? " +
        	    "AND (m.isCancelada IS NULL OR m.isCancelada = 0)",
        	    idActividad
        	    );
        
        for (Map<String, Object> ins : inscripciones) {
            Object fechaMatriculaObj = ins.get("fecha_matricula");
            if (fechaMatriculaObj != null) {
                try {
                    LocalDate fechaMatricula = LocalDate.parse(fechaMatriculaObj.toString());
                    LocalDate fechaLimite = fechaMatricula.plusDays(2);
                    ins.put("fecha_limite_pago", fechaLimite.toString());
                } catch (Exception e) {
                    ins.put("fecha_limite_pago", "-");
                }
            } else {
                ins.put("fecha_limite_pago", "-");
            }
        }
        
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
                        "No se encontró la matrícula o la actividad asociada.",
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
    
    public LocalDate getFechaHoy() {
	    return fechaHoy;
	}
    
    public List<Map<String, Object>> listarActividadesConProfesoresConPagosPendientes() {
        return db.executeQueryMap(
            """
            SELECT 
                a.id_actividad, 
                a.nombre, 
                a.remuneracion,
                p.id_profesor, 
                p.nombre AS profesor_nombre, 
                p.apellido AS profesor_apellido
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
    
    public Map<String, Object> obtenerDatosFacturaPorProfesorYActividad(int idProfesor, int idActividad) {
        List<Map<String, Object>> resultados = db.executeQueryMap(
            """
            SELECT 
                f.id_factura,
                f.numero_factura,
                f.emisor_direccion AS direccion_emisor,
                f.cantidad,
                f.fecha_factura AS fecha
            FROM FacturaP f
            WHERE f.id_profesor = ? 
              AND f.id_actividad = ?
            ORDER BY f.fecha_factura DESC
            LIMIT 1
            """,
            idProfesor, idActividad
        );

        if (resultados.isEmpty()) {
            return null;
        }

        return resultados.get(0);
    }



}