package proyecto.view;

import javax.swing.JFrame;

import proyecto.model.Database;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;

public class VentanaPrincipal {

	private JFrame frame;
	
	public VentanaPrincipal() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setVisible(true);
		frame.setTitle("Main");
		frame.setBounds(0, 0, 500, 500);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JButton btnInicializarBaseDeDatos = new JButton("Inicializar Base de Datos en Blanco");
		btnInicializarBaseDeDatos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Database db = new Database();
		        db.createDatabase(false); 
			}
		});
		frame.getContentPane().add(btnInicializarBaseDeDatos);
			
		JButton btnCargarDatosIniciales = new JButton("Cargar Datos Iniciales para Pruebas");
		btnCargarDatosIniciales.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Database db=new Database();
				db.createDatabase(false);
				db.loadDatabase();
			}
		});
		frame.getContentPane().add(btnCargarDatosIniciales);
		
		JButton btnQueryDePrueba = new JButton("Ejemplo de consulta");
		btnQueryDePrueba.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Database db=new Database();
				db.createDatabase(false);
				db.loadDatabase();
			    List<Map<String, Object>> alumnos = db.executeQueryMap("SELECT * FROM Alumno");
			    for (Map<String, Object> alumno : alumnos) {
			        System.out.println(alumno);
			    }
			}
		});
		frame.getContentPane().add(btnQueryDePrueba);
	}
	
	
	public JFrame getFrame() { 
		return this.frame; 
	}
	
	
}
