package proyecto.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import proyecto.service.UserService;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VentanaResponsable extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    private JPanel pnNorte;

    private JTextField txtNombre;
    private JTextArea txtObjetivos;
    private JTextArea txtContenidos;
    private JTextField txtPlazas;

    private JTextField txtInicioInscripcion;
    private JTextField txtFinInscripcion;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtUbicacion;

    private JComboBox<String> cmbProfesor;
    private JTextField txtRemuneracion;
    private JButton btnAnadirProfesor;
    private JButton btnEliminarProfesor;
    private JTable tableProfesoresAsignados;
    private DefaultTableModel modelProfesores;
    JTextField txtEmpresa;

    private JTextField txtNuevoNombre;
    private JTextField txtNuevoApellido;
    private JTextField txtNuevoEmail;
    private JTextField txtNuevoTelefono;
    private JButton btnGuardarNuevoProfesor;

    private JCheckBox chkGratuita;
    private JComboBox<String> cmbCuotas;
    private JTextField txtValorCuota;
    private JButton btnAnadirCuota;
    private JButton btnEliminarCuota;
    private JTable tableCuotasAsignadas;
    private DefaultTableModel modelCuotas;

    private JTextField txtNuevaCategoria;
    private JButton btnGuardarNuevaCuota;

    private JButton btnCancelar;
    private JButton btnGuardarActividad;
    private JLabel lbEmpresa;
    private UserService service;
    private Map<String, Integer> mapaProfesores = new HashMap<>();
    private Map<String, Integer> mapaEmpresas = new HashMap<>();
    private JComboBox<String> cbEmpresa;
    private JCheckBox chckNuevaEmpresa;
    private JLabel lbNuevaEmpresa;
    private JLabel lbRemu;
    private JTextField txtRemu;
    private JCheckBox chckEmpresa;

    public VentanaResponsable(UserService service) {
        this.service = service;

        setTitle("Planificacion de Actividad Formativa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1500, 1000);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(10,10));
        contentPane.setBackground(new Color(230,240,255));
        setContentPane(contentPane);

        contentPane.add(getPnNorte(), BorderLayout.NORTH);
        contentPane.add(getPanelCentral(), BorderLayout.CENTER);
        contentPane.add(getPanelSur(), BorderLayout.SOUTH);
    }

    private JPanel getPnNorte() {
        if(pnNorte==null) {
            pnNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
            pnNorte.setBackground(new Color(230,235,250));
            JLabel lblTitulo = new JLabel("Planificacion de Actividad Formativa", SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblTitulo.setForeground(new Color(30,50,90));
            lblTitulo.setBorder(BorderFactory.createEmptyBorder(20,0,10,0));
            pnNorte.add(lblTitulo);
        }
        return pnNorte;
    }

    private JPanel getPanelCentral() {
        JPanel panelCentral = new JPanel(new GridLayout(3,2,10,10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        panelCentral.add(getPanelDatosGenerales());
        panelCentral.add(getPanelProgramacion());

        panelCentral.add(getPanelProfesoresAsignados());
        panelCentral.add(getPanelNuevoProfesor());

        panelCentral.add(getPanelCuotasAsignadas());
        panelCentral.add(getPanelNuevaCuota());

        return panelCentral;
    }

    private JPanel getPanelDatosGenerales() {
        JPanel panel = new JPanel(new GridLayout(4,2,5,5));
        panel.setBorder(new TitledBorder("Datos Generales"));
        panel.setBackground(new Color(250,252,255));

        panel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(); panel.add(txtNombre);

        panel.add(new JLabel("Objetivos:"));
        txtObjetivos = new JTextArea(3,20); txtObjetivos.setLineWrap(true);
        panel.add(new JScrollPane(txtObjetivos));

        panel.add(new JLabel("Contenidos:"));
        txtContenidos = new JTextArea(3,20); txtContenidos.setLineWrap(true);
        panel.add(new JScrollPane(txtContenidos));

        panel.add(new JLabel("Numero de plazas:"));
        txtPlazas = new JTextField(); panel.add(txtPlazas);

        return panel;
    }

    private JPanel getPanelProgramacion() {
        JPanel panel = new JPanel(new GridLayout(5,2,5,5));
        panel.setBorder(new TitledBorder("Programacion"));
        panel.setBackground(new Color(250,252,255));

        panel.add(new JLabel("Inicio inscripcion (yyyy-MM-dd):"));
        txtInicioInscripcion = new JTextField(); panel.add(txtInicioInscripcion);

        panel.add(new JLabel("Fin inscripcion (yyyy-MM-dd):"));
        txtFinInscripcion = new JTextField(); panel.add(txtFinInscripcion);

        panel.add(new JLabel("Fecha inicio (yyyy-MM-dd):"));
        txtFechaInicio = new JTextField(); panel.add(txtFechaInicio);

        panel.add(new JLabel("Fecha fin (yyyy-MM-dd):"));
        txtFechaFin = new JTextField(); panel.add(txtFechaFin);

        panel.add(new JLabel("Ubicacion:"));
        txtUbicacion = new JTextField(); panel.add(txtUbicacion);

        return panel;
    }

    private JPanel getPanelProfesoresAsignados() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(new TitledBorder("Profesores asignados"));
        panel.setBackground(new Color(250,252,255));

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cmbProfesor = new JComboBox<>(); cargarProfesores();
        panelTop.add(new JLabel("Profesor:")); panelTop.add(cmbProfesor);

        txtRemuneracion = new JTextField(6); panelTop.add(new JLabel("Remuneracion:")); panelTop.add(txtRemuneracion);

        btnAnadirProfesor = new JButton("Incluir"); panelTop.add(btnAnadirProfesor);
        btnEliminarProfesor = new JButton("Eliminar"); panelTop.add(btnEliminarProfesor);

        panel.add(panelTop, BorderLayout.NORTH);
        
        chckEmpresa = new JCheckBox("Impartido por empresa");
        chckEmpresa.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
  
        		lbEmpresa.setEnabled(chckEmpresa.isSelected());
        		cbEmpresa.setEnabled(chckEmpresa.isSelected());
        		chckNuevaEmpresa.setEnabled(chckEmpresa.isSelected());
        		lbRemu.setEnabled(chckEmpresa.isSelected());
        		txtRemu.setEnabled(chckEmpresa.isSelected());
        		
        		if(!chckEmpresa.isSelected()) {
        			chckNuevaEmpresa.setSelected(false);
        			txtEmpresa.setEnabled(false);
            		lbNuevaEmpresa.setEnabled(false);
        		}
        	}
        });
        panelTop.add(chckEmpresa);

        modelProfesores = new DefaultTableModel(new Object[]{"Profesor","Remuneracion"},0);
        tableProfesoresAsignados = new JTable(modelProfesores);
        JScrollPane scroll = new JScrollPane(tableProfesoresAsignados);
        panel.add(scroll, BorderLayout.CENTER);
        
        JPanel panelSurProfesores = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lbEmpresa = new JLabel("Empresa:");
        lbEmpresa.setEnabled(false);
        panelSurProfesores.add(lbEmpresa);
        
        cbEmpresa = new JComboBox<>();
        cbEmpresa.setEnabled(false);cargarEmpresas();
        panelSurProfesores.add(cbEmpresa);
        
        chckNuevaEmpresa = new JCheckBox("Nueva");
        chckNuevaEmpresa.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(chckNuevaEmpresa.isSelected()) {
        			lbNuevaEmpresa.setEnabled(true);
            		txtEmpresa.setEnabled(true);
            		lbEmpresa.setEnabled(false);
            		cbEmpresa.setEnabled(false);
        		}else {
        			lbNuevaEmpresa.setEnabled(false);
            		txtEmpresa.setEnabled(false);
            		lbEmpresa.setEnabled(true);
            		cbEmpresa.setEnabled(true);
        		}
        		
        		
        	}
        });
        chckNuevaEmpresa.setEnabled(false);
        panelSurProfesores.add(chckNuevaEmpresa);
        
        lbNuevaEmpresa = new JLabel("Nombre:");
        lbNuevaEmpresa.setEnabled(false);
        panelSurProfesores.add(lbNuevaEmpresa);
        txtEmpresa = new JTextField(15);
        txtEmpresa.setEnabled(false);
        panelSurProfesores.add(txtEmpresa);
        panel.add(panelSurProfesores, BorderLayout.SOUTH);
        
        lbRemu = new JLabel("Remuneracion:");
        lbRemu.setEnabled(false);
        panelSurProfesores.add(lbRemu);
        
        txtRemu = new JTextField();
        txtRemu.setEnabled(false);
        panelSurProfesores.add(txtRemu);
        txtRemu.setColumns(10);


        btnAnadirProfesor.addActionListener(e -> {
            String prof = (String)cmbProfesor.getSelectedItem();
            String remuneracion = txtRemuneracion.getText().trim();
            if(prof != null && !remuneracion.isEmpty()) {
                modelProfesores.addRow(new Object[]{prof, remuneracion});
            }
        });

        btnEliminarProfesor.addActionListener(e -> {
            int row = tableProfesoresAsignados.getSelectedRow();
            if(row>=0) modelProfesores.removeRow(row);
        });

        return panel;
    }

    private JPanel getPanelNuevoProfesor() {
        JPanel panel = new JPanel(new GridLayout(5,2,5,5));
        panel.setBorder(new TitledBorder("Nuevo profesor"));
        panel.setBackground(new Color(250,252,255));

        panel.add(new JLabel("Nombre:"));
        txtNuevoNombre = new JTextField(); panel.add(txtNuevoNombre);

        panel.add(new JLabel("Apellido:"));
        txtNuevoApellido = new JTextField(); panel.add(txtNuevoApellido);

        panel.add(new JLabel("Email:"));
        txtNuevoEmail = new JTextField(); panel.add(txtNuevoEmail);

        panel.add(new JLabel("Telefono:"));
        txtNuevoTelefono = new JTextField(); panel.add(txtNuevoTelefono);
        
        panel.add(new JLabel(""));

        btnGuardarNuevoProfesor = new JButton("Guardar profesor"); panel.add(btnGuardarNuevoProfesor);

        btnGuardarNuevoProfesor.addActionListener(e -> {
            String nombre = txtNuevoNombre.getText().trim();
            String apellido = txtNuevoApellido.getText().trim();
            String email = txtNuevoEmail.getText().trim();
            String telefono = txtNuevoTelefono.getText().trim();
            if(nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Rellene nombre, apellido y email","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = service.cargarProfesor(nombre,apellido,email,telefono);
            if(ok) {
                JOptionPane.showMessageDialog(this,"Profesor incluido correctamente.");
                cargarProfesores();
                txtNuevoNombre.setText(""); txtNuevoApellido.setText(""); txtNuevoEmail.setText(""); txtNuevoTelefono.setText("");
            } else {
                JOptionPane.showMessageDialog(this,"No se pudo aincluir el profesor","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel getPanelCuotasAsignadas() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(new TitledBorder("Cuotas asignadas"));
        panel.setBackground(new Color(250,252,255));

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cmbCuotas = new JComboBox<>(); cargarCuotas();
        panelTop.add(new JLabel("Categoria:")); panelTop.add(cmbCuotas);

        txtValorCuota = new JTextField(6); panelTop.add(new JLabel("Valor:")); panelTop.add(txtValorCuota);

        btnAnadirCuota = new JButton("Incluir"); panelTop.add(btnAnadirCuota);
        btnEliminarCuota = new JButton("Eliminar"); panelTop.add(btnEliminarCuota);

        panel.add(panelTop, BorderLayout.NORTH);

        modelCuotas = new DefaultTableModel(new Object[]{"Categoria","Valor"},0);
        tableCuotasAsignadas = new JTable(modelCuotas);
        JScrollPane scroll = new JScrollPane(tableCuotasAsignadas);
        panel.add(scroll, BorderLayout.CENTER);
        
        chkGratuita = new JCheckBox("Actividad gratuita");
        JPanel panelCheckbox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCheckbox.add(chkGratuita);
        panel.add(panelCheckbox, BorderLayout.SOUTH);

        btnAnadirCuota.addActionListener(e -> {
            if(chkGratuita.isSelected()) return;
            String categoria = (String)cmbCuotas.getSelectedItem();
            String valor = txtValorCuota.getText().trim();
            if(categoria != null && !valor.isEmpty()) {
                modelCuotas.addRow(new Object[]{categoria, valor});
            }
        });

        btnEliminarCuota.addActionListener(e -> {
            if(chkGratuita.isSelected()) return;
            int row = tableCuotasAsignadas.getSelectedRow();
            if(row>=0) modelCuotas.removeRow(row);
        });

        chkGratuita.addActionListener(e -> {
            boolean seleccionada = chkGratuita.isSelected();
            btnAnadirCuota.setEnabled(!seleccionada);
            btnEliminarCuota.setEnabled(!seleccionada);
            txtValorCuota.setEnabled(!seleccionada);
            if (seleccionada) {
                modelCuotas.setRowCount(0);
            }
        });

        return panel;
    }

    private JPanel getPanelNuevaCuota() {
        JPanel panel = new JPanel(new GridLayout(2,1,5,5));
        panel.setBorder(new TitledBorder("Nueva cuota"));
        panel.setBackground(new Color(250,252,255));

        panel.add(new JLabel("Categoria:"));
        txtNuevaCategoria = new JTextField(); panel.add(txtNuevaCategoria);
        
        panel.add(new JLabel(""));

        btnGuardarNuevaCuota = new JButton("Guardar cuota"); panel.add(btnGuardarNuevaCuota);

        btnGuardarNuevaCuota.addActionListener(e -> {
            String categoria = txtNuevaCategoria.getText().trim();
            if(categoria.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Rellene la categoria","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = service.cargarCuota(categoria);
            if(ok) {
                JOptionPane.showMessageDialog(this,"Cuota incluida correctamente.");
                cargarCuotas();
                txtNuevaCategoria.setText("");
            } else {
                JOptionPane.showMessageDialog(this,"No se pudo incluida la cuota","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel getPanelSur() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        btnCancelar = new JButton("Cancelar");
        btnGuardarActividad = new JButton("Guardar actividad");
        panel.add(btnCancelar); panel.add(btnGuardarActividad);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardarActividad.addActionListener(e -> guardarActividad());

        return panel;
    }

    private void cargarProfesores() {
        cmbProfesor.removeAllItems();
        mapaProfesores.clear();
        List<Map<String,Object>> profesores = service.listarProfesores();
        for(Map<String,Object> prof : profesores) {
            String nombre = prof.get("nombre")+" "+prof.get("apellido");
            cmbProfesor.addItem(nombre);
            mapaProfesores.put(nombre, (Integer)prof.get("id_profesor"));
        }
    }

    private void cargarEmpresas() {
        cbEmpresa.removeAllItems();
        mapaEmpresas.clear();
        List<Map<String,Object>> empresas = service.listarEmpresas();
        for(Map<String,Object> emp : empresas) {
            String nombre = (String) emp.get("nombre");
            cbEmpresa.addItem(nombre);
            mapaEmpresas.put(nombre, (Integer)emp.get("id_profesor"));
        }
    }

    
    private void cargarCuotas() {
        cmbCuotas.removeAllItems();
        List<Map<String,Object>> cuotas = service.listarCuotas();
        if (cuotas == null) cuotas = List.of();
        for(Map<String,Object> c : cuotas) {
            cmbCuotas.addItem((String)c.get("categoria"));
        }
    }

    private void guardarActividad() {
    	LocalDate inicioIns, finIns, fechaInicio, fechaFin;
    	LocalDate fechaHoy = service.getFecha();   	
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe indicar un nombre para la actividad.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (modelProfesores.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe incluir al menos un profesor asignado.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!chkGratuita.isSelected() && modelCuotas.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe incluir al menos una cuota o marcar la actividad como gratuita.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (chckEmpresa.isSelected() && txtRemu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Se debe marcar remuneracion si la actividad la imparte una empresa.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (chckNuevaEmpresa.isSelected() && txtRemu.getText().trim().isEmpty()&& txtEmpresa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Se debe proporcionar remuneracion y nombre si la empresa es nueva.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            inicioIns = LocalDate.parse(txtInicioInscripcion.getText().trim());
            finIns = LocalDate.parse(txtFinInscripcion.getText().trim());
            fechaInicio = LocalDate.parse(txtFechaInicio.getText().trim());
            fechaFin = LocalDate.parse(txtFechaFin.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha invalido. Usa el formato yyyy-MM-dd",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (inicioIns.isAfter(finIns)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de inicio de inscripcion no puede ser posterior a la fecha de fin de inscripcion.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (finIns.isAfter(fechaInicio)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de fin de inscripcion no puede ser posterior al inicio de la actividad.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fechaInicio.isAfter(fechaFin)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de inicio de la actividad no puede ser posterior a la fecha de finalizacion.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fechaHoy.isAfter(inicioIns) || fechaHoy.isAfter(fechaInicio)) {
            JOptionPane.showMessageDialog(this,
                    "No puedes iniciar una inscripcion o actividad en una fecha anterior a hoy.",
                    "Error al registrar actividad",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idActividad = service.guardarActividad(
        	    txtNombre.getText().trim(),
        	    txtObjetivos.getText().trim(),
        	    txtContenidos.getText().trim(),
        	    inicioIns,
        	    finIns,
        	    fechaInicio,
        	    fechaFin,
        	    txtUbicacion.getText().trim(),
        	    txtPlazas.getText().isEmpty() ? 0 : Integer.parseInt(txtPlazas.getText()),
        	    chkGratuita.isSelected(),
        	    txtEmpresa.getText().trim()
        	);
        
        if (idActividad != -1) {
        	if(chckEmpresa.isSelected()) {
        		double remu=0;
        		if(chkGratuita.isSelected()) {
        			remu =0;
        		}else {
        			try {
        				remu = Double.parseDouble(txtRemu.getText().trim());
        			}catch (NumberFormatException ex) {
                    	JOptionPane.showMessageDialog(this,
                                "Remuneracion empresa invalida",
                                "Error al registrar actividad",
                                JOptionPane.ERROR_MESSAGE);
                    }
        			
        		}
        		
        		 String empresaTexto;
        		 int idEmpresa;
        		 if(chckNuevaEmpresa.isSelected()) {
        			 empresaTexto= txtEmpresa.getText();
        			 idEmpresa = service.insertarEmpresa(empresaTexto);
        		 }else {
        			 empresaTexto=(String)cbEmpresa.getSelectedItem();
        			 idEmpresa = mapaEmpresas.get(empresaTexto);
        		 }
                  
        		 String numeroFactura = "-1";
                 String fechaFactura = "";

                 String emisorNombre = empresaTexto;
                 String emisorNif = "N/A";
                 String emisorDireccion = "N/A";

        		service.insertFacturaP(idEmpresa, idActividad, numeroFactura, fechaFactura, remu, emisorNombre, emisorNif, emisorDireccion);
        		JOptionPane.showMessageDialog(this,
                        "Actividad guardada correctamente.");
                dispose();
        	}else {
        		
        	
        	int filas = modelProfesores.getRowCount();
            for (int r = 0; r < filas; r++) {
                String profesorTexto = (String) modelProfesores.getValueAt(r, 0);
                String remuneracionTxt = modelProfesores.getValueAt(r, 1).toString().replace(",", ".").trim();
                double remuneracion = 0.0;
                try {
                    remuneracion = Double.parseDouble(remuneracionTxt);
                } catch (NumberFormatException ex) {
                	JOptionPane.showMessageDialog(this,
                            "Remuneracion invalida",
                            "Error al registrar actividad",
                            JOptionPane.ERROR_MESSAGE);
                }

                Integer idProfesor = mapaProfesores.get(profesorTexto);
                if (idProfesor == null) {
                	JOptionPane.showMessageDialog(this,
                            "Profesor invalido",
                            "Error al registrar actividad",
                            JOptionPane.ERROR_MESSAGE);
                }

                String numeroFactura = "AUTO-" + idActividad + "-" + (r+1);
                String fechaFactura = LocalDate.now().toString();

                String emisorNombre = profesorTexto;
                String emisorNif = "N/A";
                String emisorDireccion = "N/A";

                service.insertFacturaP(idProfesor, idActividad, numeroFactura, fechaFactura, remuneracion, emisorNombre, emisorNif, emisorDireccion);
            }
            
            if (!chkGratuita.isSelected()) {
                int filasCuotas = modelCuotas.getRowCount();
                for (int r = 0; r < filasCuotas; r++) {
                    String categoria = (String) modelCuotas.getValueAt(r, 0);
                    String valorTxt = modelCuotas.getValueAt(r, 1).toString().replace(",", ".").trim();
                    double valor = 0.0;
                    try {
                        valor = Double.parseDouble(valorTxt);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Valor de cuota invalido en la fila " + (r+1),
                                "Error al registrar actividad",
                                JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    service.asociarCuotaActividad(idActividad, categoria, valor);
                }
            }
        	
            JOptionPane.showMessageDialog(this,
                    "Actividad guardada correctamente.");
            dispose();
        	}
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la actividad.",
                    "Error al registrar actividad",
                    JOptionPane.ERROR_MESSAGE);
        }

    }
}
