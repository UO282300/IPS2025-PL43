package proyecto.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import proyecto.service.UserService;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class VentanaDetallesAF extends JFrame {

    private static final long serialVersionUID = 1L;
    private UserService service;
    private int idActividad;

    private JTextField txtNombre, txtPeriodo, txtFecha, txtTotalPlazas, txtPlazasDisponibles, txtEstado;
    private JTable tableInscripciones;
    private DefaultTableModel modelInscripciones;
    private JTextField txtIngresosEstimados, txtIngresosConfirmados, txtGastosEstimados, txtGastosConfirmados;

    public VentanaDetallesAF(UserService service, int idActividad) {
        this.service = service;
        this.idActividad = idActividad;

        setTitle("Detalles de la Actividad");
        setBounds(150,150,900,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(10,10));
        setContentPane(contentPane);

        // Datos básicos
        JPanel pnDatos = new JPanel(new GridLayout(2,3,10,10));
        txtNombre = crearCampo(); txtPeriodo = crearCampo(); txtFecha = crearCampo();
        txtTotalPlazas = crearCampo(); txtPlazasDisponibles = crearCampo(); txtEstado = crearCampo();

        pnDatos.add(new JLabel("Nombre:")); pnDatos.add(txtNombre);
        pnDatos.add(new JLabel("Periodo inscripción:")); pnDatos.add(txtPeriodo);
        pnDatos.add(new JLabel("Fecha celebración:")); pnDatos.add(txtFecha);
        pnDatos.add(new JLabel("Total plazas:")); pnDatos.add(txtTotalPlazas);
        pnDatos.add(new JLabel("Plazas disponibles:")); pnDatos.add(txtPlazasDisponibles);
        pnDatos.add(new JLabel("Estado:")); pnDatos.add(txtEstado);

        contentPane.add(pnDatos, BorderLayout.NORTH);

        // Tabla inscripciones
        modelInscripciones = new DefaultTableModel(new Object[]{"Profesional","Fecha matrícula","Estado"},0);
        tableInscripciones = new JTable(modelInscripciones);
        contentPane.add(new JScrollPane(tableInscripciones), BorderLayout.CENTER);

        // Finanzas
        JPanel pnFinanzas = new JPanel(new GridLayout(2,2,10,10));
        txtIngresosEstimados = crearCampo(); txtIngresosConfirmados = crearCampo();
        txtGastosEstimados = crearCampo(); txtGastosConfirmados = crearCampo();

        pnFinanzas.add(new JLabel("Ingresos estimados:")); pnFinanzas.add(txtIngresosEstimados);
        pnFinanzas.add(new JLabel("Ingresos confirmados:")); pnFinanzas.add(txtIngresosConfirmados);
        pnFinanzas.add(new JLabel("Gastos estimados:")); pnFinanzas.add(txtGastosEstimados);
        pnFinanzas.add(new JLabel("Gastos confirmados:")); pnFinanzas.add(txtGastosConfirmados);

        contentPane.add(pnFinanzas, BorderLayout.SOUTH);

        cargarDetalles();
    }

    private JTextField crearCampo() { 
        JTextField f = new JTextField(); f.setEditable(false); return f; 
    }

    private void cargarDetalles() {
        Map<String,Object> act = service.getActividadDetalles(idActividad);
        if (act == null) return;

        txtNombre.setText((String) act.get("nombre"));
        txtPeriodo.setText(act.get("inicio_inscripcion") + " - " + act.get("fin_inscripcion"));
        txtFecha.setText((String) act.get("fecha"));
        txtEstado.setText((String) act.get("estado"));
        txtTotalPlazas.setText(String.valueOf(act.get("total_plazas")));
        txtPlazasDisponibles.setText(String.valueOf(act.get("plazas_disponibles")));

        modelInscripciones.setRowCount(0);
        @SuppressWarnings("unchecked")
		List<Map<String,Object>> inscripciones = (List<Map<String,Object>>) act.get("inscripciones");
        for(Map<String,Object> ins : inscripciones) {
            modelInscripciones.addRow(new Object[]{
                ins.get("nombre_alumno"),
                ins.get("fecha_matricula"),
                ins.get("estado")
            });
        }

        txtIngresosEstimados.setText(String.valueOf(act.get("ingresos_estimados")));
        txtIngresosConfirmados.setText(String.valueOf(act.get("ingresos_confirmados")));
        txtGastosEstimados.setText(String.valueOf(act.get("gastos_estimados")));
        txtGastosConfirmados.setText(String.valueOf(act.get("gastos_confirmados")));
    }
}
