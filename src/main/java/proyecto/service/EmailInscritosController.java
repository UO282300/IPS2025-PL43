package proyecto.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;

public class EmailInscritosController {


    public void generarEmailPagoPendiente(String nombreAlumno,
            String nombreActividad,
            double cantidadPagada,
            LocalDate fecha,
            LocalDate fechaLimite,
            boolean porEfectivo,
            double totalPagadoAntes,
            double totalDevueltoAntes,
            double montoTotalMatricula) {

		String metodo = porEfectivo ? "efectivo" : "transferencia bancaria";
		double netoAntes = totalPagadoAntes - totalDevueltoAntes;
		double netoDespues = netoAntes + cantidadPagada;
		double pendienteDespues = Math.max(0, montoTotalMatricula - netoDespues);
		
		StringBuilder sb = new StringBuilder();
		sb.append(generarMensajePagoPredeterminado(nombreAlumno,nombreActividad,fecha,cantidadPagada,metodo));

		sb.append("\nSin embargo, debo informarle que con este pago aún le quedan ")
		.append(pendienteDespues)
		.append(" € pendientes por pagar para completar su matrícula.\n")
		.append("Por lo tanto, para partcipar en el curso es necesario que \n")
		.append("efectúe más pagos para satisfacer dicha cantidad pendiente antes de ")
		.append(fechaLimite);
		
		System.out.println(sb.toString());
		
		guardarEmailEnFichero(sb.toString());
	}
    
    public void generarEmailMatriculaCompleta(String nombreAlumno,
            String nombreActividad,
            double cantidadPagada,
            LocalDate fecha,
            LocalDate fechaInicio,
            boolean porEfectivo,
            double totalPagadoAntes,
            double totalDevueltoAntes,
            double montoTotalMatricula) {

		String metodo = porEfectivo ? "el efectivo" : "la transferencia";
		double netoAntes = totalPagadoAntes - totalDevueltoAntes;
		double netoDespues = netoAntes + cantidadPagada;
		double pendienteDespues = Math.max(0, netoDespues - montoTotalMatricula);
		
		StringBuilder sb = new StringBuilder();
		sb.append(generarMensajePagoPredeterminado(nombreAlumno,nombreActividad,fecha,cantidadPagada,metodo));

		sb.append("\nCon este pago realizado, me congratula informarle que ya se ha pagado\n")
		.append("su matricula completamente y con éxito, \n por lo que el ")
		.append(fechaInicio)
		.append(" podrá empezar el curso.\n");
		if (pendienteDespues > 0) {
			sb.append("Sin embargo, debo informarle que usted ha pagado en total\n")
			.append(pendienteDespues)
			.append("€ más de lo que correspondia\n")
			.append("Debido a ello, desde COIIPA nos encargaremos de efectuarle\n")
			.append("lo más pronto posible un pago compensatorio para satisfacer ese exceso");
		}
		
		System.out.println(sb.toString());
		
		guardarEmailEnFichero(sb.toString());
	}
    
    public void generarEmailDevolucionPendiente(String nombreAlumno,
            String nombreActividad,
            double cantidadPagada,
            LocalDate fecha,
            boolean porEfectivo,
            double totalPagadoAntes,
            double totalDevueltoAntes,
            double cuota) {

		String metodo = porEfectivo ? "efectivo" : "transferencia bancaria";
		double pendienteDespues = Math.max(0, (totalPagadoAntes - cuota 	) - totalDevueltoAntes - cantidadPagada);
		
		StringBuilder sb = new StringBuilder();
		sb.append(generarMensajeDevolucionPredeterminado(nombreAlumno,nombreActividad,fecha,cantidadPagada,metodo));

		sb.append("\nSin embargo, debo informarle que tras este pago compensatorio aún no le\n")
		.append("hemos devuelto todo el exceso que sus pagos han generado, quedando en total\n")
		.append(pendienteDespues)
		.append(" € pendientes por compensarle.\n")
		.append("Lo más pronto posible efectuaremos más pagos compensatorios hasta que dicho exceso\n")
		.append("le haya sido devuelto.");
		
		System.out.println(sb.toString());
		
		guardarEmailEnFichero(sb.toString());
	}
    
