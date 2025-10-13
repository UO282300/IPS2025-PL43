package proyecto.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import proyecto.service.UserService;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

public class VentanaEstadoAF extends JFrame {

    private static final long serialVersionUID = 1L;
    private UserService service;
    private JTable table;
    private DefaultTableModel model;

    public VentanaEstadoAF(UserService service) {
        this.service = service;
        setTitle("Estado de Actividades Formativas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 500);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10,10));
        setContentPane(contentPane);

        model = new DefaultTableModel(new Object[]{"ID","Nombre","Periodo inscripción","Fecha","Estado","Detalles"},0);
        table = new JTable(model) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) { return column == 5; }
        };
        table.setRowHeight(30);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        cargarActividades();
        @SuppressWarnings("unused")
		ButtonColumn buttonColumn = new ButtonColumn(table, 5);
    }

    private void cargarActividades() {
        model.setRowCount(0);
        List<Map<String,Object>> actividades = service.listarActividades();
        for(Map<String,Object> act : actividades) {
            String periodo = act.get("inicio_inscripcion") + " - " + act.get("fin_inscripcion");
            model.addRow(new Object[]{
                act.get("id_actividad"),
                act.get("nombre"),
                periodo,
                act.get("fecha"),
                act.get("estado"),
                "Detalles"
            });
        }
    }

    class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTable table;
        private JButton renderButton, editorButton;
        private int row;

        public ButtonColumn(JTable table, int column) {
            this.table = table;
            renderButton = new JButton("Detalles");
            editorButton = new JButton("Detalles");
            editorButton.addActionListener(this);
            table.getColumnModel().getColumn(column).setCellRenderer(this);
            table.getColumnModel().getColumn(column).setCellEditor(this);
        }

        @Override
        public Object getCellEditorValue() { return "Detalles"; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) { return renderButton; }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row; return editorButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int idActividad = (int) table.getValueAt(row,0);
            VentanaDetallesAF detalles = new VentanaDetallesAF(service,idActividad);
            detalles.setVisible(true);
        }
    }
}
