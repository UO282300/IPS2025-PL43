package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import proyecto.service.UserService;

public class VentanaPrincipal {


	private JFrame frame;
	UserService service;
	private JTextField txtFechaHoy;
	JLabel lblFechaCargada;
	private JComboBox<String> comboAlumnosCancelar;
	private JComboBox<String> comboAlumnosInscripcion;
	private List<Map<String, Object>> listaAlumnos; // Para obtener el id real del alumno
	

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

        // === PANEL NORTE ===
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
        lblFechaHoy.setFont(new Font("Arial", Font.PLAIN, 16));
        txtFechaHoy = new JTextField(10);
        txtFechaHoy.setFont(new Font("Arial", Font.PLAIN, 16));

        pnPideFecha.add(lblFechaHoy);
        pnPideFecha.add(txtFechaHoy);
        JButton btnCargarFecha = new JButton("Cargar");
        btnCargarFecha.setFont(new Font("Arial", Font.PLAIN, 16));
        btnCargarFecha.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargaFecha();
            }
        });
        pnPideFecha.add(btnCargarFecha);

        lblFechaCargada = new JLabel("Fecha cargada: ");
        lblFechaCargada.setFont(new Font("Arial", Font.PLAIN, 16));
        pnDaFecha.add(lblFechaCargada);
        frame.getContentPane().add(pnNorte, BorderLayout.NORTH);

        // === PANEL CENTRO ===

        JPanel pnCentro = new JPanel();
        pnCentro.setBackground(Color.DARK_GRAY);

        JButton btnInicializarBaseDeDatos = new JButton("Inicializar Base de Datos en Blanco");
        btnInicializarBaseDeDatos.setFont(new Font("Arial", Font.PLAIN, 16));
        btnInicializarBaseDeDatos.setEnabled(false);
        btnInicializarBaseDeDatos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                service.crearDataBase();
            }
        });
        pnCentro.setLayout(new GridLayout(15, 2, 2, 2));
        pnCentro.add(btnInicializarBaseDeDatos);

        JButton btnCargarDatosIniciales = new JButton("Cargar Datos Iniciales para Pruebas");
        btnCargarDatosIniciales.setFont(new Font("Arial", Font.PLAIN, 16));
        btnCargarDatosIniciales.setEnabled(false);
        btnCargarDatosIniciales.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                service.cargarDataBase();
            }
        });
        pnCentro.add(btnCargarDatosIniciales);

        JButton btnResponsable = new JButton("Responsable - Planificar actividad");
        btnResponsable.setFont(new Font("Arial", Font.PLAIN, 16));
        btnResponsable.setEnabled(false);
        btnResponsable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarVentanaResponsable();
            }
        });
        pnCentro.add(btnResponsable);

        frame.getContentPane().add(pnCentro, BorderLayout.CENTER);
        
        JPanel pnInscripciones = new JPanel();
        pnCentro.add(pnInscripciones);
        pnInscripciones.setLayout(new GridLayout(0, 2, 0, 0));
        
     // === Combo de alumnos para inscripci�n ===
        comboAlumnosInscripcion = new JComboBox<>();
        comboAlumnosInscripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        comboAlumnosInscripcion.setEnabled(false);
        pnInscripciones.add(comboAlumnosInscripcion);
        cargarAlumnosEnCombo(comboAlumnosInscripcion, "Nuevo Alumno");

        // Evento: cuando se selecciona un alumno
        comboAlumnosInscripcion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = comboAlumnosInscripcion.getSelectedIndex();

                if (index == 0) { 
                    // "Nuevo Alumno" seleccionado
                    service.setIdAlumnoInscrip(0);
                    System.out.println("Nuevo Alumno seleccionado (ID 0)");
                    return;
                }

                if (listaAlumnos != null && index - 1 < listaAlumnos.size()) {
                    Map<String, Object> alumno = listaAlumnos.get(index - 1); // -1 porque primer item es "Nuevo Alumno"
                    int idAlumno = (int) alumno.get("id_alumno");
                    service.setIdAlumnoInscrip(idAlumno);
                    System.out.println("Alumno seleccionado Inscrip: " + alumno.get("nombre") + " (ID " + idAlumno + ")");
                }
            }
        });

        
        
        JButton btInscripcion = new JButton("Profesional - Inscribirse a actividad");
        btInscripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        btInscripcion.setEnabled(false);
        pnInscripciones.add(btInscripcion);
        btInscripcion.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		mostrarVentanaInscripcion();
        	}
        });
        
        JButton btnBalance = new JButton("Responsable - Ver Balance");
        btnBalance.setFont(new Font("Arial", Font.PLAIN, 16));
        btnBalance.setEnabled(false);
        btnBalance.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		mostrarVentanaBalance();
        	}
        });
        pnCentro.add(btnBalance);
        
      JButton btnRegistrarPagos = new JButton("Administrador - Registrar Pagos a inscritos");
      btnRegistrarPagos.setFont(new Font("Arial", Font.PLAIN, 16));
      btnRegistrarPagos.setEnabled(false);
      btnRegistrarPagos.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
        	  mostrarVentanaRegistrarPagos();
          }
      });
      
      pnCentro.add(btnRegistrarPagos);
        
        
        JButton btnPagoProfesores = new JButton("Administrador - Registrar Pago a Profesores");
        btnPagoProfesores.setFont(new Font("Arial", Font.PLAIN, 16));
        btnPagoProfesores.setEnabled(false);
        btnPagoProfesores.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarVentanaPagoProfesores();
            }
        });
        
        pnCentro.add(btnPagoProfesores);
        

        JButton btnEstadoAF = new JButton("Administrador - Ver Estado Acciones Formativas");
        btnEstadoAF.setFont(new Font("Arial", Font.PLAIN, 16));
        btnEstadoAF.setEnabled(false);
        btnEstadoAF.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		mostrarVentanaEstadoAF();
        	}
        });
        pnCentro.add(btnEstadoAF);
        
        JPanel pnCancelarInscripciones = new JPanel();
        pnCentro.add(pnCancelarInscripciones);
        pnCancelarInscripciones.setLayout(new GridLayout(0, 2, 0, 0));

        // === Combo de alumnos ===
        comboAlumnosCancelar = new JComboBox<>();
        comboAlumnosCancelar.setFont(new Font("Arial", Font.PLAIN, 16));
        comboAlumnosCancelar.setEnabled(false);
        pnCancelarInscripciones.add(comboAlumnosCancelar);

        // === Bot�n cancelar inscripciones ===
        JButton btnCancelarInscripciones = new JButton("Profesional - Cancelar Inscripciones");
        btnCancelarInscripciones.setFont(new Font("Arial", Font.PLAIN, 16));
        btnCancelarInscripciones.setEnabled(false);
        pnCancelarInscripciones.add(btnCancelarInscripciones);

        // === Cargar alumnos desde la BD ===
        cargarAlumnosEnCombo(comboAlumnosCancelar, "----------");

        // Evento: cuando se selecciona un alumno
        comboAlumnosCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = comboAlumnosCancelar.getSelectedIndex();

                if (index == 0) { 
                    // "----------" seleccionado
                    service.setIdAlumnoCancel(0);
                    System.out.println("Ning�n alumno seleccionado (ID 0)");
                    return;
                }

                // Si hay lista cargada y el �ndice es v�lido
                if (listaAlumnos != null && index - 1 < listaAlumnos.size()) {
                    Map<String, Object> alumno = listaAlumnos.get(index - 1); // -1 porque el primer item es "----------"
                    int idAlumno = (int) alumno.get("id_alumno");
                    service.setIdAlumnoCancel(idAlumno);
                    System.out.println("Alumno seleccionado: " + alumno.get("nombre") + " (ID " + idAlumno + ")");
                }
            }
        });

        // Evento: abrir ventana cancelar inscripci�n
        btnCancelarInscripciones.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (service.getIdAlumnoCancel() == 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Debes seleccionar un alumno antes de cancelar inscripciones.",
                            "Alumno no seleccionado", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                VentanaCancelarInscripcion vCancel = new VentanaCancelarInscripcion(service);
                vCancel.setLocationRelativeTo(frame);
                vCancel.setVisible(true);
            }
        });
        
        JButton btnCerrarAF = new JButton("Administrador - cerrar una actividad");
        btnCerrarAF.setFont(new Font("Arial", Font.PLAIN, 16));
        btnCerrarAF.setEnabled(false);
        btnCerrarAF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarVentanaCerrarAF();
            }
        });
        
        pnCentro.add(btnCerrarAF);
        
        
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
		enableAll();
	}

    private void enableAll() {
        // Recorrer todos los componentes del panel central
        JPanel pnCentro = (JPanel) frame.getContentPane().getComponent(1); // asumiendo que est� en BorderLayout.CENTER
        activarComponentes(pnCentro);
    }

    // M�todo recursivo para activar botones y combos
    private void activarComponentes(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton || comp instanceof JComboBox) {
                comp.setEnabled(true);
            } else if (comp instanceof Container) {
                activarComponentes((Container) comp); // recursi�n para subpaneles
            }
        }
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
	private void mostrarVentanaRegistrarPagos() {
		VentanaRegistrarPagos vB = new VentanaRegistrarPagos(service);
		vB.setLocationRelativeTo(null);
		vB.setVisible(true);
	}
	
	private void mostrarVentanaPagoProfesores() {
	    VentanaPagoProfesores vPP = new VentanaPagoProfesores(service);
	    vPP.setLocationRelativeTo(null); 
	    vPP.setVisible(true);             
	}

	private void mostrarVentanaCerrarAF() {
	    VentanaCerrarAF vC = new VentanaCerrarAF(service);
	    vC.setLocationRelativeTo(null); 
	    vC.setVisible(true);             
	}

	private void mostrarVentanaEstadoAF() {
	    VentanaEstadoAF vEA = new VentanaEstadoAF(service);
	    vEA.setLocationRelativeTo(null); 
	    vEA.setVisible(true);             
	}
	
	private void cargarAlumnosEnCombo(JComboBox<String> combo, String primerItem) {
	    listaAlumnos = service.listarAlumnos(); // obtenemos lista de alumnos

	    combo.removeAllItems();
	    combo.addItem(primerItem); // valor por defecto
	    service.setIdAlumnoCancel(0); // por defecto no hay alumno seleccionado
	    service.setIdAlumnoInscrip(0); // por defecto no hay alumno seleccionado


	    if (listaAlumnos == null || listaAlumnos.isEmpty()) {
	        combo.addItem("No hay alumnos registrados");
	        combo.setEnabled(false);
	    } else {
	        
	        for (Map<String, Object> alumno : listaAlumnos) {
	            combo.addItem(alumno.get("nombre") + " " + alumno.get("apellido"));
	        }
	    }

	    combo.setSelectedIndex(0); // mostrar por defecto primerItem
	}
}
