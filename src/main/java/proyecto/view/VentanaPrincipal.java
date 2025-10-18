package proyecto.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import proyecto.service.UserService;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;

public class VentanaPrincipal {

	private JFrame frame;
	UserService service;
	private JTextField txtFechaHoy;
	JLabel lblFechaCargada;
	
	public VentanaPrincipal() {
		service = new UserService();
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setTitle("Main");
        frame.setBounds(100, 100, 600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(10, 10));
        
        JPanel pnNorte = new JPanel();
        pnNorte.setBackground(Color.LIGHT_GRAY);
        pnNorte.setLayout(new GridLayout(2, 1, 0, 0));
        
        JPanel pnPideFecha = new JPanel();
        pnPideFecha.setBackground(Color.LIGHT_GRAY);
        FlowLayout flowLayout = (FlowLayout) pnPideFecha.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        pnNorte.add(pnPideFecha);
        
        JPanel pnDaFecha = new JPanel();
        pnDaFecha.setBackground(Color.LIGHT_GRAY);
        FlowLayout flowLayout_1 = (FlowLayout) pnDaFecha.getLayout();
        flowLayout_1.setAlignment(FlowLayout.LEFT);
        pnNorte.add(pnDaFecha);
        
        JLabel lblFechaHoy = new JLabel("Fecha de hoy (yyyy-MM-dd):");
        lblFechaHoy.setFont(new Font("Arial", Font.BOLD, 13));
        txtFechaHoy = new JTextField(10);
        txtFechaHoy.setFont(new Font("Arial", Font.BOLD, 13));
        
        pnPideFecha.add(lblFechaHoy);
        pnPideFecha.add(txtFechaHoy);
        JButton btnCargarFecha = new JButton("Cargar");
        btnCargarFecha.setFont(new Font("Arial", Font.BOLD, 13));
        btnCargarFecha.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargaFecha();
            }
        });
        pnPideFecha.add(btnCargarFecha);
        
        lblFechaCargada = new JLabel("Fecha cargada: ");
        lblFechaCargada.setFont(new Font("Arial", Font.BOLD, 13));
        pnDaFecha.add(lblFechaCargada);
        frame.getContentPane().add(pnNorte, BorderLayout.NORTH);

        JPanel pnCentro = new JPanel();
        pnCentro.setBackground(Color.GRAY);

        JButton btnInicializarBaseDeDatos = new JButton("Inicializar Base de Datos en Blanco");
        btnInicializarBaseDeDatos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                service.crearDataBase();
            }
        });
        pnCentro.setLayout(new GridLayout(18, 2, 0, 0));
        pnCentro.add(btnInicializarBaseDeDatos);

        JButton btnCargarDatosIniciales = new JButton("Cargar Datos Iniciales para Pruebas");
        btnCargarDatosIniciales.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                service.cargarDataBase();
            }
        });
        pnCentro.add(btnCargarDatosIniciales);

        JButton btnQueryDePrueba = new JButton("Ejemplo de consulta");
        btnQueryDePrueba.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                service.ejemploConsulta();
            }
        });
        pnCentro.add(btnQueryDePrueba);

        JButton btnResponsable = new JButton("Responsable añade actividad");
        btnResponsable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarVentanaResponsable();
            }
        });
        pnCentro.add(btnResponsable);

        frame.getContentPane().add(pnCentro, BorderLayout.CENTER);
        
        JButton btInscripcion = new JButton("Inscribirse a actividad");
        btInscripcion.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		mostrarVentanaInscripcion();
        	}
        });
        pnCentro.add(btInscripcion);
        
        JButton btnBalance = new JButton("Ver Balance");
        btnBalance.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		mostrarVentanaBalance();
        	}
        });
        pnCentro.add(btnBalance);
        
        JLabel label = new JLabel("");
        pnCentro.add(label);
        
        JLabel label_1 = new JLabel("");
        pnCentro.add(label_1);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void cargaFecha() {
		service.setFecha(txtFechaHoy.getText());
		txtFechaHoy.setText("");
		lblFechaCargada.setText("Fecha cargada: " + service.getFecha());
	}

	private void mostrarVentanaResponsable() {
        VentanaResponsable vR = new VentanaResponsable(service);
        vR.setLocationRelativeTo(null);
        vR.setVisible(true);
    }
	
	private void mostrarVentanaInscripcion() {
		VentanaInscripcion vI = new VentanaInscripcion(service);
		vI.setLocationRelativeTo(null);
		vI.setVisible(true);
	}
	
	private void mostrarVentanaBalance() {
		VentanaVerBalance vB = new VentanaVerBalance(service);
		vB.setLocationRelativeTo(null);
		vB.setVisible(true);
	}
}