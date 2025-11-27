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

	}
	public double getLimiteEfectivo() {
	    return LIMITE_EFECTIVO;
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

        int plazasOcupadas = ((Number) db.executeQueryMap(
        	    "SELECT COUNT(*) AS total " +
        	    "FROM Matricula " +
        	    "WHERE id_actividad = ?  AND (isCancelada IS NULL OR isCancelada = 0)",
        	    idActividad
        	).get(0).get("total")).intValue();


        int totalPlazas = ((Number) act.get("total_plazas")).intValue();
        int plazasDisponibles = totalPlazas - plazasOcupadas;
        resultado.put("plazas_disponibles", plazasDisponibles);

        List<Map<String, Object>> inscripciones = db.executeQueryMap(
            "SELECT m.id_matricula, " +
            "al.nombre || ' ' || al.apellido AS nombre_alumno, al.telefono,  " +
            "m.fecha_matricula, m.esta_pagado,m.isCancelada," +
            "m.monto_pagado, " + 
            "CASE WHEN m.esta_pagado = 1 THEN 'Cobrada' ELSE 'Pendiente' END AS estado " +
            "FROM Matricula m " +
            "JOIN Alumno al ON m.id_alumno = al.id_alumno " +
            "WHERE m.id_actividad = ? " +
            "AND (m.isCancelada IS NULL OR m.isCancelada = 0 OR 1=1)",
            idActividad
        );
        resultado.put("inscripciones", inscripciones);

        double ingresosConfirmados = inscripciones.stream()
                .filter(i -> "Cobrada".equals(i.get("estado")))
                .mapToDouble(i -> {
                    try { return Double.parseDouble(String.valueOf(i.get("monto_pagado"))); }
                    catch(Exception e) { return 0; }
                }).sum();

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
                        "No se encuentra la matrÃ­cula especificada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            String fechaStr = (String) result.get(0).get("fecha_matricula");
            return LocalDate.parse(fechaStr);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al obtener la fecha de matrÃ­cula.",
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


            Map<String, Object> info = db.executeQueryMap(
                "SELECT m.id_actividad, m.id_cuota_actividad, IFNULL(SUM(p.cantidad),0) AS total_pagado " +
                "FROM Matricula m " +
                "LEFT JOIN PagoAlumno p ON m.id_matricula = p.id_matricula " +
                "WHERE m.id_matricula = ? " +
                "GROUP BY m.id_actividad, m.id_cuota_actividad",
                idMatricula
            ).get(0);

            int idCuotaActividad = ((Number) info.get("id_cuota_actividad")).intValue();
            double totalPagado = ((Number) info.get("total_pagado")).doubleValue();


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
    
    public void imprimirMatriculasYPagosPorConsola() {
        try {
            List<Map<String, Object>> matriculas = db.executeQueryMap(
                "SELECT m.id_matricula, " +
                "a.nombre AS nombre_alumno, a.apellido AS apellido_alumno, " +
                "act.nombre AS nombre_actividad, m.fecha_matricula, m.monto_pagado, m.esta_pagado, m.isCancelada " +
                "FROM Matricula m " +
                "JOIN Alumno a ON m.id_alumno = a.id_alumno " +
                "JOIN Actividad act ON m.id_actividad = act.id_actividad " +
                "ORDER BY m.id_matricula"
            );

            for (Map<String, Object> m : matriculas) {
                int idMatricula = ((Number) m.get("id_matricula")).intValue();
                System.out.println("\n Matrícula #" + idMatricula);
                System.out.println("  Alumno: " + m.get("nombre_alumno") + " " + m.get("apellido_alumno"));
                System.out.println("  Actividad: " + m.get("nombre_actividad"));
                System.out.println("  Fecha matrícula: " + m.get("fecha_matricula"));
                System.out.println("  Monto pagado: " + m.get("monto_pagado"));
                System.out.println("  Pagado: " + (((Number) m.get("esta_pagado")).intValue() == 1));
                System.out.println("  Cancelada: " + (((Number) m.get("isCancelada")).intValue() == 1));


                List<Map<String, Object>> pagos = db.executeQueryMap(
                    "SELECT id_pago, fecha_pago, cantidad, metodo_pago " +
                    "FROM PagoAlumno WHERE id_matricula = ?",
                    idMatricula
                );

                if (pagos.isEmpty()) {
                    System.out.println("  💸 Pagos: (sin pagos registrados)");
                } else {
                    System.out.println("  💸 Pagos:");
                    for (Map<String, Object> p : pagos) {
                        System.out.println("    Pago #" + p.get("id_pago") +
                                           " | Fecha: " + p.get("fecha_pago") +
                                           " | Cantidad: " + p.get("cantidad") +
                                           " | Método: " + p.get("metodo_pago"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Error al imprimir matrículas y pagos:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }




    
    public LocalDate getFechaHoy() {
	    return fechaHoy;
	}
    

    
    public Map<String, Double> getEstadoPagoAlumno(int idMatricula) {
        Map<String, Double> datos = new HashMap<>();

        try {
            List<Map<String, Object>> result = db.executeQueryMap("""
                SELECT ca.valor*m.numero_matriculados AS cuota, m.monto_pagado, m.isCancelada, m.id_actividad, m.id_alumno
                FROM Matricula m
                JOIN CuotaActividad ca ON m.id_cuota_actividad = ca.id_cuota_actividad
                WHERE m.id_matricula = ?
            """, idMatricula);

            if (result.isEmpty()) return null;

            double cuota = ((Number) result.get(0).get("cuota")).doubleValue();
            double totalPagado = ((Number) result.get(0).get("monto_pagado")).doubleValue();
            boolean isCancelada = result.get(0).get("isCancelada") != null &&
                                  ((Number) result.get(0).get("isCancelada")).intValue() == 1;


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
                pendiente =0;
                totalPagado =0;
                totalDevuelto = 0;
            } else {
                pendiente = Math.max(0, cuota - neto);
                pendiente = Math.round(pendiente * 100.0) / 100.0;
                totalPagado = Math.round(totalPagado * 100.0) / 100.0;
                totalDevuelto = Math.round(totalDevuelto * 100.0) / 100.0;
            }

            aDevolver = Math.max(0, neto - cuota);
   
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




    public boolean registrarDevolucion(int idMatricula, double montoDevuelto, LocalDate fechaDevolucion, boolean porEfectivo) {
        try {
            String metodoPago = porEfectivo ? "Efectivo" : "Transferencia";

            Map<String, Object> info = db.executeQueryMap(
                "SELECT id_alumno, id_actividad FROM Matricula WHERE id_matricula = ?",
                idMatricula
            ).get(0);

            int idAlumno = ((Number) info.get("id_alumno")).intValue();
            int idActividad = ((Number) info.get("id_actividad")).intValue();

            db.executeUpdate("""
                INSERT INTO Devoluciones 
                (id_matricula, id_alumno, id_actividad, fecha_solicitada, fecha_enviada, monto_devuelto, metodo_pago)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """, 
                idMatricula,
                idAlumno,
                idActividad,
                fechaDevolucion.toString(),
                fechaDevolucion.toString(),
                montoDevuelto,
                metodoPago
            );

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error al registrar la devolución en la base de datos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    
    public List<Map<String, Object>> listarMovimientosAlumno(int idMatricula) {
        List<Map<String, Object>> movimientos = new ArrayList<>();

        String sqlPagos = """
            SELECT fecha_pago AS fecha, cantidad, metodo_pago AS metodo, 'Pago' AS tipo
            FROM PagoAlumno
            WHERE id_matricula = ?
            ORDER BY fecha_pago ASC
        """;
        movimientos.addAll(db.executeQueryMap(sqlPagos, idMatricula));

        String sqlDevoluciones = """
            SELECT fecha_enviada AS fecha, monto_devuelto AS cantidad, metodo_pago AS metodo, 'Devolución' AS tipo
            FROM Devoluciones
            WHERE id_matricula = ?
            ORDER BY fecha_enviada ASC
        """;
        movimientos.addAll(db.executeQueryMap(sqlDevoluciones, idMatricula));

        return movimientos;
    }
    
    public String getNombreAlumno(int idMatricula) {
        return (String) db.executeQueryMap("""
            SELECT a.nombre
            FROM Alumno a
            JOIN Matricula m ON a.id_alumno = m.id_alumno
            WHERE m.id_matricula = ?
        """, idMatricula).get(0).get("nombre");
    }

    public String getNombreActividad(int idMatricula) {
        return (String) db.executeQueryMap("""
            SELECT ac.nombre
            FROM Actividad ac
            JOIN Matricula m ON ac.id_actividad = m.id_actividad
            WHERE m.id_matricula = ?
        """, idMatricula).get(0).get("nombre");
    }

    public LocalDate getFechaLimitePago(int idMatricula) {
        Map<String, Object> row = db.executeQueryMap("""
    	    SELECT A.fin_inscripcion
    	    FROM Matricula M
    	    JOIN Actividad A ON M.id_actividad = A.id_actividad
    	    WHERE M.id_matricula = ?
    	    """, idMatricula).get(0);

        String f = (String) row.get("fin_inscripcion");

        if (f == null) return null; // evita el NullPointerException

        return LocalDate.parse(f);
    }

    public LocalDate getFechaInicioActividad(int idMatricula) {
        String f = (String) db.executeQueryMap("""
            SELECT A.fecha_inicio
            FROM Matricula M
            JOIN Actividad A ON M.id_actividad = A.id_actividad
            WHERE M.id_matricula = ?
        """, idMatricula).get(0).get("fecha_inicio");
        
        return LocalDate.parse(f);
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
    
    public Map<String, Object> cargarDatosProfesor(int idProfesor) {
        List<Map<String, Object>> result = db.executeQueryMap(
            """
            SELECT direccion, nif
            FROM Profesor
            WHERE id_profesor = ?
            """,
            idProfesor
        );

        if (result.isEmpty()) return null;
        return result.get(0);
    }
    
    public void registrarFactura(
            int idProfesor,
            int idActividad,
            String numeroFactura,
            LocalDate fechaFactura,
            double cantidad,
            String nifEmisor,
            String direccionEmisor) throws Exception {


        List<Map<String, Object>> facturas = db.executeQueryMap(
                "SELECT id_factura FROM FacturaP WHERE id_profesor = ? AND id_actividad = ?",
                idProfesor, idActividad
        );

        if (facturas.isEmpty()) {
            throw new Exception("No existe factura para este profesor y actividad.");
        }

        int idFactura = ((Number) facturas.get(0).get("id_factura")).intValue();


        String sql = """
                UPDATE FacturaP
                SET numero_factura = ?,
                    fecha_factura = ?,
                    cantidad = ?,
                    emisor_nif = ?,
                    emisor_direccion = ?
                WHERE id_factura = ?
                """;

        db.executeUpdate(
                sql,
                numeroFactura,
                fechaFactura.toString(),
                cantidad,
                nifEmisor,
                direccionEmisor,
                idFactura
        );
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

    public List<Map<String, Object>> listarTodosLosCursos() {
        return db.executeQueryMap(
            """
            SELECT 
                a.id_actividad,
                a.nombre,
                a.inicio_inscripcion,
                a.fin_inscripcion,
                a.fecha_inicio,
                a.fecha_fin,
                a.total_plazas,
                a.isClosed,
                a.isCancelada
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

	public double getMontoTotalMatricula(int idMatriculaSeleccionada) {
		try {
	        List<Map<String, Object>> result = db.executeQueryMap("""
	            SELECT 
	                ca.valor * m.numero_matriculados as monto_total
	            FROM Matricula m
	            JOIN Actividad a ON m.id_actividad = a.id_actividad
	            JOIN CuotaActividad ca on ca.id_cuota_actividad = m.id_cuota_actividad
	            WHERE m.id_matricula = ?
	            """, 
	            idMatriculaSeleccionada
	        );

	        if (result.isEmpty()) return 0.0;
	        
	        Object montoTotalObj = result.get(0).get("monto_total");
	        return montoTotalObj != null ? ((Number) montoTotalObj).doubleValue() : 0.0;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return 0.0;
	    }
	}
	
	public List<Map<String, Object>> listarMovimientosPorFactura(int idFactura, int idProfesor) {
	    List<Map<String, Object>> movimientos = new ArrayList<>();

	    String sqlPagos = """
	        SELECT fecha_pago AS fecha, cantidad, 'Pago' AS tipo
	        FROM PagoProfesor
	        WHERE id_factura = ? AND id_profesor = ?
	        ORDER BY fecha_pago ASC
	    """;
	    movimientos.addAll(db.executeQueryMap(sqlPagos, idFactura, idProfesor));

	    String sqlDevoluciones = """
	        SELECT fecha_devolucion AS fecha, cantidad, 'Devolución' AS tipo
	        FROM DevolucionProfesor
	        WHERE id_factura = ? AND id_profesor = ?
	        ORDER BY fecha_devolucion ASC
	    """;
	    movimientos.addAll(db.executeQueryMap(sqlDevoluciones, idFactura, idProfesor));

	    return movimientos;
	}


    
  
}