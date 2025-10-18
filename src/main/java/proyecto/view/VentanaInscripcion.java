package proyecto.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import proyecto.model.entity.Actividad;
import proyecto.service.UserService;
import java.awt.Color;
import java.awt.Dimension;

public class VentanaInscripcion extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panelFecha;
	private JPanel panelActividades;
	private JPanel panelFormulario;
	private JPanel pnActividades;
	private JLabel lbFecha;
	private JPanel pnBotones;
	private JButton btSelect;
	private JScrollPane scrollPane;
	private JList<Actividad> list;
	private JPanel pnPertenece;
	private JRadioButton rdbtNo;
	private JRadioButton rdbtEscuela;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btInscrip;
	private JPanel pnCorreo;
	private JLabel lbCorreo;
	private JTextField txCorreo;
	private JPanel pnNombre;
	private JLabel lbNombre;
	private JTextField txNombre;
	private JPanel lbApellidos;
	private JTextField txApellido;
	private JLabel txApellidos;
	private JPanel panel;
	private JTextField txTf;
	private JLabel lbNumeroTf;
	private JPanel panel_1;
	DefaultListModel<Actividad> modelo = new DefaultListModel<>();
	private UserService service;
	

	

	/**
	 * Create the frame.
	 * @param service2 
	 */
	public VentanaInscripcion(UserService service2) {
		setBackground(new Color(255, 128, 128));
		service = service2;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 915, 824);
		setMinimumSize(new Dimension(1500, 600));
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 128, 128));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(getPanelFecha(), BorderLayout.NORTH);
		contentPane.add(getPanelActividades(), BorderLayout.CENTER);
		contentPane.add(getPanelFormulario(), BorderLayout.SOUTH);
	}
	private JPanel getPanelFecha() {
		if (panelFecha == null) {
			panelFecha = new JPanel();
			panelFecha.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			panelFecha.add(getLbFecha());
		}
		return panelFecha;
	}
	private JPanel getPanelActividades() {
		if (panelActividades == null) {
			panelActividades = new JPanel();
			panelActividades.setLayout(new GridLayout(0, 1, 0, 0));
			panelActividades.add(getPnActividades());
		}
		return panelActividades;
	}
	private JPanel getPanelFormulario() {
		if (panelFormulario == null) {
			panelFormulario = new JPanel();
			panelFormulario.setBackground(new Color(128, 0, 0));
			panelFormulario.setLayout(new GridLayout(0, 6, 0, 0));
			panelFormulario.add(getPnNombre());
			panelFormulario.add(getLbApellidos());
			panelFormulario.add(getPnCorreo());
			panelFormulario.add(getPanel());
			panelFormulario.add(getPnPertenece());
			panelFormulario.add(getPanel_1());
		}
		return panelFormulario;
	}
	private JPanel getPnActividades() {
		if (pnActividades == null) {
			pnActividades = new JPanel();
			pnActividades.setLayout(new BorderLayout(0, 0));
			pnActividades.add(getPanel_2(), BorderLayout.SOUTH);
			pnActividades.add(getScrollPane(), BorderLayout.CENTER);
		}
		return pnActividades;
	}
	private JLabel getLbFecha() {
		if (lbFecha == null) {
			lbFecha = new JLabel("");
			lbFecha.setFont(new Font("Arial Black", Font.BOLD, 35));
		}
		return lbFecha;
	}
	private JPanel getPanel_2() {
		if (pnBotones == null) {
			pnBotones = new JPanel();
			pnBotones.setBackground(new Color(255, 128, 128));
			pnBotones.setLayout(new BorderLayout(0, 0));
			pnBotones.add(getBtSelect(), BorderLayout.EAST);
		}
		return pnBotones;
	}
	private JButton getBtSelect() {
		if (btSelect == null) {
			btSelect = new JButton("Seleccionar");
			btSelect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					seleccionActividad();
				}
			});
		}
		return btSelect;
	}
	protected void seleccionActividad() {
		Actividad selec = getList().getSelectedValue();
		if(selec!=null) {
			service.selectActividad(selec);
		}else {
			JOptionPane.showMessageDialog(
			        null, 
			        "No hay actividad seleccionada.\nPor favor selecciona actividad.", 
			        "Error", 
			        JOptionPane.ERROR_MESSAGE
			    );
		}
		
		
	}
	protected void cargarActividades() {
		modelo.removeAllElements();
		modelo.addAll(service.recuperarActividades());
		
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getList());
		}
		return scrollPane;
	}
	private JList<Actividad> getList() {
		if (list == null) {
			list = new JList<Actividad>(modelo);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setFixedCellHeight(30);
			cargarActividades();
		}
		return list;
	}
	private JPanel getPnPertenece() {
		if (pnPertenece == null) {
			pnPertenece = new JPanel();
			pnPertenece.setBackground(new Color(255, 128, 64));
			pnPertenece.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 30));
			pnPertenece.add(getRdbtEscuela());
			pnPertenece.add(getRdbtNo());
		}
		return pnPertenece;
	}
	private JRadioButton getRdbtNo() {
		if (rdbtNo == null) {
			rdbtNo = new JRadioButton("No pertenezco");
			buttonGroup.add(rdbtNo);
		}
		return rdbtNo;
	}
	private JRadioButton getRdbtEscuela() {
		if (rdbtEscuela == null) {
			rdbtEscuela = new JRadioButton("Pertenezco a la escuela");
			rdbtEscuela.setSelected(true);
			buttonGroup.add(rdbtEscuela);
		}
		return rdbtEscuela;
	}
	private JButton getBtInscrip() {
		if (btInscrip == null) {
			btInscrip = new JButton("Inscribir");
			btInscrip.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					comprobarData();
					
				}

				

				
			});
			btInscrip.setHorizontalAlignment(SwingConstants.LEADING);
		}
		return btInscrip;
	}
	
	protected void limpiarCampos() {
		getTxNombre().setText("");
		getTxApellido().setText("");
		getTxCorreo().setText("");
		getTxTf().setText("");
		
	}

	private void comprobarData() {
		if(service.getAct()==null) {
			JOptionPane.showMessageDialog(
			        null, 
			        "Actividad sin escoger.\nNo se ha realizado la inscripci�n.", 
			        "Error", 
			        JOptionPane.ERROR_MESSAGE
			    );
		
		}else if(!comprobaciones()) {
			JOptionPane.showMessageDialog(
			        null, 
			        "Campos sin rellenar.\nPor favor rellene los campos.", 
			        "Error", 
			        JOptionPane.ERROR_MESSAGE
			    );
		}else {
			
			guardarData();
			if (!CheckearData()) {
			    JOptionPane.showMessageDialog(
			        null, 
			        "Los datos ingresados no son correctos.\nNo se ha realizado la inscripci�n.", 
			        "Error", 
			        JOptionPane.ERROR_MESSAGE
			    );
			} else {
				updateData();
				cargarActividades();
				limpiarCampos();
			}
		}
		
		
		
	}
	

	private boolean comprobaciones() {
		return compruebaTexto(getTxNombre().getText()) && 
				compruebaTexto(getTxNombre().getText()) &&
				compruebaTexto(getTxNombre().getText()) && 
				compruebaTexto(getTxNombre().getText());
	}

	private boolean compruebaTexto(String text) {
		return text!=null && !text.isBlank();
	}

	private void updateData() {
		if(service.introduce()) {
			JOptionPane.showMessageDialog(
			        null,
			        "Inscripci�n realizada correctamente.\nSe ha enviado un correo con la informaci�n de pago.",
			        "Correo enviado",JOptionPane.INFORMATION_MESSAGE);
			
		}
		else {
			JOptionPane.showMessageDialog(
			        null, 
			        "NO se ha podido hacer la matricula.", 
			        "Error", 
			        JOptionPane.ERROR_MESSAGE
			    );
		}
		
	}

	private boolean CheckearData() {
		return service.checkear();
		
	}

	private void guardarData() {
		service.guardarNombre(getTxNombre().getText());
		service.guardarApellidos(getTxApellido().getText());
		service.guardarTf(getTxTf().getText());
		service.guardarCorreo(getTxCorreo().getText());
		service.guardarPertenece(!getRdbtNo().isSelected());
		
	}
	private JPanel getPnCorreo() {
		if (pnCorreo == null) {
			pnCorreo = new JPanel();
			pnCorreo.setBackground(new Color(255, 128, 64));
			pnCorreo.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 30));
			pnCorreo.add(getLbCorreo_1());
			pnCorreo.add(getTxCorreo());
		}
		return pnCorreo;
	}
	private JLabel getLbCorreo_1() {
		if (lbCorreo == null) {
			lbCorreo = new JLabel("Correo Electr\u00F3nico");
		}
		return lbCorreo;
	}
	private JTextField getTxCorreo() {
		if (txCorreo == null) {
			txCorreo = new JTextField();
			txCorreo.setColumns(10);
		}
		return txCorreo;
	}
	private JPanel getPnNombre() {
		if (pnNombre == null) {
			pnNombre = new JPanel();
			pnNombre.setBackground(new Color(255, 128, 64));
			pnNombre.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 30));
			pnNombre.add(getLbNombre_1());
			pnNombre.add(getTxNombre());
		}
		return pnNombre;
	}
	private JLabel getLbNombre_1() {
		if (lbNombre == null) {
			lbNombre = new JLabel("Nombre");
		}
		return lbNombre;
	}
	private JTextField getTxNombre() {
		if (txNombre == null) {
			txNombre = new JTextField();
			txNombre.setColumns(10);
		}
		return txNombre;
	}
	private JPanel getLbApellidos() {
		if (lbApellidos == null) {
			lbApellidos = new JPanel();
			lbApellidos.setBackground(new Color(255, 128, 64));
			lbApellidos.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 30));
			lbApellidos.add(getLbApellidos_1());
			lbApellidos.add(getTxApellido());
		}
		return lbApellidos;
	}
	private JTextField getTxApellido() {
		if (txApellido == null) {
			txApellido = new JTextField();
			txApellido.setColumns(10);
		}
		return txApellido;
	}
	private JLabel getLbApellidos_1() {
		if (txApellidos == null) {
			txApellidos = new JLabel("Apellidos");
		}
		return txApellidos;
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBackground(new Color(255, 128, 64));
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 30));
			panel.add(getLbNumeroTf_1());
			panel.add(getTxTf());
		}
		return panel;
	}
	private JTextField getTxTf() {
		if (txTf == null) {
			txTf = new JTextField();
			txTf.setColumns(10);
		}
		return txTf;
	}
	private JLabel getLbNumeroTf_1() {
		if (lbNumeroTf == null) {
			lbNumeroTf = new JLabel("Numero de Tel\u00E9fono");
		}
		return lbNumeroTf;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setBackground(new Color(255, 128, 64));
			FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
			flowLayout.setVgap(70);
			panel_1.add(getBtInscrip());
		}
		return panel_1;
	}

	
}

