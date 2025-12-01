package proyecto.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	private List<Alumno> integrantesGrupo;

	

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
        crearDataBase();
        cargarDataBase();
    }
	
	public Database getDb() {
		return db;
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
	    return db.executeQueryMap("SELECT id_profesor, nombre, apellido FROM Profesor WHERE isEmpresa = 0 ORDER BY nombre");
	}

	public List<Map<String, Object>> listarEmpresas() {
	    return db.executeQueryMap("SELECT id_profesor, nombre FROM Profesor WHERE isEmpresa = 1 ORDER BY nombre");
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
        	
        	Object cancelada = act.get("isCancelada");
            if (cancelada instanceof Number && ((Number) cancelada).intValue() == 1) {
                return "Cancelada";
            }
            if (cancelada instanceof Boolean && (Boolean) cancelada) {
                return "Cancelada";
            }
            
            
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
        int plazasOcupadas = ((Number) db.executeQueryMap("""
                SELECT COALESCE(SUM(numero_matriculados), 0) AS total 
                FROM Matricula 
                WHERE id_actividad = ? AND (isCancelada IS NULL OR isCancelada = 0)
                """, idActividad).get(0).get("total")).intValue();
        int totalPlazas = ((Number) act.get("total_plazas")).intValue();
        int plazasDisponibles = totalPlazas - plazasOcupadas;
        resultado.put("plazas_disponibles", plazasDisponibles);

        // Inscripciones
        List<Map<String, Object>> inscripciones = db.executeQueryMap(
             "SELECT m.id_matricula, m.integrantes_ids, " +
             "al.nombre || ' ' || al.apellido AS nombre_alumno, " +
             "m.fecha_matricula, " +
             "CASE WHEN m.esta_pagado = 1 THEN 'Cobrada' ELSE 'Pendiente' END AS estado, " +
             "m.numero_matriculados, " +
             "CASE WHEN m.numero_matriculados > 1 THEN 'Grupal (' || m.numero_matriculados || ' personas)' ELSE 'Individual' END AS tipo_inscripcion " +
             "FROM Matricula m " +
             "JOIN Alumno al ON m.id_alumno = al.id_alumno " +
             "WHERE m.id_actividad = ? " +
             "AND (m.isCancelada IS NULL OR m.isCancelada = 0) " +
             "ORDER BY m.fecha_matricula DESC",
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
	public boolean checkearTf(String tf) {
		Alumno al = new Alumno();
		al.setNumeroTf(tf);
		return al.validarTf();
	}
	
	public boolean checkearEmail() {
		return a.validarEmail();
	}
	
	public boolean checkearEmail(String email) {
		Alumno al = new Alumno();
		al.setCorreo(email);
		return al.validarEmail();
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
		String cuota ="SELECT valor from CuotaActividad where id_cuota_actividad = ?";
		
		List<Object[]> result = db.executeQueryArray(cuota, a.getId_cuota());
		
		if (result.isEmpty()) {
	        throw new RuntimeException("No se encontr� el valor de la cuota para id: " + a.getId_cuota());
	    }
		
		double valor = Double.valueOf(result.get(0)[0].toString());
		
		db.executeUpdate(
	            "INSERT INTO Matricula (id_alumno, id_actividad, esta_pagado, monto_pagado, fecha_matricula, numero_matriculados, integrantes_ids,"
	            + " id_cuota_actividad, monto_total) " +
	            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
	            a.getIdAlumno(),
	            ac.getId_Actividad(),
	            false,
	            0.0,
	            fechaHoy,
	            1,
	            a.getIdAlumno(),
	            a.getId_cuota(),
	            valor
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

	public Actividad getAct() {
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
	        
	        List<Map<String, Object>> facturasPagadas = db.executeQueryMap(
	            "SELECT cantidad FROM PagoProfesor WHERE id_actividad = ?", idActividad
	        );
	        List<Map<String, Object>> devolucionesProfesor = db.executeQueryMap(
	        		"select cantidad from DevolucionProfesor where id_actividad = ?", idActividad);
	        
	        
	        
	        List<Map<String, Object>> facturas = db.executeQueryMap(
		            "SELECT cantidad FROM FacturaP WHERE id_actividad = ?", idActividad
		        );
	        
	        double gastosEstimados = facturas.stream()
	                                .mapToDouble(x -> Double.parseDouble(String.valueOf(x.get("cantidad"))))
	                                .sum();
	        double gastos = facturasPagadas.stream()
                    .mapToDouble(x -> Double.parseDouble(String.valueOf(x.get("cantidad"))))
                    .sum() - devolucionesProfesor.stream().mapToDouble(x -> Double.parseDouble(String.valueOf(x.get("cantidad")))).sum();
	        f.setGastosEstimados(gastosEstimados);
	        f.setGastos(gastos);
	        // === Plazas ===
	        int pagadas = recuperarPlazasPagadas(idActividad);
	        f.setPlazasOcupPagadas(pagadas);
	        int plazasOcup = recuperarPlazasOcupadas(idActividad);
	        f.setPlazasOcup(plazasOcup);

	        // === Ingresos y balance ===
	        double ingresosReales = ingresosReales(idActividad);
	        double ingresosEstimados = ingresosEstimados(idActividad);

	        f.calcularIngresosReales(ingresosReales);
	        f.calcularIngresosEstimados(ingresosEstimados);
	        f.calcularEstimado();
	        f.calcularBalance();

	        // === Otros datos ===
	        f.setFecha(LocalDate.parse((String) fila.get("fecha_inicio")));
	        f.setEstado((String) getActividadDetalles(idActividad).get("estado"));

	        listaActividades.add(f);
	    }

	    return listaActividades;
	}
	
	private double ingresosReales(int idActividad) {
	    String sql = "SELECT SUM(monto_pagado) AS total FROM Matricula " +
	                 "WHERE id_actividad = ?";
	    String devoluciones = "SELECT SUM(monto_devuelto) as devuelto from devoluciones where id_actividad = ?";

	    List<Map<String, Object>> r = db.executeQueryMap(sql, idActividad);
	    if (r.isEmpty()) return 0.0;
	    
	    List<Map<String, Object>> d = db.executeQueryMap(devoluciones, idActividad);
	    double devolucion = 0;
	    Object val = r.get(0).get("total");
	    if(d.isEmpty()) devolucion=0;
	    else {
	    	Object dev = d.get(0).get("devuelto");
	    	devolucion = dev == null? 0.0:((Number) dev).doubleValue();
	    }
	    
	    double ingresos = val == null ? 0.0 : ((Number) val).doubleValue();
	    System.out.println("Estos son los ingresos: "+ingresos);
	    System.out.println("Estos son las devoluciones: "+ devolucion);
	    return ingresos - devolucion;
	}

	private double ingresosEstimados(int idActividad) {
	    String sql = "SELECT SUM(monto_total) AS total FROM Matricula " +
	                 "WHERE id_actividad = ?";

	    List<Map<String, Object>> r = db.executeQueryMap(sql, idActividad);
	    if (r.isEmpty()) return 0.0;

	    Object val = r.get(0).get("total");

	    return val == null ? 0.0 : ((Number) val).doubleValue();
	}

	

	private int recuperarPlazasOcupadas(int idActividad) {
	    
		List<Map<String, Object>> resultado = db.executeQueryMap("""
		        SELECT COALESCE(SUM(numero_matriculados), 0) as total 
		        FROM Matricula 
		        WHERE id_actividad = ?
		        
		        """, idActividad);
		    
		    return ((Number) resultado.get(0).get("total")).intValue();
	}


	private int recuperarPlazasPagadas(int idActividad) {
	    
	    List<Map<String, Object>> resultado = db.executeQueryMap("""
	        SELECT COALESCE(SUM(numero_matriculados), 0) as total 
	        FROM Matricula 
	        WHERE id_actividad = ? AND esta_pagado = 1
	        
	        """, idActividad);
	    
	    return ((Number) resultado.get(0).get("total")).intValue();
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
	    String sql = "SELECT COUNT(*) AS total FROM Matricula WHERE id_actividad = ? AND (esta_pagado = 0 OR isCancelada = 1)";
	    List<Map<String, Object>> result = db.executeQueryMap(sql, idActividad);
	    int pendientes = ((Number) result.get(0).get("total")).intValue();
	    return pendientes > 0;
	}
	
	public boolean actividadConMovimientosProfesores(int idActividad) {
	    String sql = "SELECT COUNT(*) AS total FROM PagoProfesor WHERE id_actividad = ?";
	    List<Map<String, Object>> result = db.executeQueryMap(sql, idActividad);
	    int pagos = ((Number) result.get(0).get("total")).intValue();
	    
	    String sql2 = "SELECT COUNT(*) AS total FROM FacturaP WHERE id_actividad = ?";
	    List<Map<String, Object>> result2 = db.executeQueryMap(sql2, idActividad);
	    int nProfesores = ((Number) result2.get(0).get("total")).intValue();
	    
	    return pagos == nProfesores;
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
        LocalDate hoy = fechaHoy != null ? fechaHoy : LocalDate.now();

        String sql = """
            SELECT m.id_matricula, m.id_actividad, a.nombre AS actividad, a.fecha_inicio AS fecha, 
                   m.monto_pagado, m.isCancelada, a.fecha_inicio
            FROM Matricula m 
            JOIN Actividad a ON m.id_actividad = a.id_actividad
            WHERE m.id_alumno = ? AND m.isCancelada = 0
            ORDER BY a.fecha_inicio
            """;

        List<Map<String, Object>> matriculas = db.executeQueryMap(sql, idAlumno);

        // Filtrar según fecha_inicio > fechaHoy
        List<Map<String, Object>> filtradas = new ArrayList<>();
        for (Map<String, Object> m : matriculas) {
            String fechaInicioStr = (String) m.get("fecha_inicio");
            LocalDate fechaInicio = fechaInicioStr != null ? LocalDate.parse(fechaInicioStr.split("T")[0]) : null;
            if (fechaInicio != null && hoy.isBefore(fechaInicio)) {
                filtradas.add(m);
            }
        }

        return filtradas;
    }



    public double calcularMontoDevolucion(LocalDate fechaActividad, double montoPagado, int idMatricula) {
    	int id_cuota = db.queryInt( "SELECT id_cuota_actividad FROM Matricula WHERE id_matricula = ?", idMatricula);
    	double cuota = db.queryDouble("SELECT valor FROM CuotaActividad WHERE id_cuota_actividad = ?", id_cuota);
    	int id_actividad = db.queryInt( "SELECT id_actividad FROM Matricula WHERE id_matricula = ?", idMatricula);	
    	int cancelada = db.queryInt("SELECT isCancelada FROM Actividad WHERE id_actividad = ?", id_actividad);
    	int retrasada = db.queryInt("SELECT isRetrasada FROM Matricula WHERE id_matricula = ?", idMatricula);
    	
    	if(cancelada == 1 || retrasada == 1) {
    		return Math.min(montoPagado,cuota);
    	}
        long diasFaltan = java.time.temporal.ChronoUnit.DAYS.between(fechaHoy, fechaActividad);
        montoPagado=Math.min(montoPagado,cuota);
        if (diasFaltan >= 7) return montoPagado;
        else if (diasFaltan >= 3) return cuota * 0.5;
        else return 0;
    }

    public void registrarDevolucion(int idMatricula, int idAlumno, int idActividad, double montoDevuelto) {
        LocalDate hoy = fechaHoy != null ? fechaHoy : LocalDate.now();
        double montoPagadoActual = db.queryDouble(
                "SELECT monto_pagado FROM Matricula WHERE id_matricula = ?", 
                idMatricula
            );
        double montoTotal=montoDevuelto + montoPagadoActual;
        
        db.executeUpdate("UPDATE Matricula SET isCancelada = 1 WHERE id_matricula = ?", idMatricula);
        
        db.executeUpdate("UPDATE Matricula SET monto_pagado = ? WHERE id_matricula = ?", montoTotal,idMatricula);

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
	        String sql1 = "SELECT id_actividad as id FROM Actividad where nombre=? and objetivos = ? ORDER BY id_actividad ASC";
	        List<Map<String, Object>> filas = db.executeQueryMap(sql1,nombre,objetivos);

	        
	        return ((Number)filas.get(0).get("id")).intValue();
	        
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
				SELECT ca.id_cuota_actividad, c.categoria, ca.valor
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

	public void guardarTipoInscripcion(boolean esIndividual, int numPersonas) {
		
		
	}
	
	
	public void setIntegrantesGrupo(List<Alumno> integrantesGrupo) {
	    this.integrantesGrupo = integrantesGrupo;
	}
	
	
	
	public void setAlumnoResponsable(Alumno a) {
		this.a = a;
	}
	
	public boolean introduceGrupo(MensajeError msj) {
        if (integrantesGrupo == null || integrantesGrupo.isEmpty()) {
            msj.setMensaje("No hay integrantes en el grupo");
            return false;
        }
        
        if(!comprobarPlazos()) {
            msj.setMensaje("Fuera de plazo");
            return false;
        }
        
        // Validar plazas suficientes para todo el grupo
        int totalPersonas = integrantesGrupo.size();
        if (!comprobarPlazasActividad(totalPersonas)) {
            msj.setMensaje("No hay plazas disponibles para " + totalPersonas + " personas");
            return false;
        }
        
        try {
            // 1. Insertar/actualizar todos los alumnos
            List<Alumno> alumnosInsertados = insertarAlumnos(integrantesGrupo);
            
            // 2. El primer alumno es el responsable
            Alumno responsable = alumnosInsertados.get(0);
            System.out.println(responsable.getId_cuota());
            
            // 3. Crear UNA sola matrícula grupal
            return crearMatriculaGrupal(responsable, alumnosInsertados, totalPersonas, msj,responsable.getId_cuota());
            
        } catch (Exception e) {
            msj.setMensaje("Error en inscripción grupal: " + e.getMessage());
            return false;
        }
    }
	
	private boolean comprobarPlazasActividad(int numPersonasSolicitadas) {
        int plazasDisponibles = obtenerPlazasDisponibles(ac.getId_Actividad());
        return plazasDisponibles >= numPersonasSolicitadas;
    }

    // 🔹 MÉTODO ACTUALIZADO PARA OBTENER PLAZAS DISPONIBLES
    public int obtenerPlazasDisponibles(int idActividad) {
        try {
            // Obtener total de plazas de la actividad
            List<Map<String, Object>> actividad = db.executeQueryMap(
                "SELECT total_plazas FROM Actividad WHERE id_actividad = ?", 
                idActividad
            );
            
            if (actividad.isEmpty()) return 0;
            
            int totalPlazas = ((Number) actividad.get(0).get("total_plazas")).intValue();
            
            // Obtener plazas ocupadas (sumando numero_matriculados)
            List<Map<String, Object>> ocupadas = db.executeQueryMap("""
                SELECT COALESCE(SUM(numero_matriculados), 0) as total_ocupadas 
                FROM Matricula 
                WHERE id_actividad = ? AND (isCancelada IS NULL OR isCancelada = 0)
                """, idActividad);
            
            int plazasOcupadas = ((Number) ocupadas.get(0).get("total_ocupadas")).intValue();
            
            return totalPlazas - plazasOcupadas;
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
	
	private boolean crearMatriculaGrupal(Alumno responsable, List<Alumno> integrantes, 
            int totalPersonas, MensajeError msj,int id_cuotaA) {

		
		if (totalPersonas < 2) {
			msj.setMensaje("Un grupo debe tener al menos 2 personas");
			return false;
		}

		if (totalPersonas > 50) { 
			msj.setMensaje("El grupo no puede exceder 50 personas");
			return false;
		}
		
		String integrantesIds = integrantes.stream()
				.map(Alumno::getIdAlumno)
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		String sql = """
				INSERT INTO Matricula 
				(id_alumno, id_actividad, fecha_matricula, monto_pagado, esta_pagado,
				numero_matriculados, integrantes_ids, id_cuota_actividad, monto_total) 
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";
		String cuota ="SELECT valor from CuotaActividad where id_cuota_actividad = ?";
		
		List<Object[]> result = db.executeQueryArray(cuota, id_cuotaA);

		Double valor = null;
		if (result.isEmpty()) {
			msj.setMensaje("No se encontr� el valor de la cuota");
		    return false;
		}
		valor = Double.valueOf(result.get(0)[0].toString());
		
		
		db.executeUpdate(sql, 
				responsable.getIdAlumno(),
				ac.getId_Actividad(),
				fechaHoy.toString(),
				0,
				false,
				totalPersonas,
				integrantesIds,
				id_cuotaA,
				valor*totalPersonas
				);
		System.out.println(integrantesIds);
		return true;
	}

	private List<Alumno> insertarAlumnos(List<Alumno> integrantesGrupo2) {
		List<Alumno> alumnos = new ArrayList<>();
		System.out.println("tamaño lista integrantes grupo: " + integrantesGrupo2.size());
		for(Alumno alumno: integrantesGrupo2) {
			String correoBuscado = (String) alumno.getCorreo();
			List<Map<String, Object>> resultados = db.executeQueryMap(
			    "SELECT * FROM Alumno WHERE email = ?", correoBuscado 
			);

			if (resultados.isEmpty()) {
				db.executeUpdate(
				        "INSERT INTO Alumno (nombre, apellido, email, telefono, es_interno) VALUES (?, ?, ?, ?, ?)",
				        alumno.getNombre(), alumno.getApellido(), alumno.getCorreo(), alumno.getTelefono(), alumno.pertenece()
				    );
				  List<Map<String, Object>> nuevo = db.executeQueryMap(
				            "SELECT id_alumno FROM Alumno WHERE email = ?", correoBuscado
				        );
				    if (!nuevo.isEmpty()) {
				            Number id = (Number) nuevo.get(0).get("id_alumno");
				            alumno.setId(id.intValue());
				        }
				    } else {
				        Number id = (Number) resultados.get(0).get("id_alumno");
				        alumno.setId(id.intValue());
				    
				    }
					alumnos.add(alumno);
		}
		return alumnos;
			
	}

	public Alumno getAlumnoById(String i) {
		List<Map<String, Object>> resultados = db.executeQueryMap(
		        "SELECT * FROM Alumno WHERE id_alumno = ?", 
		        i
		    );
		    
		    if (resultados.isEmpty()) {
		        return null;
		    }
		    
		    Map<String, Object> fila = resultados.get(0);
		    Alumno alumno = new Alumno();
		    alumno.setId(Integer.parseInt(i));
		    alumno.setNombre((String) fila.get("nombre"));
		    alumno.setApellidos((String) fila.get("apellido"));
		    alumno.setCorreo((String) fila.get("email"));
		    alumno.setNumeroTf((String) fila.get("telefono"));
		    alumno.setPerteneceDB((Integer) fila.get("es_interno"));
		    
		    return alumno;
	}

	public void guardarIdCuotaA(String id_c) {
		this.a.setId_Cuota(Integer.parseInt(id_c));
		
	}
	
	public String obtenerRangoCuotasPorActividad(int idActividad) {
	    String sql = """
	        SELECT 
	    		COALESCE(MIN(valor), 0) AS min_valor, 
	    		COALESCE(MAX(valor), 0) AS max_valor
	    		FROM CuotaActividad
	    		WHERE id_actividad = ?
	    """;
	    List<Map<String, Object>> result = db.executeQueryMap(sql, idActividad);

	    if (result.isEmpty()) return "-";
	    Map<String, Object> row = result.get(0);

	    double min = ((Number) row.get("min_valor")).doubleValue();
	    double max = ((Number) row.get("max_valor")).doubleValue();

	    if (min == max) {
	        return String.format("%.2f €", min);
	    } else {
	        return String.format("%.2f € - %.2f €", min, max);
	    }
	}

	public int insertarEmpresa(String empresaTexto) {
	    if (empresaTexto == null || empresaTexto.trim().isEmpty()) {
	        throw new IllegalArgumentException("El nombre de la empresa no puede estar vacío");
	    }

	    // Generar un email válido único
	    String email = empresaTexto.replaceAll("\\s+", "").toLowerCase()
	                 + "_" + System.currentTimeMillis() + "@empresa.local";

	    // Insertar la empresa como profesor con isEmpresa = 1
	    try {
	        String sql = "INSERT INTO Profesor (nombre, apellido, email, telefono, isEmpresa) "
	                   + "VALUES (?, '', ?, '', 1)";
	        db.executeUpdate(sql, empresaTexto, email);

	        // Recuperar el id_profesor recién insertado
	        List<Map<String, Object>> result = db.executeQueryMap(
	            "SELECT id_profesor FROM Profesor WHERE email = ?", email
	        );

	        if (!result.isEmpty()) {
	            return ((Number) result.get(0).get("id_profesor")).intValue();
	        } else {
	            throw new RuntimeException("No se pudo obtener el id de la empresa insertada.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1; // En caso de error
	    }
	}


}