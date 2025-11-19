package proyecto.view;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import proyecto.service.PagosController;
import proyecto.service.UserService;
import java.util.List;

public class VentanaPagoProfesores extends JFrame {

    private static final long serialVersionUID = 1L;

    private PagosController us;

    private JTable tableCursos, tableProfesores;
    private DefaultTableModel modelCursos, modelProfesores;
    private JTextField tfCantidad, tfFecha, tfTotalPagado, tfCantidadPendiente;
    private JTextField tfIdFactura, tfNumeroFactura, tfDireccionEmisor, tfCantidadFactura, tfFechaFactura, tfNifEmisor;
    private JRadioButton rbPago, rbDevolucion;

    private int idActividadSeleccionada = -1;
    private int idProfesorSeleccionado = -1;
    private int idFactura = -1;

    public VentanaPagoProfesores(UserService service) {
        this.us = new PagosController(service);
        setTitle("Registro de Pagos y Devoluciones a Profesores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1500, 840);
        setLocationRelativeTo(null);

        inicializar();
        cargarCursos();
    }

    private void  inicializar() {
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);

        JLabel lblTitulo = new JLabel("Registrar Pagos y Devoluciones a Profesores", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        initTablaCursos(panelCentral);
        initTablaProfesores(panelCentral);

        JPanel panelFactura = new JPanel(new GridLayout(3, 4, 10, 10));
        panelFactura.setBorder(BorderFactory.createTitledBorder("Datos de la Factura"));
        tfIdFactura = crearCampo(panelFactura, "ID Factura:");
        tfNumeroFactura = crearCampo(panelFactura, "Numero Factura:");
        tfNifEmisor = crearCampo(panelFactura, "NIF Emisor:");
        tfDireccionEmisor = crearCampo(panelFactura, "Direccion Emisor:");
        tfCantidadFactura = crearCampo(panelFactura, "Cantidad Factura (euros):");
        tfFechaFactura = crearCampo(panelFactura, "Fecha Factura:");

        JPanel centroExtendido = new JPanel(new BorderLayout(10, 10));
        centroExtendido.add(panelCentral, BorderLayout.CENTER);
        centroExtendido.add(panelFactura, BorderLayout.SOUTH);
        contentPane.add(centroExtendido, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Registrar movimiento"));
        initPanelMovimiento(panelInferior);
        contentPane.add(panelInferior, BorderLayout.SOUTH);
    }

    @SuppressWarnings("serial")
	private void initTablaCursos(JPanel panel) {
        modelCursos = new DefaultTableModel(new Object[]{"ID Actividad", "Nombre"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tableCursos = new JTable(modelCursos);
        tableCursos.setRowHeight(25);
        tableCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollCursos = new JScrollPane(tableCursos);
        scrollCursos.setBorder(BorderFactory.createTitledBorder("Cursos con facturas pendientes"));
        panel.add(scrollCursos);

        tableCursos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableCursos.getSelectedRow();
                if (row >= 0) {
                    idActividadSeleccionada = (int) modelCursos.getValueAt(row, 0);
                    cargarProfesoresDelCurso();
                    limpiarCamposFactura();
                }
            }
        });
    }

    @SuppressWarnings("serial")
	private void initTablaProfesores(JPanel panel) {
        modelProfesores = new DefaultTableModel(new Object[]{"ID Profesor", "Nombre", "Apellidos", "Telefono"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tableProfesores = new JTable(modelProfesores);
        tableProfesores.setRowHeight(25);
        JScrollPane scrollProfesores = new JScrollPane(tableProfesores);
        scrollProfesores.setBorder(BorderFactory.createTitledBorder("Profesor del curso seleccionado"));
        panel.add(scrollProfesores);

        tableProfesores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableProfesores.getSelectedRow();
                if (row >= 0) {
                    idProfesorSeleccionado = (int) modelProfesores.getValueAt(row, 0);
                    cargarDatosFactura();
                    cargarTotalesProfesor();
                }
            }
        });
    }

