package proyecto.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import proyecto.model.Database;
import proyecto.model.entity.Actividad;
import proyecto.model.entity.Alumno;
import proyecto.model.entity.Factura;
import proyecto.model.entity.FechaFiltrado;
import proyecto.model.entity.ListaActividades;
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
	private ListaActividades listaActividades = new ListaActividades();

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
            db.executeUpdate("DELETE FROM Matricula");
            db.executeUpdate("DELETE FROM Actividad");
            db.executeUpdate("DELETE FROM Alumno");
            db.executeUpdate("DELETE FROM Profesor");
            db.executeUpdate("DELETE FROM Administrador");

            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Matricula'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Actividad'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Alumno'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Profesor'");
            db.executeUpdate("DELETE FROM sqlite_sequence WHERE name='Administrador'");

            JOptionPane.showMessageDialog(null,
                    "Todos los datos han sido eliminados y los IDs reiniciados.",
                    "OperaciÃƒÆ’Ã‚Â³n completada", JOptionPane.INFORMATION_MESSAGE);

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
            throw new ApplicationException("Formato de fecha invalido. Usa el formato yyyy-MM-dd");
        }
        
        this.fechaHoy = fechaL;
	}

    public List<Map<String, Object>> listarActividades() {
        List<Map<String,Object>> actividades = db.executeQueryMap(
            "SELECT id_actividad, nombre, inicio_inscripcion, fin_inscripcion, fecha_inicio, fecha_fin, " +
            "isClosed " +
            "FROM Actividad ORDER BY fecha_inicio"
        );

        for (Map<String,Object> act : actividades) {
            act.put("estado", obtenerEstadoActividad(act));
        }
        return actividades;
    }

    
 // Calcula el estado de la actividad
    public String obtenerEstadoActividad(Map<String,Object> act) {
        try {
            Object closed = act.get("isClosed");
            if (closed instanceof Number && ((Number) closed).intValue() == 1) {
                return "Cerrada";
            }
            if (closed instanceof Boolean && (Boolean) closed) {
                return "Cerrada";
            }

            LocalDate hoy = fechaHoy;
            LocalDate inicioIns = parseFecha((String) act.get("inicio_inscripcion"));
            LocalDate finIns = parseFecha((String) act.get("fin_inscripcion"));
            LocalDate fechaInicio = parseFecha((String) act.get("fecha_inicio"));
            LocalDate fechaFin = parseFecha((String) act.get("fecha_fin"));
            
           
            if (hoy.isBefore(inicioIns)) {
                return "Planificada";
            } else if (!hoy.isBefore(inicioIns) && !hoy.isAfter(finIns)) {
                return "En periodo de inscripcion";
            } else if (hoy.isAfter(finIns) && hoy.isBefore(fechaInicio)) {
                return "Inscripcion cerrada";
            } else if ((hoy.isEqual(fechaInicio) || hoy.isAfter(fechaInicio)) && hoy.isBefore(fechaFin)) {
                return "En curso";
            } else if (!hoy.isBefore(fechaFin)) {
                return "Finalizada";
            }
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

    
    public LocalDate getFechaMatricula(int idMatricula) {
        try {
            List<Map<String, Object>> result = db.executeQueryMap(
                    "SELECT fecha_matricula FROM Matricula WHERE id_matricula = ?",
                    idMatricula
            );

            if (result.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No se encontrÃƒÆ’Ã‚Â³ la matrÃƒÆ’Ã‚Â­cula especificada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            String fechaStr = (String) result.get(0).get("fecha_matricula");
            return LocalDate.parse(fechaStr);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al obtener la fecha de matrÃƒÆ’Ã‚Â­cula.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
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

	public boolean checkearNombre() {
		return a.validarNombre();
	}
	
	public boolean checkearApellido() {
		return a.validarApellido();
	}
	
	public boolean checkearTf() {
		return a.validarTf();
	}
	public boolean checkearEmail() {
		return a.validarEmail();
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
				    System.out.println("No se insertÃƒÂ¯Ã‚Â¿Ã‚Â½ al alumno.");
				}
			a=new Alumno();
			ac=null;
			return true;
		}	
	}

	
	public List<Actividad> recuperarActividades(){
		return listaActividades.getActividades(fechaHoy, db);
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
	    return recuperarActividadesEnRango(
	        LocalDate.of(fechaHoy.getYear(), 1, 1),
	        LocalDate.of(fechaHoy.getYear(), 12, 31)
	    );
	}

	private List<Factura> recuperarActividadesEnRango(LocalDate min, LocalDate max) {
	    List<Factura> listaActividades = new ArrayList<>();
	    String sql = "SELECT * FROM Actividad WHERE fecha_inicio >= ? AND fecha_fin <= ? ORDER BY fecha_inicio ASC";

	    List<Map<String, Object>> resultados = db.executeQueryMap(sql, min.toString(), max.toString());

	    for (Map<String, Object> fila : resultados) {
	        int idActividad = (int) fila.get("id_actividad");
	        int totalPlazas = (int) fila.get("total_plazas");

	        Factura f = new Factura(totalPlazas);
	        f.setId_actividad(idActividad);
	        f.setNombre((String) fila.get("nombre"));

	        // === Recuperar las cuotas asociadas a la actividad ===
	        List<Map<String, Object>> cuotas = db.executeQueryMap(
	            "SELECT c.categoria, ca.valor FROM CuotaActividad ca " +
	            "JOIN Cuota c ON c.id_cuota = ca.id_cuota " +
	            "WHERE ca.id_actividad = ?", idActividad
	        );

	        double cuotaMedia = 0.0;
	        if (!cuotas.isEmpty()) {
	            // Puedes hacer la media, o tomar una en concreto si la lógica lo requiere
	            cuotaMedia = cuotas.stream()
	                               .mapToDouble(x -> ((Number) x.get("valor")).doubleValue())
	                               .average()
	                               .orElse(0.0);
	        }

	        // === Recuperar gastos (de profesores, por ejemplo) ===
	        List<Map<String, Object>> facturas = db.executeQueryMap(
	            "SELECT cantidad FROM FacturaP WHERE id_actividad = ?", idActividad
	        );
	        double gastos = facturas.stream()
	                                .mapToDouble(x -> Double.parseDouble(String.valueOf(x.get("cantidad"))))
	                                .sum();
	        f.setGastos(gastos);

	        // === Plazas ===
	        int pagadas = recuperarPlazasPagadas(idActividad);
	        f.setPlazasOcupPagadas(pagadas);
	        int plazasOcup = recuperarPlazasOcupadas(idActividad);
	        f.setPlazasOcup(plazasOcup);

	        // === Ingresos y balance ===
	        f.calcularIngresosReales(cuotaMedia);
	        f.calcularIngresosEstimados(cuotaMedia);
	        f.calcularEstimado(cuotaMedia);
	        f.setBalance();

	        // === Otros datos ===
	        f.setFecha(LocalDate.parse((String) fila.get("fecha_inicio")));
	        f.setEstado((String) getActividadDetalles(idActividad).get("estado"));

	        listaActividades.add(f);
	    }

	    return listaActividades;
	}

	
	private int recuperarPlazasPagadas(int idActividad) {
	    List<Map<String, Object>> resultado = db.executeQueryMap(
	        "SELECT COUNT(*) AS total FROM Matricula WHERE id_actividad = ? AND esta_pagado = 1",
	        idActividad
	    );
	    if (resultado.isEmpty() || resultado.get(0).get("total") == null) {
	        return 0;
	    }
	    return ((Number) resultado.get(0).get("total")).intValue();
	}
	
	private int recuperarPlazasOcupadas(int idActividad) {
	    List<Map<String,Object>> res = db.executeQueryMap(
	        "SELECT COUNT(*) AS total FROM Matricula WHERE id_actividad = ? AND (isCancelada IS NULL OR isCancelada = 0)",
	        idActividad
	    );
	    if (res.isEmpty() || res.get(0).get("total") == null) return 0;
	    return ((Number)res.get(0).get("total")).intValue();
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

	public List<Factura> recuperaAcabadasEnRango(LocalDate fechaIn, LocalDate fechaFin) {
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

	public List<Factura> recuperaSinAcabarEnRango(LocalDate fechaIn, LocalDate fechaFin) {
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
	        SELECT a.id_actividad, a.nombre AS actividad_nombre,
	               p.id_profesor, p.nombre AS profesor_nombre, p.apellido AS profesor_apellido,
	               fp.cantidad AS remuneracion, fp.numero_factura, fp.esta_pagado
	        FROM Actividad a
	        JOIN FacturaP fp ON a.id_actividad = fp.id_actividad
	        JOIN Profesor p ON fp.id_profesor = p.id_profesor
	        WHERE fp.esta_pagado = 0
	        ORDER BY a.fecha_inicio
	        """
	    );

	    return actividades;
	}
	
	public Map<String, Object> obtenerDatosProfesorPorActividad(int idActividad) {
	    List<Map<String, Object>> resultados = db.executeQueryMap(
		        """
		        SELECT p.id_profesor,
		               p.nombre AS profesor_nombre,
		               p.apellido AS profesor_apellido,
		               f.emisor_nombre AS profesor_nif,
		               f.emisor_direccion AS profesor_direccion,
		               f.cantidad AS remuneracion,
		               f.esta_pagado
		        FROM Profesor p
		        JOIN FacturaP f ON f.id_profesor = p.id_profesor
		        WHERE f.id_actividad = ?
		        ORDER BY f.fecha_factura DESC
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
	            """
	            SELECT p.id_profesor,
	                   p.nombre AS profesor_nombre,
	                   p.apellido AS profesor_apellido,
	                   f.emisor_nombre AS profesor_nif,
	                   f.emisor_direccion AS profesor_direccion,
	                   f.cantidad AS remuneracion,
	                   f.esta_pagado
	            FROM Profesor p
	            JOIN FacturaP f ON f.id_profesor = p.id_profesor
	            WHERE f.id_actividad = ?
	            ORDER BY f.fecha_factura DESC
	            """,
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
            SELECT *
            FROM Actividad
            ORDER BY fecha_inicio
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
            SELECT m.id_matricula, m.id_actividad, a.nombre AS actividad, a.fecha_inicio AS fecha, 
                   m.monto_pagado, m.isCancelada
            FROM Matricula m 
            JOIN Actividad a ON m.id_actividad = a.id_actividad
            WHERE m.id_alumno = ? AND m.isCancelada = 0
            ORDER BY a.fecha_inicio
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

	public Actividad getActividad(int fila) {
		return listaActividades.getActividad(fila);
	}

	public boolean cargarProfesor(String nombre, String apellidos, String email, String telefono) {
		try {
	        String sql = """
	            INSERT INTO Profesor (nombre, apellido, email, telefono)
	            VALUES (?, ?, ?, ?)
	            """;

	        db.executeUpdate(sql, nombre, apellidos, email, telefono);

	        System.out.println("Profesor aÃ±adido correctamente: " + nombre + " " + apellidos);
	        return true;

	    } catch (Exception e) {
	        System.out.println("Error al insertar el profesor: " + e.getMessage());
	        return false;
	    }
		
	}

	public int guardarActividad(String nombre, String objetivos, String contenidos, LocalDate inicioIns, LocalDate finIns, LocalDate fechaInicio,
			LocalDate fechaFin, String ubi, int plazas, boolean selected, String empresa) {
		try {
			String sql = "INSERT INTO Actividad(nombre, objetivos, contenidos, espacio, "
					+ "inicio_inscripcion, fin_inscripcion, fecha_inicio, fecha_fin, es_gratuita, "
					+ "total_plazas, empresa, isClosed) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	        db.executeUpdate(sql, nombre, objetivos, contenidos, ubi, inicioIns, finIns, fechaInicio, fechaFin,
	        		selected, plazas, empresa, 0);
	        
	        List<Map<String,Object>> r = db.executeQueryMap("SELECT last_insert_rowid() AS id");
	        return ((Number)r.get(0).get("id")).intValue();
	        
	    } catch (ApplicationException e) {
	    	System.out.println("Error al insertar actividad: " + e.getMessage());
	    	return -1;
	    }
	}
	
	public void insertFacturaP(Integer idProfesor, int idActividad, String numeroFactura, String fechaFactura,
			double remuneracion, String emisorNombre, String emisorNif, String emisorDireccion) {
		try {
			String sql = "INSERT INTO FacturaP(id_profesor, id_actividad, numero_factura, "
					+ "fecha_factura, cantidad, emisor_nombre, emisor_nif, emisor_direccion, esta_pagado) "
					+ "VALUES (?,?,?,?,?,?,?,?, ?)";
		    db.executeUpdate(sql, idProfesor, idActividad, numeroFactura, fechaFactura, 
		    		remuneracion, emisorNombre, emisorNif, emisorDireccion, 0);
		} catch (ApplicationException e) {
			System.out.println("Error al insertar profesor: " + e.getMessage());
		}		
	}

	public List<Map<String, Object>> listarCuotas() {
		return db.executeQueryMap("SELECT * FROM Cuota");
	}

	public boolean cargarCuota(String categoria) {
		try {
			String checkSql = "SELECT COUNT(*) AS total FROM Cuota WHERE categoria = ?";
			List<Map<String, Object>> res = db.executeQueryMap(checkSql, categoria);
			if (((Number) res.get(0).get("total")).intValue() > 0) {
				System.out.println("La categoría de cuota '" + categoria + "' ya existe.");
				return false;
			}

			String sql = "INSERT INTO Cuota(categoria) VALUES (?)";
			db.executeUpdate(sql, categoria);
			System.out.println("Cuota aniadida correctamente: " + categoria);
			return true;

		} catch (Exception e) {
			System.out.println("Error al insertar cuota: " + e.getMessage());
			return false;
		}
	}
	
	public void asociarCuotaActividad(int idActividad, String categoria, double valor) {
	    try {
	        List<Map<String,Object>> result = db.executeQueryMap(
	            "SELECT id_cuota FROM Cuota WHERE categoria = ?", categoria
	        );

	        int idCuota = ((Number) result.get(0).get("id_cuota")).intValue();

	        String sql = "INSERT INTO CuotaActividad (id_actividad, id_cuota, valor) VALUES (?, ?, ?)";
	        db.executeUpdate(sql, idActividad, idCuota, valor);

	        System.out.println("Cuota asociada correctamente a la actividad.");
	    } catch (Exception e) {
	        System.err.println("Error al asociar cuota a la actividad: " + e.getMessage());
	    }
	}

	public List<Map<String, Object>> listarCuotasPorActividad(int idActividad) {
		try {
			String sql = """
				SELECT c.categoria, ca.valor
				FROM CuotaActividad ca
				JOIN Cuota c ON ca.id_cuota = c.id_cuota
				WHERE ca.id_actividad = ?
				ORDER BY c.id_cuota
				""";
			return db.executeQueryMap(sql, idActividad);

		} catch (Exception e) {
			System.out.println("Error al listar cuotas por actividad: " + e.getMessage());
			return Collections.emptyList();
		}
	}

}