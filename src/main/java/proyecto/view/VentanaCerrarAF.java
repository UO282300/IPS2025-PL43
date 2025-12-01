package proyecto.view;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
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
    private JPanel panelActividadesFinalizadas;

    private UserService service;

    public VentanaCerrarAF(UserService service) {
        this.service = service;

        setTitle("Cerrar Actividades Formativas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1800, 1000);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBackground(new Color(230, 240, 255));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        contentPane.add(getTituloPanel(), BorderLayout.NORTH);
        
        panelActividadesFinalizadas = new JPanel(new BorderLayout());
        panelActividadesFinalizadas.add(getScrollActividades(), BorderLayout.CENTER);

        JPanel pnCentral = new JPanel(new GridLayout(1, 1, 0, 10));
        pnCentral.setBackground(new Color(230, 240, 255));
        pnCentral.add(panelActividadesFinalizadas);
        contentPane.add(pnCentral, BorderLayout.CENTER);
        
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
            String[] columnas = {"Nombre", "Inicio inscripcion", "Fin inscripcion", "Fecha inicio", "Fecha fin", "Gastos", "Estado"};
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
            tablaActividades.setRowHeight(40);
            tablaActividades.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

            scrollActividades = new JScrollPane(tablaActividades);
            scrollActividades.setBorder(new TitledBorder("Actividades finalizadas o canceladas:"));
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
            if (estado.equalsIgnoreCase("finalizada") || estado.equalsIgnoreCase("cancelada")) {
                String nombre = (String) act.get("nombre");
                String insI = String.valueOf(act.get("inicio_inscripcion"));
                String insF = String.valueOf(act.get("fin_inscripcion"));
                String fechaI = String.valueOf(act.get("fecha_inicio"));
                String fechaF = String.valueOf(act.get("fecha_fin"));
                int idActividad = ((Number) act.get("id_actividad")).intValue();
                String resumenGastos = obtenerResumenGastos(idActividad);
                modeloTabla.addRow(new Object[]{nombre, insI, insF, fechaI, fechaF, resumenGastos, estado});
            }
        }

        if (modeloTabla.getRowCount() == 0) {
            mostrarMensajeSinActividades(panelActividadesFinalizadas, "No hay actividades disponibles para cerrar.");
            btCerrar.setEnabled(false);
        } else {
            panelActividadesFinalizadas.removeAll();
            panelActividadesFinalizadas.add(scrollActividades, BorderLayout.CENTER);
            panelActividadesFinalizadas.revalidate();
            panelActividadesFinalizadas.repaint();
            btCerrar.setEnabled(true);
        }
    }
    
    private String obtenerResumenGastos(int id) {
        try {
            boolean pagosAlumno = service.actividadConMovimientosAlumnos(id);
            boolean pagosProfesor = service.actividadConMovimientosProfesores(id);

            StringBuilder sb = new StringBuilder("<html>");

            if (pagosAlumno)
                sb.append("- Pagos de alumnos pendientes<br>");

            if (pagosProfesor)
                sb.append("- Pagos de profesores pendientes<br>");

            if (!pagosAlumno && !pagosProfesor)
                sb.append("Todo pagado");

            sb.append("</html>");

            return sb.toString();

        } catch (Exception ex) {
            return "<html>Error al comprobar</html>";
        }
    }

	private void mostrarMensajeSinActividades(JPanel contenedor, String mensaje) {
        contenedor.removeAll();
        
        JPanel panelMensaje = new JPanel(new GridBagLayout());
        panelMensaje.setBackground(new Color(240, 245, 255));
        
        JLabel label = new JLabel(mensaje);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelMensaje.add(label);
        contenedor.add(panelMensaje, BorderLayout.CENTER);
        
        contenedor.revalidate();
        contenedor.repaint();
    }

    private void cerrarActividad() {
        int filaSeleccionada = tablaActividades.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una actividad.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreSeleccionado = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        List<Map<String, Object>> actividades = service.listarActividades();
        int idActividad = -1;
        for (Map<String, Object> act : actividades) {
            if (act.get("nombre").equals(nombreSeleccionado)) {
                idActividad = ((Number) act.get("id_actividad")).intValue();
                break;
            }
        }

        boolean pagosPendientesAlumnos = service.actividadConMovimientosAlumnos(idActividad);
        boolean pagosPendientesProfes = service.actividadConMovimientosProfesores(idActividad);

        if (pagosPendientesAlumnos || pagosPendientesProfes) {
            String mensaje = "<html>Esta actividad tiene pagos pendientes:<br><br>";
            if (pagosPendientesAlumnos) mensaje += "- Alumnos con pagos pendientes<br>";
            if (pagosPendientesProfes) mensaje += "- Profesores con pagos pendientes<br>";
            mensaje += "<br>¿Esta seguro de que desea cerrarla igualmente?</html>";

            int opcionForzada = JOptionPane.showConfirmDialog(
                    this,
                    mensaje,
                    "Confirmar cierre con pagos pendientes",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcionForzada != JOptionPane.YES_OPTION) {
                return;
            }

        } else {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Estas seguro de que deseas cerrar esta actividad?",
                    "Confirmar cierre",
                    JOptionPane.YES_NO_OPTION);

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        if (service.cerrarActividad(idActividad)) {
            JOptionPane.showMessageDialog(this, "Actividad cerrada correctamente.");
            cargarActividades();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al cerrar la actividad.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        cargarActividades();
    }
}
