package proyecto.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import proyecto.model.Database;


public class PagosController {
	private Database db;
	private LocalDate fechaHoy;
	public static final double LIMITE_EFECTIVO = 100;
	
	public PagosController (UserService us) {
		this.fechaHoy = us.getFechaHoy();
		this.db = new Database();
        //crearDataBase();
        //cargarDataBase();
	}
	public double getLimiteEfectivo() {
	    return LIMITE_EFECTIVO;
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

        // Datos básicos de la actividad
        List<Map<String,Object>> actividades = db.executeQueryMap(
            "SELECT * FROM Actividad WHERE id_actividad = ?", idActividad
        );
        if (actividades.isEmpty()) return null;
        Map<String,Object> act = actividades.get(0);
        resultado.putAll(act);
        resultado.put("estado", obtenerEstadoActividad(act));

        // Plazas ocupadas, totales y disponibles
        int plazasOcupadas = ((Number) db.executeQueryMap(
            "SELECT COUNT(*) AS total FROM Matricula WHERE id_actividad = ?", idActividad
        ).get(0).get("total")).intValue();

        int totalPlazas = ((Number) act.get("total_plazas")).intValue();
        int plazasDisponibles = totalPlazas - plazasOcupadas;
        resultado.put("plazas_disponibles", plazasDisponibles);

        // Inscripciones
        List<Map<String, Object>> inscripciones = db.executeQueryMap(
            "SELECT m.id_matricula, " +
            "al.nombre || ' ' || al.apellido AS nombre_alumno, " +
            "m.fecha_matricula, " +
            "m.monto_pagado, " + // asegurarse de tener este campo
            "CASE WHEN m.esta_pagado = 1 THEN 'Cobrada' ELSE 'Pendiente' END AS estado " +
            "FROM Matricula m " +
            "JOIN Alumno al ON m.id_alumno = al.id_alumno " +
            "WHERE m.id_actividad = ? " +
            "AND (m.isCancelada IS NULL OR m.isCancelada = 0)",
            idActividad
        );
        resultado.put("inscripciones", inscripciones);

        // Finanzas
        double ingresosConfirmados = inscripciones.stream()
                .filter(i -> "Cobrada".equals(i.get("estado")))
                .mapToDouble(i -> {
                    try { return Double.parseDouble(String.valueOf(i.get("monto_pagado"))); }
                    catch(Exception e) { return 0; }
                }).sum();

        // Obtener cuotas asociadas a la actividad
        List<Map<String,Object>> cuotas = db.executeQueryMap(
            "SELECT valor FROM CuotaActividad WHERE id_actividad = ?", idActividad
        );

        double cuotaMedia = 0.0;
        if (!cuotas.isEmpty()) {
            cuotaMedia = cuotas.stream()
                               .mapToDouble(x -> ((Number)x.get("valor")).doubleValue())
                               .average()
                               .orElse(0.0);
        }

        double ingresosEstimados = inscripciones.size() * cuotaMedia;

        // Gastos
        List<Map<String,Object>> facturas = db.executeQueryMap(
            "SELECT cantidad FROM FacturaP WHERE id_actividad = ?", idActividad
        );
        double gastosConfirmados = facturas.stream()
                .mapToDouble(f -> Double.parseDouble(String.valueOf(f.get("cantidad"))))
                .sum();
        double gastosEstimados = gastosConfirmados;

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
            "isClosed " +
            "FROM Actividad ORDER BY fecha_inicio"
        );