    public void generarEmailDevolucionCompleta(String nombreAlumno,
            String nombreActividad,
            double cantidadPagada,
            LocalDate fecha,
            boolean porEfectivo,
            double exceso) {

    	String metodo = porEfectivo ? "el efectivo" : "la transferencia";
		
		StringBuilder sb = new StringBuilder();
		sb.append(generarMensajeDevolucionPredeterminado(nombreAlumno,nombreActividad,fecha,cantidadPagada,metodo));

		sb.append("\nCon este pago compenstaorio realizado, me congratula informarle que ya se le ha devuelto\n")
		.append("todo el dinero que le debíamos a causa del exceso generado en sus pagos");
		if (exceso > 0) {
			sb.append("Sin embargo, ha habido una equivoquacion en dichos pagos y se le ha devuelto\n")
			.append(Math.abs(exceso))
			.append("€ más de lo que correspondia\n")
			.append("Debido a ello, desde COIIPA le pedimos por favor que nos efectué otro pago\n")
			.append("para devolver el dinero de más que le hemos enviado");
		}
		
		System.out.println(sb.toString());
		
		guardarEmailEnFichero(sb.toString());
	}
    
    private StringBuilder generarMensajePagoPredeterminado(
            String nombreAlumno,
            String nombreActividad,
            LocalDate fecha,
            double cantidad,
            String metodo) {
        
        StringBuilder sb = new StringBuilder();

        sb.append("Estimado/a ").append(nombreAlumno).append(",\n\n");
        sb.append("Soy Rosa, la secretaria del COIIPA. Le escribo para informarle de que el pago mediante ")
          .append(metodo.toLowerCase())
          .append("\n que usted ha emitidio por importe de ")
          .append(String.format("%.2f €", cantidad))
          .append(" el día ")
          .append(fecha) 
          .append("\ncorrespondiente a su matrícula en la actividad\n")
          .append(nombreActividad)
          .append("ha sido recibido por nosotros sin ninguna incidencia.\n\n");

        return sb;
    }
    
    private StringBuilder generarMensajeDevolucionPredeterminado(
    		String nombreAlumno,
            String nombreActividad,
            LocalDate fecha,
            double cantidad,
            String metodo) {
        
        StringBuilder sb = new StringBuilder();

        sb.append("Estimado/a ").append(nombreAlumno).append(",\n\n");
        sb.append("Soy Rosa, la secretaria del COIIPA. Le escribo para avisarle de que le hemos enviado\n")
          .append("una devolución mediante ")
          .append(metodo.toLowerCase())
          .append("\ncon un importe de ")
          .append(String.format("%.2f €", cantidad))
          .append(" el día ")
          .append(fecha) 
          .append("\npara compensar el exceso que sus pagos han generado en su matrícula de la actividad \"")
          .append(nombreActividad)
          .append("\"");
        return sb;
    }

    
    private void guardarEmailEnFichero(String email) {

        StringBuilder sb = new StringBuilder().append(email);

        sb.append("\n\nPor favor, si tiene alguna duda o quiere que le confirme \n")
          .append("cualquier información acerca de sus pagos o matrícula, \n")
          .append("contésteme a este email y volveré a entrar en contacto con usted lo más pronto posible.\n\n")
          .append("Muchas gracias y un saludo.\n")
          .append("Rosa, secretaria del COIIPA\n");

        String ruta = "EmailsPagosInscritos";

        try (FileWriter fw = new FileWriter(ruta, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println("----- NUEVO EMAIL -----");
            out.println(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error guardando el email en el fichero.");
        }
    }


}
