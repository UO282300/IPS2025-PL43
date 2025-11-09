package proyecto.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import proyecto.service.UserService;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class VentanaResponsable extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // Panel norte
    private JPanel pnNorte;

    // Datos generales
    private JTextField txtNombre;
    private JTextArea txtObjetivos;
    private JTextArea txtContenidos;
    private JTextField txtPlazas;

    // Programación
    private JTextField txtInicioInscripcion;
    private JTextField txtFinInscripcion;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtUbicacion;

    // Profesores asignados
    private JComboBox<String> cmbProfesor;
    private JTextField txtRemuneracion;
    private JButton btnAnadirProfesor;
    private JButton btnEliminarProfesor;
    private JTable tableProfesoresAsignados;
    private DefaultTableModel modelProfesores;
    JTextField txtEmpresa;

    // Nuevo profesor
    private JTextField txtNuevoNombre;
    private JTextField txtNuevoApellido;
    private JTextField txtNuevoEmail;
    private JTextField txtNuevoTelefono;
    private JButton btnGuardarNuevoProfesor;

    // Cuotas asignadas
    private JCheckBox chkGratuita;
    private JComboBox<String> cmbCuotas;
    private JTextField txtValorCuota;
    private JButton btnAnadirCuota;
    private JButton btnEliminarCuota;
    private JTable tableCuotasAsignadas;
    private DefaultTableModel modelCuotas;

    // Nueva cuota
    private JTextField txtNuevaCategoria;
    private JButton btnGuardarNuevaCuota;

    // Panel sur
    private JButton btnCancelar;
    private JButton btnGuardarActividad;

    private UserService service;
    private Map<String, Integer> mapaProfesores = new HashMap<>();

    public VentanaResponsable(UserService service) {
        this.service = service;

        setTitle("Planificación de Actividad Formativa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1200, 850);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(10,10));
        contentPane.setBackground(new Color(230,240,255));
        setContentPane(contentPane);

        contentPane.add(getPnNorte(), BorderLayout.NORTH);
        contentPane.add(getPanelCentral(), BorderLayout.CENTER);
        contentPane.add(getPanelSur(), BorderLayout.SOUTH);
    }

    private JPanel getPnNorte() {
        if(pnNorte==null) {
            pnNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
            pnNorte.setBackground(new Color(230,235,250));
            JLabel lblTitulo = new JLabel("Planificación de Actividad Formativa", SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblTitulo.setForeground(new Color(30,50,90));
            lblTitulo.setBorder(BorderFactory.createEmptyBorder(20,0,10,0));
            pnNorte.add(lblTitulo);
        }
        return pnNorte;
    }

    private JPanel getPanelCentral() {
        JPanel panelCentral = new JPanel(new GridLayout(3,2,10,10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Fila 1
        panelCentral.add(getPanelDatosGenerales());
        panelCentral.add(getPanelProgramacion());

        // Fila 2
        panelCentral.add(getPanelProfesoresAsignados());
        panelCentral.add(getPanelNuevoProfesor());

        // Fila 3
        panelCentral.add(getPanelCuotasAsignadas());
        panelCentral.add(getPanelNuevaCuota());

        return panelCentral;
    }

    private JPanel getPanelDatosGenerales() {
        JPanel panel = new JPanel(new GridLayout(4,2,5,5));
        panel.setBorder(new TitledBorder("Datos Generales"));
        panel.setBackground(new Color(250,252,255));

        panel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(); panel.add(txtNombre);

        panel.add(new JLabel("Objetivos:"));
        txtObjetivos = new JTextArea(3,20); txtObjetivos.setLineWrap(true);
        panel.add(new JScrollPane(txtObjetivos));

        panel.add(new JLabel("Contenidos:"));
        txtContenidos = new JTextArea(3,20); txtContenidos.setLineWrap(true);
        panel.add(new JScrollPane(txtContenidos));

        panel.add(new JLabel("Número de plazas:"));
        txtPlazas = new JTextField(); panel.add(txtPlazas);

        return panel;
    }

    private JPanel getPanelProgramacion() {
        JPanel panel = new JPanel(new GridLayout(5,2,5,5));
        panel.setBorder(new TitledBorder("Programación"));
        panel.setBackground(new Color(250,252,255));

        panel.add(new JLabel("Inicio inscripción (yyyy-MM-dd):"));
        txtInicioInscripcion = new JTextField(); panel.add(txtInicioInscripcion);

        panel.add(new JLabel("Fin inscripción (yyyy-MM-dd):"));
        txtFinInscripcion = new JTextField(); panel.add(txtFinInscripcion);

        panel.add(new JLabel("Fecha inicio (yyyy-MM-dd):"));
        txtFechaInicio = new JTextField(); panel.add(txtFechaInicio);

        panel.add(new JLabel("Fecha fin (yyyy-MM-dd):"));
        txtFechaFin = new JTextField(); panel.add(txtFechaFin);

        panel.add(new JLabel("Ubicación:"));
        txtUbicacion = new JTextField(); panel.add(txtUbicacion);

        return panel;
    }

    private JPanel getPanelProfesoresAsignados() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(new TitledBorder("Profesores asignados"));
        panel.setBackground(new Color(250,252,255));

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cmbProfesor = new JComboBox<>(); cargarProfesores();
        panelTop.add(new JLabel("Profesor:")); panelTop.add(cmbProfesor);

        txtRemuneracion = new JTextField(6); panelTop.add(new JLabel("Remuneración:")); panelTop.add(txtRemuneracion);

        btnAnadirProfesor = new JButton("Añadir"); panelTop.add(btnAnadirProfesor);
        btnEliminarProfesor = new JButton("Eliminar"); panelTop.add(btnEliminarProfesor);

        panel.add(panelTop, BorderLayout.NORTH);

        modelProfesores = new DefaultTableModel(new Object[]{"Profesor","Remuneración"},0);
        tableProfesoresAsignados = new JTable(modelProfesores);
        JScrollPane scroll = new JScrollPane(tableProfesoresAsignados);
        panel.add(scroll, BorderLayout.CENTER);
        
        JPanel panelSurProfesores = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSurProfesores.add(new JLabel("Empresa:"));
        txtEmpresa = new JTextField(15);
        panelSurProfesores.add(txtEmpresa);
        panel.add(panelSurProfesores, BorderLayout.SOUTH);


        btnAnadirProfesor.addActionListener(e -> {
            String prof = (String)cmbProfesor.getSelectedItem();
            String remuneracion = txtRemuneracion.getText().trim();
            if(prof != null && !remuneracion.isEmpty()) {
                modelProfesores.addRow(new Object[]{prof, remuneracion});
            }
        });

        btnEliminarProfesor.addActionListener(e -> {
            int row = tableProfesoresAsignados.getSelectedRow();
            if(row>=0) modelProfesores.removeRow(row);
        });

        return panel;
    }

    private JPanel getPanelNuevoProfesor() {
        JPanel panel = new JPanel(new GridLayout(5,2,5,5));
        panel.setBorder(new TitledBorder("Nuevo profesor"));
        panel.setBackground(new Color(250,252,255));

        panel.add(new JLabel("Nombre:"));
        txtNuevoNombre = new JTextField(); panel.add(txtNuevoNombre);

        panel.add(new JLabel("Apellido:"));
        txtNuevoApellido = new JTextField(); panel.add(txtNuevoApellido);

        panel.add(new JLabel("Email:"));
        txtNuevoEmail = new JTextField(); panel.add(txtNuevoEmail);

        panel.add(new JLabel("Teléfono:"));
        txtNuevoTelefono = new JTextField(); panel.add(txtNuevoTelefono);
        
        panel.add(new JLabel(""));

        btnGuardarNuevoProfesor = new JButton("Guardar profesor"); panel.add(btnGuardarNuevoProfesor);

        btnGuardarNuevoProfesor.addActionListener(e -> {
            String nombre = txtNuevoNombre.getText().trim();
            String apellido = txtNuevoApellido.getText().trim();
            String email = txtNuevoEmail.getText().trim();
            String telefono = txtNuevoTelefono.getText().trim();
            if(nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Rellene nombre, apellido y email","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = service.cargarProfesor(nombre,apellido,email,telefono);
            if(ok) {
                JOptionPane.showMessageDialog(this,"Profesor añadido correctamente.");
                cargarProfesores();
                txtNuevoNombre.setText(""); txtNuevoApellido.setText(""); txtNuevoEmail.setText(""); txtNuevoTelefono.setText("");
            } else {
                JOptionPane.showMessageDialog(this,"No se pudo añadir el profesor","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel getPanelCuotasAsignadas() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(new TitledBorder("Cuotas asignadas"));
        panel.setBackground(new Color(250,252,255));

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cmbCuotas = new JComboBox<>(); cargarCuotas();
        panelTop.add(new JLabel("Categoría:")); panelTop.add(cmbCuotas);

        txtValorCuota = new JTextField(6); panelTop.add(new JLabel("Valor:")); panelTop.add(txtValorCuota);

        btnAnadirCuota = new JButton("Añadir"); panelTop.add(btnAnadirCuota);
        btnEliminarCuota = new JButton("Eliminar"); panelTop.add(btnEliminarCuota);

        panel.add(panelTop, BorderLayout.NORTH);

        modelCuotas = new DefaultTableModel(new Object[]{"Categoría","Valor"},0);
        tableCuotasAsignadas = new JTable(modelCuotas);
        JScrollPane scroll = new JScrollPane(tableCuotasAsignadas);
        panel.add(scroll, BorderLayout.CENTER);
        
        chkGratuita = new JCheckBox("Actividad gratuita");
        JPanel panelCheckbox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCheckbox.add(chkGratuita);
        panel.add(panelCheckbox, BorderLayout.SOUTH);

        btnAnadirCuota.addActionListener(e -> {
            if(chkGratuita.isSelected()) return;
            String categoria = (String)cmbCuotas.getSelectedItem();
            String valor = txtValorCuota.getText().trim();
            if(categoria != null && !valor.isEmpty()) {
                modelCuotas.addRow(new Object[]{categoria, valor});
            }
        });

        btnEliminarCuota.addActionListener(e -> {
            if(chkGratuita.isSelected()) return;
            int row = tableCuotasAsignadas.getSelectedRow();
            if(row>=0) modelCuotas.removeRow(row);
        });

        chkGratuita.addActionListener(e -> {
            boolean seleccionada = chkGratuita.isSelected();
            btnAnadirCuota.setEnabled(!seleccionada);
            btnEliminarCuota.setEnabled(!seleccionada);
        });

        return panel;
    }

    private JPanel getPanelNuevaCuota() {
        JPanel panel = new JPanel(new GridLayout(2,1,5,5));
        panel.setBorder(new TitledBorder("Nueva cuota"));
        panel.setBackground(new Color(250,252,255));

        panel.add(new JLabel("Categoría:"));
        txtNuevaCategoria = new JTextField(); panel.add(txtNuevaCategoria);
        
        panel.add(new JLabel(""));

        btnGuardarNuevaCuota = new JButton("Guardar cuota"); panel.add(btnGuardarNuevaCuota);

        btnGuardarNuevaCuota.addActionListener(e -> {
            String categoria = txtNuevaCategoria.getText().trim();
            if(categoria.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Rellene la categoría","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = service.cargarCuota(categoria);
            if(ok) {
                JOptionPane.showMessageDialog(this,"Cuota añadida correctamente.");
                cargarCuotas();
                txtNuevaCategoria.setText("");
            } else {
                JOptionPane.showMessageDialog(this,"No se pudo añadir la cuota","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel getPanelSur() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        btnCancelar = new JButton("Cancelar");
        btnGuardarActividad = new JButton("Guardar actividad");
        panel.add(btnCancelar); panel.add(btnGuardarActividad);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardarActividad.addActionListener(e -> guardarActividad());

        return panel;
    }

    private void cargarProfesores() {
        cmbProfesor.removeAllItems();
        mapaProfesores.clear();
        List<Map<String,Object>> profesores = service.listarProfesores();
        for(Map<String,Object> prof : profesores) {
            String nombre = prof.get("nombre")+" "+prof.get("apellido");
            cmbProfesor.addItem(nombre);
            mapaProfesores.put(nombre, (Integer)prof.get("id_profesor"));
        }
    }

    private void cargarCuotas() {
        cmbCuotas.removeAllItems();
        List<Map<String,Object>> cuotas = service.listarCuotas();
        if (cuotas == null) cuotas = List.of();
        for(Map<String,Object> c : cuotas) {
            cmbCuotas.addItem((String)c.get("categoria"));
        }
    }

    private void guardarActividad() {
    	LocalDate inicioIns, finIns, fechaInicio, fechaFin, fechaHoy = LocalDate.now();
    	
    	// Validación de nombre obligatorio
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe indicar un nombre para la actividad.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Comprobación de profesores
        if (modelProfesores.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe añadir al menos un profesor asignado.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Comprobación de cuotas
        if (!chkGratuita.isSelected() && modelCuotas.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe añadir al menos una cuota o marcar la actividad como gratuita.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar formato de fechas
        try {
            inicioIns = LocalDate.parse(txtInicioInscripcion.getText().trim());
            finIns = LocalDate.parse(txtFinInscripcion.getText().trim());
            fechaInicio = LocalDate.parse(txtFechaInicio.getText().trim());
            fechaFin = LocalDate.parse(txtFechaFin.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Usa el formato yyyy-MM-dd",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación de orden cronológico de fechas
        if (inicioIns.isAfter(finIns)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de inicio de inscripción no puede ser posterior a la fecha de fin de inscripción.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (finIns.isAfter(fechaInicio)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de fin de inscripción no puede ser posterior al inicio de la actividad.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fechaInicio.isAfter(fechaFin)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de inicio de la actividad no puede ser posterior a la fecha de finalización.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación respecto a la fecha actual
        if (fechaHoy.isAfter(inicioIns) || fechaHoy.isAfter(fechaInicio)) {
            JOptionPane.showMessageDialog(this,
                    "No puedes iniciar una inscripción o actividad en una fecha anterior a hoy.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idActividad = service.guardarActividad(
        	    txtNombre.getText().trim(),
        	    txtObjetivos.getText().trim(),
        	    txtContenidos.getText().trim(),
        	    inicioIns,
        	    finIns,
        	    fechaInicio,
        	    fechaFin,
        	    txtUbicacion.getText().trim(),
        	    txtPlazas.getText().isEmpty() ? 0 : Integer.parseInt(txtPlazas.getText()),
        	    chkGratuita.isSelected(),
        	    txtEmpresa.getText().trim()
        	);
        
        if (idActividad != -1) {
        	int filas = modelProfesores.getRowCount();
            for (int r = 0; r < filas; r++) {
                String profesorTexto = (String) modelProfesores.getValueAt(r, 0);
                String remuneracionTxt = modelProfesores.getValueAt(r, 1).toString().replace(",", ".").trim();
                double remuneracion = 0.0;
                try {
                    remuneracion = Double.parseDouble(remuneracionTxt);
                } catch (NumberFormatException ex) {
                	JOptionPane.showMessageDialog(this,
                            "Remuneracion invalida",
                            "Error al registrar actividad",
                            JOptionPane.ERROR_MESSAGE);
                }

                Integer idProfesor = mapaProfesores.get(profesorTexto);
                if (idProfesor == null) {
                	JOptionPane.showMessageDialog(this,
                            "Profesor invalido",
                            "Error al registrar actividad",
                            JOptionPane.ERROR_MESSAGE);
                }

                String numeroFactura = "AUTO-" + idActividad + "-" + (r+1);
                String fechaFactura = LocalDate.now().toString();

                String emisorNombre = profesorTexto;
                String emisorNif = "N/A";
                String emisorDireccion = "N/A";

                service.insertFacturaP(idProfesor, idActividad, numeroFactura, fechaFactura, remuneracion, emisorNombre, emisorNif, emisorDireccion);
            }
        	
            JOptionPane.showMessageDialog(this,
                    "Actividad guardada correctamente.");
            dispose();
            
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la actividad.",
                    "Error al registrar actividad",
                    JOptionPane.ERROR_MESSAGE);
        }

    }
}
