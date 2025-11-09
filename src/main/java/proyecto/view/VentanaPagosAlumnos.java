package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import proyecto.service.UserService;
import proyecto.service.pagosController;

public class VentanaPagosAlumnos extends JFrame {

    private static final long serialVersionUID = 1L;
    private pagosController us;

    // === Componentes principales ===
    private JTable tableActividades;
    private JTable tableInscripciones;
    private DefaultTableModel modelActividades;
    private DefaultTableModel modelInscripciones;
    private JTextField tfCantidad;
    private JTextField tfFecha;

    // === Datos ===
    private Map<Integer, Map<String, Object>> actividadData = new HashMap<>();
    private Map<Integer, Map<String, Object>> inscripcionData = new HashMap<>();
    private double cuotaSeleccionada = 0;
    private int idActividadSeleccionada = -1;
    private int idMatriculaSeleccionada = -1;

    @SuppressWarnings({ "serial" })
	public VentanaPagosAlumnos(UserService service) {
        this.us = new pagosController(service);
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

        // === PANEL INFERIOR: DETALLE DE PAGO ===
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Registrar pago"));

        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));

        panelForm.add(new JLabel("Cantidad pagada (EUR):"));
        tfCantidad = new JTextField();
        panelForm.add(tfCantidad);

        panelForm.add(new JLabel("Fecha de pago (yyyy-MM-dd):"));
        tfFecha = new JTextField();
        panelForm.add(tfFecha);

        panelInferior.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegistrar = new JButton("Registrar Pago");
        btnRegistrar.setBackground(new Color(255, 255, 255));
        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(new Color(255, 255, 255));
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

            //Verificar si tiene inscripciones pendientes
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> inscripciones = (List<Map<String, Object>>) detalles.get("inscripciones");
            if (inscripciones == null || inscripciones.isEmpty()) continue;

            boolean tienePendientes = inscripciones.stream()
                    .anyMatch(ins -> "Pendiente".equalsIgnoreCase((String) ins.get("estado")));

            if (!tienePendientes) continue; //Omitir cursos sin pagos pendientes

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
        if (idActividadSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un curso de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (idMatriculaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un alumno pendiente en la lista.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(tfCantidad.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaPago;
        try {
            fechaPago = LocalDate.parse(tfFecha.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido (use yyyy-MM-dd).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Math.abs(cantidad - cuotaSeleccionada) > 0.01) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser exactamente igual al costo del curso (" + cuotaSeleccionada + " EUR).",
                    "Cantidad incorrecta", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaMatricula = us.getFechaMatricula(idMatriculaSeleccionada);
        LocalDate fechaMaxPago = fechaMatricula.plusDays(2);

        if (fechaPago.isBefore(fechaMatricula) || fechaPago.isAfter(fechaMaxPago)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de pago debe estar entre " +
                            fechaMatricula.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " y " +
                            fechaMaxPago.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean hayPlazas = us.registrarPago(idMatriculaSeleccionada, cantidad, fechaPago);

        if (hayPlazas) {
            JOptionPane.showMessageDialog(this,
                    "Pago registrado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Pago registrado, pero el alumno queda en lista de espera por falta de plazas.",
                    "Reserva", JOptionPane.WARNING_MESSAGE);
        }

        //Limpieza visual
        tfCantidad.setText("");
        tfFecha.setText("");

        //Recargar todo el contenido actualizado
        idActividadSeleccionada = -1;
        idMatriculaSeleccionada = -1;
        modelInscripciones.setRowCount(0);
        tableActividades.clearSelection();

        cargarActividadesActivas(); //Recalcula plazas y elimina cursos sin pendientes
    }
}

