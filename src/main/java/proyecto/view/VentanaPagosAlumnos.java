package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import proyecto.service.PagosController;
import proyecto.service.UserService;

public class VentanaPagosAlumnos extends JFrame {

    private static final long serialVersionUID = 1L;
    private PagosController us;

    private JTable tableActividades;
    private JTable tableInscripciones;
    private DefaultTableModel modelActividades;
    private DefaultTableModel modelInscripciones;
    private JTextField tfCantidad;
    private JTextField tfFecha;
    private JRadioButton rbPago;
    private JRadioButton rbDevolucion;
    private JRadioButton rbTransferencia;
    private JRadioButton rbEfectivo;
    private JLabel lblPendiente;
    private JTextField tfPendiente;
    private JTextField tfTotalPagado;
    private Map<Integer, Map<String, Object>> actividadData = new HashMap<>();
    private Map<Integer, Map<String, Object>> inscripcionData = new HashMap<>();
    private double cuotaSeleccionada = 0;
    private int idActividadSeleccionada = -1;
    private int idMatriculaSeleccionada = -1;

    @SuppressWarnings({ "serial" })
	public VentanaPagosAlumnos(UserService service) {
        this.us = new PagosController(service);
        setTitle("Registro de Pagos de Inscripciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1500, 800);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);

        JLabel lblTitulo = new JLabel("Registrar Pagos de Alumnos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));

        modelActividades = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Plazas disp."}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableActividades = new JTable(modelActividades);
        tableActividades.setRowHeight(25);
        tableActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollActividades = new JScrollPane(tableActividades);
        scrollActividades.setBorder(BorderFactory.createTitledBorder("Cursos con pagos pendientes"));
        panelCentral.add(scrollActividades);

        modelInscripciones = new DefaultTableModel(
            new Object[]{"ID Matrícula", "Nombre", "Apellido", "Teléfono", "Fecha inscripción", "Último día pago", "Estado"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableInscripciones = new JTable(modelInscripciones);
        tableInscripciones.setRowHeight(25);
        tableInscripciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollInscripciones = new JScrollPane(tableInscripciones);
        scrollInscripciones.setBorder(BorderFactory.createTitledBorder("Alumnos con pagos pendientes"));
        panelCentral.add(scrollInscripciones);
        contentPane.add(panelCentral, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Registrar movimiento"));

        JPanel panelTipoOperacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        rbPago = new JRadioButton("Pago del alumno", true);
        rbPago.setFont(new Font("Tahoma", Font.BOLD, 14));
        rbDevolucion = new JRadioButton("Devolución al alumno");
        rbDevolucion.setFont(new Font("Tahoma", Font.BOLD, 14));
        ButtonGroup grupoTipo = new ButtonGroup();
        grupoTipo.add(rbPago);
        grupoTipo.add(rbDevolucion);
        panelTipoOperacion.add(rbPago);
        panelTipoOperacion.add(rbDevolucion);
        panelInferior.add(panelTipoOperacion, BorderLayout.NORTH);
        
        rbTransferencia = new JRadioButton("Transferencia", true);
        rbTransferencia.setFont(new Font("Tahoma", Font.BOLD, 14));
        rbEfectivo = new JRadioButton("Efectivo");
        rbEfectivo.setFont(new Font("Tahoma", Font.BOLD, 14));
        ButtonGroup grupoMetodo = new ButtonGroup();
        grupoMetodo.add(rbTransferencia);
        grupoMetodo.add(rbEfectivo);
        panelTipoOperacion.add(rbTransferencia);
        panelTipoOperacion.add(rbEfectivo);
        
        ActionListener actualizarVistaPago = e -> {
            if (idMatriculaSeleccionada > 0) {
                actualizarCamposVisuales(rbPago.isSelected());
            }
        };

        rbPago.addActionListener(actualizarVistaPago);
        rbDevolucion.addActionListener(actualizarVistaPago);

        JPanel panelForm = new JPanel(new GridLayout(2, 2, 30, 15)); 

        JPanel panelTotalPagado = new JPanel(new BorderLayout(5, 5));
        panelTotalPagado.add(new JLabel("Total pagado hasta la fecha (€):"), BorderLayout.NORTH);
        tfTotalPagado = new JTextField();
        tfTotalPagado.setEditable(false);
        panelTotalPagado.add(tfTotalPagado, BorderLayout.CENTER);
        panelForm.add(panelTotalPagado);

        JPanel panelPendiente = new JPanel(new BorderLayout());
        lblPendiente = new JLabel("Cantidad pendiente (€):");
        panelPendiente.add(lblPendiente, BorderLayout.NORTH);
        tfPendiente = new JTextField();
        tfPendiente.setEditable(false);
        panelPendiente.add(tfPendiente, BorderLayout.CENTER);
        panelForm.add(panelPendiente);

        JPanel panelCantidad = new JPanel(new BorderLayout(5, 5));
        panelCantidad.add(new JLabel("Cantidad del movimiento (€):"), BorderLayout.NORTH);
        tfCantidad = new JTextField();
        panelCantidad.add(tfCantidad, BorderLayout.CENTER);
        panelForm.add(panelCantidad);

        JPanel panelFecha = new JPanel(new BorderLayout(5, 5));
        panelFecha.add(new JLabel("Fecha del movimiento (yyyy-MM-dd):"), BorderLayout.NORTH);
        tfFecha = new JTextField();
        LocalDate fechaHoy = us.getFechaHoy();
        tfFecha.setText(fechaHoy.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        panelFecha.add(tfFecha, BorderLayout.CENTER);
        panelForm.add(panelFecha);

        panelInferior.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        JButton btnRegistrar = new JButton("Registrar movimiento");
        btnRegistrar.setBackground(Color.WHITE);
        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(Color.WHITE);
        panelBotones.add(btnVolver);
        panelBotones.add(btnRegistrar);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
   
        contentPane.add(panelInferior, BorderLayout.SOUTH);

        tableActividades.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableActividades.getSelectedRow();
                if (row >= 0) {
                    idActividadSeleccionada = (int) modelActividades.getValueAt(row, 0);
                    cargarInscripcionesPendientes();
                }
            }
        });

        tableInscripciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableInscripciones.getSelectedRow();
                if (row >= 0) {
                    idMatriculaSeleccionada = (int) modelInscripciones.getValueAt(row, 0);
                    actualizarVistaPago.actionPerformed(null);
                }
            }
        });

        btnRegistrar.addActionListener(e -> registrarPago());
        btnVolver.addActionListener(e -> dispose());

        cargarActividadesActivas();

    }

