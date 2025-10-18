package proyecto.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

import proyecto.service.UserService;

public class VentanaPrincipal {

	private JFrame frame;
	UserService service;
	
	public VentanaPrincipal() {
		service = new UserService();
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Main");
		frame.setBounds(0, 0, 500, 500);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JButton btnInicializarBaseDeDatos = new JButton("Inicializar Base de Datos en Blanco");
		btnInicializarBaseDeDatos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        service.crearDataBase();
			}
		});
		frame.getContentPane().add(btnInicializarBaseDeDatos);
			
		JButton btnCargarDatosIniciales = new JButton("Cargar Datos Iniciales para Pruebas");
		btnCargarDatosIniciales.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				service.cargarDataBase();
			}
		});
		frame.getContentPane().add(btnCargarDatosIniciales);
		
		JButton btnQueryDePrueba = new JButton("Ejemplo de consulta");
		btnQueryDePrueba.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				service.ejemploConsulta();
			}
		});
		frame.getContentPane().add(btnQueryDePrueba);
		
		JButton btnRegistrarPagos = new JButton("Registrar Pagos");
		btnRegistrarPagos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VentanaRegistrarPagos ventanaPagos = new VentanaRegistrarPagos();
                ventanaPagos.getFrame().setVisible(true);
			}
		});
		frame.getContentPane().add(btnRegistrarPagos);

		
		JButton btnResponsable = new JButton("Consultar el estado de una actividad de formaci�n");
		btnResponsable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mostrarVentanaEstadoAF();
			}
		});
		frame.getContentPane().add(btnResponsable);
	
		
		frame.setVisible(true);
	}
	
	private void mostrarVentanaEstadoAF() {
		VentanaEstadoAF vE = new VentanaEstadoAF(service);
		vE.setLocationRelativeTo(null);
		vE.setVisible(true);
	}
	
}