        for (Map<String, Object> act : actividades) {
            act.put("estado", obtenerEstadoActividad(act));
        }
        return actividades;
    }


    public boolean registrarPago(int idMatricula, double montoPagado, LocalDate fechaPago, boolean porEfectivo) {
        try {
            String metodoPago = porEfectivo ? "Efectivo" : "Transferencia";

            db.executeUpdate(
                "INSERT INTO PagoAlumno (id_matricula, fecha_pago, cantidad, metodo_pago) VALUES (?, ?, ?, ?)",
                idMatricula, fechaPago.toString(), montoPagado, metodoPago
            );

            // Obtener la matrícula y la cuota asociada
            Map<String, Object> info = db.executeQueryMap(
                "SELECT m.id_actividad, m.id_cuota_actividad, IFNULL(SUM(p.cantidad),0) AS total_pagado " +
                "FROM Matricula m " +
                "LEFT JOIN PagoAlumno p ON m.id_matricula = p.id_matricula " +
                "WHERE m.id_matricula = ? " +
                "GROUP BY m.id_actividad, m.id_cuota_actividad",
                idMatricula
            ).get(0);

            int idActividad = ((Number) info.get("id_actividad")).intValue();
            int idCuotaActividad = ((Number) info.get("id_cuota_actividad")).intValue();
            double totalPagado = ((Number) info.get("total_pagado")).doubleValue();

            // Obtener el valor de la cuota elegida
            double cuota = 0.0;
            if(idCuotaActividad > 0) {
                cuota = ((Number) db.executeQueryMap(
                    "SELECT valor FROM CuotaActividad WHERE id_cuota_actividad = ?",
                    idCuotaActividad
                ).get(0).get("valor")).doubleValue();
            }

            boolean estaPagado = totalPagado >= cuota - 0.01;

            db.executeUpdate(
                "UPDATE Matricula SET monto_pagado = ?, esta_pagado = ? WHERE id_matricula = ?",
                totalPagado, estaPagado ? 1 : 0, idMatricula
            );

            // Control de plazas
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
            // Obtenemos la matrícula junto con el valor de la cuota asociada
            List<Map<String, Object>> result = db.executeQueryMap("""
                SELECT ca.valor AS cuota, m.monto_pagado, m.isCancelada, m.id_actividad, m.id_alumno
                FROM Matricula m
                JOIN CuotaActividad ca ON m.id_cuota_actividad = ca.id_cuota_actividad
                WHERE m.id_matricula = ?
            """, idMatricula);

            if (result.isEmpty()) return null;

            double cuota = ((Number) result.get(0).get("cuota")).doubleValue();
            double totalPagado = ((Number) result.get(0).get("monto_pagado")).doubleValue();
            boolean isCancelada = result.get(0).get("isCancelada") != null &&
                                  ((Number) result.get(0).get("isCancelada")).intValue() == 1;

            // Sumamos devoluciones si existen
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

            // Redondeo
            totalPagado = Math.round(totalPagado * 100.0) / 100.0;
            totalDevuelto = Math.round(totalDevuelto * 100.0) / 100.0;
            pendiente = Math.round(pendiente * 100.0) / 100.0;
            aDevolver = Math.round(aDevolver * 100.0) / 100.0;

            datos.put("cuota", cuota);
            datos.put("total_pagado", totalPagado);
            datos.put("total_devuelto", totalDevuelto);
            datos.put("pendiente", pendiente);
            datos.put("a_devolver", aDevolver);
            datos.put("is_cancelada", isCancelada ? 1.0 : 0.0);

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
                    m.monto_pagado
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

            db.executeUpdate("""
                INSERT INTO Devoluciones (id_matricula, id_alumno, id_actividad, fecha_solicitada, fecha_enviada, monto_devuelto)
                VALUES (?, ?, ?, ?, ?, ?)
            """, idMatricula, idAlumno, idActividad, fechaDevolucion.toString(), fechaHoy.toString(), montoDevuelto);

            double totalPagado = pagado - montoDevuelto; 
            boolean estaPagado = totalPagado >= 0; 

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
            ORDER BY a.fecha_pago
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
            SELECT a.id_actividad, a.nombre
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
            System.err.println("Error al registrar la devolución: " + e.getMessage());
            return false;
        }
    }
	public double getCuotaMatricula(int idMatricula) {
		Map<String, Object> data = db.executeQueryMap(
		        "SELECT ca.valor " +
		        "FROM Matricula m " +
		        "JOIN CuotaActividad ca ON m.id_cuota_actividad = ca.id_cuota_actividad " +
		        "WHERE m.id_matricula = ?", idMatricula
		    ).get(0);

		    return data != null ? ((Number) data.get("valor")).doubleValue() : 0.0;
	}
    
  
}