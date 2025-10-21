package proyecto.view;

import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import proyecto.service.UserService;

public class VentanaPagoProfesores extends JFrame {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> comboActividades;
    private JTextField tfCantidad;
    private JTextField tfFecha;
    private UserService us;

    private Map<String, Integer> actividadMap = new HashMap<>();
    private JTextField txtprofesor;
    private JTextField txtNIF;
    private JTextField txtDireccion;

    // Flag para evitar eventos durante la carga
    private boolean cargandoActividades = false;

    public VentanaPagoProfesores(UserService service) {
        this.us = service;
        initialize();
    }

    private void initialize() {
        setTitle("Registro de Pagos a Profesores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 550, 400);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitulo = new JLabel("Pago a profesores");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(100, 20, 330, 30);
        getContentPane().add(lblTitulo);

        // --- ACTIVIDADES ---
        JLabel lblICursos = new JLabel("Cursos:");
        lblICursos.setBounds(50, 60, 120, 25);
        getContentPane().add(lblICursos);

        comboActividades = new JComboBox<>();
        comboActividades.setBounds(180, 60, 280, 25);
        getContentPane().add(comboActividades);

        // --- PROFESOR ---
        JLabel lblProfeso = new JLabel("Profesor:");
        lblProfeso.setBounds(50, 98, 120, 25);
        getContentPane().add(lblProfeso);

        txtprofesor = new JTextField();
        txtprofesor.setEditable(false);
        txtprofesor.setBounds(180, 98, 150, 25);
        getContentPane().add(txtprofesor);

        txtNIF = new JTextField();
        txtNIF.setEditable(false);
        txtNIF.setBounds(180, 133, 150, 25);
        getContentPane().add(txtNIF);

        txtDireccion = new JTextField();
        txtDireccion.setEditable(false);
        txtDireccion.setBounds(180, 167, 150, 25);
        getContentPane().add(txtDireccion);

        JLabel Nif = new JLabel("NIF:");
        Nif.setBounds(50, 133, 120, 25);
        getContentPane().add(Nif);

        JLabel Direccion = new JLabel("Dirección:");
        Direccion.setBounds(50, 167, 120, 25);
        getContentPane().add(Direccion);

        // --- CANTIDAD ---
        JLabel lblCantidad = new JLabel("Cantidad (€):");
        lblCantidad.setBounds(50, 210, 120, 25);
        getContentPane().add(lblCantidad);

        tfCantidad = new JTextField();
        tfCantidad.setBounds(180, 210, 150, 25);
        getContentPane().add(tfCantidad);

        // --- FECHA ---
        JLabel lblFecha = new JLabel("Fecha de pago:");
        lblFecha.setBounds(50, 245, 120, 25);
        getContentPane().add(lblFecha);

        tfFecha = new JTextField();
        tfFecha.setBounds(180, 245, 150, 25);
        getContentPane().add(tfFecha);

        if (us.getFechaHoy() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            tfFecha.setText(us.getFechaHoy().format(formatter));
        }

        // --- BOTONES ---
        JButton btnRegistrar = new JButton("Registrar Pago");
        btnRegistrar.setBackground(new Color(128, 255, 0));
        btnRegistrar.setBounds(280, 297, 180, 35);
        getContentPane().add(btnRegistrar);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(new Color(255, 0, 0));
        btnVolver.setBounds(82, 297, 150, 35);
        getContentPane().add(btnVolver);

        btnVolver.addActionListener(e -> dispose());
        btnRegistrar.addActionListener(e -> registrarPago());

        comboActividades.addActionListener(e -> {
            if (cargandoActividades) return;
            String key = (String) comboActividades.getSelectedItem();
            if (key == null || !actividadMap.containsKey(key)) return;
            int idActividad = actividadMap.get(key);
            autocompletarDatosProfesor(idActividad);
        });

        cargarActividadesActivas();
    }

    private void cargarActividadesActivas() {
        cargandoActividades = true;
        comboActividades.removeAllItems();
        actividadMap.clear();

        List<Map<String, Object>> actividades = us.listarActividadesConProfesoresConPagosPendientes();

        if (actividades.isEmpty()) {
            comboActividades.addItem("No hay cursos con pagos pendientes");
            comboActividades.setEnabled(false);
            txtprofesor.setText("");
            txtNIF.setText("");
            txtDireccion.setText("");
            cargandoActividades = false;
            return;
        }

        for (Map<String, Object> act : actividades) {
            String nombre = (String) act.get("nombre");
            int id = ((Number) act.get("id_actividad")).intValue();
            String key = id + " - " + nombre;
            comboActividades.addItem(key);
            actividadMap.put(key, id);
        }

        comboActividades.setEnabled(true);
        comboActividades.setSelectedIndex(0);

        String primerKey = (String) comboActividades.getSelectedItem();
        if (primerKey != null && actividadMap.containsKey(primerKey)) {
            autocompletarDatosProfesor(actividadMap.get(primerKey));
        }

        cargandoActividades = false;
    }

    private void autocompletarDatosProfesor(int idActividad) {
        Map<String, Object> profesor = us.obtenerDatosProfesorPorActividad(idActividad);
        if (profesor != null) {
            txtprofesor.setText(profesor.get("profesor_nombre") + " " + profesor.get("profesor_apellido"));
            txtNIF.setText((String) profesor.get("profesor_nif"));
            txtDireccion.setText((String) profesor.get("profesor_direccion"));
        } else {
            txtprofesor.setText("");
            txtNIF.setText("");
            txtDireccion.setText("");
        }
    }

    private void registrarPago() {
        if (comboActividades.getSelectedItem() == null || !comboActividades.isEnabled()) {
            JOptionPane.showMessageDialog(this, "No hay actividad seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String key = (String) comboActividades.getSelectedItem();
        Integer idActividad = actividadMap.get(key);
        if (idActividad == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una actividad válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double remuneracion = us.obtenerRemuneracionActividad(idActividad);
        double cantidadIntroducida;
        try {
            cantidadIntroducida = Double.parseDouble(tfCantidad.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Introduce un número válido para la cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidadIntroducida != remuneracion) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad introducida no coincide con la remuneración del profesor.\nCantidad correcta: " + remuneracion + " €",
                    "Cantidad incorrecta",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Object> profesor = us.obtenerDatosProfesorPorActividad(idActividad);
        if (profesor == null) {
            JOptionPane.showMessageDialog(this, "No se encontraron datos del profesor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idProfesor = ((Number) profesor.get("id_profesor")).intValue();
        int idFactura = us.obtenerIdFactura(idProfesor, idActividad);
        
        us.registrarPagoProfesor(idProfesor, idFactura, idActividad, tfFecha.getText(), cantidadIntroducida);

        JOptionPane.showMessageDialog(this, "Pago registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        us.imprimirPagosProfesor();

        tfCantidad.setText("");
        cargarActividadesActivas();
    }
}

