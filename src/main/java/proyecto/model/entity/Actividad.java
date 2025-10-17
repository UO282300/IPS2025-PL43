package proyecto.model.entity;

import java.time.LocalDate;


public class Actividad {
	private int id_Actividad;
	private String nombre;
	private String objetivos;
	private String contenidos;
	private double remuneracion;
	private String espacio;
	private LocalDate fecha;
	private String horaInicio;
	private String horaFin;
	private LocalDate inicio_insc;
	private LocalDate fin_inscr;
	private double cuota;
	private boolean es_gratuita;
	private int id_profesor;
	private int plazas;
	
	public int getId_Actividad() {
		return id_Actividad;
	}
	public void setId_Actividad(int i) {
		this.id_Actividad = i;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getObjetivos() {
		return objetivos;
	}
	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}
	public String getContenidos() {
		return contenidos;
	}
	public void setContenidos(String contenidos) {
		this.contenidos = contenidos;
	}
	public double getRemuneracion() {
		return remuneracion;
	}
	public void setRemuneracion(double remuneracion) {
		this.remuneracion = remuneracion;
	}
	public String getEspacio() {
		return espacio;
	}
	public void setEspacio(String string) {
		this.espacio = string;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public String getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}
	public String getHoraFin() {
		return horaFin;
	}
	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}
	public LocalDate getInicio_insc() {
		return inicio_insc;
	}
	public void setInicio_insc(LocalDate inicio_insc) {
		this.inicio_insc = inicio_insc;
	}
	public LocalDate getFin_inscr() {
		return fin_inscr;
	}
	public void setFin_inscr(LocalDate fin_inscr) {
		this.fin_inscr = fin_inscr;
	}
	public double getCuota() {
		return cuota;
	}
	public void setCuota(double cuota) {
		this.cuota = cuota;
	}
	public boolean isEs_gratuita() {
		return es_gratuita;
	}
	public void setEs_gratuita(boolean es_gratuita) {
		this.es_gratuita = es_gratuita;
	}
	public int getId_profesor() {
		return id_profesor;
	}
	public void setId_profesor(int i) {
		this.id_profesor = i;
	}
	
	public String toString() {
		return "Nombre: " + getNombre() + " --------------  Objetivo : " + getObjetivos() + " -------------- FECHA : " + getFecha() + " | " + getHoraInicio()+"--"+getHoraFin();
	}
	public void setPlazas(Number number) {
		// TODO Auto-generated method stub
		plazas=(int) number;
	}
	

}
