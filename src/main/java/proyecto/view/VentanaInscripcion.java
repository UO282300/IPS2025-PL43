package proyecto.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import proyecto.model.entity.Actividad;
import proyecto.model.entity.Alumno;
import proyecto.service.UserService;
import proyecto.util.MensajeError;

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
	private JPanel pnCuotas;
	private JComboBox<CuotaItem> cbCuotas;
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
	private JTable tablaActividades;
	private DefaultTableModel modeloActividades;
	private JPanel pnTipoInscripcion;
	private JRadioButton rdbtIndividual;
	private JRadioButton rdbtGrupo;
	private final ButtonGroup buttonGroupTipo = new ButtonGroup();
	private JTextField txNumeroPersonas;
	private JLabel lbNumeroPersonas;
	private JLabel lbPlazasDisponibles;
	private JPanel pnGestionGrupo;
	private JButton btnAnadirPersona;
	private JButton btnQuitarPersona;
	private JList<String> listIntegrantes;
	private DefaultListModel<String> modeloIntegrantes;
	private JLabel lbContadorGrupo;
	private JButton btnInscribirGrupo;
	
	private JTable tablaIntegrantes;
	private DefaultTableModel modeloTablaIntegrantes;
	
	private List<Alumno> integrantesTemporales = new ArrayList<>();

	/**
	 * Create the frame.
	 * 
	 * @param service2
	 */
	public VentanaInscripcion(UserService service2) {
	    setTitle("Inscripcion Alumnos");
	    setBackground(new Color(255, 128, 128));
	    service = service2;
	    modeloIntegrantes = new DefaultListModel<>();
	    listIntegrantes = new JList<>(modeloIntegrantes);
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setBounds(100, 100, 915, 824);
	    setMinimumSize(new Dimension(1500, 600));
	    contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    
	    setContentPane(contentPane);
	    contentPane.setLayout(new BorderLayout(0, 0));
	    contentPane.add(getPanelFecha(), BorderLayout.NORTH);
	    
	    JPanel panelCentral = new JPanel(new BorderLayout());
	    panelCentral.add(getPanelTipoInscripcion(), BorderLayout.NORTH);
	    panelCentral.add(getPanelGestionGrupo(), BorderLayout.CENTER);
	    panelCentral.add(getPanelActividades(), BorderLayout.SOUTH);
	    
	    contentPane.add(panelCentral, BorderLayout.CENTER);
	    contentPane.add(getPanelFormulario(), BorderLayout.SOUTH);
	    
	    getRdbtIndividual().setSelected(true);
	    getPanelGestionGrupo().setVisible(false);
	    getBtnInscribirGrupo().setVisible(false);
	    
	    cargarElementosFormulario();
	}

	private void cargarElementosFormulario() {
		Alumno a = service.getInformacionAlumno();
		if (a != null) {
			getTxNombre().setText(a.getNombre());
			getTxApellido().setText(a.getApellido());
			getTxCorreo().setText(a.getCorreo());
			getTxTf().setText(a.getTelefono());
//			if (a.pertenece()) {
//				getRdbtEscuela().setSelected(true);
//			} else {
//				getRdbtNo().setSelected(true);
//			}
			desactivarFormulario();
		} else {
			limpiarCampos();
		}

	}

	private void desactivarFormulario() {
		getTxNombre().setEditable(false);
		getTxApellido().setEditable(false);
		getTxCorreo().setEditable(false);
		getTxTf().setEditable(false);
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
			//panelFormulario.setBackground(new Color(128, 0, 0));
			panelFormulario.setLayout(new GridLayout(0, 6, 0, 0));
			panelFormulario.add(getPnNombre());
			panelFormulario.add(getLbApellidos());
			panelFormulario.add(getPnCorreo());
			panelFormulario.add(getPanel());
			panelFormulario.add(getPnCuotas());
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
			//pnBotones.setBackground(new Color(255, 128, 128));
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
	
	private JLabel getLbPlazasDisponibles() {
	    if (lbPlazasDisponibles == null) {
	        lbPlazasDisponibles = new JLabel("Seleccione una actividad");
	        lbPlazasDisponibles.setForeground(Color.BLUE);
	        lbPlazasDisponibles.setFont(new Font("Arial", Font.BOLD, 12));
	    }
	    return lbPlazasDisponibles;
	}

	protected void seleccionActividad() {
	    Actividad selec = getActividadSeleccionada();
	    if (selec != null) {
	        service.selectActividad(selec);

	        cargarCuotasActividad(selec.getId_Actividad()); // NUEVO MÉTODO
	    } else {
	        JOptionPane.showMessageDialog(null, "No hay actividad seleccionada. Por favor selecciona actividad.",
	                "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	private void cargarCuotasActividad(int idActividad) {
	    cbCuotas.removeAllItems();
	    List<Map<String, Object>> cuotas = service.listarCuotasPorActividad(idActividad);

	    if (cuotas.isEmpty()) {
	        cbCuotas.addItem(new CuotaItem(-1, "No hay cuotas disponibles"));
	        cbCuotas.setEnabled(false);
	    } else {
	        cbCuotas.setEnabled(true);
	        for (Map<String, Object> cuota : cuotas) {
	            int id = ((Number) cuota.get("id_cuota_actividad")).intValue(); // 👈 asegúrate que el Map tenga esa clave
	            String categoria = (String) cuota.get("categoria");
	            double valor = ((Number) cuota.get("valor")).doubleValue();

	            String descripcion = categoria + " - " + valor + "€";
	            cbCuotas.addItem(new CuotaItem(id, descripcion)); // 👈 añadimos el objeto completo
	        }

	        actualizarInfoPlazas();
	        actualizarEstadoBotonInscribir();
	    }
	}
	
	
	private void actualizarInfoPlazas() {
	    if (service.getAct() != null) {
	        int plazasDisponibles = service.obtenerPlazasDisponibles(service.getAct().getId_Actividad());
	        
	        // Actualizar el label de plazas disponibles si existe
	        if (getLbPlazasDisponibles() != null) {
	            getLbPlazasDisponibles().setText("Plazas disponibles: " + plazasDisponibles);
	        }
	        
	        // También actualizar el tooltip del botón de inscripción individual
	        if (getBtInscrip() != null) {
	            getBtInscrip().setToolTipText("Plazas disponibles: " + plazasDisponibles);
	        }
	        
	        // Actualizar estado de los botones
	        actualizarEstadoBotonInscribir();
	    } else {
	        if (getLbPlazasDisponibles() != null) {
	            getLbPlazasDisponibles().setText("Seleccione una actividad");

	        }
	    }
	}
	
	private Actividad getActividadSeleccionada() {
	    int fila = getTablaActividades().getSelectedRow();
	    if (fila == -1) return null;

	    return service.getActividad(fila);
	}
	
	private JTable getTablaActividades() {
	    if (tablaActividades == null) {
	        modeloActividades = new DefaultTableModel(
	            new Object[]{"Nombre","Descripcion","Periodo inscripciï¿½n","Fechas","Precio"}, 0
	        ) {
	            private static final long serialVersionUID = 1L;

	            @Override
	            public boolean isCellEditable(int row, int column) {
	                return false;
	            }
	        };

	        tablaActividades = new JTable(modeloActividades);
	        tablaActividades.setRowHeight(25);
	        tablaActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        cargarActividades();
	    }
	    return tablaActividades;
	}

	
	protected void cargarActividades() {
	    modeloActividades.setRowCount(0);

	    List<Actividad> actividades = service.recuperarActividades();
	    for (Actividad act : actividades) {
	        String periodo = act.getInicio_insc() + " a " + act.getFin_inscr();
	        String fechas = act.getFechaInicio() + " a " + act.getFechaFin();

	        
	        String rangoCuotas = service.obtenerRangoCuotasPorActividad(act.getId_Actividad());

	        modeloActividades.addRow(new Object[]{
	            act.getNombre(),
	            act.getObjetivos(),
	            periodo,
	            fechas,
	            rangoCuotas
	        });
	    }

	    actualizarInfoPlazas();
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTablaActividades());
		}
		return scrollPane;
	}


	private JPanel getPnCuotas() {
	    if (pnCuotas == null) {
	    	pnCuotas = new JPanel();
	    	pnCuotas.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 30));
	        
	        cbCuotas = new JComboBox<>();
	        
	        pnCuotas.add(cbCuotas);
	    }
	    return pnCuotas;
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
		getTxNombre().setEditable(true);
		getTxApellido().setText("");
		getTxApellido().setEditable(true);
		getTxCorreo().setText("");
		getTxCorreo().setEditable(true);
		getTxTf().setText("");
		getTxTf().setEditable(true);
		

	}

	private void comprobarData() {
	    if (getRdbtGrupo().isSelected()) {
	        JOptionPane.showMessageDialog(this, 
	            "Para inscripción grupal use el botón 'Inscribir Grupo'", 
	            "Información", JOptionPane.INFORMATION_MESSAGE);
	        return;
	    }
	    
	    if (!validarActividadSeleccionada()) {
	        return;
	    }
	    
	    if (!validarCamposFormulario()) {
	        return;
	    }
	    Integer id = getCuotaSeleccionada();
	    if (id == null || id == -1) {
	        JOptionPane.showMessageDialog(this, "Seleccione una cuota válida", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    guardarData(String.valueOf(id));
	    if (!validarDatosAlumno()) {
	        return;
	    }
	    
	    updateData();
	    cargarActividades();
	    cargarElementosFormulario();
	}
	
	
	
	
	
	
	private boolean compruebaTexto(String text) {
		return text != null && !text.isBlank();
	}

	
	
	private void updateData() {
		MensajeError msj = new MensajeError();
		msj.setMensaje("NO se ha podido hacer la matricula.");
		if (service.introduce(msj)) {
			JOptionPane.showMessageDialog(null,
					"Inscripcion realizada correctamente. Se debe pagar en un plazo de 48 horas.",
					"Inscripcion Realizada", JOptionPane.INFORMATION_MESSAGE);

		} else {
			JOptionPane.showMessageDialog(null, msj.getMensaje(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}
	
	public Integer getCuotaSeleccionada() {
	    if (cbCuotas != null && cbCuotas.getSelectedItem() instanceof CuotaItem item) {
	        return item.getId();
	    }
	    return null;
	}
	
	private void guardarData(String id_c) {

		service.guardarNombre(getTxNombre().getText());
		service.guardarApellidos(getTxApellido().getText());
		service.guardarTf(getTxTf().getText());
		service.guardarCorreo(getTxCorreo().getText());
		//service.guardarPertenece(!getRdbtNo().isSelected());
		service.guardarIdCuotaA(id_c);
	    
	    boolean esIndividual = getRdbtIndividual().isSelected();
	    int numPersonas = getPlazasSolicitadas();
	    
	    service.guardarTipoInscripcion(esIndividual, numPersonas);
	}

	private JPanel getPnCorreo() {
		if (pnCorreo == null) {
			pnCorreo = new JPanel();
			pnCorreo.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 30));
			pnCorreo.add(getLbCorreo_1());
			pnCorreo.add(getTxCorreo());
		}
		return pnCorreo;
	}

	private JLabel getLbCorreo_1() {
		if (lbCorreo == null) {
			lbCorreo = new JLabel("Correo Electronico");
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
			//pnNombre.setBackground(new Color(255, 255, 255));
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
			//lbApellidos.setBackground(new Color(255, 128, 64));
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
			//panel.setBackground(new Color(255, 128, 64));
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
			lbNumeroTf = new JLabel("Numero de Telefono");
		}
		return lbNumeroTf;
	}

	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
			flowLayout.setVgap(70);
			panel_1.add(getBtInscrip());
		}
		return panel_1;
	}
	
	private JPanel getPanelTipoInscripcion() {
	    if (pnTipoInscripcion == null) {
	        pnTipoInscripcion = new JPanel();
	        pnTipoInscripcion.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
	        
	        pnTipoInscripcion.add(getRdbtIndividual());
	        pnTipoInscripcion.add(getRdbtGrupo());
	        pnTipoInscripcion.add(getLbNumeroPersonas());
	        pnTipoInscripcion.add(getTxNumeroPersonas());
	        pnTipoInscripcion.add(getLbPlazasDisponibles());
	        
	        getRdbtIndividual().setSelected(true);
	        getTxNumeroPersonas().setEnabled(false);
	    }
	    return pnTipoInscripcion;
	}

	private JRadioButton getRdbtIndividual() {
	    if (rdbtIndividual == null) {
	        rdbtIndividual = new JRadioButton("Inscripción Individual");
	        rdbtIndividual.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                getTxNumeroPersonas().setEnabled(false);
	                getTxNumeroPersonas().setText("1");
	                getPanelGestionGrupo().setVisible(false);
	                
	                // MOSTRAR componentes individuales, OCULTAR grupales
	                getBtInscrip().setVisible(true);
	                getBtnInscribirGrupo().setVisible(false);
	                getPanelFormulario().setVisible(true);
	                
	                actualizarEstadoBotonInscribir();
	            }
	        });
	        buttonGroupTipo.add(rdbtIndividual);
	    }
	    return rdbtIndividual;
	}

	private JRadioButton getRdbtGrupo() {
	    if (rdbtGrupo == null) {
	        rdbtGrupo = new JRadioButton("Inscripción Grupal");
	        rdbtGrupo.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	               
	                comprobarGrupo();
	            }
	        });
	        buttonGroupTipo.add(rdbtGrupo);
	    }
	    return rdbtGrupo;
	}

	private void comprobarGrupo() {
	    String numPersonasStr = JOptionPane.showInputDialog(
	        this, 
	        "¿Cuántas personas desea inscribir en el grupo?",
	        "Número de Personas",
	        JOptionPane.QUESTION_MESSAGE
	    );
	    
	    if (numPersonasStr == null) {
	        getRdbtIndividual().setSelected(true);
	        return;
	    }
	    
	    try {
	        int numPersonas = Integer.parseInt(numPersonasStr.trim());
	        if (numPersonas < 2) {
	            JOptionPane.showMessageDialog(this, 
	                "Para inscripción grupal debe haber al menos 2 personas.", 
	                "Error", JOptionPane.ERROR_MESSAGE);
	            getRdbtIndividual().setSelected(true);
	            return;
	        }
	        
	        getTxNumeroPersonas().setText(String.valueOf(numPersonas));
	        getTxNumeroPersonas().setEnabled(true);
	        getPanelGestionGrupo().setVisible(true);
	        
	        // MOSTRAR/OCULTAR componentes
	        getBtInscrip().setVisible(false);
	        getBtnInscribirGrupo().setVisible(true);
	        getPanelFormulario().setVisible(true); // Mantener visible para el responsable
	        
	        // Inicializar lista (sin el responsable)
	        
	        actualizarEstadoBotonInscribir();
	        
	    } catch (NumberFormatException ex) {
	        JOptionPane.showMessageDialog(this, 
	            "Debe ingresar un número válido.", 
	            "Error", JOptionPane.ERROR_MESSAGE);
	        getRdbtIndividual().setSelected(true);
	    }
	}

	private JTextField getTxNumeroPersonas() {
	    if (txNumeroPersonas == null) {
	        txNumeroPersonas = new JTextField();
	        txNumeroPersonas.setColumns(5);
	        txNumeroPersonas.setText("1");
	    }
	    return txNumeroPersonas;
	}

	private JLabel getLbNumeroPersonas() {
	    if (lbNumeroPersonas == null) {
	        lbNumeroPersonas = new JLabel("Nº Personas:");
	    }
	    return lbNumeroPersonas;
	}
	
	private boolean validarActividadSeleccionada() {
	    if (service.getAct() == null) {
	        JOptionPane.showMessageDialog(null, 
	            "Actividad sin escoger. Pulse el botón seleccionar.", 
	            "Error", JOptionPane.ERROR_MESSAGE);
	        return false;
	    }
	    return true;
	}

	private int getPlazasSolicitadas() {
	    if (getRdbtIndividual().isSelected()) {
	        return 1;
	    } else {
	        try {
	            return Integer.parseInt(getTxNumeroPersonas().getText());
	        } catch (NumberFormatException e) {
	            return 0;
	        }
	    }
	}

	private boolean validarCamposFormulario() {
	    if (!compruebaTexto(getTxApellido().getText())) {
	        mostrarErrorCampo("apellido");
	        return false;
	    } 
	    if (!compruebaTexto(getTxNombre().getText())) {
	        mostrarErrorCampo("Nombre");
	        return false;
	    } 
	    if (!compruebaTexto(getTxCorreo().getText())) {
	        mostrarErrorCampo("email");
	        return false;
	    } 
	    if (!compruebaTexto(getTxTf().getText())) {
	        mostrarErrorCampo("teléfono");
	        return false;
	    }
	    return true;
	}

	private void mostrarErrorCampo(String nombreCampo) {
	    JOptionPane.showMessageDialog(null, 
	        "Campo " + nombreCampo + " sin rellenar. Por favor rellene el campo correctamente.", 
	        "Error", JOptionPane.ERROR_MESSAGE);
	}

	private boolean validarDatosAlumno() {
	    if (!service.checkearNombre()) {
	        JOptionPane.showMessageDialog(null,
	            "Nombre no es correcto. No se ha realizado la inscripción.", "Error",
	            JOptionPane.ERROR_MESSAGE);
	        return false;
	    }
	    if (!service.checkearApellido()) {
	        JOptionPane.showMessageDialog(null,
	            "Apellidos no son correctos. No se ha realizado la inscripción.", "Error",
	            JOptionPane.ERROR_MESSAGE);
	        return false;
	    }
	    if (!service.checkearTf()) {
	        JOptionPane.showMessageDialog(null,
	            "Número telefónico no es correcto. No se ha realizado la inscripción.", "Error",
	            JOptionPane.ERROR_MESSAGE);
	        return false;
	    }
	    if (!service.checkearEmail()) {
	        JOptionPane.showMessageDialog(null,
	            "Email no es correcto. No se ha realizado la inscripción.", "Error",
	            JOptionPane.ERROR_MESSAGE);
	        return false;
	    }
	    return true;
	}
	
	private JPanel getPanelGestionGrupo() {
	    if (pnGestionGrupo == null) {
	        pnGestionGrupo = new JPanel(new BorderLayout(10, 10));
	        pnGestionGrupo.setBorder(BorderFactory.createTitledBorder("Gestión del Grupo - Lista de Integrantes"));
	        pnGestionGrupo.setPreferredSize(new Dimension(800, 400));

	        // 🔹 1. Crear modelo y tabla
	        modeloTablaIntegrantes = new DefaultTableModel(
	            new Object[]{"#", "Tipo", "Nombre", "Apellidos", "Email", "Teléfono"}, 0
	        ) {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public boolean isCellEditable(int row, int column) {
	                return false;
	            }
	        };

	        tablaIntegrantes = new JTable(modeloTablaIntegrantes);
	        tablaIntegrantes.setRowHeight(25);
	        tablaIntegrantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        tablaIntegrantes.getTableHeader().setReorderingAllowed(false);

	        JScrollPane scrollTabla = new JScrollPane(tablaIntegrantes);

	        // 🔹 2. Panel superior (botones de gestión)
	        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        panelSuperior.add(getBtnAnadirPersona());
	        panelSuperior.add(getBtnQuitarPersona());

	        panelSuperior.add(getLbContadorGrupo());

	        // 🔹 3. Panel inferior combinado (instrucciones + botón inscribir)
	        JPanel panelInferior = new JPanel(new BorderLayout());	        

	        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        panelBoton.add(getBtnInscribirGrupo());

	        
	        panelInferior.add(panelBoton, BorderLayout.SOUTH);

	        // 🔹 4. Añadir todo al panel principal
	        pnGestionGrupo.add(panelSuperior, BorderLayout.NORTH);
	        pnGestionGrupo.add(scrollTabla, BorderLayout.CENTER);
	        pnGestionGrupo.add(panelInferior, BorderLayout.EAST);

	        // 🔹 5. Inicialmente oculto (solo visible cuando se elige inscripción grupal)
	        pnGestionGrupo.setVisible(false);
	    }
	    return pnGestionGrupo;
	}
	
	private JButton getBtnAnadirPersona() {
	    if (btnAnadirPersona == null) {
	        btnAnadirPersona = new JButton("Añadir Persona");
	        btnAnadirPersona.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	agregarAlumno();
	            	checkLimite();
	            	
	            	
	            }
	        });
	    }
	    return btnAnadirPersona;
	}

	protected void checkLimite() {
		System.out.println("Personas lista: " + integrantesTemporales.size());
		System.out.println("Numero metido: " + getTxNumeroPersonas().getText());
		if(integrantesTemporales.size()==Integer.parseInt(getTxNumeroPersonas().getText())){
			getBtnAnadirPersona().setEnabled(false);
		}
		
	}

	private JButton getBtnQuitarPersona() {
	    if (btnQuitarPersona == null) {
	        btnQuitarPersona = new JButton("Quitar Seleccionado");
	        btnQuitarPersona.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	 int filaSeleccionada = tablaIntegrantes.getSelectedRow();
	            	    if (filaSeleccionada != -1) {
	            	        eliminarAlumno(filaSeleccionada);  // Elimina de la lista y refresca la tabla
	            	    } else {
	            	        JOptionPane.showMessageDialog(VentanaInscripcion.this, "Integrante no seleccionado","Informacion",
	            	        		JOptionPane.INFORMATION_MESSAGE);
	            	    }
	                quitarPersonaDelGrupo();
	            }
	        });
	    }
	    return btnQuitarPersona;
	}

	private JButton getBtnInscribirGrupo() {
	    if (btnInscribirGrupo == null) {
	        btnInscribirGrupo = new JButton("Inscribir Grupo");
	        btnInscribirGrupo.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                inscribirGrupo();
	            }
	        });
	    }
	    return btnInscribirGrupo;
	}
	
	
	private JLabel getLbContadorGrupo() {
	    if (lbContadorGrupo == null) {
	        lbContadorGrupo = new JLabel("Integrantes: 0");
	    }
	    return lbContadorGrupo;
	}
	
	


	private void actualizarEstadoBotonInscribir() {
	    if (getRdbtIndividual().isSelected()) {
	        // Para individual: activo si hay actividad seleccionada
	        boolean actividadSeleccionada = (service.getAct() != null);
	        getBtInscrip().setEnabled(actividadSeleccionada);
	    } else {
	        // Para grupal: activo si hay actividad, integrantes y plazas suficientes
	        if (service.getAct() != null) {
	            int plazasDisponibles = service.obtenerPlazasDisponibles(service.getAct().getId_Actividad());
	            boolean puedeInscribir = !integrantesTemporales.isEmpty() && 
	                                   integrantesTemporales.size() <= plazasDisponibles;
	            
	            getBtnInscribirGrupo().setEnabled(puedeInscribir);
	            
	            // Actualizar mensaje de plazas
	            if (integrantesTemporales.size() > plazasDisponibles) {
	                getLbContadorGrupo().setText("Integrantes: " + integrantesTemporales.size() + " (Plazas insuficientes!)");
	                getLbContadorGrupo().setForeground(Color.RED);
	            } else {
	                getLbContadorGrupo().setText("Integrantes: " + integrantesTemporales.size());
	                getLbContadorGrupo().setForeground(Color.BLACK);
	            }
	        } else {
	            getBtnInscribirGrupo().setEnabled(false);
	        }
	    }
	}
	
	private boolean validarAlumno(Alumno alumno, boolean mostrarMensajes) {
	    // Validar nombre
	    if (alumno.getNombre() == null || alumno.getNombre().trim().isEmpty()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    if (!alumno.validarNombre()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "El nombre no es válido", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    // Validar apellidos - CORREGIR: usar getApellidos() en lugar de getApellido()
	    if (alumno.getApellido() == null || alumno.getApellido().trim().isEmpty()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "Los apellidos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    if (!alumno.validarApellido()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "Los apellidos no son válidos", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    // Validar email
	    if (alumno.getCorreo() == null || alumno.getCorreo().trim().isEmpty()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "El email es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    if (!alumno.validarEmail()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "El email no es válido", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    return true;
	}
	
	
	
	private void quitarPersonaDelGrupo() {
	    int filaSeleccionada = tablaIntegrantes.getSelectedRow();
	    if (filaSeleccionada == -1) {
	        JOptionPane.showMessageDialog(this, 
	            "Integrante eliminado correctamente.", 
	            "Eliminado", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // Quitar de la lista y del modelo
	    integrantesTemporales.remove(filaSeleccionada);
	    modeloTablaIntegrantes.removeRow(filaSeleccionada);

	    // Reajustar numeración y tipo (por si se borró el responsable)
	    for (int i = 0; i < integrantesTemporales.size(); i++) {
	        String tipo = (i == 0) ? "RESPONSABLE" : "INTEGRANTE";
	        modeloTablaIntegrantes.setValueAt(i + 1, i, 0);
	        modeloTablaIntegrantes.setValueAt(tipo, i, 1);
	    }

	    actualizarContadorGrupo();

	    if (integrantesTemporales.isEmpty()) {
	        pnGestionGrupo.setVisible(false);
	    }
	}

	private void actualizarContadorGrupo() {
	    getLbContadorGrupo().setText(
	        "Integrantes: " + integrantesTemporales.size()
	    );
	    if (integrantesTemporales.isEmpty()) {
	        getLbContadorGrupo().setForeground(Color.RED);
	    } else {
	        getLbContadorGrupo().setForeground(new Color(0, 128, 0));
	    }
	}

	private void inscribirGrupo() {
	    if (service.getAct() == null) {
	        JOptionPane.showMessageDialog(this, "Seleccione una actividad primero", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    
	    
	    Alumno responsable = integrantesTemporales.get(0);
	    	    
	    
	    int totalPersonas = integrantesTemporales.size();
	    int plazasDisponibles = service.obtenerPlazasDisponibles(service.getAct().getId_Actividad());
	    
	    if (totalPersonas > plazasDisponibles) {
	        JOptionPane.showMessageDialog(this, 
	            "No hay suficientes plazas. Necesita " + totalPersonas + " pero solo hay " + plazasDisponibles, 
	            "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    
	    
	    StringBuilder resumen = new StringBuilder();
	    resumen.append("¿Está seguro de inscribir a ").append(totalPersonas).append(" personas?\n\n");
	    resumen.append("RESPONSABLE: ").append(integrantesTemporales.get(0).getNombre())
	           .append(" ").append(integrantesTemporales.get(0).getApellido())
	           .append(" (").append(integrantesTemporales.get(0).getCorreo()).append(")\n\n");
	    
	    if (totalPersonas > 1) {
	        resumen.append("INTEGRANTES:\n");
	        for (int i = 1; i < integrantesTemporales.size(); i++) {
	            Alumno a = integrantesTemporales.get(i);
	            resumen.append("- ").append(a.getNombre()).append(" ").append(a.getApellido())
	                   .append(" (").append(a.getCorreo()).append(")\n");
	        }
	    }
	    
	    int confirmacion = JOptionPane.showConfirmDialog(this,
	        resumen.toString(),
	        "Confirmar Inscripción Grupal",
	        JOptionPane.YES_NO_OPTION);
	    
	    if (confirmacion != JOptionPane.YES_OPTION) {
	        return;
	    }
	    
	    
	    try {
	        service.setIntegrantesGrupo(new ArrayList<>(integrantesTemporales));
	        service.setAlumnoResponsable(responsable);
	        MensajeError msj = new MensajeError();
	        msj.setMensaje("NO se ha podido hacer la matricula grupal.");
	        
	        if (service.introduceGrupo(msj)) {
	            JOptionPane.showMessageDialog(this,
	                "Inscripción grupal realizada para " + totalPersonas + " personas.\nSe debe pagar en 48 horas.",
	                "Inscripción Exitosa", JOptionPane.INFORMATION_MESSAGE);
	            
	            // Limpiar y volver a individual
	            integrantesTemporales.clear();
	            modeloIntegrantes.clear();
	            actualizarContadorGrupo();
	            cargarActividades();
	            cargarElementosFormulario();
	            getRdbtIndividual().setSelected(true);
	        } else {
	            JOptionPane.showMessageDialog(this, msj.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}


	private Alumno crearAlumnoDesdeFormulario(int id_c) {
	    Alumno alumno = new Alumno();
	    alumno.setNombre(getTxNombre().getText().trim());
	    alumno.setApellidos(getTxApellido().getText().trim());
	    alumno.setCorreo(getTxCorreo().getText().trim());
	    alumno.setNumeroTf(getTxTf().getText().trim());
	    alumno.setId_Cuota(id_c);
	    
	    return alumno;
	}
	 private void agregarAlumno() {
		 if(!service.checkearEmail(getTxCorreo().getText())) {
			 JOptionPane.showMessageDialog(this, 
	    	            "Error con el formato del correo electrónico.", 
	    	            "Por favor, rellenelo correctamente", JOptionPane.ERROR_MESSAGE);
			 return;
		 }
		 if(!service.checkearTf(getTxTf().getText())){
			 JOptionPane.showMessageDialog(this, 
	    	            "Error con el formato del numero de telefono.", 
	    	            "Por favor, rellenelo correctamente", JOptionPane.ERROR_MESSAGE);
			 return;
		 }
		 Integer id_c = getCuotaSeleccionada();
		 if(validarCamposFormulario() && id_c!=null) {
			 
			 Alumno alumno = crearAlumnoDesdeFormulario(id_c);
		        boolean yaExiste = integrantesTemporales.stream()
		    	        .anyMatch(a -> a.getCorreo().equalsIgnoreCase(alumno.getCorreo()));
		    	    if (yaExiste) {
		    	        JOptionPane.showMessageDialog(this, 
		    	            "Ya existe un integrante con este correo electrónico.", 
		    	            "Duplicado", JOptionPane.WARNING_MESSAGE);
		    	        return;
		    	    }
		    	  
		        integrantesTemporales.add(alumno);
		        refrescarTabla();
		        limpiarCampos();
		 }
	        
	    }

	    private void eliminarAlumno(int index) {
	        if (index >= 0 && index < integrantesTemporales.size()) {
	            integrantesTemporales.remove(index);
	            refrescarTabla();
	        }
	    }

	    private void refrescarTabla() {
	        modeloTablaIntegrantes.setRowCount(0);
	        int i = 1;
	        for (Alumno alumno : integrantesTemporales) {
	            String tipo = (i == 1) ? "RESPONSABLE" : "INTEGRANTE";
	            modeloTablaIntegrantes.addRow(new Object[]{
	                i,
	                tipo,
	                alumno.getNombre(),
	                alumno.getApellido(),
	                alumno.getCorreo(),
	                alumno.getTelefono()
	            });
	            i++;
	        }
	        actualizarContadorGrupo();
	    }
	
	    
	    private static class CuotaItem {
	        private int id;
	        private String descripcion;

	        public CuotaItem(int id, String descripcion) {
	            this.id = id;
	            this.descripcion = descripcion;
	        }

	        public int getId() {
	            return id;
	        }

	        @Override
	        public String toString() {
	            return descripcion; // lo que se muestra en el JComboBox
	        }
	    }  
}
