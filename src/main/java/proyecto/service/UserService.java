package proyecto.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
	            throw new ApplicationException("Formato de fecha inválido. Usa el formato dd/MM/yyyy");
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

    
}