    private void cargarActividadesActivas() {
        modelActividades.setRowCount(0);
        actividadData.clear();

        List<Map<String, Object>> actividades = us.listarActividades();
        if (actividades == null || actividades.isEmpty()) {
            modelActividades.addRow(new Object[]{"-", "No hay actividades registradas", "-", "-"});
            tableActividades.setEnabled(false);
            return;
        }

        for (Map<String, Object> act : actividades) {
            Object idObj = act.get("id_actividad");
            Object nombreObj = act.get("nombre");
            if (idObj == null || nombreObj == null) continue;

            int id = ((Number) idObj).intValue();
            String nombre = String.valueOf(nombreObj);

            Map<String, Object> detalles = us.getActividadDetalles(id);
            if (detalles == null) continue;

            int plazas = 0;
            if (detalles.get("plazas_disponibles") != null) {
                plazas = ((Number) detalles.get("plazas_disponibles")).intValue();
                if (plazas < 0) plazas = 0;
            }
            
            modelActividades.addRow(new Object[]{id, nombre, plazas});
            actividadData.put(id, act);
        }
        if (modelActividades.getRowCount() == 0) {
            modelActividades.addRow(new Object[]{"-", "No hay actividades con pagos pendientes", "-", "-"});
            tableActividades.setEnabled(false);
            return;
        }

        tableActividades.setEnabled(modelActividades.getRowCount() > 0);
    }

