package proyecto.model.entity;

import java.time.LocalDate;

public class FechaFiltrado {
	private String fechaIn;
	private String fechaFin;
	
	private LocalDate fechaInicio;
	private LocalDate fechaFinal;
	
	public FechaFiltrado(String init, String fin) {
		fechaIn = init;
		fechaFin = fin;
		fechaInicio=LocalDate.parse(fechaIn);
		fechaFinal = LocalDate.parse(fechaFin);
	}
	
	public LocalDate getFechaIn() {
		return fechaInicio;
	}
	public LocalDate getFechaFin() {
		return fechaFinal;
	}
}
