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
    private JTextField tfCantidad, tfFecha;
    private JTextField tfNumeroFactura, tfDireccionEmisor, tfCantidadFactura, tfFechaFactura, tfNifEmisor;
    private JRadioButton rbPago, rbDevolucion;
    private JTable tableMovimientos;
    private DefaultTableModel modelMovimientos;
    private JButton btnRegistrarMovimiento;
    private JButton btnRegistrarFactura;


    private int idActividadSeleccionada = -1;
    private int idProfesorSeleccionado = -1;
    private int idFactura = -1;

    public VentanaPagoProfesores(UserService service) {
        this.us = new PagosController(service);
        setTitle("Registro de Pagos y Devoluciones a Profesores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1500, 800);
        setLocationRelativeTo(null);

        inicializar();
        cargarCursos();
    }

    private void inicializar() {

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);

        JLabel lblTitulo = new JLabel("Registrar Pagos y Devoluciones a Profesores", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPane.add(panelPrincipal, BorderLayout.CENTER);


        JPanel panelSuperior = new JPanel(new GridLayout(1, 2, 10, 10));
        initTablaCursos(panelSuperior);
        initTablaProfesores(panelSuperior);

        panelPrincipal.add(panelSuperior);


        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel panelMov = new JPanel(new BorderLayout());
        initTablaMovimientos(panelMov);
        panelCentral.add(panelMov);

        JPanel panelFactura = new JPanel(new GridLayout(3, 2, 10, 10));
        panelFactura.setBorder(BorderFactory.createTitledBorder("Datos de la Factura"));

        tfNumeroFactura = new JTextField(10);
        tfNifEmisor = new JTextField(10);

        JPanel pNumFactura = new JPanel(new BorderLayout(5, 0));
        pNumFactura.add(new JLabel("Número Factura:"), BorderLayout.NORTH);
        pNumFactura.add(tfNumeroFactura, BorderLayout.CENTER);

        JPanel pNifEmisor = new JPanel(new BorderLayout(5, 0));
        pNifEmisor.add(new JLabel("NIF Emisor:"), BorderLayout.NORTH);
        pNifEmisor.add(tfNifEmisor, BorderLayout.CENTER);

        panelFactura.add(pNumFactura);
        panelFactura.add(pNifEmisor);

        tfDireccionEmisor = new JTextField(10);
        tfCantidadFactura = new JTextField(10);

        JPanel pDireccion = new JPanel(new BorderLayout(5, 0));
        pDireccion.add(new JLabel("Dirección Emisor:"), BorderLayout.NORTH);
        pDireccion.add(tfDireccionEmisor, BorderLayout.CENTER);

        JPanel pCantidad = new JPanel(new BorderLayout(5, 0));
        pCantidad.add(new JLabel("Cantidad Factura (euros):"), BorderLayout.NORTH);
        pCantidad.add(tfCantidadFactura, BorderLayout.CENTER);

        panelFactura.add(pDireccion);
        panelFactura.add(pCantidad);

        tfFechaFactura = new JTextField(10);
        btnRegistrarFactura = new JButton("Registrar Factura");
        btnRegistrarFactura.addActionListener(e -> registrarFactura());

        JPanel pFecha = new JPanel(new BorderLayout(5, 0));
        pFecha.add(new JLabel("Fecha Factura:"), BorderLayout.NORTH);
        pFecha.add(tfFechaFactura, BorderLayout.CENTER);

        panelFactura.add(pFecha);
        panelFactura.add(btnRegistrarFactura);


        panelCentral.add(panelFactura);

        panelPrincipal.add(panelCentral);

        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Registrar movimiento"));
        initPanelMovimiento(panelInferior);

        panelPrincipal.add(panelInferior);

        contentPane.setPreferredSize(new Dimension(1200, 900));
        pack();
    }


    @SuppressWarnings("serial")
	private void initTablaCursos(JPanel panel) {
    	modelCursos = new DefaultTableModel(
    		    new Object[]{
    		        "ID Actividad",
    		        "Nombre",
    		        "Inicio Inscripción",
    		        "Fin Inscripción",
    		        "Fecha Inicio",
    		        "Fecha Fin",
    		        "Total Plazas",
    		        "Estado"
    		    }, 0
    		) {
    		    @Override
    		    public boolean isCellEditable(int row, int col) { return false; }
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
                    tableCursos.getColumnModel().getColumn(0).setMinWidth(0);
                    tableCursos.getColumnModel().getColumn(0).setMaxWidth(0);
                    tableCursos.getColumnModel().getColumn(0).setWidth(0);
                    cargarProfesoresDelCurso();
                    limpiarCamposFactura();
                }
            }
        });
    }

    @SuppressWarnings("serial")
	private void initTablaProfesores(JPanel panel) {
    	modelProfesores = new DefaultTableModel(
    		    new Object[]{"ID Profesor", "Nombre", "Apellidos", "Telefono", "Total pagado", "Pendiente"}, 
    		    0
    		) {
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
                    cargarMovimientos();
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
        tfCantidad = crearCampoConEtiqueta(panelForm, "Cantidad del movimiento (euros):", true);
        tfFecha = crearCampoConEtiqueta(panelForm, "Fecha (yyyy-MM-dd):", true);
        tfFecha.setText(us.getFechaHoy().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        panelInferior.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegistrarMovimiento = new JButton("Registrar Movimiento");
        JButton btnVolver = new JButton("Volver");
        panelBotones.add(btnVolver);
        panelBotones.add(btnRegistrarMovimiento);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        btnVolver.addActionListener(e -> dispose());
        btnRegistrarMovimiento.addActionListener(e -> registrarMovimiento());

        rbPago.addActionListener(e -> cargarTotalesProfesor());
        rbDevolucion.addActionListener(e -> cargarTotalesProfesor());
    }

    private JTextField crearCampo(JPanel panel, String label) {
        panel.add(new JLabel(label));
        JTextField tf = new JTextField();
        tf.setEditable(true);
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
            modelCursos.addRow(new Object[]{"-", "No hay cursos disponibles", "-", "-", "-", "-", "-", "-"});
            tableCursos.setEnabled(false);
            return;
        }

        LocalDate hoy = us.getFechaHoy();

        for (Map<String, Object> curso : cursos) {
            int id = ((Number) curso.get("id_actividad")).intValue();
            String nombre = (String) curso.get("nombre");

            LocalDate inicioInscripcion = curso.get("inicio_inscripcion") != null
                    ? LocalDate.parse(curso.get("inicio_inscripcion").toString())
                    : null;
            LocalDate finInscripcion = curso.get("fin_inscripcion") != null
                    ? LocalDate.parse(curso.get("fin_inscripcion").toString())
                    : null;
            LocalDate fechaInicio = curso.get("fecha_inicio") != null
                    ? LocalDate.parse(curso.get("fecha_inicio").toString())
                    : null;
            LocalDate fechaFin = curso.get("fecha_fin") != null
                    ? LocalDate.parse(curso.get("fecha_fin").toString())
                    : null;
            int totalPlazas = curso.get("total_plazas") != null ? ((Number) curso.get("total_plazas")).intValue() : 0;
            boolean isClosed = curso.get("isClosed") != null && ((Number) curso.get("isClosed")).intValue() == 1;
            boolean isCancelada = curso.get("isCancelada") != null && ((Number) curso.get("isCancelada")).intValue() == 1;

            String estado;
            if (isClosed) {
                estado = "Cerrada";
            } else if (isCancelada) {
                estado = "Cancelada";
            } else if (inicioInscripcion != null && finInscripcion != null &&
                       !hoy.isBefore(inicioInscripcion) && !hoy.isAfter(finInscripcion)) {
                estado = "Periodo de inscripción";
            } else if (fechaInicio != null && fechaFin != null &&
                       !hoy.isBefore(fechaInicio) && !hoy.isAfter(fechaFin)) {
                estado = "En curso";
            } else if (finInscripcion != null && fechaInicio != null &&
                       hoy.isAfter(finInscripcion) && hoy.isBefore(fechaInicio)) {
                estado = "Por empezar";
            } else if (fechaFin != null && hoy.isAfter(fechaFin)) {
                estado = "Cursada";
            } else {
                estado = "Sin actividad";
            }

            modelCursos.addRow(new Object[]{
                id,
                nombre,
                inicioInscripcion != null ? inicioInscripcion.toString() : "-",
                finInscripcion != null ? finInscripcion.toString() : "-",
                fechaInicio != null ? fechaInicio.toString() : "-",
                fechaFin != null ? fechaFin.toString() : "-",
                totalPlazas,
                estado
            });

            tableCursos.getColumnModel().getColumn(0).setMinWidth(0);
            tableCursos.getColumnModel().getColumn(0).setMaxWidth(0);
            tableCursos.getColumnModel().getColumn(0).setWidth(0);
        }

        tableCursos.setEnabled(true);
    }

    private void cargarProfesoresDelCurso() {
        modelProfesores.setRowCount(0);
        idProfesorSeleccionado = -1;
        if (idActividadSeleccionada == -1) return;

        List<Map<String, Object>> profesores = us.obtenerProfesoresPorActividad(idActividadSeleccionada);
        if (profesores == null || profesores.isEmpty()) return;

        int rowIndex = 0;

        for (Map<String, Object> prof : profesores) {
            int id = ((Number) prof.get("id_profesor")).intValue();
            String nombre = (String) prof.get("profesor_nombre");
            String apellido = (String) prof.get("profesor_apellido");
            String telefono = (String) prof.get("profesor_telefono");

            modelProfesores.addRow(new Object[]{id, nombre, apellido, telefono, "0.00", "0.00"});

            calcularTotalesFila(id, idActividadSeleccionada, rowIndex);

            rowIndex++;
        }


        tableProfesores.getColumnModel().getColumn(0).setMinWidth(0);
        tableProfesores.getColumnModel().getColumn(0).setMaxWidth(0);
        tableProfesores.getColumnModel().getColumn(0).setWidth(0);
    }


    private void calcularTotalesFila(int idProfesor, int actividad, int rowIndex) {
        Map<String, Object> factura = us.obtenerDatosFacturaPorProfesorYActividad(idProfesor, actividad);
        if (factura == null || factura.get("id_factura") == null) {
            modelProfesores.setValueAt("0.00", rowIndex, 4);
            modelProfesores.setValueAt("0.00", rowIndex, 5);
            return;
        }

        int idFacturaTemp = ((Number) factura.get("id_factura")).intValue();

        Map<String, Object> totales = us.obtenerTotalesFacturaProfesor(idFacturaTemp);
        if (totales == null) return;

        double totalPagado   = ((Number) totales.getOrDefault("total_pagado", 0.0)).doubleValue();
        double totalDevuelto = ((Number) totales.getOrDefault("total_devuelto", 0.0)).doubleValue();
        double importe       = ((Number) totales.getOrDefault("importe_factura", 0.0)).doubleValue();

        double neto = totalPagado - totalDevuelto;
        double pendiente = Math.max(0, importe - neto);

        modelProfesores.setValueAt(String.format("%.2f", neto), rowIndex, 4);
        modelProfesores.setValueAt(String.format("%.2f", pendiente), rowIndex, 5);
    }

    
    private void cargarDatosFactura() {
        limpiarCamposFactura();

        if (idProfesorSeleccionado == -1 || idActividadSeleccionada == -1) return;

        Map<String, Object> factura = us.obtenerDatosFacturaPorProfesorYActividad(idProfesorSeleccionado, idActividadSeleccionada);

        if (factura != null && !factura.get("numero_factura").equals("-1")) {
        	
        	btnRegistrarFactura.setEnabled(false);
            btnRegistrarMovimiento.setEnabled(true);
        	
            idFactura = ((Number) factura.get("id_factura")).intValue();
            tfNumeroFactura.setText(String.valueOf(factura.get("numero_factura")));
            tfNifEmisor.setText(String.valueOf(factura.get("emisor_nif")));
            tfDireccionEmisor.setText(String.valueOf(factura.get("direccion_emisor")));
            tfCantidadFactura.setText(String.format("%.2f", ((Number) factura.get("cantidad")).doubleValue()));
            tfFechaFactura.setText(String.valueOf(factura.get("fecha")));

            tfNumeroFactura.setEditable(false);
            tfNifEmisor.setEditable(false);
            tfDireccionEmisor.setEditable(false);
            tfCantidadFactura.setEditable(false);
            tfFechaFactura.setEditable(false);

        } else {
            idFactura = -1;

            Map<String, Object> profesor = us.cargarDatosProfesor(idProfesorSeleccionado);
            if (profesor != null && !profesor.isEmpty()) {
                tfDireccionEmisor.setText(String.valueOf(profesor.get("direccion")));
                tfNifEmisor.setText(String.valueOf(profesor.get("nif")));
            }
            
            btnRegistrarFactura.setEnabled(true);
            btnRegistrarMovimiento.setEnabled(false);

            tfNumeroFactura.setText("");
            tfCantidadFactura.setText("");
            tfFechaFactura.setText(String.valueOf(us.getFechaHoy()));

            tfNumeroFactura.setEditable(true);
            tfNifEmisor.setEditable(true);
            tfDireccionEmisor.setEditable(true);
            tfCantidadFactura.setEditable(true);
            tfFechaFactura.setEditable(true);
        }
    }


    
    @SuppressWarnings("serial")
    private void initTablaMovimientos(JPanel panel) {
        modelMovimientos = new DefaultTableModel(
            new Object[]{"Fecha", "Cantidad", "Tipo"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tableMovimientos = new JTable(modelMovimientos);
        tableMovimientos.setRowHeight(25);
        JScrollPane scrollMovimientos = new JScrollPane(tableMovimientos);
        scrollMovimientos.setBorder(BorderFactory.createTitledBorder("Historial de pagos y devoluciones de la factura"));
        panel.add(scrollMovimientos);
    }

    private void cargarMovimientos() {
        modelMovimientos.setRowCount(0);
        if (idFactura == -1 || idProfesorSeleccionado == -1) return;

        List<Map<String, Object>> movimientos = us.listarMovimientosPorFactura(idFactura, idProfesorSeleccionado);
        if (movimientos == null || movimientos.isEmpty()) return;

        for (Map<String, Object> mov : movimientos) {
            LocalDate fecha = LocalDate.parse(mov.get("fecha").toString());
            double cantidad = ((Number) mov.get("cantidad")).doubleValue();
            String tipo = (String) mov.get("tipo");

            modelMovimientos.addRow(new Object[]{
                fecha.toString(),
                String.format("%.2f", cantidad),
                tipo
            });
        }
    }


    private void cargarTotalesProfesor() {
        if (idProfesorSeleccionado == -1 || idActividadSeleccionada == -1 || idFactura == -1)
            return;

        Map<String, Object> totales = us.obtenerTotalesFacturaProfesor(idFactura);
        if (totales == null || totales.isEmpty()) return;

        double totalPagado = ((Number) totales.getOrDefault("total_pagado", 0.0)).doubleValue();
        double totalDevuelto = ((Number) totales.getOrDefault("total_devuelto", 0.0)).doubleValue();
        double importeFactura = ((Number) totales.getOrDefault("importe_factura", 0.0)).doubleValue();

        double neto = totalPagado - totalDevuelto;

        double totalMostrar;
        if (rbPago.isSelected()) {
            totalMostrar = totalPagado;    
        } else {
            totalMostrar = totalDevuelto;   
        }

        double pendiente;
        if (rbPago.isSelected()) {
            pendiente = Math.max(0, importeFactura - neto);
        } else {
            pendiente = Math.max(0, neto - importeFactura);
        }

        int row = tableProfesores.getSelectedRow();
        if (row >= 0) {
            modelProfesores.setValueAt(String.format("%.2f", totalMostrar), row, 4); 
            modelProfesores.setValueAt(String.format("%.2f", pendiente), row, 5);   
        }
    }



    private void registrarFactura() {
        if (idProfesorSeleccionado == -1 || idActividadSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un curso y un profesor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String numeroFactura = tfNumeroFactura.getText().trim();
        String nifEmisor = tfNifEmisor.getText().trim();
        String direccionEmisor = tfDireccionEmisor.getText().trim();
        String cantidadStr = tfCantidadFactura.getText().trim();
        String fechaStr = tfFechaFactura.getText().trim();

        if (numeroFactura.isEmpty() || nifEmisor.isEmpty() || direccionEmisor.isEmpty()
                || cantidadStr.isEmpty() || fechaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos de la factura deben estar completos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadStr.replace(",", "."));
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida mayor que 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            us.registrarFactura(
                    idProfesorSeleccionado,
                    idActividadSeleccionada,
                    numeroFactura,
                    fecha,
                    cantidad,
                    nifEmisor,
                    direccionEmisor
            );

            JOptionPane.showMessageDialog(this, "Factura registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            cargarMovimientos();
            cargarTotalesProfesor();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar la factura: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        cargarDatosFactura();
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
        cargarMovimientos();

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
                    "Pago adicional: %.2f €\n" +
                    "Neto pagado antes: %.2f €\n" +
                    "Importe de la factura: %.2f €\n" +
                    "Exceso total tras este pago: %.2f €.\n\n" +
                    "¿Desea continuar?",
                    cantidad,
                    netoAntes,
                    importeFactura,
                    excesoPrevisto
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
                    "Devolución excesiva.\nCantidad devolución: %.2f €\nCantidad pendiente %.2f €\nSe generará un excesp de  %.2f €"
                    + "\n¿Desea continuar y registrar la devolución hasta el máximo permitido?",
                    cantidad, netoActual, Math.abs(cantidad - netoActual)
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
                        "Se devolvieron %.2f € (máximo posible).\nHay un exceso de %.2f €\nSe deben efectuar registrará como compensación.",
                        cantidad, exceso
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
