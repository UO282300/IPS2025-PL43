package proyecto.service;

import java.time.LocalDate;
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
        crearDataBase();
        cargarDataBase();
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
            "cuota, isClosed " +
            "FROM Actividad ORDER BY fecha_inicio"
        );

        for (Map<String,Object> act : actividades) {
            act.put("estado", obtenerEstadoActividad(act));
        }
        return actividades;
    }
    
    public List<Map<String, Object>> imprimirActividades() {
        List<Map<String,Object>> actividades = db.executeQueryMap(
            "SELECT id_actividad, nombre, inicio_inscripcion, fin_inscripcion, fecha_inicio, fecha_fin, " +
            "cuota" +
            "FROM Actividad ORDER BY fecha_inicio"
        );

        for (Map<String,Object> act : actividades) {
            String estado = obtenerEstadoActividad(act);
            act.put("estado", estado);

            System.out.println("===== Actividad =====");
            System.out.println("ID: " + act.get("id_actividad"));
            System.out.println("Nombre: " + act.get("nombre"));
            System.out.println("Inicio inscripcion: " + act.get("inicio_inscripcion"));
            System.out.println("Fin inscripcion: " + act.get("fin_inscripcion"));
            System.out.println("Fecha inicio actividad: " + act.get("fecha_inicio"));
            System.out.println("Fecha fin actividad: " + act.get("fecha_fin"));
            System.out.println("Cuota: " + act.get("cuota"));
            System.out.println("Estado: " + estado);
            System.out.println("====================\n");
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
            	System.out.println("Devuelve planif");
                return "Planificada";
            } else if (!hoy.isBefore(inicioIns) && !hoy.isAfter(finIns)) {
            	System.out.println("Devuelve periodo inscr");
                return "En periodo de inscripcion";
            } else if (hoy.isAfter(finIns) && hoy.isBefore(fechaInicio)) {
            	System.out.println("Devuelve inscr cerrada");
                return "Inscripcion cerrada";
            } else if ((hoy.isEqual(fechaInicio) || hoy.isAfter(fechaInicio)) && hoy.isBefore(fechaFin)) {
            	System.out.println("Devuelve en curso");
                return "En curso";
            } else if (!hoy.isBefore(fechaFin)) {
            	System.out.println("Devuelve f");
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
    
    // Obtiene todos los detalles de una actividad
    public Map<String, Object> getActividadDetalles(int idActividad) {
        Map<String,Object> resultado = new HashMap<>();

        // Datos bÃ¡sicos de la actividad
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
            "CASE WHEN m.esta_pagado = 1 THEN 'Cobrada' ELSE 'Pendiente' END AS estado " +
            "FROM Matricula m " +
            "JOIN Alumno al ON m.id_alumno = al.id_alumno " +
            "WHERE m.id_actividad = ? " +
            "AND (m.isCancelada IS NULL OR m.isCancelada = 0)",
            idActividad
        );
        resultado.put("inscripciones", inscripciones);

        // Finanzas
        // Ingresos confirmados: sumamos cuotas de inscripciones pagadas
        double ingresosConfirmados = inscripciones.stream()
                .filter(i -> "Cobrada".equals(i.get("estado")))
                .mapToDouble(i -> {
                    try {
                        return Double.parseDouble(String.valueOf(i.get("monto_pagado"))); // si tienes ese campo en Matricula
                    } catch(Exception e) {
                        return 0;
                    }
                }).sum();

        // Ingresos estimados: sumamos todas las cuotas previstas (Matricula o algÃºn campo de cuota en Actividad)
        double cuota = act.get("cuota") != null ? Double.parseDouble(String.valueOf(act.get("cuota"))) : 0;
        double ingresosEstimados = inscripciones.size() * cuota;

        // Gastos: sumamos todas las facturas asociadas a esta actividad
        List<Map<String,Object>> facturas = db.executeQueryMap(
            "SELECT cantidad FROM FacturaP WHERE id_actividad = ?", idActividad
        );
        double gastosConfirmados = facturas.stream()
                .mapToDouble(f -> Double.parseDouble(String.valueOf(f.get("cantidad"))))
                .sum();
        double gastosEstimados = gastosConfirmados; // asumimos que siempre se confirma la remuneraciÃ³n

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
	

    public List<Map<String, Object>> listarActividadesConPagosPendientes() {
        List<Map<String, Object>> actividades = db.executeQueryMap(
            """
            SELECT DISTINCT a.id_actividad, a.nombre, a.inicio_inscripcion, a.fin_inscripcion, a.fecha_inicio, a.cuota
            FROM Actividad a
            JOIN Matricula m ON a.id_actividad = m.id_actividad
            WHERE m.esta_pagado = 0
            ORDER BY a.fecha_inicio
            """
        );

        for (Map<String, Object> act : actividades) {
            act.put("estado", obtenerEstadoActividad(act));
        }

        return actividades;
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


    public boolean registrarPago(int idMatricula, double montoPagado, LocalDate fechaPago) {
        try {
            // Obtener datos de la matrÃƒÆ’Ã‚Â­cula y actividad
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
                        "No se encontrÃƒÆ’Ã‚Â³ la matrÃƒÆ’Ã‚Â­cula o la actividad asociada.",
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
                        "La cantidad pagada debe coincidir con la cuota del curso (" + cuota + " ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬).",
                        "Monto incorrecto", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Registrar pago
            db.executeUpdate("""
                UPDATE Matricula
                SET monto_pagado = ?, esta_pagado = 1
                WHERE id_matricula = ?
            """, montoPagado, idMatricula);

            // Recalcular plazas ocupadas despuÃƒÆ’Ã‚Â©s del pago
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

	
	public List<Factura> recuperarActividadesEnRango(LocalDate min, LocalDate max) {
	    List<Factura> listaActividades = new ArrayList<>();
	    String sql = "SELECT * FROM Actividad WHERE fecha_inicio >= ? AND fecha_fin <= ? ORDER BY fecha_inicio ASC";

	    List<Map<String, Object>> resultados = db.executeQueryMap(sql, min.toString(), max.toString());

	    for (Map<String, Object> fila : resultados) {
	        double cuota = fila.get("cuota") != null ? ((Number) fila.get("cuota")).doubleValue() : 0;
	        int totalPlazas = (int) fila.get("total_plazas");

	        Factura f = new Factura(totalPlazas);
	        f.setId_actividad((int) fila.get("id_actividad"));
	        f.setNombre((String) fila.get("nombre"));

	       
	        List<Map<String, Object>> facturas = db.executeQueryMap(
	            "SELECT cantidad FROM FacturaP WHERE id_actividad = ?", f.getId_actividad()
	        );
	        double gastos = facturas.stream()
	                                .mapToDouble(x -> Double.parseDouble(String.valueOf(x.get("cantidad"))))
	                                .sum();
	        f.setGastos(gastos);

	        int pagadas = recuperarPlazasPagadas(f.getId_actividad());
	        f.setPlazasOcupPagadas(pagadas);
	        int plazasOcup = recuperarPlazasOcupadas(f.getId_actividad());
	        f.setPlazasOcup(plazasOcup);
	        f.calcularIngresosReales(cuota);
	        f.calcularIngresosEstimados(cuota);
	        f.calcularEstimado(cuota);
	        f.setBalance();
	        f.getBalance();

	        f.setFecha(LocalDate.parse((String) fila.get("fecha_inicio")));
	        f.setEstado((String) getActividadDetalles(f.getId_actividad()).get("estado"));
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
		System.out.println("ESTO FUNCIONA");
	    String sql = "SELECT COUNT(*) AS total FROM Matricula WHERE id_actividad = ? AND esta_pagado = 0";
	    List<Map<String, Object>> result = db.executeQueryMap(sql, idActividad);
	    int pendientes = ((Number) result.get(0).get("total")).intValue();
	    return pendientes > 0;
	}
	
	public boolean actividadConMovimientosProfesores(int idActividad) {
		System.out.println("TIENE QUE ENTRAR AQUI");
	    String sql = "SELECT COUNT(*) AS total FROM PagoProfesor WHERE id_actividad = ?";
	    List<Map<String, Object>> result = db.executeQueryMap(sql, idActividad);
	    int pagos = ((Number) result.get(0).get("total")).intValue();
	    System.out.println("################# " + pagos);
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
					+ "inicio_inscripcion, fin_inscripcion, fecha_inicio, fecha_fin, cuota, es_gratuita, "
					+ "total_plazas, empresa, isClosed) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	        db.executeUpdate(sql, nombre, objetivos, contenidos, ubi, inicioIns, finIns, fechaInicio, fechaFin,
	        		200, selected, plazas, empresa, 0);
	        
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
		// TODO Auto-generated method stub
		return null;
	}

	public boolean cargarCuota(String categoria) {
		// TODO Auto-generated method stub
		return false;
	}

	

}