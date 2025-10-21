
package proyecto.model.entity;

import java.time.LocalDate;

public class Factura {
	
	private int id_actividad;
	private double ingresos;
	private double gastos;
	private double estimado;
	private double balance;
	private String nombre;
	private int plazas;
	private LocalDate fecha;
	private String estado;
	
	
	public Factura(int plazas) {
		this.plazas=plazas;
	}
	
	public int getId_actividad() {
		return id_actividad;
	}
	public void setId_actividad(int id_actividad) {
		this.id_actividad = id_actividad;
	}
	public double getIngresos() {
		return ingresos;
	}
	public void setIngresos(double cuota) {
		this.ingresos = cuota*plazas;
	}
	public double getGastos() {
		return gastos;
	}
	public void setGastos(double gastos) {
		this.gastos = gastos;
	}
	public double getEstimado() {
		return estimado;
	}
	public void setEstimado(double cuota) {
		this.estimado = cuota*plazas - gastos;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance,int ocupadas) {
		this.balance = balance*ocupadas - gastos;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	

	public void setFecha(LocalDate parse) {
		fecha = parse;
		
	}
	
	public LocalDate getFecha() {
		return fecha;
	}
	
	public void setEstado(String e) {
		this.estado = e;
	}
	
	public String getEstado() {
	    return estado;
	}

	public boolean estaAbierta() {
	    System.out.println("Revisando apertura: '" + estado + "'");
	    String e = estado.trim().toLowerCase();
	    return e.equals("planificada") || e.equals("en periodo de inscripci�n");
	}

	public boolean estaCerrada() {
	    System.out.println("Revisando cierre: '" + estado + "'");
	    String e = estado.trim().toLowerCase();
	    return e.equals("inscripci�n cerrada") || e.equals("finalizada")|| e.equals("cerrada");
	}

	
	public String toString() {
		StringBuilder sb  =new StringBuilder("---------------------------------"+"\n");
		sb.append("Fecha de la Actividad: " + getFecha() + "\n");
		sb.append("Nombre: " + getNombre()+ "\n");
		sb.append("Estado de la Actividad: " + getEstado()+ "\n");
		sb.append("Ingresos: " + getIngresos()+ "\n");
		sb.append("Total gastos: "+ getGastos()+ "\n");
		sb.append("Balance: " + getBalance()+ "\n");
		sb.append("-------------------------------"+ "\n");
		return sb.toString();
	}

	public Object toStringSin() {
		StringBuilder sb  =new StringBuilder("---------------------------------"+"\n");
		sb.append("Fecha de la Actividad: " + getFecha() + "\n");
		sb.append("Nombre: " + getNombre()+ "\n");
		sb.append("Estado de la Actividad: " + getEstado()+ "\n");
		sb.append("Ingresos: " + getIngresos()+ "\n");
		sb.append("Total gastos: "+ getGastos()+ "\n");
		sb.append("Balance: " + getBalance()+ "\n");
		sb.append("Estimado: " + getEstimado()+ "\n");
		sb.append("-------------------------------"+ "\n");
		return sb.toString();
	}
	
	

}
