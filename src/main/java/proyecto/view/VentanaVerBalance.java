package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;



import proyecto.service.UserService;

import java.awt.GridLayout;


import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import java.awt.FlowLayout;
import java.awt.Color;

import javax.swing.border.MatteBorder;
import java.awt.Font;
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
	private JButton btTodo;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane2;
	private JTextArea txSinAcabar;
	private JTextArea txAcabar;
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
		this.service = ser;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1162, 950);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 2, 0, 0));
		contentPane.add(getPnBotones());
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
	    switch(bt) {
	        case 0: 
	            getTxAcabar().setText(service.getFacturasTextoAcabadasEnRango(inicio, fin));
	            getTxSinAcabar().setText("");
	            break;
	        case 1: 
	            getTxAcabar().setText("");
	            getTxSinAcabar().setText(service.getFacturasTextoSinAcabarEnRango(inicio, fin));
	            break;
	        case 2: // Ambas
	            getTxAcabar().setText(service.getFacturasTextoAcabadasEnRango(inicio, fin));
	            getTxSinAcabar().setText(service.getFacturasTextoSinAcabarEnRango(inicio, fin));
	            break;
	    }
	}

	private JPanel getPnFechas() {
	    if (pnFechas == null) {
	        pnFechas = new JPanel();
	        pnFechas.setBorder(new MatteBorder(3, 3, 3, 3, Color.BLACK));
	        pnFechas.setBackground(new Color(255, 128, 128));
	        FlowLayout fl = new FlowLayout();
	        fl.setHgap(500);
	        fl.setVgap(20);
	        pnFechas.setLayout(fl);

	        pnFechas.add(getLbFechaI());
	        pnFechas.add(getTxFecha());
	        pnFechas.add(getLbFechaF());
	        pnFechas.add(getTxFinal());
	        pnFechas.add(getLbInstruc());
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
	        pnRadioBotones.setBorder(new MatteBorder(0, 3, 3, 3, Color.BLACK));
	        pnRadioBotones.setBackground(new Color(255, 128, 128));
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

	// Panel del bot�n Filtrar
	private JPanel getPnBotonFiltrar() {
	    if (pnBotonFiltrar == null) {
	        pnBotonFiltrar = new JPanel();
	        pnBotonFiltrar.setBorder(new MatteBorder(0, 3, 3, 3, Color.BLACK));
	        pnBotonFiltrar.setBackground(new Color(255, 128, 128));
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

	       
	        btFiltrar.setBackground(new Color(255, 128, 64));
	    }
	    return btFiltrar;
	}

	private JPanel getPnInfo() {
		if (pnInfo == null) {
			pnInfo = new JPanel();
			pnInfo.setLayout(new GridLayout(0, 2, 0, 0));
			pnInfo.add(getScrollPane());
			pnInfo.add(getScrollSin());
			
		}
		return pnInfo;
	}
	
	
	private Component getScrollSin() {
		if (scrollPane2 == null) {
			scrollPane2 = new JScrollPane();
			scrollPane2.setViewportView(getTxSinAcabar());
			
			JLabel lbAcabadas = new JLabel("Sin Finalizar");
			scrollPane2.setColumnHeaderView(lbAcabadas);
		}
		return scrollPane2;
	}
	private JTextArea getTxSinAcabar() {
		if (txSinAcabar == null) {
			txSinAcabar = new JTextArea();
			txSinAcabar.setLineWrap(true);
			txSinAcabar.setEditable(false);
			preparaFacturas(1);
			
		}
		return txSinAcabar;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTxAcabar());
			
			JLabel lbAcabadas = new JLabel("Finalizadas");
			scrollPane.setColumnHeaderView(lbAcabadas);
		}
		return scrollPane;
	}
	private JTextArea getTxAcabar() {
		if (txAcabar == null) {
			txAcabar = new JTextArea();
			txAcabar.setLineWrap(true);
			txAcabar.setEditable(false);
			preparaFacturas(0);
			
		}
		return txAcabar;
	}
	
	
	private JButton getBtTodo() {
		if (btTodo == null) {
			btTodo = new JButton("Todos");
			btTodo.setBackground(new Color(255, 128, 64));
			btTodo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					preparaFacturasGeneral();
				}
			});
		}
		return btTodo;
	}
	
	private JPanel getPnBotones() {
	    if (pnBotones == null) {
	        pnBotones = new JPanel();
	        pnBotones.setLayout(new BorderLayout(0, 0));
	        pnBotones.add(getBtTodo(), BorderLayout.NORTH);
	        pnBotones.add(getPnFiltro(), BorderLayout.CENTER);
	        
	    }
	    return pnBotones;
	}

	private JPanel getPnFiltro() {
	    if (pnFiltro == null) {
	        pnFiltro = new JPanel();
	        pnFiltro.setLayout(new GridLayout(4, 1, 0, 0));
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
	        pnTituloFiltro.setBackground(new Color(255, 128, 128));
	        pnTituloFiltro.setLayout(new BorderLayout());
	        JLabel lbFiltro = new JLabel("Busqueda con Filtros", SwingConstants.CENTER);
	        pnTituloFiltro.add(lbFiltro, BorderLayout.CENTER);
	    }
	    return pnTituloFiltro;
	}
	
	
	
	
	protected void preparaFacturasGeneral() {
		getTxAcabar().setText(service.getFacturasTextoAcabadas());
		getTxSinAcabar().setText(service.getFacturasTextoSinAcabar());
		checkearDatos();
		
	}

	private void preparaFacturas(int orden) {
		if(orden==0) {
			getTxAcabar().setText(service.getFacturasTextoAcabadas());
		}else {
			getTxSinAcabar().setText(service.getFacturasTextoSinAcabar());
		}
		checkearDatos();
		
	}
}
