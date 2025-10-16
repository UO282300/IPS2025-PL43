package proyecto.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import proyecto.model.Database;
import proyecto.util.ApplicationException;

public class UserService {
	private Database db;
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
	            (nombre, objetivos, contenidos, id_profesor, remuneracion, espacio, plazas, fecha,
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

    
}
