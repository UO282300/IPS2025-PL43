package proyecto.view;

import javax.swing.*;

import proyecto.service.UserService;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class VentanaPagoProfesores {

    private JFrame frame;
    private JComboBox<String> comboActividades;
    private JTextField tfCantidad;
    private JTextField tfFechaFactura;
    private JTextField tfNumeroFactura;
    private JTextField tfNombreProfesor;
    private JTextField tfNifProfesor;
    private JTextField tfDireccionProfesor;
    private UserService us;

    private Map<String, Integer> actividadMap = new HashMap<>();
    private double remuneracionSeleccionada = 0;

    public VentanaPagoProfesores(UserService us) {
        this.us = us;
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Pago a Profesores");
        frame.setBounds(100, 100, 600, 450);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setLocationRelativeTo(null);

        JLabel lblTitulo = new JLabel("Registro de Factura y Pago al Profesor");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(50, 20, 500, 30);
        frame.getContentPane().add(lblTitulo);

        // --- ACTIVIDADES ---
        JLabel lblActividades = new JLabel("Actividad:");
        lblActividades.setBounds(50, 70, 120, 25);
        frame.getContentPane().add(lblActividades);

        comboActividades = new JComboBox<>();
        comboActividades.setBounds(180, 70, 350, 25);
        frame.getContentPane().add(comboActividades);

        cargarActividadesConPagosPendientes();

        comboActividades.addActionListener(e -> cargarDatosProfesor());

        // --- DATOS DEL PROFESOR ---
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(50, 110, 120, 25);
        frame.getContentPane().add(lblNombre);

        tfNombreProfesor = new JTextField();
        tfNombreProfesor.setBounds(180, 110, 350, 25);
        tfNombreProfesor.setEditable(false);
        frame.getContentPane().add(tfNombreProfesor);

        JLabel lblNif = new JLabel("NIF:");
        lblNif.setBounds(50, 150, 120, 25);
        frame.getContentPane().add(lblNif);

        tfNifProfesor = new JTextField();
        tfNifProfesor.setBounds(180, 150, 150, 25);
        tfNifProfesor.setEditable(false);
        frame.getContentPane().add(tfNifProfesor);

        JLabel lblDireccion = new JLabel("Dirección:");
        lblDireccion.setBounds(50, 190, 120, 25);
        frame.getContentPane().add(lblDireccion);

        tfDireccionProfesor = new JTextField();
        tfDireccionProfesor.setBounds(180, 190, 350, 25);
        tfDireccionProfesor.setEditable(false);
        frame.getContentPane().add(tfDireccionProfesor);

        // --- FACTURA ---
        JLabel lblNumeroFactura = new JLabel("Número de factura:");
        lblNumeroFactura.setBounds(50, 230, 120, 25);
        frame.getContentPane().add(lblNumeroFactura);

        tfNumeroFactura = new JTextField();
        tfNumeroFactura.setBounds(180, 230, 150, 25);
        frame.getContentPane().add(tfNumeroFactura);

        JLabel lblFechaFactura = new JLabel("Fecha factura:");
        lblFechaFactura.setBounds(50, 270, 120, 25);
        frame.getContentPane().add(lblFechaFactura);

        tfFechaFactura = new JTextField();
        tfFechaFactura.setBounds(180, 270, 150, 25);
        frame.getContentPane().add(tfFechaFactura);

        // --- CANTIDAD ---
        JLabel lblCantidad = new JLabel("Cantidad (€):");
        lblCantidad.setBounds(50, 310, 120, 25);
        frame.getContentPane().add(lblCantidad);

        tfCantidad = new JTextField();
        tfCantidad.setBounds(180, 310, 150, 25);
        frame.getContentPane().add(tfCantidad);

        // --- BOTONES ---
        JButton btnRegistrarPago = new JButton("Registrar Pago");
        btnRegistrarPago.setBounds(350, 360, 180, 35);
        btnRegistrarPago.setBackground(new Color(128, 255, 0));
        frame.getContentPane().add(btnRegistrarPago);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBounds(80, 360, 150, 35);
        btnVolver.setBackground(new Color(255, 0, 0));
        frame.getContentPane().add(btnVolver);

        btnRegistrarPago.addActionListener(e -> registrarPagoProfesor());
        btnVolver.addActionListener(e -> frame.dispose());
    }

    private void cargarActividadesConPagosPendientes() {
        comboActividades.removeAllItems();
        actividadMap.clear();

        List<Map<String, Object>> actividades = us.listarActividadesConPagosPendientesProfesores();

        for (Map<String, Object> act : actividades) {
            String nombre = (String) act.get("nombre");
            int id = ((Number) act.get("id_actividad")).intValue();
            comboActividades.addItem(id + " - " + nombre);
            actividadMap.put(id + " - " + nombre, id);
        }

        if (comboActividades.getItemCount() == 0) {
            comboActividades.addItem("No hay actividades pendientes");
            comboActividades.setEnabled(false);
        } else {
            comboActividades.setEnabled(true);
            cargarDatosProfesor();
        }
    }

    private void cargarDatosProfesor() {
        String seleccion = (String) comboActividades.getSelectedItem();
        if (seleccion == null || seleccion.contains("No hay")) return;

        int idActividad = actividadMap.get(seleccion);
        Map<String, Object> datos = us.getProfesorDeActividad(idActividad);

        tfNombreProfesor.setText((String) datos.get("nombre") + " " + (String) datos.get("apellido"));
        tfNifProfesor.setText((String) datos.get("nif"));
        tfDireccionProfesor.setText((String) datos.get("direccion"));

        remuneracionSeleccionada = ((Number) datos.get("remuneracion")).doubleValue();
        tfCantidad.setText(String.valueOf(remuneracionSeleccionada));
    }

    private void registrarPagoProfesor() {
        String seleccion = (String) comboActividades.getSelectedItem();
        if (seleccion == null || seleccion.contains("No hay")) return;

        int idActividad = actividadMap.get(seleccion);

        double cantidad = Double.parseDouble(tfCantidad.getText());
        if (Math.abs(cantidad - remuneracionSeleccionada) > 0.01) {
            JOptionPane.showMessageDialog(frame,
                    "La cantidad debe ser exactamente la remuneración del profesor (" + remuneracionSeleccionada + " €).",
                    "Cantidad incorrecta", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String numeroFactura = tfNumeroFactura.getText();
        LocalDate fechaFactura = LocalDate.parse(tfFechaFactura.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        us.registrarFacturaYPago(idActividad, numeroFactura, fechaFactura, cantidad);

        JOptionPane.showMessageDialog(frame, "Pago registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        tfNumeroFactura.setText("");
        tfFechaFactura.setText("");
        tfCantidad.setText("");

        cargarActividadesConPagosPendientes();
    }

    public JFrame getFrame() {
        return frame;
    }
}