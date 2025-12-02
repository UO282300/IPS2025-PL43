package proyecto.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
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
	private int numPersonas;
	
	private JTable tablaIntegrantes;
	private DefaultTableModel modeloTablaIntegrantes;
	
	private List<Alumno> integrantesTemporales = new ArrayList<>();

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
	

	protected void seleccionActividad() {
	    Actividad selec = getActividadSeleccionada();
	    if (selec != null) {
	        service.selectActividad(selec);

	        cargarCuotasActividad(selec.getId_Actividad());
	    } else {
	        JOptionPane.showMessageDialog(null, "No hay actividad seleccionada. Por favor selecciona actividad.",
	                "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	private void cargarCuotasActividad(int idActividad) {
	    cbCuotas.removeAllItems();
	    List<Map<String, Object>> cuotas = service.listarCuotasPorActividad(idActividad);

	    if (cuotas.isEmpty()) {
	    	service.cargarCuota("Gratuito");
	    	service.asociarCuotaActividad(service.getAct().getId_Actividad(),"Gratuito",0.0);
	        cbCuotas.addItem(new CuotaItem(-1, "No hay cuotas disponibles"));
	        cbCuotas.setEnabled(false);
	    } else {
	        cbCuotas.setEnabled(true);
	        for (Map<String, Object> cuota : cuotas) {
	            int id = ((Number) cuota.get("id_cuota_actividad")).intValue();
	            String categoria = (String) cuota.get("categoria");
	            double valor = ((Number) cuota.get("valor")).doubleValue();

	            String descripcion = categoria + " - " + valor + "€";
	            cbCuotas.addItem(new CuotaItem(id, descripcion));
	            }

	        actualizarInfoPlazas();	        
	    }
	}
	
	
	private void actualizarInfoPlazas() {
	    if (service.getAct() != null) {	    	 	       
	        actualizarEstadoBotonInscribir();
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
	            new Object[]{"Nombre","Descripcion","Periodo inscripcion","Fechas","Precio"}, 0
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
	        if(rangoCuotas.equals("-"))rangoCuotas="0.0";
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
	        cbCuotas.addItemListener(e -> {
	            if (e.getStateChange() == ItemEvent.SELECTED) {
	                CuotaItem item = (CuotaItem) e.getItem();
	                System.out.println("Seleccionada cuota: " + item.getId());
	            }
	        });
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
	        JOptionPane.showMessageDialog(this, "Seleccione una cuota valida", "Error", JOptionPane.ERROR_MESSAGE);
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
		if (service.introduce(msj,String.valueOf(((CuotaItem)cbCuotas.getSelectedItem()).getId()))) {
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
	        getRdbtIndividual().setSelected(true);
	    }
	    return pnTipoInscripcion;
	}

	private JRadioButton getRdbtIndividual() {
	    if (rdbtIndividual == null) {
	        rdbtIndividual = new JRadioButton("Inscripción Individual");
	        rdbtIndividual.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	getPanelGestionGrupo().setVisible(false);
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
	        numPersonas = Integer.parseInt(numPersonasStr.trim());
	        if (numPersonas < 2) {
	            JOptionPane.showMessageDialog(this, 
	                "Para inscripción grupal debe haber al menos 2 personas.", 
	                "Error", JOptionPane.ERROR_MESSAGE);
	            getRdbtIndividual().setSelected(true);
	            return;
	        }
	        getPanelGestionGrupo().setVisible(true);
	        getBtInscrip().setVisible(false);
	        getBtnInscribirGrupo().setVisible(true);
	        getPanelFormulario().setVisible(true); 
	        actualizarEstadoBotonInscribir();
	        
	    } catch (NumberFormatException ex) {
	        JOptionPane.showMessageDialog(this, 
	            "Debe ingresar un número válido.", 
	            "Error", JOptionPane.ERROR_MESSAGE);
	        getRdbtIndividual().setSelected(true);
	    }
	}


	
	private boolean validarActividadSeleccionada() {
	    if (service.getAct() == null) {
	        JOptionPane.showMessageDialog(null, 
	            "Actividad sin escoger. Pulse el boton seleccionar.", 
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
	            return numPersonas;
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
	        mostrarErrorCampo("telefono");
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
	            "Nombre no es correcto. No se ha realizado la inscripcion.", "Error",
	            JOptionPane.ERROR_MESSAGE);
	        return false;
	    }
	    if (!service.checkearApellido()) {
	        JOptionPane.showMessageDialog(null,
	            "Apellidos no son correctos. No se ha realizado la inscripcion.", "Error",
	            JOptionPane.ERROR_MESSAGE);
	        return false;
	    }
	    if (!service.checkearTf()) {
	        JOptionPane.showMessageDialog(null,
	            "Número telefonico no es correcto. No se ha realizado la inscripcion.", "Error",
	            JOptionPane.ERROR_MESSAGE);
	        return false;
	    }
	    if (!service.checkearEmail()) {
	        JOptionPane.showMessageDialog(null,
	            "Email no es correcto. No se ha realizado la inscripcion.", "Error",
	            JOptionPane.ERROR_MESSAGE);
	        return false;
	    }
	    return true;
	}
	
	private JPanel getPanelGestionGrupo() {
	    if (pnGestionGrupo == null) {
	        pnGestionGrupo = new JPanel(new BorderLayout(10, 10));
	        pnGestionGrupo.setBorder(BorderFactory.createTitledBorder("Gestion del Grupo - Lista de Integrantes"));
	        pnGestionGrupo.setPreferredSize(new Dimension(800, 400));

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

	        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        panelSuperior.add(getBtnAnadirPersona());
	        panelSuperior.add(getBtnQuitarPersona());

	        panelSuperior.add(getLbContadorGrupo());

	        JPanel panelInferior = new JPanel(new BorderLayout());	        

	        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        panelBoton.add(getBtnInscribirGrupo());

	        
	        panelInferior.add(panelBoton, BorderLayout.SOUTH);

	        pnGestionGrupo.add(panelSuperior, BorderLayout.NORTH);
	        pnGestionGrupo.add(scrollTabla, BorderLayout.CENTER);
	        pnGestionGrupo.add(panelInferior, BorderLayout.EAST);

	        pnGestionGrupo.setVisible(false);
	    }
	    return pnGestionGrupo;
	}
	
	private JButton getBtnAnadirPersona() {
	    if (btnAnadirPersona == null) {
	        btnAnadirPersona = new JButton("Incluir Persona");
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
		if(integrantesTemporales.size()==numPersonas){
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
	            	        eliminarAlumno(filaSeleccionada);
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
	        
	        boolean actividadSeleccionada = (service.getAct() != null);
	        getBtInscrip().setEnabled(actividadSeleccionada);
	    } else {
	        
	        if (service.getAct() != null) {
	            //int plazasDisponibles = service.obtenerPlazasDisponibles(service.getAct().getId_Actividad());
	            //boolean puedeInscribir = !integrantesTemporales.isEmpty() && 
	                                  // integrantesTemporales.size() <= plazasDisponibles;
	            
	            getBtnInscribirGrupo().setEnabled(true);
	        } else {
	            getBtnInscribirGrupo().setEnabled(false);
	        }
	    }
	}
	
	private void checkEleccion(int plazasDisponibles) {
		if (integrantesTemporales.size() > plazasDisponibles) {

		    String mensaje = 
		        "Solo hay " + plazasDisponibles + " plazas disponibles.\n" +
		        "Pero el grupo tiene " + integrantesTemporales.size() + " personas.\n\n" +
		        "Seleccione una opci�n:\n" +
		        " 1 - Inscribir a los que tengan plaza y poner el resto en lista de espera\n" +
		        " 2 - Modificar el grupo (volver atr�s y quitar personas)\n" +
		        " 3 - Cancelar inscripci�n\n";

		    Object[] opciones = {
		        "1. Inscribir y lista de espera",
		        "2. Modificar grupo",
		        "3. Cancelar"
		    };

		    int opcion = JOptionPane.showOptionDialog(
		        this,
		        mensaje,
		        "Plazas insuficientes",
		        JOptionPane.DEFAULT_OPTION,
		        JOptionPane.WARNING_MESSAGE,
		        null,
		        opciones,
		        opciones[0]
		    );

		    if (opcion == 0) {

		       
		        List<Alumno> aEspera = new ArrayList<>();

		        for (int i = plazasDisponibles; i < integrantesTemporales.size(); i++) {
		            aEspera.add(integrantesTemporales.get(i));
		        }
		        
		        integrantesTemporales.removeAll(aEspera);
		        System.out.println("El id de la cuota pasada es :" + String.valueOf(((CuotaItem)cbCuotas.getSelectedItem()).getId()));
		        service.toListaEspera(aEspera,String.valueOf(((CuotaItem)cbCuotas.getSelectedItem()).getId()));
		        
		        JOptionPane.showMessageDialog(
		            this,
		            "Inscritos: " + integrantesTemporales.size() + "\n" +
		            "En lista de espera: " + aEspera.size(),
		            "Inscripci�n completada",
		            JOptionPane.INFORMATION_MESSAGE
		        );

		    }
		    else if (opcion == 1) {
		        return;
		    }
		    else {
		        JOptionPane.showMessageDialog(
		            this,
		            "Inscripci�n cancelada.",
		            "Cancelado",
		            JOptionPane.INFORMATION_MESSAGE
		        );
		        return;
		    }
		}
		
	}

	private boolean validarAlumno(Alumno alumno, boolean mostrarMensajes) {
	    if (alumno.getNombre() == null || alumno.getNombre().trim().isEmpty()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    if (!alumno.validarNombre()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "El nombre no es valido", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    
	    if (alumno.getApellido() == null || alumno.getApellido().trim().isEmpty()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "Los apellidos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    if (!alumno.validarApellido()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "Los apellidos no son validos", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    
	    if (alumno.getCorreo() == null || alumno.getCorreo().trim().isEmpty()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "El email es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	        return false;
	    }
	    
	    if (!alumno.validarEmail()) {
	        if (mostrarMensajes) {
	            JOptionPane.showMessageDialog(this, "El email no es valido", "Error", JOptionPane.ERROR_MESSAGE);
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

	    integrantesTemporales.remove(filaSeleccionada);
	    modeloTablaIntegrantes.removeRow(filaSeleccionada);

	    
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
	    if(!service.comprobarPlazos()) {
	    	JOptionPane.showMessageDialog(this, "Fuera de plazo", "Error", JOptionPane.ERROR_MESSAGE);
	    	return;
	    }
	    
	    int plazasDisponibles = service.obtenerPlazasDisponibles(service.getAct().getId_Actividad());
        
	    checkEleccion(plazasDisponibles);
	    actualizarEstadoBotonInscribir();
	    if(integrantesTemporales.size()>0) {
	    	Alumno responsable = integrantesTemporales.get(0);
    	    
		    
		    int totalPersonas = integrantesTemporales.size();
		    
		    if (totalPersonas > plazasDisponibles) {
		        JOptionPane.showMessageDialog(this, 
		            "No hay suficientes plazas. Necesita " + totalPersonas + " pero solo hay " + plazasDisponibles, 
		            "Error", JOptionPane.ERROR_MESSAGE);
		        return;
		    }
		    
		    
		    StringBuilder resumen = new StringBuilder();
		    resumen.append("�Esta seguro de inscribir a ").append(totalPersonas).append(" personas?\n\n");
		    resumen.append("RESPONSABLE: ").append(integrantesTemporales.get(0).getNombre())
		           .append(" ").append(integrantesTemporales.get(0).getApellido())
		           .append(" (").append(integrantesTemporales.get(0).getCorreo()).append(")\n\n");
		    
		    if (totalPersonas >= 1) {
		        resumen.append("INTEGRANTES:\n");
		        for (int i = 1; i < integrantesTemporales.size(); i++) {
		            Alumno a = integrantesTemporales.get(i);
		            resumen.append("- ").append(a.getNombre()).append(" ").append(a.getApellido())
		                   .append(" (").append(a.getCorreo()).append(")\n");
		        }
		    }
		    
		    int confirmacion = JOptionPane.showConfirmDialog(this,
		        resumen.toString(),
		        "Confirmar Inscripcion Grupal",
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
		                "Inscripcion grupal realizada para " + totalPersonas + " personas.\nSe debe pagar en 48 horas.",
		                "Inscripcion Exitosa", JOptionPane.INFORMATION_MESSAGE);
		            
		            
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
	    	            "Error con el formato del correo electronico.", 
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
		    	            "Ya existe un integrante con este correo electr0nico.", 
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
	            return descripcion;
	        }
	    }  
}
