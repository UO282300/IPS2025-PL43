package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

public class VentanaPagoProfesores extends JFrame {

    private static final long serialVersionUID = 1L;

    private pagosController us;

    private JTable tableCursos;
    private JTable tableProfesores;
    private DefaultTableModel modelCursos;
    private DefaultTableModel modelProfesores;

    private JTextField tfCantidad;
    private JTextField tfFecha;

    // 🧾 Campos de datos de factura
    private JTextField tfIdFactura;
    private JTextField tfNumeroFactura;
    private JTextField tfDireccionEmisor;
    private JTextField tfCantidadFactura;
    private JTextField tfFechaFactura;

    private int idActividadSeleccionada = -1;
    private int idProfesorSeleccionado = -1;
    private double remuneracionSeleccionada = 0;

    private Map<Integer, Map<String, Object>> cursoData = new HashMap<>();

    public VentanaPagoProfesores(UserService service) {
        this.us = new pagosController(service);
        setTitle("Registro de Pagos a Profesores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1500, 840);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);

        JLabel lblTitulo = new JLabel("Registrar Pagos a Profesores", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contentPane.add(lblTitulo, BorderLayout.NORTH);

     // === Panel central con tablas ===
        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));

        // --- Tabla de cursos ---
        modelCursos = new DefaultTableModel(
            new Object[]{"ID Actividad", "Nombre", "Remuneración (€)"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableCursos = new JTable(modelCursos);
        tableCursos.setRowHeight(25);
        tableCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollCursos = new JScrollPane(tableCursos);
        scrollCursos.setBorder(BorderFactory.createTitledBorder("Cursos con facturas pendientes"));
        panelCentral.add(scrollCursos);

        // --- Tabla de profesor (solo 1 por curso) ---
        modelProfesores = new DefaultTableModel(
            new Object[]{"ID Profesor", "Nombre", "Apellidos", "Teléfono"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableProfesores = new JTable(modelProfesores);
        tableProfesores.setRowHeight(25);
        JScrollPane scrollProfesores = new JScrollPane(tableProfesores);
        scrollProfesores.setBorder(BorderFactory.createTitledBorder("Profesor del curso seleccionado"));
        panelCentral.add(scrollProfesores);
        
     // === Panel de datos de factura ===
        JPanel panelFactura = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFactura.setBorder(BorderFactory.createTitledBorder("Datos de la Factura"));

        panelFactura.add(new JLabel("ID Factura:"));
        tfIdFactura = new JTextField();
        tfIdFactura.setEditable(false);
        panelFactura.add(tfIdFactura);

        panelFactura.add(new JLabel("Número Factura:"));
        tfNumeroFactura = new JTextField();
        tfNumeroFactura.setEditable(false);
        panelFactura.add(tfNumeroFactura);

        panelFactura.add(new JLabel("Dirección Emisor:"));
        tfDireccionEmisor = new JTextField();
        tfDireccionEmisor.setEditable(false);
        panelFactura.add(tfDireccionEmisor);

        panelFactura.add(new JLabel("Cantidad Factura (€):"));
        tfCantidadFactura = new JTextField();
        tfCantidadFactura.setEditable(false);
        panelFactura.add(tfCantidadFactura);

        panelFactura.add(new JLabel("Fecha Factura:"));
        tfFechaFactura = new JTextField();
        tfFechaFactura.setEditable(false);
        panelFactura.add(tfFechaFactura);

        // Lo añadimos debajo de la tabla de profesor, pero encima del panel de pago
        JPanel centroExtendido = new JPanel(new BorderLayout(10, 10));
        centroExtendido.add(panelCentral, BorderLayout.CENTER);
        centroExtendido.add(panelFactura, BorderLayout.SOUTH);
        contentPane.add(centroExtendido, BorderLayout.CENTER);



        // === Panel inferior (formulario de pago) ===
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Registrar pago"));

        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));

        panelForm.add(new JLabel("Cantidad pagada (EUR):"));
        tfCantidad = new JTextField();
        panelForm.add(tfCantidad);

        panelForm.add(new JLabel("Fecha de factura (yyyy-MM-dd):"));
        tfFecha = new JTextField();
        panelForm.add(tfFecha);

        if (us.getFechaHoy() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            tfFecha.setText(us.getFechaHoy().format(formatter));
        }

        panelInferior.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(new Color(255, 255, 255));
        JButton btnRegistrar = new JButton("Registrar Pago");
        JButton btnVolver = new JButton("Volver");
        btnRegistrar.setBackground(new Color(128, 255, 128));
        btnVolver.setBackground(new Color(255, 255, 255));
        panelBotones.add(btnVolver);
        panelBotones.add(btnRegistrar);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        contentPane.add(panelInferior, BorderLayout.SOUTH);

        // === Eventos ===
        btnVolver.addActionListener(e -> dispose());
        btnRegistrar.addActionListener(e -> registrarPago());

        // 🔹 Cuando seleccionas un curso, carga profesor y factura
        tableCursos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableCursos.getSelectedRow();
                if (row >= 0) {
                    idActividadSeleccionada = (int) modelCursos.getValueAt(row, 0);
                    remuneracionSeleccionada = Double.parseDouble(modelCursos.getValueAt(row, 2).toString());
                    cargarProfesorAsociado();
                    cargarDatosFactura();
                }
            }
        });

