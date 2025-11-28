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
import javax.swing.table.TableColumn;

import proyecto.service.EmailInscritosController;
import proyecto.service.PagosController;
import proyecto.service.UserService;

public class VentanaPagosAlumnos extends JFrame {

    private static final long serialVersionUID = 1L;
    private PagosController us;

    private JTable tableActividades;
    private JTable tableInscripciones;
    private JTable tableMovimientos;
    private DefaultTableModel modelActividades;
    private DefaultTableModel modelInscripciones;
    private DefaultTableModel modelMovimientos;
    private JTextField tfCantidad;
    private JTextField tfFecha;
    private JRadioButton rbPago;
    private JRadioButton rbDevolucion;
    private JRadioButton rbTransferencia;
    private JRadioButton rbEfectivo;
    private Map<Integer, Map<String, Object>> actividadData = new HashMap<>();
    private Map<Integer, Map<String, Object>> inscripcionData = new HashMap<>();
    private int idActividadSeleccionada = -1;
    private int idMatriculaSeleccionada = -1;
    private EmailInscritosController ec;
    
    public VentanaPagosAlumnos(UserService service) {
        this.us = new PagosController(service);
        
        ec = new EmailInscritosController();
        
        setTitle("Registro de Pagos de Inscripciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1500, 800);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);

        contentPane.add(crearTitulo(), BorderLayout.NORTH);
        contentPane.add(crearPanelCentral(), BorderLayout.CENTER);
        contentPane.add(crearPanelInferior(), BorderLayout.SOUTH);

        agregarListeners();

        cargarActividadesCompletas();

    }

    private JLabel crearTitulo() {
        JLabel lblTitulo = new JLabel("Registrar Pagos y Devoluciones de Alumnos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return lblTitulo;
    }

    private JPanel crearPanelCentral() {
        JPanel panelCentral = new JPanel(new GridLayout(3, 1, 10, 10));

        panelCentral.add(crearTablaActividades());
        panelCentral.add(crearTablaInscripciones());
        panelCentral.add(crearTablaMovimientos());

        return panelCentral;
    }

    @SuppressWarnings("serial")
	private JScrollPane crearTablaActividades() {
    	modelActividades = new DefaultTableModel(
    		    new Object[]{
    		        "ID", "Nombre", "Inicio Inscripción", "Fin Inscripción",
    		        "Inicio Curso", "Fin Curso", "Total Plazas","Plazas disp.", "Estado"
    		    }, 0
    		) {
    		    @Override public boolean isCellEditable(int r, int c) { return false; }
    		};


        tableActividades = new JTable(modelActividades);
        tableActividades.setRowHeight(25);
        tableActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumn idCol = tableActividades.getColumnModel().getColumn(0);
        idCol.setMinWidth(0);
        idCol.setMaxWidth(0);
        idCol.setWidth(0);
        idCol.setPreferredWidth(0);

        JScrollPane scroll = new JScrollPane(tableActividades);
        scroll.setBorder(BorderFactory.createTitledBorder("Cursos"));
        return scroll;
    }


    @SuppressWarnings("serial")
	private JScrollPane crearTablaInscripciones() {
    	modelInscripciones = new DefaultTableModel(
    		    new Object[]{
    		        "ID Matricula", "Nombre", "Apellido", "Telefono", 
    		        "Fecha inscripcion", "Ultimo dia pago", "Estado",
    		        "Total pagado (€)", "Pendiente (€)"
    		    }, 0
    		) {
    		    @Override public boolean isCellEditable(int r, int c) { return false; }
    		};

        tableInscripciones = new JTable(modelInscripciones);
        tableInscripciones.setRowHeight(25);
        tableInscripciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumn idCol = tableInscripciones.getColumnModel().getColumn(0);
        idCol.setMinWidth(0);
        idCol.setMaxWidth(0);
        idCol.setWidth(0);
        idCol.setPreferredWidth(0);

        JScrollPane scroll = new JScrollPane(tableInscripciones);
        scroll.setBorder(BorderFactory.createTitledBorder("Alumnos"));
        return scroll;
    }

    @SuppressWarnings("serial")
	private JScrollPane crearTablaMovimientos() {
        modelMovimientos = new DefaultTableModel(
            new Object[]{"Fecha", "Tipo", "Metodo", "Cantidad (€)"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableMovimientos = new JTable(modelMovimientos);
        tableMovimientos.setRowHeight(25);
        tableMovimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tableMovimientos);
        scroll.setBorder(BorderFactory.createTitledBorder("Pagos y Devoluciones"));
        return scroll;
    }

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Registrar movimiento"));

        panelInferior.add(crearPanelTipoOperacion(), BorderLayout.NORTH);
        panelInferior.add(crearPanelForm(), BorderLayout.CENTER);
        panelInferior.add(crearPanelBotones(), BorderLayout.SOUTH);

        return panelInferior;
    }

    private JPanel crearPanelTipoOperacion() {
        JPanel panelTipoOperacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        rbPago = new JRadioButton("Pago del alumno", true);
        rbPago.setFont(new Font("Tahoma", Font.BOLD, 14));
        rbDevolucion = new JRadioButton("Devolucion al alumno");
        rbDevolucion.setFont(new Font("Tahoma", Font.BOLD, 14));
        ButtonGroup grupoTipo = new ButtonGroup();
        grupoTipo.add(rbPago);
        grupoTipo.add(rbDevolucion);
        panelTipoOperacion.add(rbPago);
        panelTipoOperacion.add(rbDevolucion);

        rbTransferencia = new JRadioButton("Transferencia", true);
        rbTransferencia.setFont(new Font("Tahoma", Font.BOLD, 14));
        rbEfectivo = new JRadioButton("Efectivo");
        rbEfectivo.setFont(new Font("Tahoma", Font.BOLD, 14));
        ButtonGroup grupoMetodo = new ButtonGroup();
        grupoMetodo.add(rbTransferencia);
        grupoMetodo.add(rbEfectivo);
        panelTipoOperacion.add(rbTransferencia);
        panelTipoOperacion.add(rbEfectivo);

        return panelTipoOperacion;
    }

    private JPanel crearPanelForm() {
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 30, 15));

        panelForm.add(crearPanelCantidad());
        panelForm.add(crearPanelFecha());

        return panelForm;
    }

