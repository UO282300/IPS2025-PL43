
package proyecto.model.entity;

public class Alumno {
	private String nombre;
	private String apellidos;
	private String correo;
	private String numeroTf;
	private boolean pertenece;
	private int id;
	private int id_cuota;
	
	public Alumno() {
		this.nombre=null;
		this.apellidos=null;
		this.correo=null;
		this.numeroTf=null;
		this.id_cuota=0;
	}
	
	
	
	public void setId_Cuota(int id) {
		this.id_cuota=id;
	}
	
	public int getId_cuota() {
		return id_cuota;
	}
	
	public boolean validarTf() {
		for(char c: numeroTf.toCharArray()) {
			if(!Character.isDigit(c)) return false;
			
		}
		return true;
	}

	public boolean validarEmail() {
		if (correo == null) return false;
	    correo = correo.trim(); // eliminar espacios al inicio y fin
	    return correo.contains("@") && correo.contains(".");
	}

	public boolean validarApellido() {
		for(char c: apellidos.toCharArray()) {
			if(Character.isDigit(c)) return false;
			
		}
		return true;
	}

	public boolean validarNombre() {
		for(char c: nombre.toCharArray()) {
			if(Character.isDigit(c)) return false;
			
		}
		return true;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public void setNumeroTf(String numeroTf) {
		this.numeroTf = numeroTf;
	}

	public void setPertenece(boolean selected) {
		this.pertenece = selected;
		
	}
	
	public boolean pertenece() {
		return pertenece;
	}
	
	public String getCorreo() {
		return correo;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellido() {
		return apellidos;
	}

	public String getTelefono() {
		return numeroTf;
	}

	public Object getIdAlumno() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setPerteneceDB(int i) {
		if(i==0) pertenece=false;
		else {
			pertenece=true;
		}
		
	}
	
	
	
}