    private void cargarInscripcionesPendientes() {
    	modelInscripciones.setRowCount(0);
        inscripcionData.clear();
        idMatriculaSeleccionada = -1;

        if (idActividadSeleccionada == -1) return;

        Map<String, Object> actividad = us.getActividadDetalles(idActividadSeleccionada);
        if (actividad == null) return;

        @SuppressWarnings("unchecked")
		List<Map<String, Object>> inscripciones = (List<Map<String, Object>>) actividad.get("inscripciones");
        if (inscripciones == null || inscripciones.isEmpty()) return;

        for (Map<String, Object> ins : inscripciones) {
            int idMatricula = ((Number) ins.get("id_matricula")).intValue();
            String nombreCompleto = (String) ins.get("nombre_alumno");
            String[] partes = nombreCompleto.split(" ", 2);
            String nombre = partes.length > 0 ? partes[0] : "";
            String apellido = partes.length > 1 ? partes[1] : "";
            String telefono = ins.get("telefono") != null ? ins.get("telefono").toString() : "-";
            String fechaMatricula = ins.get("fecha_matricula") != null ? ins.get("fecha_matricula").toString() : "-";

            String fechaLimite;
            try {
                LocalDate fecha = LocalDate.parse(fechaMatricula);
                fechaLimite = fecha.plusDays(2).toString();
            } catch (Exception e) {
                fechaLimite = "-";
            }

            String estado;
            Object isCanceladaObj = ins.get("isCancelada");
            Object estaPagadoObj = ins.get("esta_pagado");

            boolean isCancelada = isCanceladaObj != null && ((Number) isCanceladaObj).intValue() == 1;
            boolean estaPagado = estaPagadoObj != null && ((Number) estaPagadoObj).intValue() == 1;

            if (isCancelada) estado = "Cancelada";
            else if (estaPagado) estado = "Cobrada";
            else estado = "Pendiente";

            modelInscripciones.addRow(new Object[]{idMatricula, nombre, apellido, telefono, fechaMatricula, fechaLimite, estado});
            inscripcionData.put(idMatricula, ins);

            cuotaSeleccionada = us.getCuotaMatricula(idMatricula);
        }
    }



