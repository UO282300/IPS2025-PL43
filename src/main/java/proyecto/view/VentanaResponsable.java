package proyecto.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import proyecto.service.UserService;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.event.ActionEvent;

public class VentanaResponsable extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    private JTextField txtNombre;
    
    private JComboBox<String> cmbProfesor;
    private Map<String, Integer> mapaProfesores = new HashMap<>();
    
    private JTextField txtRemuneracion;
    private JTextField txtEspacio;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JTextField txtInicioInscripcion;
    private JTextField txtFinInscripcion;
    private JTextField txtCuota;
    private JTextArea txtObjetivos;
    private JTextArea txtContenidos;
    private JCheckBox chkGratuita;
    private JTextField txtPlazas;

    private JButton btnCargar;
    private JButton btnCancelar;

    private JPanel pnNorte;
    private JPanel pnCentro;
    private JPanel pnSur;
    private JPanel pnDatosGenerales;
    private JPanel pnProfesorEspacio;
    private JPanel pnProgramacion;
    private JPanel pnInscripcionCuota;
    
    UserService service;

    public VentanaResponsable(UserService service) {
    	this.service = service;
    	
        setTitle("PLANIFICACI�N DE ACTIVIDADES");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 700);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(230, 240, 255));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);

        contentPane.add(getPnNorte(), BorderLayout.NORTH);
        contentPane.add(getPnCentro(), BorderLayout.CENTER);
        contentPane.add(getPnSur(), BorderLayout.SOUTH);
    }

    private JPanel getPnNorte() {
        if (pnNorte == null) {
            pnNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
            pnNorte.setBackground(new Color(230, 235, 250));

            JLabel lblTitulo = new JLabel("Planificaci�n de Actividad Formativa", SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblTitulo.setForeground(new Color(30, 50, 90));
            lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
            pnNorte.add(lblTitulo);
        }
        return pnNorte;
    }

    private JPanel getPnCentro() {
        if (pnCentro == null) {
            pnCentro = new JPanel(new GridLayout(2, 2, 20, 20));
            pnCentro.setBackground(new Color(245, 250, 255));
            pnCentro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            pnCentro.add(getPnDatosGenerales());
            pnCentro.add(getPnProfesorEspacio());
            pnCentro.add(getPnProgramacion());
            pnCentro.add(getPnInscripcionCuota());
        }
        return pnCentro;
    }

    private JPanel getPnSur() {
        if (pnSur == null) {
            pnSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
            pnSur.setBackground(new Color(230, 235, 250));
            
            pnSur.add(getBtnCancelar());
            pnSur.add(getBtnCargar());
        }
        return pnSur;
    }
    
    private JButton getBtnCargar() {
    	if (btnCargar == null) {
    		btnCargar = new JButton("Cargar");
    		btnCargar.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				cargarActividad();
    			}
    		});
    	}
    	return btnCargar;
    }
    
    private JButton getBtnCancelar() {
    	if (btnCancelar == null) {
    		btnCancelar = new JButton("Cancelar");
    		btnCancelar.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				cancelarActividad();
    			}
    		});
    	}
    	return btnCancelar;
    }

    private JPanel getPnDatosGenerales() {
        if (pnDatosGenerales == null) {
        	pnDatosGenerales = new JPanel(new GridLayout(3, 2, 10, 10));
            pnDatosGenerales.setBorder(new TitledBorder("Datos Generales"));
            pnDatosGenerales.setBackground(new Color(250, 252, 255));

            pnDatosGenerales.add(new JLabel("Nombre de la actividad:"));
            pnDatosGenerales.add(getTxtNombre());

            pnDatosGenerales.add(new JLabel("Objetivos:"));
            pnDatosGenerales.add(new JScrollPane(getTxtObjetivos()));

            pnDatosGenerales.add(new JLabel("Contenidos:"));
            pnDatosGenerales.add(new JScrollPane(getTxtContenidos()));
        }
        return pnDatosGenerales;
    }
    
    private JTextField getTxtNombre() {
        if (txtNombre == null) {
            txtNombre = new JTextField(35);
        }
        return txtNombre;
    }

    private JTextArea getTxtObjetivos() {
        if (txtObjetivos == null) {
            txtObjetivos = new JTextArea(4, 35);
            txtObjetivos.setLineWrap(true);
        }
        return txtObjetivos;
    }

    private JTextArea getTxtContenidos() {
        if (txtContenidos == null) {
            txtContenidos = new JTextArea(4, 35);
            txtContenidos.setLineWrap(true);
        }
        return txtContenidos;
    }

    private JPanel getPnProfesorEspacio() {
        if (pnProfesorEspacio == null) {
        	pnProfesorEspacio = new JPanel(new GridLayout(4, 2, 10, 10));
            pnProfesorEspacio.setBorder(new TitledBorder("Profesorado y Espacio"));
            pnProfesorEspacio.setBackground(new Color(250, 252, 255));

            pnProfesorEspacio.add(new JLabel("Profesor:"));
            pnProfesorEspacio.add(getCmbProfesor());

            pnProfesorEspacio.add(new JLabel("Remuneraci�n (�):"));
            pnProfesorEspacio.add(getTxtRemuneracion());

            pnProfesorEspacio.add(new JLabel("Espacio:"));
            pnProfesorEspacio.add(getTxtEspacio());
            
            pnProfesorEspacio.add(new JLabel("Plazas:"));
            pnProfesorEspacio.add(getTxtPlazas());
        }
        return pnProfesorEspacio;
    }
    
    private JTextField getTxtPlazas() {
        if (txtPlazas == null) {
            txtPlazas = new JTextField(10);
        }
        return txtPlazas;
    }
	private JComboBox<String> getCmbProfesor() {
        if (cmbProfesor == null) {
        	cmbProfesor = new JComboBox<>();
            cargarProfesores();
        }
        return cmbProfesor;
    }
    
    private void cargarProfesores() {
    	cmbProfesor.removeAllItems();
        mapaProfesores.clear();

        List<Map<String, Object>> profesores = service.listarProfesores();

        for (Map<String, Object> prof : profesores) {
            int id = ((Number) prof.get("id_profesor")).intValue();
            String nombre = (String) prof.get("nombre");
            String apellido = (String) prof.get("apellido");
            String texto = nombre + " " + apellido + "";
            cmbProfesor.addItem(texto);
            mapaProfesores.put(texto, id);
        }
		
	}

	private JTextField getTxtRemuneracion() {
        if (txtRemuneracion == null) {
        	txtRemuneracion = new JTextField(10);
        }
        return txtRemuneracion;
    }
    
    private JTextField getTxtEspacio() {
        if (txtEspacio == null) {
        	txtEspacio = new JTextField(25);
        }
        return txtEspacio;
    }

    private JPanel getPnProgramacion() {
        if (pnProgramacion == null) {
        	pnProgramacion = new JPanel(new GridLayout(3, 2, 10, 10));
            pnProgramacion.setBorder(new TitledBorder("Programaci�n"));
            pnProgramacion.setBackground(new Color(250, 252, 255));

            pnProgramacion.add(new JLabel("Fecha (yyyy-MM-dd):"));
            pnProgramacion.add(getTxtFecha());

            pnProgramacion.add(new JLabel("Hora de inicio (hh:mm):"));
            pnProgramacion.add(getTxtHoraInicio());

            pnProgramacion.add(new JLabel("Hora de finalizaci�n (hh:mm):"));
            pnProgramacion.add(getTxtHoraFin());
           
        }
        return pnProgramacion;
    }
    
    private JTextField getTxtFecha() {
        if (txtFecha == null) {
        	txtFecha = new JTextField(10);
        }
        return txtFecha;
    }
    
    private JTextField getTxtHoraInicio() {
        if (txtHoraInicio == null) {
        	txtHoraInicio = new JTextField(7);
        }
        return txtHoraInicio;
    }
    
    private JTextField getTxtHoraFin() {
        if (txtHoraFin == null) {
        	txtHoraFin = new JTextField(7);
        }
        return txtHoraFin;
    }

    private JPanel getPnInscripcionCuota() {
        if (pnInscripcionCuota == null) {
        	pnInscripcionCuota = new JPanel(new GridLayout(4, 2, 10, 10));
            pnInscripcionCuota.setBorder(new TitledBorder("Inscripci�n y Cuota"));
            pnInscripcionCuota.setBackground(new Color(250, 252, 255));

            pnInscripcionCuota.add(new JLabel("Inicio inscripci�n (yyyy-MM-dd):"));
            pnInscripcionCuota.add(getTxtInicioInscripcion());

            pnInscripcionCuota.add(new JLabel("Cierre inscripci�n (yyyy-MM-dd):"));
            pnInscripcionCuota.add(getTxtFinInscripcion());

            pnInscripcionCuota.add(new JLabel("Cuota (�):"));
            pnInscripcionCuota.add(getTxtCuota());

            pnInscripcionCuota.add(new JLabel(""));
            pnInscripcionCuota.add(getChkGratuita());
        }
        return pnInscripcionCuota;
    }
    
    private JTextField getTxtInicioInscripcion() {
        if (txtInicioInscripcion == null) {
        	txtInicioInscripcion = new JTextField(10);
        }
        return txtInicioInscripcion;
    }
    
    private JTextField getTxtFinInscripcion() {
        if (txtFinInscripcion == null) {
        	txtFinInscripcion = new JTextField(10);
        }
        return txtFinInscripcion;
    }
    
    private JTextField getTxtCuota() {
        if (txtCuota == null) {
        	txtCuota = new JTextField(10);
        }
        return txtCuota;
    }
    
    private JCheckBox getChkGratuita() {
        if (chkGratuita == null) {
            chkGratuita = new JCheckBox("Actividad gratuita");
            chkGratuita.setOpaque(false);
            chkGratuita.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean seleccionada = chkGratuita.isSelected();
                    getTxtCuota().setEnabled(!seleccionada);
                    if (seleccionada) {
                        getTxtCuota().setText("0");
                    } else {
                        getTxtCuota().setText("");
                    }
                }
            });
        }
        return chkGratuita;
    }
    
    private void cargarActividad() {
    	String profesorSeleccionado = (String) cmbProfesor.getSelectedItem();
    	int idProfesor = mapaProfesores.get(profesorSeleccionado);
    	
    	 if (service.cargarActividad(
    	            getTxtNombre().getText(),
    	            idProfesor,
    	            getTxtRemuneracion().getText(),
    	            getTxtEspacio().getText(),
    	            getTxtFecha().getText(),
    	            getTxtHoraInicio().getText(),
    	            getTxtHoraFin().getText(),
    	            getTxtInicioInscripcion().getText(),
    	            getTxtFinInscripcion().getText(),
    	            getTxtCuota().getText(),
    	            getTxtObjetivos().getText(),
    	            getTxtContenidos().getText(),
    	            getTxtPlazas().getText(),
    	            getChkGratuita().isSelected())) {

    		
    		dispose();
    	}
    }

    private void cancelarActividad() {
    	dispose();
    }
}
