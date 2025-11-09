package proyecto.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import proyecto.model.Database;


public class PagosController {
	private Database db;
	private LocalDate fechaHoy;
	
	public PagosController (UserService us) {
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

 
 // Ventana Registrar Pagos Alumnos  

    public String obtenerEstadoActividad(Map<String,Object> act) {
        try {
        	Object closed = act.get("isClosed");
            if (closed.equals(1)) {
                return "Cerrada";
            }
            LocalDate hoy = fechaHoy;
            LocalDate inicio = parseFecha((String) act.get("inicio_inscripcion"));
            LocalDate fin = parseFecha((String) act.get("fin_inscripcion"));
            LocalDate fecha = parseFecha((String) act.get("fecha_inicio"));

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

    public Map<String, Object> getActividadDetalles(int idActividad) {
        Map<String,Object> resultado = new HashMap<>();

        List<Map<String,Object>> actividades = db.executeQueryMap(
            "SELECT * FROM Actividad WHERE id_actividad = ?", idActividad
        );
        if (actividades.isEmpty()) return null;
        Map<String,Object> act = actividades.get(0);
        resultado.putAll(act);
        resultado.put("estado", obtenerEstadoActividad(act));

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
                        "No se encuentra la matrícula especificada.",
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
    
    public List<Map<String, Object>> listarActividades() {
        List<Map<String, Object>> actividades = db.executeQueryMap(
            "SELECT id_actividad, nombre, inicio_inscripcion, fin_inscripcion, fecha_inicio, " +
            "cuota, isClosed " +
            "FROM Actividad ORDER BY fecha_inicio"
        );

        for (Map<String, Object> act : actividades) {
            act.put("estado", obtenerEstadoActividad(act));
        }
        return actividades;
    }


    public boolean registrarPago(int idMatricula, double montoPagado, LocalDate fechaPago) {
        try {
            db.executeUpdate(
                "INSERT INTO PagoAlumno (id_matricula, fecha_pago, cantidad) VALUES (?, ?, ?)",
                idMatricula, fechaPago.toString(), montoPagado
            );

            Map<String, Object> info = db.executeQueryMap(
                "SELECT a.id_actividad, a.cuota, IFNULL(SUM(p.cantidad), 0) AS total_pagado " +
                "FROM Matricula m " +
                "JOIN Actividad a ON m.id_actividad = a.id_actividad " +
                "LEFT JOIN PagoAlumno p ON m.id_matricula = p.id_matricula " +
                "WHERE m.id_matricula = ? " +
                "GROUP BY a.id_actividad, a.cuota",
                idMatricula
            ).get(0);

            int idActividad = ((Number) info.get("id_actividad")).intValue();
            double cuota = ((Number) info.get("cuota")).doubleValue();
            double totalPagado = ((Number) info.get("total_pagado")).doubleValue();

            boolean estaPagado = false;
            if (totalPagado >= cuota - 0.01) {
                estaPagado = true;
            }

            db.executeUpdate(
                "UPDATE Matricula SET monto_pagado = ?, esta_pagado = ? WHERE id_matricula = ?",
                totalPagado, estaPagado ? 1 : 0, idMatricula
            );

            if (estaPagado) {
                List<Map<String, Object>> res = db.executeQueryMap(
                    "SELECT (a.total_plazas - COUNT(CASE WHEN m.esta_pagado = 1 AND (m.isCancelada IS NULL OR m.isCancelada = 0) THEN 1 END)) AS plazas_libres " +
                    "FROM Actividad a " +
                    "LEFT JOIN Matricula m ON a.id_actividad = m.id_actividad " +
                    "WHERE a.id_actividad = ? " +
                    "GROUP BY a.id_actividad",
                    idActividad
                );

                int plazasLibres = ((Number) res.get(0).get("plazas_libres")).intValue();

                if (plazasLibres < 0) {
                    db.executeUpdate(
                        "UPDATE Matricula SET isCancelada = 1 WHERE id_matricula = ?",
                        idMatricula
                    );

                    JOptionPane.showMessageDialog(
                        null,
                        "No quedaban plazas disponibles.\n\n" +
                        "La matrícula ha sido cancelada automáticamente.\n" +
                        "El pago realizado queda registrado y podrá gestionarse manualmente desde la ventana de devoluciones.",
                        "Matrícula cancelada",
                        JOptionPane.WARNING_MESSAGE
                    );

                    return true;
                }
            }

            
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Error al registrar el pago:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    } 


    
    public LocalDate getFechaHoy() {
	    return fechaHoy;
	}
    
    //Pagos alumnos devoluciones
    
    public Map<String, Double> getEstadoPagoAlumno(int idMatricula) {
        Map<String, Double> datos = new HashMap<>();

        try {
            // Obtener matrícula y actividad
            List<Map<String, Object>> result = db.executeQueryMap("""
                SELECT a.cuota, m.monto_pagado, m.isCancelada, a.id_actividad, m.id_alumno
                FROM Matricula m
                JOIN Actividad a ON m.id_actividad = a.id_actividad
                WHERE m.id_matricula = ?
            """, idMatricula);

            if (result.isEmpty()) return null;

            double cuota = ((Number) result.get(0).get("cuota")).doubleValue();
            double totalPagado = ((Number) result.get(0).get("monto_pagado")).doubleValue();
            boolean isCancelada = result.get(0).get("isCancelada") != null && ((Number) result.get(0).get("isCancelada")).intValue() == 1;

            // Sumar todas las devoluciones
            List<Map<String, Object>> devoluciones = db.executeQueryMap("""
                SELECT IFNULL(SUM(monto_devuelto), 0) AS total_devuelto
                FROM Devoluciones
                WHERE id_matricula = ?
            """, idMatricula);

            double totalDevuelto = ((Number) devoluciones.get(0).get("total_devuelto")).doubleValue();

            double neto = totalPagado - totalDevuelto;
            double pendiente;
            double aDevolver;

            if (isCancelada) {
                pendiente = Math.max(0, -neto);
                aDevolver = Math.max(0, neto);
            } else {
                pendiente = Math.max(0, cuota - neto);
                aDevolver = Math.max(0, neto - cuota);
            }

            totalPagado = Math.round(totalPagado * 100.0) / 100.0;
            totalDevuelto = Math.round(totalDevuelto * 100.0) / 100.0;
            pendiente = Math.round(pendiente * 100.0) / 100.0;
            aDevolver = Math.round(aDevolver * 100.0) / 100.0;

            datos.put("cuota", cuota);
            datos.put("total_pagado", totalPagado);
            datos.put("total_devuelto", totalDevuelto);
            datos.put("pendiente", pendiente);
            datos.put("a_devolver", aDevolver);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return datos;
    }


    public boolean registrarDevolucion(int idMatricula, double montoDevuelto, LocalDate fechaDevolucion) {
        try {
            List<Map<String, Object>> datos = db.executeQueryMap("""
                SELECT 
                    m.id_alumno, 
                    m.id_actividad,
                    m.monto_pagado,
                    a.cuota
                FROM Matricula m
                JOIN Actividad a ON m.id_actividad = a.id_actividad
                WHERE m.id_matricula = ?
            """, idMatricula);

            if (datos.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No se encontró la matrícula especificada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Map<String, Object> info = datos.get(0);
            int idAlumno = ((Number) info.get("id_alumno")).intValue();
            int idActividad = ((Number) info.get("id_actividad")).intValue();
            double pagado = ((Number) info.get("monto_pagado")).doubleValue();

            if (montoDevuelto <= 0) {
                JOptionPane.showMessageDialog(null,
                        "El monto devuelto debe ser mayor que 0.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // --- Insertar registro en la tabla Devoluciones ---
            db.executeUpdate("""
                INSERT INTO Devoluciones (id_matricula, id_alumno, id_actividad, fecha_solicitada, fecha_enviada, monto_devuelto)
                VALUES (?, ?, ?, ?, ?, ?)
            """, idMatricula, idAlumno, idActividad, fechaDevolucion.toString(), fechaHoy.toString(), montoDevuelto);

            // --- Actualizar estado de pago ---
            double totalPagado = pagado - montoDevuelto; // neto, aunque sea negativo
            boolean estaPagado = totalPagado >= 0; // si queda negativo, sigue siendo "no pagado" o deuda

            db.executeUpdate("""
                UPDATE Matricula
                SET esta_pagado = ?
                WHERE id_matricula = ?
            """, estaPagado ? 1 : 0, idMatricula);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al registrar la devolución en la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    
//Ventana Registrar pagos profesores
    
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
        SELECT DISTINCT 
            p.id_profesor,
            p.nombre AS profesor_nombre,
            p.apellido AS profesor_apellido,
            p.telefono AS profesor_telefono,
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
    
    public void marcarFacturaComoPagada(int idFactura) {
        db.executeUpdate(
            "UPDATE FacturaP SET esta_pagado = 1 WHERE id_factura = ?",
            idFactura
        );
    }
    
    public Map<String, Object> obtenerDatosFacturaPorProfesorYActividad(int idProfesor, int idActividad) {
        List<Map<String, Object>> facturas = db.executeQueryMap(
            """
            SELECT f.id_factura,
                   f.numero_factura,
                   f.emisor_nif,
                   f.emisor_direccion AS direccion_emisor,
                   f.cantidad,
                   f.fecha_factura AS fecha
            FROM FacturaP f
            WHERE f.id_profesor = ? AND f.id_actividad = ?
            ORDER BY f.fecha_factura DESC
            LIMIT 1
            """,
            idProfesor, idActividad
        );

        if (facturas.isEmpty()) return null;
        return facturas.get(0);
    }

    public Map<String, Object> obtenerTotalesFacturaProfesor(int idFactura) {
        List<Map<String, Object>> res = db.executeQueryMap(
            """
            SELECT 
                f.cantidad AS importe_factura,
                COALESCE(SUM(pp.cantidad), 0) AS total_pagado,
                COALESCE((
                    SELECT SUM(dp.cantidad)
                    FROM DevolucionProfesor dp
                    WHERE dp.id_factura = f.id_factura
                ), 0) AS total_devuelto
            FROM FacturaP f
            LEFT JOIN PagoProfesor pp ON pp.id_factura = f.id_factura
            WHERE f.id_factura = ?
            GROUP BY f.id_factura
            """,
            idFactura
        );

        if (res.isEmpty()) {
            return Map.of("importe_factura", 0.0, "total_pagado", 0.0, "total_devuelto", 0.0);
        }

        Map<String, Object> fila = res.get(0);
        return Map.of(
            "importe_factura", ((Number) fila.get("importe_factura")).doubleValue(),
            "total_pagado", ((Number) fila.get("total_pagado")).doubleValue(),
            "total_devuelto", ((Number) fila.get("total_devuelto")).doubleValue()
        );
    }



    
    public List<Map<String, Object>> listarTodosLosCursosConProfesores() {
        return db.executeQueryMap(
            """
            SELECT a.id_actividad, a.nombre, a.cuota, a.fecha_inicio, a.fecha_fin
            FROM Actividad a
            ORDER BY a.fecha_inicio
            """
        );
    }

    
    public List<Map<String, Object>> obtenerProfesoresPorActividad(int idActividad) {
        return db.executeQueryMap(
            """
            SELECT DISTINCT p.id_profesor,
                            p.nombre AS profesor_nombre,
                            p.apellido AS profesor_apellido,
                            p.telefono AS profesor_telefono
            FROM Profesor p
            JOIN FacturaP f ON f.id_profesor = p.id_profesor
            WHERE f.id_actividad = ?
            """,
            idActividad
        );
    }

    
    
    public boolean registrarDevolucionProfesor(int idProfesor, int idActividad, int idFactura, String fecha, double cantidad) {
        try {
            db.executeUpdate(
                """
                INSERT INTO DevolucionProfesor (id_profesor, id_actividad, id_factura, fecha_devolucion, cantidad)
                VALUES (?, ?, ?, ?, ?)
                """,
                idProfesor, idActividad, idFactura, fecha, cantidad
            );
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al registrar la devolución: " + e.getMessage());
            return false;
        }
    }
    public void verificarConsistenciaFinanciera() {
        System.out.println("🔍 Iniciando verificación de consistencia financiera...");

        List<Map<String, Object>> facturas = db.executeQueryMap("""
            SELECT 
                f.id_factura,
                p.nombre || ' ' || p.apellido AS profesor,
                a.nombre AS actividad,
                f.cantidad AS importe_factura,
                COALESCE(SUM(DISTINCT pp.cantidad), 0) AS total_pagado,
                COALESCE(SUM(DISTINCT dp.cantidad), 0) AS total_devuelto
            FROM FacturaP f
            LEFT JOIN Profesor p ON p.id_profesor = f.id_profesor
            LEFT JOIN Actividad a ON a.id_actividad = f.id_actividad
            LEFT JOIN PagoProfesor pp ON pp.id_factura = f.id_factura
            LEFT JOIN DevolucionProfesor dp ON dp.id_factura = f.id_factura
            GROUP BY f.id_factura
        """);

        for (Map<String, Object> row : facturas) {
            double importeFactura = ((Number) row.get("importe_factura")).doubleValue();
            double totalPagado = ((Number) row.get("total_pagado")).doubleValue();
            double totalDevuelto = ((Number) row.get("total_devuelto")).doubleValue();
            double neto = totalPagado - totalDevuelto;

            if (Math.abs(neto - importeFactura) > 0.01) {
                System.out.printf("⚠️ Inconsistencia detectada (Profesor): %s | Actividad: %s | Factura #%s%n", 
                    row.get("profesor"), row.get("actividad"), row.get("id_factura"));
                System.out.printf("   - Importe factura: %.2f | Pagado: %.2f | Devuelto: %.2f | Neto: %.2f%n",
                    importeFactura, totalPagado, totalDevuelto, neto);
            }
        }

        List<Map<String, Object>> matriculas = db.executeQueryMap("""
            SELECT 
                m.id_matricula,
                al.nombre || ' ' || al.apellido AS alumno,
                ac.nombre AS actividad,
                m.monto_pagado,
                COALESCE(SUM(d.monto_devuelto), 0) AS total_devuelto
            FROM Matricula m
            LEFT JOIN Alumno al ON al.id_alumno = m.id_alumno
            LEFT JOIN Actividad ac ON ac.id_actividad = m.id_actividad
            LEFT JOIN Devoluciones d ON d.id_matricula = m.id_matricula
            GROUP BY m.id_matricula
        """);

        for (Map<String, Object> row : matriculas) {
            double pagado = ((Number) row.get("monto_pagado")).doubleValue();
            double devuelto = ((Number) row.get("total_devuelto")).doubleValue();
            if (devuelto > pagado + 0.01) {
                System.out.printf("Error (Alumno): %s | Actividad: %s | Devolvió %.2f€ de %.2f€ pagados.%n",
                    row.get("alumno"), row.get("actividad"), devuelto, pagado);
            } else if (Math.abs(devuelto - pagado) > 0.01) {
                System.out.printf("Pendiente o parcial (Alumno): %s | Actividad: %s | Pagado %.2f€ | Devuelto %.2f€%n",
                    row.get("alumno"), row.get("actividad"), pagado, devuelto);
            }
        }

        System.out.println("Verificación finalizada.");
    }


    

}