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

    // === Componentes principales ===
    private JTable tableActividades;
    private JTable tableInscripciones;
    private DefaultTableModel modelActividades;
    private DefaultTableModel modelInscripciones;
    private JTextField tfCantidad;
    private JTextField tfFecha;
    private JRadioButton rbPago;
    private JRadioButton rbDevolucion;
    private JLabel lblPendiente;
    private JTextField tfPendiente;
    private JTextField tfTotalPagado;
    // === Datos ===
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

        // === PANEL CENTRAL con dos tablas: Actividades arriba e Inscripciones abajo ===
        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));

        // --- Tabla de Actividades ---
        modelActividades = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Cuota (€)", "Plazas disp."}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableActividades = new JTable(modelActividades);
        tableActividades.setRowHeight(25);
        tableActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollActividades = new JScrollPane(tableActividades);
        scrollActividades.setBorder(BorderFactory.createTitledBorder("Cursos con pagos pendientes"));
        panelCentral.add(scrollActividades);

        // --- Tabla de Inscripciones ---
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

     // === PANEL INFERIOR: REGISTRAR MOVIMIENTO ===
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Registrar movimiento"));

        // 🔹 Panel superior: tipo de operación centrado
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
        
        ActionListener actualizarVistaPago = e -> {
            if (idMatriculaSeleccionada > 0) {
                actualizarCamposVisuales(rbPago.isSelected());
            }
        };


        // Asignamos el mismo listener a ambos botones
        rbPago.addActionListener(actualizarVistaPago);
        rbDevolucion.addActionListener(actualizarVistaPago);

        // 🔹 Panel central: formulario de 4 campos (2 filas x 2 columnas)
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 30, 15)); // espaciamiento entre columnas y filas

        // --- Fila 1: total pagado / cantidad pendiente ---
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

        // --- Fila 2: cantidad a pagar / fecha del movimiento ---
        JPanel panelCantidad = new JPanel(new BorderLayout(5, 5));
        panelCantidad.add(new JLabel("Cantidad del movimiento (€):"), BorderLayout.NORTH);
        tfCantidad = new JTextField();
        panelCantidad.add(tfCantidad, BorderLayout.CENTER);
        panelForm.add(panelCantidad);

        JPanel panelFecha = new JPanel(new BorderLayout(5, 5));
        panelFecha.add(new JLabel("Fecha del movimiento (yyyy-MM-dd):"), BorderLayout.NORTH);
        tfFecha = new JTextField();
        panelFecha.add(tfFecha, BorderLayout.CENTER);
        panelForm.add(panelFecha);

        panelInferior.add(panelForm, BorderLayout.CENTER);

        // 🔹 Panel inferior: botones alineados a la derecha
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        JButton btnRegistrar = new JButton("Registrar movimiento");
        btnRegistrar.setBackground(Color.WHITE);
        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(Color.WHITE);
        panelBotones.add(btnVolver);
        panelBotones.add(btnRegistrar);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
   
        contentPane.add(panelInferior, BorderLayout.SOUTH);



        // === EVENTOS ===
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

        // === CARGA INICIAL ===
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
            Object cuotaObj = act.get("cuota");
            if (idObj == null || nombreObj == null || cuotaObj == null) continue;

            int id = ((Number) idObj).intValue();
            String nombre = String.valueOf(nombreObj);
            double cuota = Double.parseDouble(String.valueOf(cuotaObj));

            //Obtener detalles de la actividad
            Map<String, Object> detalles = us.getActividadDetalles(id);
            if (detalles == null) continue;

     

            


            // Calcular plazas disponibles (solo pagadas)
            int plazas = 0;
            if (detalles.get("plazas_disponibles") != null) {
                plazas = ((Number) detalles.get("plazas_disponibles")).intValue();
                if (plazas < 0) plazas = 0;
            }
            
          

            modelActividades.addRow(new Object[]{id, nombre, cuota, plazas});
            actividadData.put(id, act);
        }
        if (modelActividades.getRowCount() == 0) {
            modelActividades.addRow(new Object[]{"-", "No hay actividades con pagos pendientes", "-", "-"});
            tableActividades.setEnabled(false);
            return;
        }

        tableActividades.setEnabled(modelActividades.getRowCount() > 0);
    }

    @SuppressWarnings("unchecked")
    private void cargarInscripcionesPendientes() {
        modelInscripciones.setRowCount(0);
        inscripcionData.clear();
        idMatriculaSeleccionada = -1;

        if (idActividadSeleccionada == -1) return;

        Map<String, Object> actividad = us.getActividadDetalles(idActividadSeleccionada);
        if (actividad == null) return;
        cuotaSeleccionada = Double.parseDouble(String.valueOf(actividad.get("cuota")));

        List<Map<String, Object>> inscripciones = (List<Map<String, Object>>) actividad.get("inscripciones");
        if (inscripciones == null || inscripciones.isEmpty()) return;

        for (Map<String, Object> ins : inscripciones) {
            String estado = (String) ins.get("estado");
            if (!"Pendiente".equalsIgnoreCase(estado)) continue;

            int idMatricula = ((Number) ins.get("id_matricula")).intValue();
            String nombreCompleto = (String) ins.get("nombre_alumno");
            String[] partes = nombreCompleto.split(" ", 2);
            String nombre = partes.length > 0 ? partes[0] : "";
            String apellido = partes.length > 1 ? partes[1] : "";
            String telefono = ins.get("telefono") != null ? ins.get("telefono").toString() : "-";
            String fechaMatricula = ins.get("fecha_matricula") != null ? ins.get("fecha_matricula").toString() : "-";
            String fechaLimite = ins.get("fecha_limite_pago") != null ? ins.get("fecha_limite_pago").toString() : "-";

            modelInscripciones.addRow(new Object[]{idMatricula, nombre, apellido, telefono, fechaMatricula, fechaLimite, estado});
            inscripcionData.put(idMatricula, ins);
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
            fechaMovimiento = LocalDate.parse(tfFecha.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            mostrarError("Formato de fecha inválido. Use yyyy-MM-dd.");
            return null;
        }

        LocalDate fechaHoy = us.getFechaHoy();
        LocalDate fechaMatricula = us.getFechaMatricula(idMatriculaSeleccionada);

        if (fechaMovimiento.isAfter(fechaHoy)) {
            mostrarAviso("La fecha del movimiento no puede ser posterior a la fecha actual (" + fechaHoy + ").");
            return null;
        }

        if (fechaMovimiento.isBefore(fechaMatricula)) {
            mostrarAviso("La fecha del movimiento no puede ser anterior a la fecha de matrícula (" + fechaMatricula + ").");
            return null;
        }

        return fechaMovimiento;
    }
    private void procesarPago(double cantidad, LocalDate fechaMovimiento, Map<String, Double> estado) {
        double totalPagado = estado.getOrDefault("total_pagado", 0.0);
        double totalDevuelto = estado.getOrDefault("total_devuelto", 0.0);
        double cuota = cuotaSeleccionada;

        // Registrar siempre el pago
        if (us.registrarPago(idMatriculaSeleccionada, cantidad, fechaMovimiento)) {
            double nuevoTotalPagado = totalPagado + cantidad; // Total real pagado por el alumno

            // Redondear para evitar problemas de coma flotante
            nuevoTotalPagado = Math.round(nuevoTotalPagado * 100.0) / 100.0;

            double pendiente = Math.max(0, cuota - (nuevoTotalPagado - totalDevuelto));
            pendiente = Math.round(pendiente * 100.0) / 100.0;

            double exceso = Math.max(0, (nuevoTotalPagado - totalDevuelto) - cuota);
            exceso = Math.round(exceso * 100.0) / 100.0;

            if (pendiente > 0) {
                mostrarAviso(String.format("⚠️ Pago parcial.\nEl alumno aún debe %.2f €.", pendiente));
            } else if (exceso > 0) {
                mostrarAviso(String.format(
                    "✅ Pago registrado.\nEl alumno ha pagado %.2f € de más.\nEste exceso queda pendiente de devolución manual.",
                    exceso
                ));
            } else {
                mostrarInfo("✅ Pago correcto.\nEl alumno ha completado su matrícula.");
            }

            // Actualizar campos visuales con total real pagado y pendiente
            actualizarCamposVisuales(true);
        }
    }





        
    private void procesarDevolucion(double cantidad, LocalDate fechaMovimiento, Map<String, Double> estado) {
        double totalPagado = estado.get("total_pagado");
       

        if (Math.abs(cantidad - totalPagado) < 0.01) {
            if (us.registrarDevolucion(idMatriculaSeleccionada, cantidad, fechaMovimiento)) {
                mostrarInfo("✅ Devolución correcta.\nEl alumno ha recibido la totalidad de lo pagado.");
            }
        } else if (cantidad < totalPagado) {
            if (us.registrarDevolucion(idMatriculaSeleccionada, cantidad, fechaMovimiento)) {
                Map<String, Double> nuevoEstado = us.getEstadoPagoAlumno(idMatriculaSeleccionada);
                double falta = nuevoEstado.getOrDefault("a_devolver", 0.0);

                if (falta > 0.01) {
                    mostrarAviso(String.format(
                        "⚠️ Devolución parcial.\nFaltan %.2f € por devolver al alumno.", falta
                    ));
                } else {
                    mostrarInfo("✅ Devolución completada correctamente.\nNo queda dinero pendiente de devolver.");
                }
            }
        }
        else {
            double exceso = cantidad - totalPagado;
            if (us.registrarDevolucion(idMatriculaSeleccionada, totalPagado, fechaMovimiento)) {
                mostrarAviso("⚠️ Devolución excesiva.\nSe está devolviendo " + String.format("%.2f €", exceso) + " más de lo pagado.");
            }
        }
    }
    private void actualizarCamposVisuales(boolean esPago) {
        if (idMatriculaSeleccionada <= 0) return;

        Map<String, Double> estadoPago = us.getEstadoPagoAlumno(idMatriculaSeleccionada);
        if (estadoPago == null) return;

        // total real pagado por el alumno (sin limitar a cuota)
        double totalPagado = estadoPago.getOrDefault("total_pagado", 0.0);
        double totalDevuelto = estadoPago.getOrDefault("total_devuelto", 0.0);
        double cuota = cuotaSeleccionada;

        if (esPago) {
            lblPendiente.setText("Cantidad pendiente (€):");
            tfTotalPagado.setText(String.format("%.2f", totalPagado));

            // pendiente real: lo que falta por pagar teniendo en cuenta devoluciones
            double pendiente = Math.max(0, cuota - (totalPagado - totalDevuelto));
            tfPendiente.setText(String.format("%.2f", pendiente));

        } else {
            lblPendiente.setText("A devolver (€):");
            tfTotalPagado.setText(String.format("%.2f", totalDevuelto));

            double aDevolver = Math.max(0, totalPagado - cuota - totalDevuelto);
            tfPendiente.setText(String.format("%.2f", aDevolver));
        }
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

