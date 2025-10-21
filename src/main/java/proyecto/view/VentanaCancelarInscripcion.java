package proyecto.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import proyecto.service.UserService;

public class VentanaCancelarInscripcion extends JFrame {

    private static final long serialVersionUID = 1L;
    private final UserService service;
    private JTable tableMatriculas;
    private DefaultTableModel modelMatriculas;

    public VentanaCancelarInscripcion(UserService service) {
        this.service = service;
        setTitle("Cancelar Inscripci�n de Actividades");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // === PANEL CENTRAL (TABLA) ===
        modelMatriculas = new DefaultTableModel(
                new Object[]{"ID", "Actividad", "Fecha", "Pagado (�)", "Acci�n"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // solo el bot�n
            }
        };

        tableMatriculas = new JTable(modelMatriculas);
        tableMatriculas.setRowHeight(30);

        // Renderizar bot�n real en la columna "Acci�n"
        tableMatriculas.getColumn("Acci�n").setCellRenderer(new ButtonRenderer());
        tableMatriculas.getColumn("Acci�n").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scroll = new JScrollPane(tableMatriculas);
        scroll.setBorder(new TitledBorder("Actividades inscritas"));
        add(scroll, BorderLayout.CENTER);

        // === Cargar inscripciones autom�ticamente ===
        cargarMatriculasAlumno();
    }

    /** Carga las matr�culas del alumno actual **/
    private void cargarMatriculasAlumno() {
        modelMatriculas.setRowCount(0);
        int idAlumno = service.getIdAlumnoActual();

        if (idAlumno == 0) {
            JOptionPane.showMessageDialog(this,
                    "No se ha seleccionado ning�n alumno en la ventana principal.",
                    "Alumno no seleccionado", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        List<Map<String, Object>> matriculas = service.listarMatriculasPorAlumno(idAlumno);
        if (matriculas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El alumno no tiene inscripciones activas.",
                    "Sin inscripciones", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Map<String, Object> m : matriculas) {
            modelMatriculas.addRow(new Object[]{
                    m.get("id_matricula"),
                    m.get("actividad"),
                    m.get("fecha"),
                    m.get("monto_pagado"),
                    "Cancelar"
            });
        }
    }

    /** L�gica de cancelaci�n **/
    public void cancelarInscripcion(int row) {
        int idAlumno = service.getIdAlumnoActual();
        int idMatricula = (int) modelMatriculas.getValueAt(row, 0);
        String nombreActividad = modelMatriculas.getValueAt(row, 1).toString();
        LocalDate fechaActividad = LocalDate.parse(modelMatriculas.getValueAt(row, 2).toString());
        double montoPagado = Double.parseDouble(modelMatriculas.getValueAt(row, 3).toString());

        double montoDevuelto = service.calcularMontoDevolucion(fechaActividad, montoPagado);

        String msg = "�Desea cancelar la inscripci�n a '" + nombreActividad + "'?\n" +
                     "Se devolver�n " + String.format("%.2f �", montoDevuelto) + ".";
        int opcion = JOptionPane.showConfirmDialog(this, msg, "Confirmar cancelaci�n", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            int idActividad = (int) service.obtenerIdActividadPorMatricula(idMatricula);

            service.registrarDevolucion(idMatricula, idAlumno, idActividad, montoDevuelto);
            JOptionPane.showMessageDialog(this, "Inscripci�n cancelada correctamente.\nDevoluci�n: " + montoDevuelto + " �.");
            cargarMatriculasAlumno();
        }
    }

    // === Renderizador para mostrar botones en JTable ===
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(220, 53, 69));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 12));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "Cancelar" : value.toString());
            return this;
        }
    }

    // === Editor para manejar clics en el bot�n ===
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int row;
        private VentanaCancelarInscripcion parent;

        public ButtonEditor(JCheckBox checkBox, VentanaCancelarInscripcion parent) {
            super(checkBox);
            this.parent = parent;
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(220, 53, 69));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.label = (value == null) ? "Cancelar" : value.toString();
            button.setText(label);
            this.row = row;
            this.clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                parent.cancelarInscripcion(row);
            }
            clicked = false;
            return label;
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
