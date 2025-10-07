package proyecto.service;

import java.util.List;
import java.util.Map;

import proyecto.model.Database;

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

	public void cargarActividad(String nombre, String profesor, String remuneracion,
			String espacio, String fecha ,String horaI, String horaF, String inscripcionI,
			String inscripcionF, String cuota, String objetivos) {
		
		//Falta implementar
	}

    
}
