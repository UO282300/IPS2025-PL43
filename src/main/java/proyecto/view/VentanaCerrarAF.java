package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import proyecto.service.UserService;

public class VentanaCerrarAF extends JFrame {

	private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JLabel lbTitulo;
    private JScrollPane scrollActividades;
    private JList<String> listActividades;
    private DefaultListModel<String> modeloActividades;
    private JPanel pnBotones;
    private JButton btCerrar;
    private JButton btVolver;

    private UserService service;
    private Map<String, Integer> mapaActividades;
	
	public VentanaCerrarAF(UserService service) {
		this.service = service;

        setTitle("Cerrar Actividades Formativas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel(new BorderLayout(0, 10));
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
            lbTitulo = new JLabel("GestiÃ³n de Cierre de Actividades", SwingConstants.CENTER);
            lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lbTitulo.setOpaque(true);
            lbTitulo.setBackground(new Color(200, 220, 255));
            lbTitulo.setBorder(new MatteBorder(2, 2, 2, 2, Color.BLACK));
            lbTitulo.setPreferredSize(new Dimension(100, 50));
        }
        return lbTitulo;
    }

    private JScrollPane getScrollActividades() {
        if (scrollActividades == null) {
            modeloActividades = new DefaultListModel<>();
            listActividades = new JList<>(modeloActividades);
            listActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listActividades.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            listActividades.setBackground(new Color(240, 245, 255));
            listActividades.setBorder(new EmptyBorder(10, 10, 10, 10));

            scrollActividades = new JScrollPane(listActividades);
            scrollActividades.setBorder(new TitledBorder("Actividades disponibles para cerrar"));
            scrollActividades.setBackground(new Color(240, 245, 255));
        }
        return scrollActividades;
    }

    private JPanel getPanelBotones() {
        if (pnBotones == null) {
            pnBotones = new JPanel();
            pnBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
            pnBotones.setBorder(new MatteBorder(2, 2, 2, 2, Color.BLACK));
            pnBotones.setBackground(new Color(200, 220, 255));

            btCerrar = new JButton("Cerrar Actividad");
            btCerrar.setBackground(new Color(255, 140, 80));
            btCerrar.addActionListener(e -> cerrarActividad());
            
                        btVolver = new JButton("Volver");
                        btVolver.setBackground(new Color(255, 180, 180));
                        btVolver.addActionListener(e -> dispose());
                        pnBotones.add(btVolver);

            pnBotones.add(btCerrar);
        }
        return pnBotones;
    }

    private void cargarActividades() {
        modeloActividades.clear();
        mapaActividades = new java.util.HashMap<>();

        List<Map<String, Object>> actividades = service.listarActividades();

        if (actividades == null || actividades.isEmpty()) {
            modeloActividades.addElement("No hay actividades disponibles para cerrar.");
            listActividades.setEnabled(false);
            btCerrar.setEnabled(false);
            return;
        }
 
        for (Map<String, Object> act : actividades) {
            int id = ((Number) act.get("id_actividad")).intValue();
            String nombre = (String) act.get("nombre");
            String fecha = (String) act.get("fecha");
            String estado = (String) act.get("estado");

            String texto = String.format("Nombre: %s | Fecha: %s | Estado: %s",
                    nombre, fecha, estado);

            if (estado.equals("Finalizada")) {
            	modeloActividades.addElement(texto);
                mapaActividades.put(texto, id);
            }         
        }

        listActividades.setEnabled(true);
        btCerrar.setEnabled(true);
    }

    private void cerrarActividad() {
        String seleccion = listActividades.getSelectedValue();

        if (seleccion == null || !mapaActividades.containsKey(seleccion)) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una actividad.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idActividad = mapaActividades.get(seleccion);

        if (service.actividadConMovimientosAlumnos(idActividad)) {
            JOptionPane.showMessageDialog(this,
                    "No se puede cerrar la actividad. Existen pagos pendientes de alumnos.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (service.actividadConMovimientosProfesores(idActividad)) {
            JOptionPane.showMessageDialog(this,
                    "No se puede cerrar la actividad. Existen pagos pendientes de profesores.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Estas seguro de que deseas cerrar esta actividad?",
                "Confirmar cierre", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            if (service.cerrarActividad(idActividad)) {
                JOptionPane.showMessageDialog(this,
                        "Actividad cerrada correctamente.");
                cargarActividades();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al cerrar la actividad.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
