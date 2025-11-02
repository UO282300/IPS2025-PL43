
package proyecto.model.entity;

import java.time.LocalDate;

public class FechaFiltrado {
	private String fechaIn;
	private String fechaFin;
	
	private LocalDate fechaInicio;
	private LocalDate fechaFinal;
	
	public FechaFiltrado(String init, String fin, LocalDate hoy) {
		fechaIn = init;
		fechaFin = fin;
		if(fechaIn==null) {
			fechaInicio = LocalDate.of(hoy.getYear(), 1, 1);
			fechaFinal = LocalDate.parse(fechaFin);
		}else if(fechaFin==null) {
			fechaInicio=LocalDate.parse(fechaIn);
			fechaFinal=LocalDate.of(fechaInicio.getYear(), 12, 31);
		}else {
			fechaInicio=LocalDate.parse(fechaIn);
			fechaFinal = LocalDate.parse(fechaFin);
		}
		
	}
	
	public LocalDate getFechaIn() {
		return fechaInicio;
	}
	public LocalDate getFechaFin() {
		return fechaFinal;
	}
}
