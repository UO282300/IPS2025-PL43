package proyecto.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;

public class EmailProfesoresController {


    public void generarEmailCobroPendiente(String nombreProfesor,
            String nombreActividad,
            double cantidadPagada,
            LocalDate fecha,
            double totalPagadoAntes,
            double totalDevueltoAntes,
            double montoTotalFactura) {

		double netoAntes = totalPagadoAntes - totalDevueltoAntes;
		double netoDespues = netoAntes + cantidadPagada;
		double pendienteDespues = Math.max(0, montoTotalFactura - netoDespues);
		
		StringBuilder sb = new StringBuilder();
		sb.append(generarMensajePagoPredeterminado(nombreProfesor,nombreActividad,fecha,cantidadPagada));

		sb.append("\nSin embargo, debo informarle que con este pago aún nos quedan ")
		.append(pendienteDespues)
		.append(" € pendientes por pagarle para que su cobro esté completo.\n")
		.append("Nos aseguraremos de efectuar pagos próximamente para pagarle la cantidad pendiente.\n");
		
		guardarEmailEnFichero(sb.toString());
	}
    
    public void generarEmailPCobroCompleto(String nombreProfesor,
            String nombreActividad,
            double cantidadPagada,
            LocalDate fecha,
            double totalPagadoAntes,
            double totalDevueltoAntes,
            double montoTotalFactura) {

		double netoAntes = totalPagadoAntes - totalDevueltoAntes;
		double netoDespues = netoAntes + cantidadPagada;
		double pendienteDespues = Math.max(0, netoDespues - montoTotalFactura);
		
		StringBuilder sb = new StringBuilder();
		sb.append(generarMensajePagoPredeterminado(nombreProfesor,nombreActividad,fecha,cantidadPagada));

		sb.append("\nCon este pago realizado, me congratula informarle que ya usted ya ha cobrado\n")
		.append("todo el dinero que figuraba en su factura.\n");
		if (pendienteDespues > 0) {
			sb.append("Sin embargo, debo informarle que ha habido un error en los pagos y usted ha cobrado\n")
			.append(pendienteDespues)
			.append("€ más de lo que correspondia.\n")
			.append("Debido a ello, le pedimos por favor que nos efectúe unos pagos compensatorios\n")
			.append("lo más pronto posible para satisfacer ese exceso.");
		}
		
		
		guardarEmailEnFichero(sb.toString());
	}
    
    public void generarEmailDevolucionPendiente(String nombreProfesor,
            String nombreActividad,
            double cantidadPagada,
            LocalDate fecha,
            double diferencia) {

		
		StringBuilder sb = new StringBuilder();
		sb.append(generarMensajeDevolucionPredeterminado(nombreProfesor,nombreActividad,fecha,cantidadPagada));

		sb.append("\nSin embargo, debo informarle que tras este pago compensatorio\n")
		.append("aún quedan ")
		.append(diferencia)
		.append(" € pendientes por compersar.\n")
		.append("Esperamos que usted efectúe otros pagos compensatorios para satisfacer dicha deuda próximamente");
		
		
		guardarEmailEnFichero(sb.toString());
	}
    
    
    public void generarEmailDevolucionCompleta(String nombreProfesor,
            String nombreActividad,
            double cantidadPagada,
            LocalDate fecha,
            double diferencia) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(generarMensajeDevolucionPredeterminado(nombreProfesor,nombreActividad,fecha,cantidadPagada));

		sb.append("\nCon este pago compensatorio realizado, me congratula informarle que usted ya ha devuelto\n")
		.append("todo el dinero que habíamos generado en exceso al mandarle los pagos de su factura.\n");
		if (diferencia > 0) {
			sb.append("Sin embargo, se ha detectado una incidencia en dichos pagos, generándose un exceso de\n")
			.append(Math.abs(diferencia))
			.append("€ más de lo que correspondia.\n")
			.append("Debido a ello, desde COIIPA le enviaremos más pagos para saldar ese exceso.\n");
		}
		
		
		guardarEmailEnFichero(sb.toString());
	}
    
    private StringBuilder generarMensajePagoPredeterminado(
            String nombreProfesor,
            String nombreActividad,
            LocalDate fecha,
            double cantidad) {
        
        StringBuilder sb = new StringBuilder();

        sb.append("Estimado/a ").append(nombreProfesor).append(",\n\n");
        sb.append("Soy Rosa, la secretaria del COIIPA. Le escribo para informarle de que ya le hemos enviado\n")
          .append("un pago con un importe de ")
          .append(String.format("%.2f €", cantidad))
          .append(" el día ")
          .append(fecha) 
          .append("\npara saldar su cobro por impartir el curso ")
          .append(nombreActividad)
          .append(".");

        return sb;
    }
    
    private StringBuilder generarMensajeDevolucionPredeterminado(
    		String nombreProfesor,
            String nombreActividad,
            LocalDate fecha,
            double cantidad) {
        
        StringBuilder sb = new StringBuilder();

        sb.append("Estimado/a ").append(nombreProfesor).append(",\n\n");
        sb.append("Soy Rosa, la secretaria del COIIPA. Le escribo para notificarle de que hemos recibido\n")
          .append("el pago compensatorio que ha producido con un importe de ")
          .append(String.format("%.2f €", cantidad))
          .append(" el día ")
          .append(fecha) 
          .append("\npara compensar el exceso que hemos generado por error al pagar\n")
          .append("la factura correspondiente por impartir ")
          .append(nombreActividad)
          .append(".");
        return sb;
    }

    
    private void guardarEmailEnFichero(String email) {

        StringBuilder sb = new StringBuilder().append(email);

        sb.append("\n\nPor favor, si tiene alguna duda o quiere que le confirme \n")
          .append("cualquier información acerca de sus pagos o factura, \n")
          .append("contésteme a este email y volveré a entrar en contacto con usted lo más pronto posible.\n\n")
          .append("Muchas gracias y un saludo.\n")
          .append("Rosa, secretaria del COIIPA\n");

        String ruta = "EmailsPagosProfesores";

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