        // Cargar cursos pendientes al inicio
        cargarCursosPendientes();
    }

    private void cargarCursosPendientes() {
        modelCursos.setRowCount(0);
        cursoData.clear();

        List<Map<String, Object>> actividades = us.listarActividadesConProfesoresConPagosPendientes();

        if (actividades == null || actividades.isEmpty()) {
            modelCursos.addRow(new Object[]{"-", "No hay cursos con pagos pendientes", "-"});
            tableCursos.setEnabled(false);
            return;
        }

        for (Map<String, Object> act : actividades) {
            try {
                int id = ((Number) act.get("id_actividad")).intValue();
                String nombre = String.valueOf(act.get("nombre"));
                double remuneracion = act.get("remuneracion") != null
                        ? ((Number) act.get("remuneracion")).doubleValue()
                        : 0.0;
                modelCursos.addRow(new Object[]{id, nombre, remuneracion});
                cursoData.put(id, act);
            } catch (Exception ex) {
                System.err.println("Error al procesar actividad: " + ex.getMessage());
            }
        }

        tableCursos.setEnabled(modelCursos.getRowCount() > 0);
    }

    private void cargarProfesorAsociado() {
        modelProfesores.setRowCount(0);
        idProfesorSeleccionado = -1;

        if (idActividadSeleccionada == -1) return;

        Map<String, Object> profesor = us.obtenerDatosProfesorPorActividad(idActividadSeleccionada);
        if (profesor == null) return;

        idProfesorSeleccionado = ((Number) profesor.get("id_profesor")).intValue();
        String nombre = (String) profesor.get("profesor_nombre");
        String apellido = (String) profesor.get("profesor_apellido");
        String telefono = (String) profesor.get("profesor_telefono");

        modelProfesores.addRow(new Object[]{idProfesorSeleccionado, nombre, apellido, telefono});

        // 🔹 Cargar la factura asociada automáticamente
        cargarDatosFactura();
    }

    // 🧾 Nuevo método para cargar los datos de la factura
    private void cargarDatosFactura() {
        // Limpia por si acaso
        tfIdFactura.setText("");
        tfNumeroFactura.setText("");
        tfDireccionEmisor.setText("");
        tfCantidadFactura.setText("");
        tfFechaFactura.setText("");

        if (idActividadSeleccionada == -1 || idProfesorSeleccionado == -1) return;

        Map<String, Object> factura = us.obtenerDatosFacturaPorProfesorYActividad(idProfesorSeleccionado, idActividadSeleccionada);
        if (factura == null) return;

        tfIdFactura.setText(String.valueOf(factura.get("id_factura")));
        tfNumeroFactura.setText(String.valueOf(factura.get("numero_factura")));
        tfDireccionEmisor.setText(String.valueOf(factura.get("direccion_emisor")));
        tfCantidadFactura.setText(String.valueOf(factura.get("cantidad")));
        tfFechaFactura.setText(String.valueOf(factura.get("fecha")));
    }

    private void limpiarCamposFactura() {
        tfIdFactura.setText("");
        tfNumeroFactura.setText("");
        tfDireccionEmisor.setText("");
        tfCantidadFactura.setText("");
        tfFechaFactura.setText("");
    }

    private void registrarPago() {
        if (idActividadSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un curso.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (idProfesorSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "No se encontró profesor asociado al curso.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(tfCantidad.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Math.abs(cantidad - remuneracionSeleccionada) > 0.01) {
            JOptionPane.showMessageDialog(this,
                "La cantidad debe coincidir con la remuneración del curso (" + remuneracionSeleccionada + " EUR).",
                "Cantidad incorrecta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fechaPago = tfFecha.getText().trim();
        if (fechaPago.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe introducir una fecha válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idFactura = us.obtenerIdFactura(idProfesorSeleccionado, idActividadSeleccionada);
        us.registrarPagoProfesor(idProfesorSeleccionado, idFactura, idActividadSeleccionada, fechaPago, cantidad);

        JOptionPane.showMessageDialog(this, "Pago registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        tfCantidad.setText("");
        limpiarCamposFactura();
        idProfesorSeleccionado = -1;
        idActividadSeleccionada = -1;
        modelProfesores.setRowCount(0);
        tableCursos.clearSelection();
        cargarCursosPendientes();
    }
}

