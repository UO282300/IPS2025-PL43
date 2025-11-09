package proyecto.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import proyecto.service.UserService;

public class VentanaNuevoProfesor extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtNombre;
	private JTextField txtApellidos;
	private JTextField txtEmail;
	private JTextField txtTelefono;
	private JButton btnGuardar;
	private JButton btnCancelar;

	UserService service;
	VentanaResponsable ventanaP;	

	public VentanaNuevoProfesor(UserService service, VentanaResponsable ventana) {
		this.service = service;
		this.ventanaP = ventana;
		

		setTitle("Nuevo Profesor");
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);

		Color fondoPrincipal = new Color(230, 240, 255);
		Color fondoPanel = new Color(250, 252, 255);
		Color fondoEncabezado = new Color(230, 235, 250);
		Color textoTitulo = new Color(30, 50, 90);

		getContentPane().setLayout(new BorderLayout(10, 10));
		getContentPane().setBackground(fondoPrincipal);

		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
		headerPanel.setBackground(fondoEncabezado);

		JLabel lblTitulo = new JLabel("Incluir nuevo profesor");
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTitulo.setForeground(textoTitulo);
		headerPanel.add(lblTitulo);

		getContentPane().add(headerPanel, BorderLayout.NORTH);

		JPanel formPanel = new JPanel(null);
		formPanel.setBackground(fondoPanel);
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(70, 60, 120, 25);
		formPanel.add(lblNombre);
		txtNombre = new JTextField();
		txtNombre.setBounds(190, 60, 220, 30);
		formPanel.add(txtNombre);

		JLabel lblApellidos = new JLabel("Apellidos:");
		lblApellidos.setBounds(70, 115, 120, 25);
		formPanel.add(lblApellidos);
		txtApellidos = new JTextField();
		txtApellidos.setBounds(190, 115, 220, 30);
		formPanel.add(txtApellidos);

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(70, 170, 120, 25);
		formPanel.add(lblEmail);
		txtEmail = new JTextField();
		txtEmail.setBounds(190, 170, 220, 30);
		formPanel.add(txtEmail);

		JLabel lblTelefono = new JLabel("Teléfono:");
		lblTelefono.setBounds(70, 225, 120, 25);
		formPanel.add(lblTelefono);
		txtTelefono = new JTextField();
		txtTelefono.setBounds(190, 225, 220, 30);
		formPanel.add(txtTelefono);

		getContentPane().add(formPanel, BorderLayout.CENTER);

		JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
		botonesPanel.setBackground(fondoEncabezado);

		btnGuardar = new JButton("Guardar");
		btnCancelar = new JButton("Cancelar");

		btnGuardar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		botonesPanel.add(btnCancelar);
		botonesPanel.add(btnGuardar);
		getContentPane().add(botonesPanel, BorderLayout.SOUTH);

		btnCancelar.addActionListener(e -> dispose());
		btnGuardar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				guardarProfesor();
			}
		});
	}

	private void guardarProfesor() {
		String nombre = txtNombre.getText().trim();
		String apellidos = txtApellidos.getText().trim();
		String email = txtEmail.getText().trim();
		String telefono = txtTelefono.getText().trim();

		if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Los campos Nombre, Apellidos y Email son obligatorios.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		boolean bien = service.cargarProfesor(nombre, apellidos, email, telefono);

		if (bien) {
			JOptionPane.showMessageDialog(this, "Profesor añadido correctamente.");
		} else {
			JOptionPane.showMessageDialog(this, "No se pudo añadir el profesor",
					"Registro fallido", JOptionPane.ERROR_MESSAGE);
		}

		ventanaP.cargarProfesores();
		dispose();
	}
}
