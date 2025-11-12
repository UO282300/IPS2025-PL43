package proyecto.view;

import proyecto.service.RetrasarController;
import proyecto.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class VentanaRetrasarAF extends JFrame {

    private static final long serialVersionUID = 1L;

    private final RetrasarController controller;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;

    // Formulario de fechas
    private final JTextField tfFinInscripcion = new JTextField(10);
    private final JTextField tfFechaInicio = new JTextField(10);
    private final JTextField tfFechaFin = new JTextField(10);

    public VentanaRetrasarAF(UserService service) {
        this.controller = new RetrasarController(service);

        setTitle("Retrasar Actividades Formativas");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Tabla ---
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Inicio", "Fin", "Estado", "Acción"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // solo columna de acción editable
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(30);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0); // ocultar ID

        tabla.getColumn("Acción").setCellRenderer(new ButtonRenderer());
        tabla.getColumn("Acción").setCellEditor(new ButtonEditor(new JCheckBox(), this));
        getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

        JScrollPane scrollPane = new JScrollPane(tabla);
        getContentPane().add(scrollPane);

        // --- Panel de formulario ---
        JPanel panelFormulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Nuevas fechas"));
        panelFormulario.add(new JLabel("Fin Inscripción (yyyy-MM-dd):"));
        panelFormulario.add(tfFinInscripcion);
        panelFormulario.add(new JLabel("Fecha Inicio (yyyy-MM-dd):"));
        panelFormulario.add(tfFechaInicio);
        panelFormulario.add(new JLabel("Fecha Fin (yyyy-MM-dd):"));
        panelFormulario.add(tfFechaFin);

        getContentPane().add(panelFormulario);

        // Cargar actividades al iniciar
        cargarActividades();
    }

    public void cargarActividades() {
        modeloTabla.setRowCount(0);

        List<Map<String, Object>> actividades = controller.listarActividadesRetrasables();

        for (Map<String, Object> act : actividades) {
            int id = (int) act.get("id_actividad");
            String nombre = String.valueOf(act.get("nombre"));
            String inicio = String.valueOf(act.get("fecha_inicio"));
            String fin = String.valueOf(act.get("fecha_fin"));
            String estado = controller.obtenerEstadoActividad(act);

            modeloTabla.addRow(new Object[]{id, nombre, inicio, fin, estado, "Retrasar"});
        }
    }

    /** Lógica para retrasar una actividad */
    public void retrasarActividad(int idActividad) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas retrasar esta actividad?\n Los Campos no rellenos permaneceran igual",
                "Confirmar retraso",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

      
        LocalDate finInscripcion = parseFecha(tfFinInscripcion.getText());
        LocalDate fechaInicio = parseFecha(tfFechaInicio.getText());
        LocalDate fechaFin = parseFecha(tfFechaFin.getText());

        // Comprobamos si hay fechas no nulas pero mal formateadas
        boolean formatosIncorrectos = (!tfFinInscripcion.getText().trim().isEmpty() && finInscripcion == null)
                || (!tfFechaInicio.getText().trim().isEmpty() && fechaInicio == null)
                || (!tfFechaFin.getText().trim().isEmpty() && fechaFin == null);

        if (formatosIncorrectos) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hay campos con formato de fecha incorrecto. Corrige antes de retrasar.",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE
            );
            return; // No continuar con el retraso
        }

        controller.retrasarActividad(idActividad, finInscripcion, fechaInicio, fechaFin);

        JOptionPane.showMessageDialog(this, "Actividad retrasada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        cargarActividades();
    }


    private LocalDate parseFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(fechaStr.trim());
        } catch (Exception e) {
            return null;
        }
    }

    // === Renderizador del botón ===
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            setFont(new Font("Arial", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Retrasar" : value.toString());
            return this;
        }
    }

    // === Editor del botón (maneja clics) ===
    private static class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private final VentanaRetrasarAF parent;
        private int idActividad;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox, VentanaRetrasarAF parent) {
            super(checkBox);
            this.parent = parent;
            this.button = new JButton();
            button.setOpaque(true);
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            idActividad = (int) table.getValueAt(row, 0);
            button.setText((value == null) ? "Retrasar" : value.toString());
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                parent.retrasarActividad(idActividad);
            }
            clicked = false;
            return "Retrasar";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            try { super.fireEditingStopped(); } catch (Exception ignored) {}
        }
    }
}
