package proyecto.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import proyecto.service.CancelarController;
import proyecto.service.UserService;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class VentanaDevolucionesPendientes extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CancelarController controller;

    private JPanel contentPane;
    private JTable tabla;
    private DefaultTableModel modelo;

    public VentanaDevolucionesPendientes(UserService service) {
        this.controller = new CancelarController(service);

        setTitle("Devoluciones Pendientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 700, 450);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);

        initUI();
        cargarDatos();
    }

    private void initUI() {

        JLabel lblTitulo = new JLabel("Devoluciones pendientes", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{
                "Nombre",
                "Apellido",
                "Acción Formativa",
                "Pendiente (euros)"
        }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(tabla);
        contentPane.add(scroll, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        modelo.setRowCount(0);

        List<Map<String, Object>> devoluciones = controller.listarDevolucionesPendientes();

        for (Map<String, Object> d : devoluciones) {

            double pendiente = d.get("pendiente") != null ? ((Number) d.get("pendiente")).doubleValue() : 0.0;

            if (pendiente <= 0.01) continue;

            modelo.addRow(new Object[]{
                    d.get("nombre"),
                    d.get("apellido"),
                    d.get("actividad"),
                    String.format("%.2f", pendiente)
            });
        }
    }
    
    
}
