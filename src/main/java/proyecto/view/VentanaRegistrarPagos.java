package proyecto.view;

import javax.swing.*;

import proyecto.service.UserService;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class VentanaRegistrarPagos extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<String> comboActividades;
    private JComboBox<String> comboInscripciones;
    private JTextField tfCantidad;
    private JTextField tfFecha;
    private UserService us;

    private Map<String, Integer> actividadMap = new HashMap<>();
    private Map<String, Integer> inscripcionMap = new HashMap<>();
    private double cuotaSeleccionada = 0;

    public VentanaRegistrarPagos(UserService service) {
        this.us = service;
        initialize();
    }

    private void initialize() {
        setTitle("Registro de Pagos de Inscripciones");
        setBounds(100, 100, 550, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitulo = new JLabel("Registro de Pagos");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(100, 20, 330, 30);
        getContentPane().add(lblTitulo);

        // --- ACTIVIDADES ---
        JLabel lblICursos = new JLabel("Cursos:");
        lblICursos.setBounds(50, 80, 120, 25);
        getContentPane().add(lblICursos);

        comboActividades = new JComboBox<>();
        comboActividades.setBounds(180, 80, 280, 25);
        getContentPane().add(comboActividades);

        // --- INSCRIPCIONES ---
        JLabel lblInscripcion = new JLabel("Inscripcon:");
        lblInscripcion.setBounds(50, 120, 120, 25);
        getContentPane().add(lblInscripcion);

        comboInscripciones = new JComboBox<>();
        comboInscripciones.setBounds(180, 120, 280, 25);
        getContentPane().add(comboInscripciones);

        cargarActividadesActivas();
        if (comboActividades.getItemCount() > 0 && comboActividades.isEnabled()) {
            cargarInscripciones();
        }

        comboActividades.addActionListener(e -> cargarInscripciones());

        // --- CANTIDAD ---
        JLabel lblCantidad = new JLabel("Cantidad (EUR):");
        lblCantidad.setBounds(50, 160, 120, 25);
        getContentPane().add(lblCantidad);

        tfCantidad = new JTextField();
        tfCantidad.setBounds(180, 160, 150, 25);
        getContentPane().add(tfCantidad);

        // --- FECHA ---
        JLabel lblFecha = new JLabel("Fecha de pago:");
        lblFecha.setBounds(50, 200, 120, 25);
        getContentPane().add(lblFecha);

        tfFecha = new JTextField();
        tfFecha.setBounds(180, 200, 150, 25);
        getContentPane().add(tfFecha);

        // --- BOTONES ---
        JButton btnRegistrar = new JButton("Registrar Pago");
        btnRegistrar.setBackground(new Color(128, 255, 0));
        btnRegistrar.setBounds(280, 280, 180, 35);
        getContentPane().add(btnRegistrar);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(new Color(255, 0, 0));
        btnVolver.setBounds(80, 280, 150, 35);
        getContentPane().add(btnVolver);

        btnRegistrar.addActionListener(e -> registrarPago());
        btnVolver.addActionListener(e -> dispose());
    }

    private void cargarActividadesActivas() {
        comboActividades.removeAllItems();
        actividadMap.clear();

        List<Map<String, Object>> actividades = us.listarActividadesConPagosPendientes();

        for (Map<String, Object> act : actividades) {
            String nombre = (String) act.get("nombre");
            int id = ((Number) act.get("id_actividad")).intValue();
            comboActividades.addItem(id + " - " + nombre);
            actividadMap.put(id + " - " + nombre, id);
        }

        if (comboActividades.getItemCount() == 0) {
            comboActividades.addItem("No hay cursos con pagos pendientes");
            comboActividades.setEnabled(false);
        } else {
            comboActividades.setEnabled(true);
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarInscripciones() {
        comboInscripciones.removeAllItems();
        inscripcionMap.clear();

        String seleccion = (String) comboActividades.getSelectedItem();
        if (seleccion == null) return;

        int idActividad = Integer.parseInt(seleccion.split(" - ")[0].trim());
        Map<String, Object> actividad = us.getActividadDetalles(idActividad);

        cuotaSeleccionada = Double.parseDouble(String.valueOf(actividad.get("cuota")));

        List<Map<String, Object>> inscripciones = (List<Map<String, Object>>) actividad.get("inscripciones");

        for (Map<String, Object> ins : inscripciones) {
            String estado = (String) ins.get("estado");
            if ("Cobrada".equalsIgnoreCase(estado)) continue;
            String nombreAlumno = (String) ins.get("nombre_alumno");
            int idMatricula = ((Number) ins.get("id_matricula")).intValue();
            comboInscripciones.addItem(nombreAlumno);
            inscripcionMap.put(nombreAlumno, idMatricula);
        }

        if (comboInscripciones.getItemCount() == 0) {
            comboInscripciones.addItem("No hay pagos pendientes");
            comboInscripciones.setEnabled(false);
        } else {
            comboInscripciones.setEnabled(true);
        }
    }

    private void registrarPago() {
        String alumnoSel = (String) comboInscripciones.getSelectedItem();
        if (alumnoSel == null || !inscripcionMap.containsKey(alumnoSel)) return;

        double cantidad = Double.parseDouble(tfCantidad.getText());
        LocalDate fechaPago = LocalDate.parse(tfFecha.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if (Math.abs(cantidad - cuotaSeleccionada) > 0.01) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser exactamente igual al costo del curso (" + cuotaSeleccionada + " EUR).",
                    "Cantidad incorrecta", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idMatricula = inscripcionMap.get(alumnoSel);
        LocalDate fechaMatricula = us.getFechaMatricula(idMatricula);
        LocalDate fechaMaxPago = fechaMatricula.plusDays(2);

        if (fechaPago.isBefore(fechaMatricula) || fechaPago.isAfter(fechaMaxPago)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de pago debe estar entre " +
                    		fechaMatricula.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " y " +
                    		fechaMaxPago.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean hayPlazas = us.registrarPago(idMatricula, cantidad, fechaPago);

        if (hayPlazas) {
            JOptionPane.showMessageDialog(this,
                    "Pago registrado correctamente para " + alumnoSel,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Pago registrado correctamente, pero el alumno queda en lista de espera por falta de plazas.",
                    "Reserva", JOptionPane.WARNING_MESSAGE);
        }

        tfCantidad.setText("");
        tfFecha.setText("");

        int seleccionPrevActividad = comboActividades.getSelectedIndex();
        cargarActividadesActivas();

        if (seleccionPrevActividad >= 0 && seleccionPrevActividad < comboActividades.getItemCount()) {
            comboActividades.setSelectedIndex(seleccionPrevActividad);
        }

        cargarInscripciones();
    }
}
