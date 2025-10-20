package proyecto.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
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
	private JPanel pnBotones;
	private JPanel pnInfo;
	private JPanel pnTitulo;
	private JPanel pnAcciones;
	
	private JScrollPane scrollPane;
	private JList<String> listActividades;
	private DefaultListModel<String> modeloActividades;
	
	private JButton btCerrar;
	private JButton btCancelar;
	private JButton btRefrescar;
	
	private UserService service;
	private Map<String, Integer> mapaActividades; // para relacionar nombre mostrado con id real
	
	public VentanaCerrarAF(UserService service) {
	    this.service = service;
	
	    setTitle("Cerrar Actividades Formativas");
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setBounds(100, 100, 900, 700);
	    setLocationRelativeTo(null);
	
	    contentPane = new JPanel();
	    contentPane.setLayout(new GridLayout(1, 2, 0, 0));
	    setContentPane(contentPane);
	
	    contentPane.add(getPnBotones());
	    contentPane.add(getPnInfo());
	}
	
	private JPanel getPnBotones() {
	    if (pnBotones == null) {
	        pnBotones = new JPanel();
	        pnBotones.setLayout(new BorderLayout(0, 0));
	        pnBotones.add(getPnTitulo(), BorderLayout.NORTH);
	        pnBotones.add(getPnAcciones(), BorderLayout.CENTER);
	    }
	    return pnBotones;
	}
	
	private JPanel getPnTitulo() {
	    if (pnTitulo == null) {
	        pnTitulo = new JPanel();
	        pnTitulo.setBackground(new Color(128, 170, 255));
	        pnTitulo.setBorder(new MatteBorder(3, 3, 3, 3, Color.BLACK));
	        JLabel lbTitulo = new JLabel("Gestión de Cierre de Actividades", SwingConstants.CENTER);
	        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
	        pnTitulo.add(lbTitulo);
	    }
	    return pnTitulo;
	}
	
	private JPanel getPnAcciones() {
	    if (pnAcciones == null) {
	        pnAcciones = new JPanel();
	        pnAcciones.setBackground(new Color(200, 220, 255));
	        pnAcciones.setBorder(new MatteBorder(0, 3, 3, 3, Color.BLACK));
	        pnAcciones.setLayout(new GridLayout(3, 1, 20, 20));
	
	        btRefrescar = new JButton("Refrescar Lista");
	        btRefrescar.setBackground(new Color(100, 180, 255));
	        btRefrescar.addActionListener(e -> cargarActividades());
	
	        btCerrar = new JButton("Cerrar Actividad Seleccionada");
	        btCerrar.setBackground(new Color(255, 128, 64));
	        btCerrar.addActionListener(e -> cerrarActividad());
	
	        btCancelar = new JButton("Cancelar");
	        btCancelar.setBackground(new Color(255, 180, 180));
	        btCancelar.addActionListener(e -> dispose());
	
	        pnAcciones.add(btRefrescar);
	        pnAcciones.add(btCerrar);
	        pnAcciones.add(btCancelar);
	    }
	    return pnAcciones;
	}
	
	private JPanel getPnInfo() {
	    if (pnInfo == null) {
	        pnInfo = new JPanel();
	        pnInfo.setLayout(new BorderLayout(0, 0));
	        pnInfo.add(getScrollPane(), BorderLayout.CENTER);
	    }
	    return pnInfo;
	}
	
	private JScrollPane getScrollPane() {
	    if (scrollPane == null) {
	        scrollPane = new JScrollPane(getListActividades());
	        scrollPane.setBorder(new TitledBorder("Actividades disponibles para cerrar"));
	    }
	    return scrollPane;
	}
	
	private JList<String> getListActividades() {
	    if (listActividades == null) {
	        modeloActividades = new DefaultListModel<>();
	        listActividades = new JList<>(modeloActividades);
	        listActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        listActividades.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	        listActividades.setBackground(new Color(240, 245, 255));
	        listActividades.setBorder(new EmptyBorder(10, 10, 10, 10));
	        cargarActividades();
	    }
	    return listActividades;
	}
	
	/**
	 * Carga la lista de actividades que pueden cerrarse.
	 */
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
	        String profesor = (String) act.get("profesor");
	        String fechaFin = (String) act.get("fecha_fin");
	        String estado = (String) act.get("estado");
	
	        String texto = String.format("%s | %s | Fin: %s | Estado: %s",
	                nombre, profesor, fechaFin, estado);
	
	        modeloActividades.addElement(texto);
	        mapaActividades.put(texto, id);
	    }
	
	    listActividades.setEnabled(true);
	    btCerrar.setEnabled(true);
	}
	
	/**
	 * Intenta cerrar la actividad seleccionada, validando reglas.
	 */
	private void cerrarActividad() {
	    String seleccion = listActividades.getSelectedValue();
	
	    if (seleccion == null || !mapaActividades.containsKey(seleccion)) {
	        JOptionPane.showMessageDialog(this, "Selecciona una actividad válida.", "Aviso", JOptionPane.WARNING_MESSAGE);
	        return;
	    }
	
	    int idActividad = mapaActividades.get(seleccion);
	
	    if (service.tieneMovimientosPendientes(idActividad)) {
	        JOptionPane.showMessageDialog(this,
	                "No se puede cerrar la actividad.\nExisten pagos o devoluciones pendientes.",
	                "Aviso", JOptionPane.WARNING_MESSAGE);
	        return;
	    }
	
	    if (!service.fechaFinalizacionSuperada(idActividad)) {
	        JOptionPane.showMessageDialog(this,
	                "No se puede cerrar la actividad antes de su fecha de finalización.",
	                "Aviso", JOptionPane.WARNING_MESSAGE);
	        return;
	    }
	
	    int opcion = JOptionPane.showConfirmDialog(this,
	            "¿Estás seguro de que deseas cerrar esta actividad?",
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