    private void initPanelMovimiento(JPanel panelInferior) {
        JPanel panelTipo = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        rbPago = new JRadioButton("Pago al profesor", true);
        rbPago.setFont(new Font("Tahoma", Font.BOLD, 14));
        rbDevolucion = new JRadioButton("Devolucion del profesor");
        rbDevolucion.setFont(new Font("Tahoma", Font.BOLD, 14));
        ButtonGroup grupoTipo = new ButtonGroup();
        grupoTipo.add(rbPago);
        grupoTipo.add(rbDevolucion);
        panelTipo.add(rbPago);
        panelTipo.add(rbDevolucion);
        panelInferior.add(panelTipo, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(2, 2, 20, 10));
        tfTotalPagado = crearCampoConEtiqueta(panelForm, "Total pagado (euros):", false);
        tfCantidadPendiente = crearCampoConEtiqueta(panelForm, "Cantidad pendiente (euros):", false);
        tfCantidad = crearCampoConEtiqueta(panelForm, "Cantidad del movimiento (euros):", true);
        tfFecha = crearCampoConEtiqueta(panelForm, "Fecha (yyyy-MM-dd):", true);
        tfFecha.setText(us.getFechaHoy().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        panelInferior.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegistrar = new JButton("Registrar Movimiento");
        JButton btnVolver = new JButton("Volver");
        panelBotones.add(btnVolver);
        panelBotones.add(btnRegistrar);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        btnVolver.addActionListener(e -> dispose());
        btnRegistrar.addActionListener(e -> registrarMovimiento());

        rbPago.addActionListener(e -> cargarTotalesProfesor());
        rbDevolucion.addActionListener(e -> cargarTotalesProfesor());
    }

    private JTextField crearCampo(JPanel panel, String label) {
        panel.add(new JLabel(label));
        JTextField tf = new JTextField();
        tf.setEditable(false);
        panel.add(tf);
        return tf;
    }

    private JTextField crearCampoConEtiqueta(JPanel panel, String label, boolean editable) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(label), BorderLayout.NORTH);
        JTextField tf = new JTextField();
        tf.setEditable(editable);
        p.add(tf, BorderLayout.CENTER);
        panel.add(p);
        return tf;
    }

    private void limpiarCamposFactura() {
        tfIdFactura.setText("");
        tfNumeroFactura.setText("");
        tfNifEmisor.setText("");
        tfDireccionEmisor.setText("");
        tfCantidadFactura.setText("");
        tfFechaFactura.setText("");
    }

    private void cargarCursos() {
        modelCursos.setRowCount(0);
        List<Map<String, Object>> cursos = us.listarTodosLosCursosConProfesores();
        if (cursos == null || cursos.isEmpty()) {
            modelCursos.addRow(new Object[]{"-", "No hay cursos disponibles", "-"});
            tableCursos.setEnabled(false);
            return;
        }
        for (Map<String, Object> curso : cursos) {
            int id = ((Number) curso.get("id_actividad")).intValue();
            String nombre = (String) curso.get("nombre");
            double remuneracion = curso.get("remuneracion") != null ? ((Number) curso.get("remuneracion")).doubleValue() : 0.0;
            modelCursos.addRow(new Object[]{id, nombre, remuneracion});
        }
        tableCursos.setEnabled(true);
    }

    private void cargarProfesoresDelCurso() {
        modelProfesores.setRowCount(0);
        idProfesorSeleccionado = -1;
        if (idActividadSeleccionada == -1) return;

        List<Map<String, Object>> profesores = us.obtenerProfesoresPorActividad(idActividadSeleccionada);
        if (profesores == null || profesores.isEmpty()) return;

        for (Map<String, Object> prof : profesores) {
            int id = ((Number) prof.get("id_profesor")).intValue();
            String nombre = (String) prof.get("profesor_nombre");
            String apellido = (String) prof.get("profesor_apellido");
            String telefono = (String) prof.get("profesor_telefono");
            modelProfesores.addRow(new Object[]{id, nombre, apellido, telefono});
        }
    }

    private void cargarDatosFactura() {
        limpiarCamposFactura();
        if (idProfesorSeleccionado == -1 || idActividadSeleccionada == -1) return;

        Map<String, Object> factura = us.obtenerDatosFacturaPorProfesorYActividad(idProfesorSeleccionado, idActividadSeleccionada);
        if (factura == null || factura.isEmpty()) {
            idFactura = -1; 
            return;
        }

        idFactura = ((Number) factura.get("id_factura")).intValue();
        tfIdFactura.setText(String.valueOf(idFactura));
        tfNumeroFactura.setText(String.valueOf(factura.get("numero_factura")));
        tfNifEmisor.setText(String.valueOf(factura.get("emisor_nif")));
        tfDireccionEmisor.setText(String.valueOf(factura.get("direccion_emisor")));
        tfCantidadFactura.setText(String.format("%.2f", ((Number) factura.get("cantidad")).doubleValue()));
        tfFechaFactura.setText(String.valueOf(factura.get("fecha")));
    }


    private void cargarTotalesProfesor() {
        if (idProfesorSeleccionado == -1 || idActividadSeleccionada == -1) return;

        Map<String, Object> totales = us.obtenerTotalesFacturaProfesor(idFactura);
        if (totales == null || totales.isEmpty()) return;

        double totalPagado = ((Number) totales.getOrDefault("total_pagado", 0.0)).doubleValue();
        double totalDevuelto = ((Number) totales.getOrDefault("total_devuelto", 0.0)).doubleValue();
        double importeFactura = ((Number) totales.getOrDefault("importe_factura", 0.0)).doubleValue();
        double neto = totalPagado - totalDevuelto;

        if (rbDevolucion.isSelected()) {
            tfTotalPagado.setText(String.format("%.2f", totalDevuelto));
            tfCantidadPendiente.setText(String.format("%.2f", Math.max(0, neto - importeFactura)));
        } else {
            tfTotalPagado.setText(String.format("%.2f", totalPagado));
            tfCantidadPendiente.setText(String.format("%.2f", Math.max(0, importeFactura - neto)));
        }
    }




    private void registrarMovimiento() {
        if (idProfesorSeleccionado == -1 || idActividadSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un curso y un profesor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(tfCantidad.getText().trim().replace(",", "."));
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida mayor que 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(tfFecha.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, Object> factura = us.obtenerDatosFacturaPorProfesorYActividad(idProfesorSeleccionado, idActividadSeleccionada);
        int idFactura;

        if (factura != null && factura.get("id_factura") != null) {
            idFactura = ((Number) factura.get("id_factura")).intValue();
        } else {
            idFactura = -1;
        }

        if (rbPago.isSelected()) {
            procesarPagoProfesor(cantidad, fecha, idFactura);
        } else {
            procesarDevolucionProfesor(cantidad, fecha, idFactura);
        }

        cargarTotalesProfesor();
        tfCantidad.setText("");

    }

    private void procesarPagoProfesor(double cantidad, LocalDate fecha, int idFactura) {
        Map<String, Object> totalesAntes = us.obtenerTotalesFacturaProfesor(idFactura);
        double totalPagadoAntes = ((Number) totalesAntes.getOrDefault("total_pagado", 0.0)).doubleValue();
        double totalDevueltoAntes = ((Number) totalesAntes.getOrDefault("total_devuelto", 0.0)).doubleValue();
        double importeFactura = ((Number) totalesAntes.getOrDefault("importe_factura", 0.0)).doubleValue();

        double netoAntes = totalPagadoAntes - totalDevueltoAntes;
        double nuevoNeto = netoAntes + cantidad;
        double excesoPrevisto = Math.max(0, nuevoNeto - importeFactura);
        double pendientePrevista = Math.max(0, importeFactura - nuevoNeto);

        if (excesoPrevisto > 0.01) {
            int opcion = JOptionPane.showConfirmDialog(
                this,
                String.format(
                    "Se están pagando %.2f € en vez de %.2f €\n Se provocará un exceso de %.2f €.\n¿Desea continuar?",
                    cantidad, importeFactura, excesoPrevisto
                ),
                "Confirmar pago en exceso",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (opcion != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Operación cancelada. No se registró el pago.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else if (pendientePrevista > 0.01) {
            int opcion = JOptionPane.showConfirmDialog(
                this,
                String.format(
                		 "Se están pagando %.2f € en vez de %.2f €\n Quedarán pendiente de pago %.2f €.\n¿Desea continuar?",
                         cantidad, importeFactura,pendientePrevista
                ),
                "Confirmar pago parcial",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (opcion != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Operación cancelada. No se registró el pago.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        us.registrarPagoProfesor(idProfesorSeleccionado, idFactura, idActividadSeleccionada, fecha.toString(), cantidad);

        Map<String, Object> totales = us.obtenerTotalesFacturaProfesor(idFactura);
        double neto = ((Number) totales.getOrDefault("total_pagado", 0.0)).doubleValue()
                     - ((Number) totales.getOrDefault("total_devuelto", 0.0)).doubleValue();
        importeFactura = ((Number) totales.getOrDefault("importe_factura", 0.0)).doubleValue();

        double exceso = Math.max(0, neto - importeFactura);
        double pendiente = Math.max(0, importeFactura - neto);

        if (idFactura != -1 && neto >= importeFactura - 0.01) {
            us.marcarFacturaComoPagada(idFactura);
        }

        String mensaje;
        if (exceso > 0.01) {
            mensaje = String.format("Pago registrado. Se ha pagado %.2f € de más.", exceso);
        } else if (pendiente > 0.01) {
            mensaje = String.format("Pago parcial registrado. Pendiente: %.2f €.", pendiente);
        } else {
            mensaje = "Pago completo registrado correctamente.";
        }

        JOptionPane.showMessageDialog(this, mensaje, "Resultado del pago", JOptionPane.INFORMATION_MESSAGE);
        cargarTotalesProfesor();
    }


    private void procesarDevolucionProfesor(double cantidad, LocalDate fecha, int idFactura) {
        Map<String, Object> totales = us.obtenerTotalesFacturaProfesor(idFactura);
        double totalPagado = ((Number) totales.getOrDefault("total_pagado", 0.0)).doubleValue();
        double totalDevuelto = ((Number) totales.getOrDefault("total_devuelto", 0.0)).doubleValue();
        double importeFactura = ((Number) totales.getOrDefault("importe_factura", 0.0)).doubleValue();

        double netoActual = totalPagado - totalDevuelto;

        if (cantidad > netoActual + 0.01) {
            double exceso = cantidad - netoActual;

            int opcion = JOptionPane.showConfirmDialog(
                this,
                String.format(
                    "Devolución excesiva.\nSe intenta devolver %.2f €, pero solo se pueden devolver %.2f €.\n\n¿Desea continuar y registrar la devolución hasta el máximo permitido?",
                    cantidad, netoActual
                ),
                "Confirmar devolución excesiva",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (opcion != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Operación cancelada. No se registró la devolución.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (us.registrarDevolucionProfesor(idProfesorSeleccionado, idActividadSeleccionada, idFactura, fecha.toString(), netoActual)) {
                JOptionPane.showMessageDialog(this,
                    String.format(
                        "Se devolvieron %.2f € (máximo posible).\nEl exceso de %.2f € se registrará como compensación.",
                        netoActual, exceso
                    ),
                    "Devolución parcial registrada", JOptionPane.INFORMATION_MESSAGE);

                us.registrarPagoProfesor(idProfesorSeleccionado, idFactura, idActividadSeleccionada, fecha.toString(), exceso);
            }
            cargarTotalesProfesor();
            return;
        }

        double diferenciaAntes = netoActual - importeFactura;
        if (diferenciaAntes > 0.01) {
            int opcion = JOptionPane.showConfirmDialog(
                this,
                String.format("Se estan devolviendo %.2f €\n"
                		+ "Quedaban por devolver %.2f €\n" +
                		"¿Quieres registrar la devolución?", cantidad,diferenciaAntes),
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (opcion != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Operación cancelada. No se registró la devolución.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        if (!us.registrarDevolucionProfesor(idProfesorSeleccionado, idActividadSeleccionada, idFactura, fecha.toString(), cantidad)) {
            JOptionPane.showMessageDialog(this, "Error al registrar la devolución.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        totales = us.obtenerTotalesFacturaProfesor(idFactura);
        totalPagado = ((Number) totales.getOrDefault("total_pagado", 0.0)).doubleValue();
        totalDevuelto = ((Number) totales.getOrDefault("total_devuelto", 0.0)).doubleValue();
        importeFactura = ((Number) totales.getOrDefault("importe_factura", 0.0)).doubleValue();

        netoActual = totalPagado - totalDevuelto;
        double diferencia = netoActual - importeFactura;

        if (Math.abs(diferencia) < 0.01) {
            JOptionPane.showMessageDialog(this,
                "Devolución registrada correctamente.\nEl saldo con el profesor está equilibrado.",
                "Devolución completa", JOptionPane.INFORMATION_MESSAGE);
        } 
        else if (diferencia > 0.01) {
            JOptionPane.showMessageDialog(this,
                    String.format("Devolución parcial.\nQuedan %.2f € por devolver.", Math.abs(diferencia)),
                "Aviso: pago en exceso", JOptionPane.WARNING_MESSAGE);
        } 
        else {
            JOptionPane.showMessageDialog(this,
                String.format("Devolución registrada.\nSe debe efecturar pago compensatorio de %.2f €. ",  Math.abs(diferencia)),
                "Aviso: devolución incompleta", JOptionPane.WARNING_MESSAGE);
        }


        cargarTotalesProfesor();
    }


}
