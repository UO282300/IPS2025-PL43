package proyecto.model.entity;

public class Alumno {
	private String nombre;
	private String apellidos;
	private String correo;
	private String numeroTf;
	private boolean pertenece;
	private int id;
	
	public Alumno() {
		this.nombre=null;
		this.apellidos=null;
		this.correo=null;
		this.numeroTf=null;
	}
	
	public boolean validar() {
		return validaNombre()&&validaApellido()&&validaCorreo()&&validaNumTf();
	}

	private boolean validaNumTf() {
		for(char c: numeroTf.toCharArray()) {
			if(!Character.isDigit(c)) return false;
			
		}
		return true;
	}

	private boolean validaCorreo() {
		if (correo == null) return false;
	    correo = correo.trim(); // eliminar espacios al inicio y fin
	    return correo.contains("@") && correo.contains(".");
	}

	private boolean validaApellido() {
		for(char c: apellidos.toCharArray()) {
			if(Character.isDigit(c)) return false;
			
		}
		return true;
	}

	private boolean validaNombre() {
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

	public Object getCorreo() {
		return correo;
	}

	public Object getNombre() {
		return nombre;
	}

	public Object getApellido() {
		return apellidos;
	}

	public Object getTelefono() {
		return numeroTf;
	}

	public Object getIdAlumno() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	
}
