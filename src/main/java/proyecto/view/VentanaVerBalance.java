package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import proyecto.model.entity.Factura;
import proyecto.service.UserService;

import java.awt.GridLayout;

import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import java.awt.FlowLayout;
import java.awt.Color;

import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;

public class VentanaVerBalance extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel pnBotones;
	private JPanel pnInfo;
	private JPanel pnFiltro;
	private JPanel pnTituloFiltro;
	private JPanel pnFechas;
	private JPanel pnRadioBotones;
	private JPanel pnBotonFiltrar;
	private JTable tablaAcabadas;
	private JTable tablaSinAcabar;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane2;
	private UserService service;
	private JTextField txFecha;
	private JTextField txFinal;
	private JLabel lbInstruc;
	private JLabel lbFechaI;
	private JLabel lbFechaF;
	private JRadioButton rdBtAcabadas;
	private JRadioButton rdBtSinFin;
	private JButton btFiltrar;
	

	

	/**
	 * Create the frame.
	 */
	public VentanaVerBalance(UserService ser) {
		setTitle("Ver Balances");
		this.service = ser;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1162, 950);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(getPnBotones(), BorderLayout.NORTH);
		contentPane.add(getPnInfo());
		
	}

	protected void checkearDatos() {
		String fechaIn = getTxFecha().getText();
		String fechaFin = getTxFinal().getText();
		boolean fecha1 = false;
		boolean fecha2 = false;
		int bt = 2;
		if(fechaIn.isBlank()&&fechaFin.isBlank()) {
			fecha1 =false;
			fecha2 = false;
			service.setFechaFiltrado(service.getFechaHoy().toString(),"2100-01-01");
		}else if(fechaIn.isBlank()) {
			fecha1=false;
			fecha2=true;
			service.setFechaFiltrado(service.getFechaHoy().toString(),fechaFin);
		}else if(fechaFin.isBlank()) {
			fecha1=true;
			fecha2=false;
			service.setFechaFiltrado(fechaIn,"2100-01-01");
		}
		else {
			service.setFechaFiltrado(fechaIn,fechaFin);
			fecha1=true;
			fecha2=true;
		}
		if(fecha1 && !service.compruebaFormatoFecha(fechaIn)) {
			JOptionPane.showMessageDialog(
			        null, 
			        "Formato fecha inadecuado.\nNo se ha realizado el filtrado.", 
			        "Error", 
			        JOptionPane.ERROR_MESSAGE
			    );
		}else if(fecha2 && !service.compruebaFormatoFecha(fechaFin)) {
			JOptionPane.showMessageDialog(
			        null, 
			        "Formato fecha inadecuado.\nNo se ha realizado el filtrado.", 
			        "Error", 
			        JOptionPane.ERROR_MESSAGE
			    );
		}
		
		
		if(getRdBtAcabadas().isSelected() && getRdBtSinFin().isSelected()) {
			bt = 2;
		}else if(getRdBtSinFin().isSelected()) {
			bt = 1;
		}else if(getRdBtAcabadas().isSelected()) {
			bt=0;
		}
		
		filtrado(bt);
	}
	
	
	
	private void filtrado(int bt) {
	    LocalDate inicio = service.getFechaFiltrado().getFechaIn();
	    LocalDate fin = service.getFechaFiltrado().getFechaFin();

	    switch (bt) {
        case 0:
            getTablaAcabadas().setModel(crearModeloFacturas(service.recuperaAcabadasEnRango(inicio, fin)));
            getTablaSinAcabar().setModel(new DefaultTableModel()); // tabla vacía
            break;
        case 1:
            getTablaAcabadas().setModel(new DefaultTableModel()); // tabla vacía
            getTablaSinAcabar().setModel(crearModeloFacturas(service.recuperaSinAcabarEnRango(inicio, fin)));
            break;
        case 2:
            getTablaAcabadas().setModel(crearModeloFacturas(service.recuperaAcabadasEnRango(inicio, fin)));
            getTablaSinAcabar().setModel(crearModeloFacturas(service.recuperaSinAcabarEnRango(inicio, fin)));
            break;
    }
	}

	private JPanel getPnFechas() {
	    if (pnFechas == null) {
	    	 pnFechas = new JPanel(new GridBagLayout());
	         pnFechas.setBorder(new MatteBorder(3, 0, 3, 0, (Color) new Color(0, 0, 0)));
	         //pnFechas.setBackground(new Color(255, 128, 128));

	         GridBagConstraints gbc = new GridBagConstraints();
	         gbc.insets = new Insets(5, 10, 5, 10);
	         gbc.anchor = GridBagConstraints.WEST;

	         gbc.gridx = 0;
	         gbc.gridy = 0;
	         pnFechas.add(getLbFechaI(), gbc);
	         
	         GridBagConstraints gbc2 = new GridBagConstraints();
	         gbc2.insets = new Insets(5, 10, 5, 10);
	         gbc2.anchor = GridBagConstraints.WEST;
	         gbc2.gridy = 0;
	         gbc2.gridx = 1;
	         pnFechas.add(getTxFecha(), gbc2);
	         
	         GridBagConstraints gbc3 = new GridBagConstraints();
	         gbc3.insets = new Insets(5, 10, 5, 10);
	         gbc3.anchor = GridBagConstraints.WEST;	         
	         gbc3.gridx = 0;
	         gbc3.gridy = 1;
	         pnFechas.add(getLbFechaF(), gbc3);
	         
	         GridBagConstraints gbc4 = new GridBagConstraints();
	         gbc4.insets = new Insets(5, 10, 5, 10);
	         gbc4.anchor = GridBagConstraints.WEST;
	         gbc4.gridy = 1;
	         gbc4.gridx = 1;

	         pnFechas.add(getTxFinal(), gbc4);
	         
	         
	         GridBagConstraints gbc5 = new GridBagConstraints();
	         gbc5.insets = new Insets(5, 10, 5, 10);
	         gbc5.anchor = GridBagConstraints.WEST;
	         gbc5.gridx = 0;
	         gbc5.gridy = 2;
	         gbc5.gridwidth = 2;
	         pnFechas.add(getLbInstruc(), gbc5);
	    }
	    return pnFechas;
	}
	
	private JLabel getLbFechaI() {
	    if (lbFechaI == null) {
	    	lbFechaI = new JLabel("Fecha Inicial");
	    }
	    return lbFechaI;
	}

	private JTextField getTxFecha() {
	    if (txFecha == null) {
	        txFecha = new JTextField(10);
	        txFecha.setText(service.getFechaHoy().toString());
	    }
	    return txFecha;
	}

	private JLabel getLbFechaF() {
	    if (lbFechaF == null) {
	    	lbFechaF = new JLabel("Fecha Final");
	    }
	    return lbFechaF;
	}

	private JTextField getTxFinal() {
	    if (txFinal == null) {
	    	txFinal = new JTextField(10);
	    	txFinal.setText(service.getFechaHoy().plusYears(1).toString());
	    }
	    return txFinal;
	}

	private JLabel getLbInstruc() {
	    if (lbInstruc == null) {
	        lbInstruc = new JLabel("Formato Fecha: YYYY-MM-DD");
	        lbInstruc.setFont(new Font("Arial Black", Font.BOLD, 10));
	    }
	    return lbInstruc;
	}

	// Panel de radio botones
	private JPanel getPnRadioBotones() {
	    if (pnRadioBotones == null) {
	        pnRadioBotones = new JPanel();
	        pnRadioBotones.setBorder(new MatteBorder(3, 0, 3, 0, (Color) new Color(0, 0, 0)));
	        //pnRadioBotones.setBackground(new Color(255, 128, 128));
	        FlowLayout fl = new FlowLayout();
	        fl.setHgap(30);
	        fl.setVgap(50);
	        pnRadioBotones.setLayout(fl);

	        pnRadioBotones.add(getRdBtAcabadas());
	        pnRadioBotones.add(getRdBtSinFin());
	    }
	    return pnRadioBotones;
	}

	private JRadioButton getRdBtAcabadas() {
	    if (rdBtAcabadas == null) {
	        rdBtAcabadas = new JRadioButton("Solo Finalizados");
	                
	        rdBtAcabadas.setHorizontalTextPosition(SwingConstants.CENTER);
	        rdBtAcabadas.setVerticalTextPosition(SwingConstants.BOTTOM);
	    }
	    return rdBtAcabadas;
	}

	private JRadioButton getRdBtSinFin() {
	    if (rdBtSinFin == null) {
	        rdBtSinFin = new JRadioButton("Solo en Curso");
	                
	        rdBtSinFin.setHorizontalTextPosition(SwingConstants.CENTER);
	        rdBtSinFin.setVerticalTextPosition(SwingConstants.BOTTOM);
	    }
	    return rdBtSinFin;
	}

	
	private JPanel getPnBotonFiltrar() {
	    if (pnBotonFiltrar == null) {
	        pnBotonFiltrar = new JPanel();
	        pnBotonFiltrar.setBorder(new MatteBorder(3, 0, 3, 3, (Color) new Color(0, 0, 0)));
	        //pnBotonFiltrar.setBackground(new Color(255, 128, 128));
	        pnBotonFiltrar.add(getBtFiltrar());
	    }
	    return pnBotonFiltrar;
	}

	private JButton getBtFiltrar() {
	    if (btFiltrar == null) {
	        btFiltrar = new JButton("Filtrar");
	        btFiltrar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					checkearDatos();
				}
			});

	       
	        //btFiltrar.setBackground(new Color(255, 128, 64));
	    }
	    return btFiltrar;
	}

	private JPanel getPnInfo() {
		if (pnInfo == null) {
			pnInfo = new JPanel();
			pnInfo.setLayout(new GridLayout(2, 0, 0, 0));
			pnInfo.add(getScrollPane());
			pnInfo.add(getScrollSin());
			
		}
		return pnInfo;
	}
	
	
	private Component getScrollSin() {
		if (scrollPane2 == null) {
			scrollPane2 = new JScrollPane();
			scrollPane2.setViewportView(getTablaSinAcabar());
			
			JLabel lbAcabadas = new JLabel("Sin Finalizar");
			scrollPane2.setColumnHeaderView(lbAcabadas);
		}
		return scrollPane2;
	}
	

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTablaAcabadas());
			
			JLabel lbAcabadas = new JLabel("Finalizadas");
			scrollPane.setColumnHeaderView(lbAcabadas);
		}
		return scrollPane;
	}
	
	private JPanel getPnBotones() {
	    if (pnBotones == null) {
	        pnBotones = new JPanel();
	        pnBotones.setLayout(new BorderLayout(0, 0));
	        pnBotones.add(getPnFiltro());
	        
	    }
	    return pnBotones;
	}

	private JPanel getPnFiltro() {
	    if (pnFiltro == null) {
	    	pnFiltro = new JPanel();
	        pnFiltro.setLayout(new GridLayout(0, 4, 0, 0));
	        pnFiltro.add(getPnTituloFiltro());
	        pnFiltro.add(getPnFechas());
	        pnFiltro.add(getPnRadioBotones());
	        pnFiltro.add(getPnBotonFiltrar());
	    }
	    return pnFiltro;
	}

	private JPanel getPnTituloFiltro() {
	    if (pnTituloFiltro == null) {
	        pnTituloFiltro = new JPanel();
	        pnTituloFiltro.setBorder(new MatteBorder(3, 0, 3, 0, (Color) new Color(0, 0, 0)));
	        //pnTituloFiltro.setBackground(new Color(255, 128, 128));
	        pnTituloFiltro.setLayout(new BorderLayout());
	        JLabel lbFiltro = new JLabel("Busqueda con Filtros", SwingConstants.CENTER);
	        lbFiltro.setBackground(new Color(192, 192, 192));
	        pnTituloFiltro.add(lbFiltro, BorderLayout.CENTER);
	    }
	    return pnTituloFiltro;
	}

	private void preparaFacturas(int orden) {
	    if (orden == 0) {
	    	DefaultTableModel nuevoModelo = crearModeloFacturas(service.recuperaAcabadas());
	    	getTablaAcabadas().setModel(nuevoModelo);
	    } else {
	    	DefaultTableModel nuevoModelo = crearModeloFacturas(service.recuperaSinAcabar());
	    	getTablaSinAcabar().setModel(nuevoModelo);
	    }
	    checkearDatos();
	}
	
	private DefaultTableModel crearModeloFacturas(List<Factura> lista) {
	    String[] columnas = {"Fecha", "Nombre", "Estado", "Ingresos", "Total Gastos", "Balance", "Ingresos Estimados", "Balance Estimado"};
	    DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

	    for (Factura f : lista) {
	        Object[] fila = {
	            f.getFecha().toString(),
	            f.getNombre(),
	            f.estaCerrada() ? "Finalizada" : "En curso",
	            f.getIngresos(),
	            f.getGastos(),
	            f.getBalance(),
	            f.getIngEstimados(),
	            f.getEstimado()
	        };
	        modelo.addRow(fila);
	    }

	    return modelo;
	}

	private JTable crearTablaFacturas(List<Factura> lista) {
	    String[] columnas = {"Fecha", "Nombre", "Estado", "Ingresos", "Total Gastos","Balance","Ingresos Estimados","Balance Estimado"};
	    DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

	    for (Factura f : lista) {
	        Object[] fila = {
	            f.getFecha().toString(),
	            f.getNombre(),
	            f.estaCerrada() ? "Finalizada" : "En curso",
	            f.getIngresos(),
	            f.getGastos(),
	            f.getBalance(),
	            f.getIngEstimados(),
	            f.getEstimado()
	        };
	        modelo.addRow(fila);
	    }

	    return new JTable(modelo);
	}
	
	private JTable getTablaAcabadas() {
	    if (tablaAcabadas == null) {
	        tablaAcabadas = new JTable();
	        preparaFacturas(0);
	    }
	    return tablaAcabadas;
	}

	private JTable getTablaSinAcabar() {
	    if (tablaSinAcabar == null) {
	        tablaSinAcabar = new JTable();
	        preparaFacturas(1);
	    }
	    return tablaSinAcabar;
	}
}
