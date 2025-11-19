package proyecto.view;

import proyecto.service.CancelarController;
import proyecto.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class VentanaCancelarAF extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CancelarController controller;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;

    public VentanaCancelarAF(UserService service) {
        this.controller = new CancelarController(service);

        setTitle("Cancelar Actividades Formativas");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Título ---
        JLabel lblTitulo = new JLabel("Actividades que pueden cancelarse", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Tabla ---
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Inicio", "Fin", "Estado", "Acción"}, 0
        ) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // solo la columna de acción es editable
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(30);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0); // ocultar ID

        // Asignar renderizador y editor
        tabla.getColumn("Acción").setCellRenderer(new ButtonRenderer());
        tabla.getColumn("Acción").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);

        // Cargar datos al iniciar
        cargarActividades();
    }

    /** Carga las actividades que se pueden cancelar en la tabla */
    public void cargarActividades() {
        modeloTabla.setRowCount(0);

        List<Map<String, Object>> actividades = controller.listarActividadesCancelables();

        for (Map<String, Object> act : actividades) {
            int id = (int) act.get("id_actividad");
            String nombre = String.valueOf(act.get("nombre"));
            String inicio = String.valueOf(act.get("fecha_inicio"));
            String fin = String.valueOf(act.get("fecha_fin"));
            String estado = controller.obtenerEstadoActividad(act);

            modeloTabla.addRow(new Object[]{id, nombre, inicio, fin, estado, "Cancelar"});
        }
    }

    /** Lógica para cancelar una actividad concreta */
    public void cancelarActividad(int idActividad) {
        controller.cancelarActividad(idActividad);
        JOptionPane.showMessageDialog(
                this,
                "Actividad cancelada correctamente.",
                "Exito",
                JOptionPane.INFORMATION_MESSAGE
        );

        //  Recargar la tabla tras cancelar
        SwingUtilities.invokeLater(this::cargarActividades);
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
            setText((value == null) ? "Cancelar" : value.toString());
            return this;
        }
    }

    // === Editor del boton (maneja clics) ===
    private static class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private final VentanaCancelarAF parent;
        private int idActividad;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox, VentanaCancelarAF parent) {
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
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            idActividad = (int) table.getValueAt(row, 0);
            button.setText((value == null) ? "Cancelar" : value.toString());
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int confirm = JOptionPane.showConfirmDialog(
                        parent,
                        "Seguro que deseas cancelar esta actividad?",
                        "Confirmar cancelacion",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    parent.cancelarActividad(idActividad);
                }
            }
            clicked = false;
            return "Cancelar";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            try {
                super.fireEditingStopped();
            } catch (Exception e) {
                // evita error al recargar la tabla mientras se cierra la edición
            }
        }
    }
}