    private JPanel crearPanelCantidad() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Cantidad del movimiento (eur):"), BorderLayout.NORTH);
        tfCantidad = new JTextField();
        panel.add(tfCantidad, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelFecha() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Fecha del movimiento (yyyy-MM-dd):"), BorderLayout.NORTH);
        tfFecha = new JTextField();
        LocalDate fechaHoy = us.getFechaHoy();
        tfFecha.setText(fechaHoy.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        panel.add(tfFecha, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        JButton btnRegistrar = new JButton("Registrar movimiento");
        btnRegistrar.setBackground(Color.WHITE);
        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(Color.WHITE);
        panel.add(btnVolver);
        panel.add(btnRegistrar);

        btnRegistrar.addActionListener(e -> registrarPago());
        btnVolver.addActionListener(e -> dispose());

        return panel;
    }

    private void agregarListeners() {
        ActionListener actualizarVistaPago = e -> {
            if (idMatriculaSeleccionada > 0) {
                actualizarCamposVisuales(rbPago.isSelected());
            }
        };

        rbPago.addActionListener(actualizarVistaPago);
        rbDevolucion.addActionListener(actualizarVistaPago);

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
                    cargarMovimientos(idMatriculaSeleccionada);
                }
            }
        });
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
            
            double totalPagado = us.getEstadoPagoAlumno(idMatricula).getOrDefault("total_pagado", 0.0);
            double pendiente = us.getEstadoPagoAlumno(idMatricula).getOrDefault("pendiente", 0.0);

            modelInscripciones.addRow(new Object[]{
                idMatricula, nombre, apellido, telefono, fechaMatricula, fechaLimite, estado,
                String.format("%.2f", totalPagado), String.format("%.2f", pendiente)
            });


            inscripcionData.put(idMatricula, ins);

        }
    }

    private void cargarMovimientos(int idMatricula) {
        modelMovimientos.setRowCount(0);
        List<Map<String, Object>> movimientos = us.listarMovimientosAlumno(idMatricula);
        if (movimientos == null) return;

        for (Map<String, Object> mov : movimientos) {
            String fecha = mov.get("fecha") != null ? mov.get("fecha").toString() : "-";
            String tipo = mov.get("tipo") != null ? mov.get("tipo").toString() : "-";

            String metodo;
 
            metodo = mov.get("metodo") != null ? mov.get("metodo").toString() : "-";
            

            double cantidad = mov.get("cantidad") != null ? ((Number)mov.get("cantidad")).doubleValue() : 0.0;

            modelMovimientos.addRow(new Object[]{fecha, tipo, metodo, cantidad});
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
            mostrarError("No se pudo obtener la informacion de pago del alumno.");
            return;
        }
        if (rbPago.isSelected()) {
            procesarPago(cantidad, fechaMovimiento, estado);
        } else {
            procesarDevolucion(cantidad, fechaMovimiento, estado);
        }

        limpiarCampos();
        actualizarCamposVisuales(rbPago.isSelected());
        cargarMovimientos(idMatriculaSeleccionada);


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
            mostrarError("Ingrese una cantidad valida y mayor que 0.");
            return null;
        }
    }
    private LocalDate validarFecha() {
        LocalDate fechaMovimiento;
        try {
            fechaMovimiento = LocalDate.parse(tfFecha.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            mostrarError("Formato de fecha invalido. Use el formato yyyy-MM-dd (por ejemplo, 2025-11-09).");
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
                "La fecha del movimiento no puede ser anterior a la fecha de matricula.\n\n" +
                "Fecha matrícula: %s\n" +
                "Fecha introducida: %s",
                fechaMatricula, fechaMovimiento
            ));
            return null;
        }

        return fechaMovimiento;
    }
    
    private void cargarActividadesCompletas() {
        modelActividades.setRowCount(0);
        actividadData.clear();

        List<Map<String, Object>> actividades = us.listarTodosLosCursos(); 
        if (actividades == null || actividades.isEmpty()) {
            modelActividades.addRow(new Object[]{"-", "No hay actividades disponibles", "-", "-", "-", "-", "-", "-", "-" });
            tableActividades.setEnabled(false);
            return;
        }

        LocalDate hoy = us.getFechaHoy();

        for (Map<String, Object> act : actividades) {
            int id = ((Number) act.get("id_actividad")).intValue();
            String nombre = (String) act.get("nombre");

            LocalDate inicioInscripcion = act.get("inicio_inscripcion") != null
                    ? LocalDate.parse(act.get("inicio_inscripcion").toString())
                    : null;
            LocalDate finInscripcion = act.get("fin_inscripcion") != null
                    ? LocalDate.parse(act.get("fin_inscripcion").toString())
                    : null;
            LocalDate fechaInicio = act.get("fecha_inicio") != null
                    ? LocalDate.parse(act.get("fecha_inicio").toString())
                    : null;
            LocalDate fechaFin = act.get("fecha_fin") != null
                    ? LocalDate.parse(act.get("fecha_fin").toString())
                    : null;

            int totalPlazas = act.get("total_plazas") != null ? ((Number) act.get("total_plazas")).intValue() : 0;

            Map<String, Object> detalles = us.getActividadDetalles(id);
            
            int plazasDisponibles = 0;

            if (detalles != null && detalles.get("plazas_disponibles") != null) {
                plazasDisponibles = ((Number) detalles.get("plazas_disponibles")).intValue();
            } else {
                plazasDisponibles = totalPlazas;
            }



            boolean isClosed = act.get("isClosed") != null && ((Number) act.get("isClosed")).intValue() == 1;
            boolean isCancelada = act.get("isCancelada") != null && ((Number) act.get("isCancelada")).intValue() == 1;

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

            modelActividades.addRow(new Object[]{
                id,
                nombre,
                inicioInscripcion != null ? inicioInscripcion.toString() : "-",
                finInscripcion != null ? finInscripcion.toString() : "-",
                fechaInicio != null ? fechaInicio.toString() : "-",
                fechaFin != null ? fechaFin.toString() : "-",
                totalPlazas,
                plazasDisponibles,
                estado
            });

            actividadData.put(id, act);
        }

        tableActividades.setEnabled(modelActividades.getRowCount() > 0);
    }



    private void procesarPago(double cantidad, LocalDate fechaMovimiento, Map<String, Double> estado) {

        boolean porEfectivo = this.rbEfectivo.isSelected();
        double limiteEfectivo = us.getLimiteEfectivo(); 
        if (porEfectivo && cantidad > limiteEfectivo) {
            mostrarError(String.format(
                "No se puede pagar mas de %.2f euros en efectivo por este movimiento.",
                limiteEfectivo
            ));
            return; 
        }
        
        String avisoPlazo = verificarPlazoPago(fechaMovimiento, idMatriculaSeleccionada) + "\n";

        double totalPagado = estado.getOrDefault("total_pagado", 0.0);
        double totalDevuelto = estado.getOrDefault("total_devuelto", 0.0);
        double montoTotalMatricula = us.getMontoTotalMatricula(idMatriculaSeleccionada);

        double netoActual = totalPagado - totalDevuelto;
        double pendienteAntes = Math.max(0, montoTotalMatricula - netoActual);
        double excesoAntes = Math.max(0, (netoActual + cantidad) - montoTotalMatricula);
        double restanteDespues = Math.max(0, pendienteAntes - cantidad);

        String nombreAlumno = us.getNombreAlumno(idMatriculaSeleccionada);
        String nombreActividad = us.getNombreActividad(idMatriculaSeleccionada);
        LocalDate fechaLimite = us.getFechaLimitePago(idMatriculaSeleccionada);
        LocalDate fechaInicio = us.getFechaInicioActividad(idMatriculaSeleccionada);

        String mensaje;
        boolean esPagoCompleto = false;
        boolean esPagoParcial = false;

        if (Math.abs(cantidad - pendienteAntes) <= 0.01) {
            mensaje = String.format(
                "Confirmar pago\n" +
                avisoPlazo +
                "Cantidad a pagar: %.2f €\n" +
                "Cantidad pendiente antes del pago: %.2f euros\n" +
                "Con este pago, la matricula quedará pagada\n\n" +
                "Desea continuar?\n",
                cantidad, pendienteAntes
            );
            esPagoCompleto = true;

        } else if (cantidad < pendienteAntes) {
            mensaje = String.format(
                avisoPlazo +
                "El pago ingresado es menor que la cantidad pendiente.\n\n" +
                "Cantidad a pagar: %.2f euros\n" +
                "Cantidad pendiente antes del pago: %.2f euros\n" +
                "Cantidad que quedara pendiente despues del pago: %.2f euros\n\n" +
                "¿Desea continuar y registrar este pago parcial?",
                cantidad, pendienteAntes, restanteDespues
            );
            esPagoParcial = true;

        } else {
            mensaje = String.format(
                avisoPlazo +
                "El pago ingresado es mayor que la cantidad pendiente.\n\n" +
                "Cantidad a pagar: %.2f euros\n" +
                "Cantidad pendiente antes del pago: %.2f euros\n" +
                "La matricula quedará pagada pero se generará\n" +
                "un exceso que quedara registrado: %.2f euros\n\n" +
                "Desea continuar y registrar este pago?",
                cantidad, pendienteAntes, excesoAntes
            );
            esPagoCompleto = true;
        }

        int opcion = JOptionPane.showConfirmDialog(
            this, mensaje, "Confirmar pago", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );

        if (opcion != JOptionPane.YES_OPTION) {
            mostrarAviso("Operacion cancelada. El pago no se ha registrado.");
            return;
        }

        boolean ok = us.registrarPago(idMatriculaSeleccionada, cantidad, fechaMovimiento, porEfectivo);
        if (!ok) {
            mostrarError("Error al registrar el pago. No se guardaron los cambios.");
            return;
        }

        mostrarInfo(String.format("Pago registrado correctamente.\n\nSe pagaron %.2f euros.", cantidad));


        Map<String, Double> nuevoEstado = us.getEstadoPagoAlumno(idMatriculaSeleccionada);
        if (nuevoEstado == null) return;
        
        if (esPagoCompleto) ec.generarEmailMatriculaCompleta(
        		nombreAlumno, 
        		nombreActividad, 
        		cantidad, 
        		fechaMovimiento, 
        		fechaInicio, 
        		porEfectivo, 
        		totalPagado, 
        		totalDevuelto, 
        		montoTotalMatricula);
        else if (esPagoParcial) ec.generarEmailPagoPendiente(nombreAlumno, 
        		nombreActividad, 
        		cantidad, 
        		fechaMovimiento, 
        		fechaLimite, 
        		porEfectivo, 
        		totalPagado, 
        		totalDevuelto, 
        		montoTotalMatricula);

        actualizarCamposVisuales(true);
        cargarInscripcionesPendientes();
        cargarActividadesCompletas();
    }


    private void procesarDevolucion(double cantidad, LocalDate fechaMovimiento, Map<String, Double> estado) {

        boolean porEfectivo = this.rbEfectivo.isSelected();
        double limiteEfectivo = us.getLimiteEfectivo();
        if (porEfectivo && cantidad > limiteEfectivo) {
            mostrarError(String.format(
                    "No se puede devolver más de %.2f euros en efectivo por este movimiento.",
                    limiteEfectivo
            ));
            return;
        }

        double totalPagado = estado.getOrDefault("total_pagado", 0.0);
        double totalDevuelto = estado.getOrDefault("total_devuelto", 0.0);
        double cuota = estado.getOrDefault("cuota", 0.0);
        boolean isCancelada = estado.getOrDefault("is_cancelada", 0.0) == 1.0;

        String nombreAlumno = us.getNombreAlumno(idMatriculaSeleccionada);
        String nombreActividad = us.getNombreActividad(idMatriculaSeleccionada);

        double pendienteAntes = isCancelada
                ? Math.max(0.0, totalPagado - totalDevuelto)
                : Math.max(0.0, totalPagado - totalDevuelto - cuota);

        double pendienteDespues = pendienteAntes - cantidad;

        boolean esPagoCompleto = false;
        boolean esPagoParcial = false;

        String mensajeConfirmacion;

        if (pendienteAntes <= 0.01) {
            mostrarError("No hay importe pendiente por devolver.");
            return;
        }

        if (Math.abs(pendienteDespues) < 0.01) {
            mensajeConfirmacion = String.format(
                    "Confirmar devolución completa\n\n" +
                    "Cantidad a devolver: %.2f €\n" +
                    "Cantidad pendiente antes: %.2f €\n\n" +
                    "¿Desea continuar?",
                    cantidad, pendienteAntes
            );
            esPagoCompleto = true;

        } else if (pendienteDespues > 0.01) {
            mensajeConfirmacion = String.format(
                    "Confirmar devolución parcial\n\n" +
                    "Cantidad a devolver: %.2f €\n" +
                    "Pendiente: %.2f €\n" +
                    "Cantidad que quedará pendiente: %.2f €\n\n" +
                    "¿Desea continuar?",
                    cantidad, pendienteAntes, pendienteDespues
            );
            esPagoParcial = true;

        } else { 
            mensajeConfirmacion = String.format(
                    "Confirmar devolución parcial\n\n" +
                    "Cantidad a devolver: %.2f €\n" +
                    "Pendiente antes: %.2f €\n" +
                    "Pendiente después: %.2f €\n\n" +
                    "¿Desea continuar?",
                    cantidad, pendienteAntes, Math.abs(pendienteDespues)
            );
            esPagoCompleto = true;
        }
        int opcion = JOptionPane.showConfirmDialog(
                this, mensajeConfirmacion, "Confirmar devolución",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );

        if (opcion != JOptionPane.YES_OPTION) {
            mostrarAviso("Operación cancelada. La devolución no se ha registrado.");
            return;
        }

        boolean ok = registrarDevolucion(cantidad, fechaMovimiento, porEfectivo);
        if (!ok) return;

        actualizarCamposVisuales(false);
        cargarActividadesCompletas();

        if (esPagoCompleto) {
            ec.generarEmailDevolucionCompleta(
                    nombreAlumno,
                    nombreActividad,
                    cantidad,
                    fechaMovimiento,
                    porEfectivo,
                    Math.abs(pendienteDespues)
            );
        } else if (esPagoParcial) {
            ec.generarEmailDevolucionPendiente(
                    nombreAlumno,
                    nombreActividad,
                    cantidad,
                    fechaMovimiento,
                    porEfectivo,
                    totalPagado,
                    totalDevuelto,
                    cuota
            );
        }

        String metodo = porEfectivo ? "efectivo" : "transferencia";

        mostrarInfo(String.format(
                "Devolución registrada correctamente (%s).\n\nSe devolvieron %.2f euros.",
                metodo, cantidad
        ));
    }




	    private boolean registrarDevolucion(double cantidad, LocalDate fechaMovimiento, boolean porEfectivo) {
	        boolean ok = us.registrarDevolucion(idMatriculaSeleccionada, cantidad, fechaMovimiento, porEfectivo);
	        if (!ok) {
	            mostrarError("Error al registrar la devolución. No se guardaron los cambios.");
	            return false;
	        }
	        return true;
	    }

	    private void actualizarCamposVisuales(boolean esPago) {
	        if (idMatriculaSeleccionada <= 0) return;

	        Map<String, Double> estadoPago = us.getEstadoPagoAlumno(idMatriculaSeleccionada);
	        if (estadoPago == null) return;

	        double totalPagado = estadoPago.getOrDefault("total_pagado", 0.0);
	        double totalDevuelto = estadoPago.getOrDefault("total_devuelto", 0.0);
	        double pendiente = estadoPago.getOrDefault("pendiente", 0.0);
	        double aDevolver = estadoPago.getOrDefault("a_devolver", 0.0);

	        for (int i = 0; i < modelInscripciones.getRowCount(); i++) {
	            int idFila = ((Number) modelInscripciones.getValueAt(i, 0)).intValue();
	            if (idFila == idMatriculaSeleccionada) {
	                if (esPago) {
	                    modelInscripciones.setValueAt(String.format("%.2f", totalPagado), i, 7); 
	                    modelInscripciones.setValueAt(String.format("%.2f", pendiente), i, 8);   
	                } else {
	                    modelInscripciones.setValueAt(String.format("%.2f", totalDevuelto), i, 7); 
	                    modelInscripciones.setValueAt(String.format("%.2f", aDevolver), i, 8);     
	                }
	                break;
	            }
	        }
	        
	        tfFecha.setText(us.getFechaHoy().toString());
	        
	    }

       
    
    public String verificarPlazoPago(LocalDate fechaMovimiento, int idMatricula) {
        LocalDate fechaMatricula = us.getFechaMatricula(idMatricula);
        if (fechaMatricula == null) {
            return "No se ha podido obtener la fecha de matricula del alumno.";
        }

        LocalDate fechaLimite = fechaMatricula.plusDays(2);

        if (fechaMovimiento.isBefore(fechaMatricula) || fechaMovimiento.isAfter(fechaLimite)) {
            return String.format(
                "La fecha introducida (%s) esta fuera del plazo permitido.\n\n" +
                "El plazo valido de pago es desde %s hasta %s (ambos inclusive).",
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
        JOptionPane.showMessageDialog(this, msg, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

}