    private void registrarPago() {
        if (!validarSeleccion()) return;

        Double cantidad = validarCantidad();
        if (cantidad == null) return;

        LocalDate fechaMovimiento = validarFecha();
        if (fechaMovimiento == null) return;

        Map<String, Double> estado = us.getEstadoPagoAlumno(idMatriculaSeleccionada);
        if (estado == null) {
            mostrarError("No se pudo obtener la información de pago del alumno.");
            return;
        }
        if (rbPago.isSelected()) {
            procesarPago(cantidad, fechaMovimiento, estado);
        } else {
            procesarDevolucion(cantidad, fechaMovimiento, estado);
        }

        limpiarCampos();
        actualizarCamposVisuales(rbPago.isSelected());

    }
    private boolean validarSeleccion() {
        if (idActividadSeleccionada == -1) {
            mostrarError("Debe seleccionar un curso de la tabla.");
            return false;
        }
        if (idMatriculaSeleccionada == -1) {
            mostrarError("Debe seleccionar un alumno de la lista.");
            return false;
        }
        return true;
    }
    private Double validarCantidad() {
        try {
            double cantidad = Double.parseDouble(tfCantidad.getText());
            if (cantidad <= 0) throw new NumberFormatException();
            return cantidad;
        } catch (NumberFormatException e) {
            mostrarError("Ingrese una cantidad válida y mayor que 0.");
            return null;
        }
    }
    private LocalDate validarFecha() {
        LocalDate fechaMovimiento;
        try {
            fechaMovimiento = LocalDate.parse(tfFecha.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            mostrarError("Formato de fecha inválido. Use el formato yyyy-MM-dd (por ejemplo, 2025-11-09).");
            return null;
        }

        LocalDate fechaHoy = us.getFechaHoy();
        LocalDate fechaMatricula = us.getFechaMatricula(idMatriculaSeleccionada);

        if (fechaMovimiento.isAfter(fechaHoy)) {
            mostrarError(String.format(
                "No se puede registrar una fecha futura.\n\n" +
                "Fecha introducida: %s\n" +
                "Fecha actual del sistema: %s",
                fechaMovimiento, fechaHoy
            ));
            return null;
        }

        if (fechaMovimiento.isBefore(fechaMatricula)) {
            mostrarError(String.format(
                "La fecha del movimiento no puede ser anterior a la fecha de matrícula.\n\n" +
                "Fecha matrícula: %s\n" +
                "Fecha introducida: %s",
                fechaMatricula, fechaMovimiento
            ));
            return null;
        }

        return fechaMovimiento;
    }

	    private void procesarPago(double cantidad, LocalDate fechaMovimiento, Map<String, Double> estado) {
	    	
	    	boolean porEfectivo = this.rbEfectivo.isSelected();
			double limiteEfectivo = us.getLimiteEfectivo(); 
	        if (porEfectivo && cantidad > limiteEfectivo) {
	            mostrarError(String.format(
	                "No se puede pagar más de %.2f € en efectivo por este movimiento.",
	                limiteEfectivo
	            ));
	            return; 
	        }
	        
	        String avisoPlazo = verificarPlazoPago(fechaMovimiento, idMatriculaSeleccionada) + "\n";

	        double totalPagado = estado.getOrDefault("total_pagado", 0.0);
	        double totalDevuelto = estado.getOrDefault("total_devuelto", 0.0);
	        double cuota = cuotaSeleccionada;
	
	        double netoActual = totalPagado - totalDevuelto;
	        double pendienteAntes = Math.max(0, cuota - netoActual);
	        double excesoAntes = Math.max(0, (netoActual + cantidad) - cuota);
	        double restanteDespues = Math.max(0, pendienteAntes - cantidad);
	
	        String mensaje;
	        if (Math.abs(cantidad - pendienteAntes) <= 0.01) {
	            mensaje = String.format(
	                "Confirmar pago\n"+
	                avisoPlazo +
	                "Cantidad a pagar: %.2f €\n" +
	                "Cantidad pendiente antes del pago: %.2f €\n" +
	                "Cantidad que quedará pendiente después del pago: %.2f €\n\n" +
	                "¿Desea continuar?",
	                cantidad, pendienteAntes, restanteDespues
	            );
	        } else if (cantidad < pendienteAntes) {
	            mensaje = String.format(
	            	avisoPlazo +
	                "El pago ingresado es menor que la cantidad pendiente.\n\n" +
	                "Cantidad a pagar: %.2f €\n" +
	                "Cantidad pendiente antes del pago: %.2f €\n" +
	                "Cantidad que quedará pendiente después del pago: %.2f €\n\n" +
	                "¿Desea continuar y registrar este pago parcial?",
	                cantidad, pendienteAntes, restanteDespues
	            );
	        } else { 
	            mensaje = String.format(
	            		avisoPlazo +
	                "El pago ingresado es mayor que la cantidad pendiente.\n\n" +
	                "Cantidad a pagar: %.2f €\n" +
	                "Cantidad pendiente antes del pago: %.2f €\n" +
	                "Exceso que quedará registrado: %.2f €\n\n" +
	                "¿Desea continuar y registrar este pago?",
	                cantidad, pendienteAntes, excesoAntes
	            );
	        }
	
	        int opcion = JOptionPane.showConfirmDialog(
	            this, mensaje, "Confirmar pago", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
	        );
	
	        if (opcion != JOptionPane.YES_OPTION) {
	            mostrarAviso("Operación cancelada. El pago no se ha registrado.");
	            return;
	        }
	
	        boolean ok = us.registrarPago(idMatriculaSeleccionada, cantidad, fechaMovimiento,this.rbEfectivo.isSelected());
	        if (!ok) {
	            mostrarError("Error al registrar el pago. No se guardaron los cambios.");
	            return;
	        }
	        mostrarInfo(String.format("Pago registrado correctamente.\n\nSe pagaron %.2f €.", cantidad));

	
	        Map<String, Double> nuevoEstado = us.getEstadoPagoAlumno(idMatriculaSeleccionada);
	        if (nuevoEstado == null) return;

	        actualizarCamposVisuales(true);
	        cargarInscripcionesPendientes();
	        cargarActividadesActivas();

	    }

	    private void procesarDevolucion(double cantidad, LocalDate fechaMovimiento, Map<String, Double> estado) {
	        double totalPagado = estado.getOrDefault("total_pagado", 0.0);
	        double totalDevuelto = estado.getOrDefault("total_devuelto", 0.0);
	        double cuota = estado.getOrDefault("cuota", 0.0);
	        boolean isCancelada = estado.getOrDefault("is_cancelada", 0.0) == 1.0; 

	        double disponibleParaDevolver = isCancelada
	                ? Math.max(0.0, totalPagado - totalDevuelto)
	                : Math.max(0.0, totalPagado - totalDevuelto - cuota);

	        double diferencia = cantidad - disponibleParaDevolver;
	        String mensajeConfirmacion;

	        if (isCancelada) {
	            mensajeConfirmacion = String.format(
	                "La matrícula fue cancelada, por lo que puede devolverse el total pagado.\n\n" +
	                "Cantidad registrada: %.2f €\n" +
	                "Cantidad que estaba pendiente: %.2f €\n" +
	                "¿Desea continuar con la devolución?",
	                cantidad, disponibleParaDevolver
	            );
	        } else if (Math.abs(diferencia) < 0.01) {
	            mensajeConfirmacion = String.format(
	                "Confirmar devolución\n\n" +
	                "Cantidad a devolver: %.2f €\n" +
	                "Saldo disponible para devolver: %.2f €\n" +
	                "¿Desea continuar?",
	                cantidad, disponibleParaDevolver, disponibleParaDevolver-cantidad
	            );
	        } else if (cantidad < disponibleParaDevolver) {
	            double restante = disponibleParaDevolver - cantidad;
	            mensajeConfirmacion = String.format(
	                "Confirmar devolución parcial\n\n" +
	                "Cantidad a devolver: %.2f €\n" +
	                "Cantidad pendiente a devolver: %.2f €\n" +
	                "Cantidad pendiente que se genera: %.2f €\n\n" +
	                "¿Desea continuar con la devolución parcial?",
	                cantidad, disponibleParaDevolver, restante
	            );
	        } else {
	            double exceso = cantidad - disponibleParaDevolver;
	            mensajeConfirmacion = String.format(
	                "Atención: la cantidad a devolver supera la disponible.\n\n" +
	                "Cantidad a devolver: %.2f €\n" +
	                "Cantidad pendiente por devolucion: %.2f €\n" +
	                "Exceso : %.2f €\n\n" +
	                "¿Desea continuar igualmente?",
	                cantidad, disponibleParaDevolver, exceso
	            );
	        }

	        int opcion = JOptionPane.showConfirmDialog(
	            this, mensajeConfirmacion, "Confirmar devolución", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
	        );

	        if (opcion != JOptionPane.YES_OPTION) {
	            mostrarAviso("Operación cancelada. La devolución no se ha registrado.");
	            return;
	        }

	        boolean ok = us.registrarDevolucion(idMatriculaSeleccionada, cantidad, fechaMovimiento);
	        if (!ok) {
	            mostrarError("Error al registrar la devolución. No se guardaron los cambios.");
	            return;
	        }

	        actualizarCamposVisuales(false);
	        cargarActividadesActivas();

	        mostrarInfo(String.format("Devolución registrada correctamente.\n\nSe devolvieron %.2f €.", cantidad));
	    }


    private void actualizarCamposVisuales(boolean esPago) {
        if (idMatriculaSeleccionada <= 0) return;
        tfFecha.setText(us.getFechaHoy().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Map<String, Double> estadoPago = us.getEstadoPagoAlumno(idMatriculaSeleccionada);
        if (estadoPago == null) return;

        double totalPagado = estadoPago.getOrDefault("total_pagado", 0.0);
        double totalDevuelto = estadoPago.getOrDefault("total_devuelto", 0.0);
        double aDevolver = estadoPago.getOrDefault("a_devolver", 0.0);

        if (esPago) {
            lblPendiente.setText("Cantidad pendiente (€):");
            tfTotalPagado.setText(String.format("%.2f", totalPagado));
            double pendiente = estadoPago.getOrDefault("pendiente", 0.0);
            tfPendiente.setText(String.format("%.2f", pendiente));
        } else {
            lblPendiente.setText("A devolver (€):");
            tfTotalPagado.setText(String.format("%.2f", totalDevuelto));
            tfPendiente.setText(String.format("%.2f", aDevolver)); 
        }
    }
    
    public String verificarPlazoPago(LocalDate fechaMovimiento, int idMatricula) {
        LocalDate fechaMatricula = us.getFechaMatricula(idMatricula);
        if (fechaMatricula == null) {
            return "No se ha podido obtener la fecha de matrícula del alumno.";
        }

        LocalDate fechaLimite = fechaMatricula.plusDays(2);

        if (fechaMovimiento.isBefore(fechaMatricula) || fechaMovimiento.isAfter(fechaLimite)) {
            return String.format(
                "La fecha introducida (%s) está fuera del plazo permitido.\n\n" +
                "El plazo válido de pago es desde %s hasta %s (ambos inclusive).",
                fechaMovimiento.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                fechaMatricula.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                fechaLimite.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
        }

        return ""; 
    }



    private void limpiarCampos() {
        tfCantidad.setText("");
        tfFecha.setText("");
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

}

