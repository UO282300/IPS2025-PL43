package proyecto.view;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import proyecto.service.UserService;

public class VentanaCerrarAF extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JLabel lbTitulo;
    private JScrollPane scrollActividades;
    private JTable tablaActividades;
    private DefaultTableModel modeloTabla;
    private JPanel pnBotones;
    private JButton btCerrar;
    private JButton btVolver;

    private UserService service;

    public VentanaCerrarAF(UserService service) {
        this.service = service;

        setTitle("Cerrar Actividades Formativas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBackground(new Color(230, 240, 255));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        contentPane.add(getTituloPanel(), BorderLayout.NORTH);
        contentPane.add(getScrollActividades(), BorderLayout.CENTER);
        contentPane.add(getPanelBotones(), BorderLayout.SOUTH);

        cargarActividades();
    }

    private JLabel getTituloPanel() {
        if (lbTitulo == null) {
            lbTitulo = new JLabel("Gestion de Cierre de Actividades", SwingConstants.CENTER);
            lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lbTitulo.setForeground(new Color(30, 50, 90));
            lbTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
            lbTitulo.setOpaque(false);
        }
        return lbTitulo;
    }

    private JScrollPane getScrollActividades() {
        if (scrollActividades == null) {
            String[] columnas = {"Nombre", "Fecha inicio", "Fecha fin", "Estado"};
            modeloTabla = new DefaultTableModel(columnas, 0) {
                private static final long serialVersionUID = 1L;
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            tablaActividades = new JTable(modeloTabla);
            tablaActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tablaActividades.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tablaActividades.setRowHeight(26);
            tablaActividades.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

            // Centrar estado
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            tablaActividades.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

            scrollActividades = new JScrollPane(tablaActividades);
            scrollActividades.setBorder(new TitledBorder("Actividades finalizadas:"));
        }
        return scrollActividades;
    }

    private JPanel getPanelBotones() {
        if (pnBotones == null) {
            pnBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
            pnBotones.setBackground(new Color(230, 235, 250));

            btVolver = new JButton("Volver");
            btVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btVolver.addActionListener(e -> dispose());

            btCerrar = new JButton("Cerrar Actividad");
            btCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btCerrar.setBackground(new Color(220, 220, 220));
            btCerrar.addActionListener(e -> cerrarActividad());

            pnBotones.add(btVolver);
            pnBotones.add(btCerrar);
        }
        return pnBotones;
    }

    private void cargarActividades() {
        modeloTabla.setRowCount(0);

        List<Map<String, Object>> actividades = service.listarActividades();

        for (Map<String, Object> act : actividades) {
            String estado = String.valueOf(act.get("estado"));
            if (estado.equalsIgnoreCase("finalizada")) {
                String nombre = (String) act.get("nombre");
                String fechaI = String.valueOf(act.get("fecha_inicio"));
                String fechaF = String.valueOf(act.get("fecha_fin"));
                modeloTabla.addRow(new Object[]{nombre, fechaI, fechaF, estado});
            }
        }
        
        if (modeloTabla.getRowCount() == 0) {
        	mostrarMensajeSinActividades("No hay actividades disponibles para cerrar.");
            btCerrar.setEnabled(false);
        } else {
            btCerrar.setEnabled(true);
        }
        
    }
    
    private void mostrarMensajeSinActividades(String mensaje) {
        // Reemplaza el contenido central por un panel de aviso bonito
        JPanel panelMensaje = new JPanel(new GridBagLayout());
        panelMensaje.setBackground(new Color(240, 245, 255));
        
        JLabel label = new JLabel(mensaje);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelMensaje.add(label);
        getContentPane().remove(scrollActividades);
        getContentPane().add(panelMensaje, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void cerrarActividad() {
        int filaSeleccionada = tablaActividades.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una actividad.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtenemos el id de la actividad real a partir del nombre
        String nombreSeleccionado = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        List<Map<String, Object>> actividades = service.listarActividades();
        int idActividad = -1;
        for (Map<String, Object> act : actividades) {
            if (act.get("nombre").equals(nombreSeleccionado)) {
                idActividad = ((Number) act.get("id_actividad")).intValue();
                break;
            }
        }

        if (idActividad == -1) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener el ID de la actividad seleccionada.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (service.actividadConMovimientosAlumnos(idActividad)) {
            JOptionPane.showMessageDialog(this,
                    "No se puede cerrar la actividad. Existen pagos pendientes de alumnos.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!service.actividadConMovimientosProfesores(idActividad)) {
            JOptionPane.showMessageDialog(this,
                    "No se puede cerrar la actividad. Existen pagos pendientes de profesores.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "Estas seguro de que deseas cerrar esta actividad?",
                "Confirmar cierre", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            if (service.cerrarActividad(idActividad)) {
                JOptionPane.showMessageDialog(this, "Actividad cerrada correctamente.");
                cargarActividades();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al cerrar la actividad.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
