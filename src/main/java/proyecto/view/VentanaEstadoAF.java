package proyecto.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import proyecto.service.UserService;

public class VentanaEstadoAF extends JFrame {

    private static final long serialVersionUID = 1L;
    private UserService service;

    // Tabla de actividades
    private JTable tableActividades;
    private DefaultTableModel modelActividades;

    // Tabla de inscripciones
    private JTable tableInscripciones;
    private DefaultTableModel modelInscripciones;

    // Campos detalle
    private JTextField txtNombre;
    private JTextField txtPeriodo;
    private JTextField txtFecha;
    private JTextField txtEstado;
    private JTextField txtTotalPlazas;
    private JTextField txtPlazasDisponibles;
    private JTextField txtIngresosEstimados;
    private JTextField txtIngresosConfirmados;
    private JTextField txtGastosEstimados;
    private JTextField txtGastosConfirmados;

    // Filtro
    private JComboBox<String> comboEstado;

    public VentanaEstadoAF(UserService service) {
        this.service = service;
        setTitle("Estado de Actividades Formativas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 650);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10,10));
        setContentPane(contentPane);

        // === PANEL SUPERIOR: FILTRO Y TABLA DE ACTIVIDADES ===
        JPanel panelSuperior = new JPanel(new BorderLayout(5,5));

        // Filtro por estado
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.add(new JLabel("Filtrar por estado:"));

         comboEstado = new JComboBox<>(
        	    new DefaultComboBoxModel<>(new String[]{
        	        "Todas", "Planificada", "En periodo de inscripcion",
        	        "Inscripcion cerrada", "Cerrada", "Cancelada"
        	    })
        	);
        panelFiltro.add(comboEstado);

        JButton btnFiltrar = new JButton("Aplicar filtro");
        panelFiltro.add(btnFiltrar);
        panelSuperior.add(panelFiltro, BorderLayout.NORTH);

        // Tabla de actividades
        modelActividades = new DefaultTableModel(
            new Object[]{"ID","Nombre","Periodo inscripción","Fecha","Estado"}, 0
        );
        tableActividades = new JTable(modelActividades);
        tableActividades.setRowHeight(25);
        tableActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollActividades = new JScrollPane(tableActividades);
        scrollActividades.setBorder(BorderFactory.createTitledBorder("Listado de actividades"));
        panelSuperior.add(scrollActividades, BorderLayout.CENTER);

        // === PANEL INFERIOR: DETALLES ===
        JPanel panelDetalles = new JPanel(new BorderLayout(10,10));
        panelDetalles.setBorder(BorderFactory.createTitledBorder("Detalles de la actividad"));

        // Panel de datos generales
        JPanel panelDatos = new JPanel(new GridLayout(3,4,10,10));

        txtNombre = new JTextField(); txtNombre.setEditable(false);
        txtPeriodo = new JTextField(); txtPeriodo.setEditable(false);
        txtFecha = new JTextField(); txtFecha.setEditable(false);
        txtEstado = new JTextField(); txtEstado.setEditable(false);
        txtTotalPlazas = new JTextField(); txtTotalPlazas.setEditable(false);
        txtPlazasDisponibles = new JTextField(); txtPlazasDisponibles.setEditable(false);

        panelDatos.add(new JLabel("Nombre:")); panelDatos.add(txtNombre);
        panelDatos.add(new JLabel("Periodo inscripción:")); panelDatos.add(txtPeriodo);
        panelDatos.add(new JLabel("Fecha celebración:")); panelDatos.add(txtFecha);
        panelDatos.add(new JLabel("Estado:")); panelDatos.add(txtEstado);
        panelDatos.add(new JLabel("Total plazas:")); panelDatos.add(txtTotalPlazas);
        panelDatos.add(new JLabel("Plazas disponibles:")); panelDatos.add(txtPlazasDisponibles);
        panelDetalles.add(panelDatos, BorderLayout.NORTH);

        // Tabla inscripciones
        modelInscripciones = new DefaultTableModel(
            new Object[]{"Profesional","Fecha matrícula","Estado"},0
        );
        tableInscripciones = new JTable(modelInscripciones);
        JScrollPane scrollInscripciones = new JScrollPane(tableInscripciones);
        scrollInscripciones.setBorder(BorderFactory.createTitledBorder("Inscripciones"));
        panelDetalles.add(scrollInscripciones, BorderLayout.CENTER);

        // Panel de finanzas
        JPanel panelFinanzas = new JPanel(new GridLayout(2,2,10,10));
        txtIngresosEstimados = new JTextField(); txtIngresosEstimados.setEditable(false);
        txtIngresosConfirmados = new JTextField(); txtIngresosConfirmados.setEditable(false);
        txtGastosEstimados = new JTextField(); txtGastosEstimados.setEditable(false);
        txtGastosConfirmados = new JTextField(); txtGastosConfirmados.setEditable(false);

        panelFinanzas.add(new JLabel("Ingresos estimados:")); panelFinanzas.add(txtIngresosEstimados);
        panelFinanzas.add(new JLabel("Ingresos confirmados:")); panelFinanzas.add(txtIngresosConfirmados);
        panelFinanzas.add(new JLabel("Gastos estimados:")); panelFinanzas.add(txtGastosEstimados);
        panelFinanzas.add(new JLabel("Gastos confirmados:")); panelFinanzas.add(txtGastosConfirmados);
        panelDetalles.add(panelFinanzas, BorderLayout.SOUTH);

        // === SPLIT ENTRE LISTA Y DETALLES ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelSuperior, panelDetalles);
        splitPane.setResizeWeight(0.4);
        contentPane.add(splitPane, BorderLayout.CENTER);

        // === EVENTOS ===
        // Cargar detalles al hacer clic en una actividad
        tableActividades.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = tableActividades.getSelectedRow();
                    if (row >= 0) {
                        int idActividad = (int) modelActividades.getValueAt(row, 0);
                        cargarDetalles(idActividad);
                    }
                }
            }
        });

        // Filtro de actividades
        btnFiltrar.addActionListener(e -> cargarActividades());

        // Cargar datos iniciales
        cargarActividades();
    }

    private void cargarActividades() {
        modelActividades.setRowCount(0);

        String estadoSeleccionado = comboEstado.getSelectedItem().toString();
        List<Map<String,Object>> actividades;

        if (estadoSeleccionado.equals("Todas")) {
            actividades = service.listarActividades();
        } else {
            actividades = service.listarActividadesPorEstado(estadoSeleccionado);
        }

        if (actividades == null || actividades.isEmpty()) return;

        for(Map<String,Object> act : actividades) {
            String periodo = act.get("inicio_inscripcion") + " - " + act.get("fin_inscripcion");
            modelActividades.addRow(new Object[]{
                act.get("id_actividad"),
                act.get("nombre"),
                periodo,
                act.get("fecha"),
                act.get("estado")
            });
        }
    }

    private void cargarDetalles(int idActividad) {
        Map<String,Object> act = service.getActividadDetalles(idActividad);
        if (act == null) return;

        txtNombre.setText((String) act.get("nombre"));
        txtPeriodo.setText(act.get("inicio_inscripcion") + " - " + act.get("fin_inscripcion"));
        txtFecha.setText((String) act.get("fecha"));
        txtEstado.setText((String) act.get("estado"));
        txtTotalPlazas.setText(String.valueOf(act.get("total_plazas")));
        txtPlazasDisponibles.setText(String.valueOf(act.get("plazas_disponibles")));

        // Tabla inscripciones
        modelInscripciones.setRowCount(0);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> inscripciones = (List<Map<String,Object>>) act.get("inscripciones");
        if (inscripciones != null) {
            for (Map<String,Object> ins : inscripciones) {
                modelInscripciones.addRow(new Object[]{
                    ins.get("nombre_alumno"),
                    ins.get("fecha_matricula"),
                    ins.get("estado")
                });
            }
        }

        txtIngresosEstimados.setText(String.valueOf(act.get("ingresos_estimados")));
        txtIngresosConfirmados.setText(String.valueOf(act.get("ingresos_confirmados")));
        txtGastosEstimados.setText(String.valueOf(act.get("gastos_estimados")));
        txtGastosConfirmados.setText(String.valueOf(act.get("gastos_confirmados")));
    }
}
