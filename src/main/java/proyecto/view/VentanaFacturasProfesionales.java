package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import proyecto.service.UserService;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class VentanaFacturasProfesionales extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable tableActividades;
    private JTable tableInscripciones;
    private DefaultTableModel modelActividades;
    private DefaultTableModel modelInscripciones;
    private int idActividadSeleccionada = -1;
    private int idMatriculaSeleccionada = -1;
    private UserService service;
    private Map<Integer, Map<String, Object>> actividadData = new HashMap<>();
    private Map<Integer, Map<String, Object>> inscripcionData = new HashMap<>();
    private JTextField tfNumeroFactura;
    private JTextField tfFechaFactura;
    private JTextField tfImporteFactura;

    public VentanaFacturasProfesionales(UserService service) {
    	this.service=service;
        setTitle("Registrar Factura a Profesionales");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1500, 800);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);

        contentPane.add(crearTitulo(), BorderLayout.NORTH);
        contentPane.add(crearPanelCentral(), BorderLayout.CENTER);
        contentPane.add(crearPanelInferior(), BorderLayout.SOUTH);

        agregarListeners();
        cargarActividadesCompletas();
    }

    private JLabel crearTitulo() {
        JLabel lblTitulo = new JLabel("Registrar Factura a Profesionales", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return lblTitulo;
    }

    private JPanel crearPanelCentral() {
        JPanel panelCentral = new JPanel(new GridLayout(3, 1, 10, 10));

        panelCentral.add(crearTablaActividades());
        panelCentral.add(crearTablaInscripciones());
        panelCentral.add(crearPanelFactura());

        return panelCentral;
    }


    private JScrollPane crearTablaActividades() {
        modelActividades = new DefaultTableModel(
            new Object[]{"ID","Nombre","Inicio Curso", "Fin Curso", "Estado"}, 0
        ) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableActividades = new JTable(modelActividades);
        tableActividades.setRowHeight(25);
        tableActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumn idCol = tableActividades.getColumnModel().getColumn(0);
        idCol.setMinWidth(0);
        idCol.setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tableActividades);
        scroll.setBorder(BorderFactory.createTitledBorder("Cursos"));
        return scroll;
    }


    @SuppressWarnings("serial")
    private JScrollPane crearTablaInscripciones() {

        modelInscripciones = new DefaultTableModel(
            new Object[]{"Id","Nombre", "Apellido", "Estado",
                "Total pagado (�)", "Pago Total (�)"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableInscripciones = new JTable(modelInscripciones);
        tableInscripciones.setRowHeight(25);
        tableInscripciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumn idCol = tableInscripciones.getColumnModel().getColumn(0);
        idCol.setMinWidth(0);
        idCol.setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tableInscripciones);
        scroll.setBorder(BorderFactory.createTitledBorder("Alumnos"));
        
        return scroll;
    }

    private JPanel crearPanelFactura() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBorder(BorderFactory.createTitledBorder("Factura"));
        panel.add(new JLabel("N�mero de factura:"));
        tfNumeroFactura = new JTextField();
        panel.add(tfNumeroFactura);
        panel.add(new JLabel("Fecha de emisi�n:"));
        tfFechaFactura = new JTextField();
        panel.add(tfFechaFactura);

        panel.add(new JLabel("Importe (�):"));
        tfImporteFactura = new JTextField();
        tfImporteFactura.setEditable(false);
        panel.add(tfImporteFactura);

        return panel;
    }
   

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(null);
        panelInferior.add(crearPanelBotones(), BorderLayout.SOUTH);

        return panelInferior;
    }


    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));

        JButton btnRegistrar = new JButton("Generar Factura");
        btnRegistrar.setBackground(Color.WHITE);
        

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(Color.WHITE);

        panel.add(btnVolver);
        panel.add(btnRegistrar);
        
        btnRegistrar.addActionListener(e -> generarFactura());
        btnVolver.addActionListener(e -> dispose());

        return panel;
    }


    private void generarFactura() {
    	if (!validarSeleccion()) return;
    	String numeroFact = validarNumeroFactura();
    	if(numeroFact==null) {
    		mostrarError("Numero de la factura no es correcto");
    		return;
    	}
    	Double cantidad = validarCantidad();
        if (cantidad == null) return;
        LocalDate fechaMovimiento = validarFecha();
        if (fechaMovimiento == null) {
        	//mostrarError("La fecha seleccionada no es correcta");
        	return;
        }
        double pagado = service.getTotalPagado(idMatriculaSeleccionada);
        if (pagado < cantidad) {

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "El pago no est� completo.\n�Desea generar la factura igualmente?",
                    "Confirmar generaci�n",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (opcion != JOptionPane.YES_OPTION) {
                return; 
            }
        }
        if(isFacturada()) {
        	mostrarError("Ya est� facturada");
        	return;
        }
        if(generaFactura(cantidad,fechaMovimiento,numeroFact)) {
    		mostrarInfo("Factura generada correctamente");
    		cargarInscripcionesPendientes();
    	}else {
    		mostrarError("Ya existe factura con ese numero");
    	}
	}

	private String validarNumeroFactura() {
		String numero = tfNumeroFactura.getText();
		if(numero==null || numero.isBlank()) {
			return null;
		}else {
			return numero;
		}
	}
	
	
	private boolean isFacturada() {
		return service.getFacturada(idMatriculaSeleccionada);
	}
	
	private boolean generaFactura(double cantidad, LocalDate fechaMovimiento, String numero) {
		return service.generaFactura(cantidad,fechaMovimiento, numero, idMatriculaSeleccionada);	
	}

	private void agregarListeners() {

        tableActividades.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
            	int row = tableActividades.getSelectedRow();
                idActividadSeleccionada = (int) modelActividades.getValueAt(row, 0);
                System.out.println("Curso seleccionado: fila " + idActividadSeleccionada);
                cargarInscripcionesPendientes();
            }
        });

        tableInscripciones.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
            	int row = tableInscripciones.getSelectedRow();
            	idMatriculaSeleccionada = (int) modelInscripciones.getValueAt(row, 0);
                System.out.println("Alumno seleccionado: fila " + idMatriculaSeleccionada);
                
                cargarFacturaAlumno();
            }
        });
    }


    protected void cargarFacturaAlumno() {
    	tfFechaFactura.setText(service.getFechaHoy().toString());
    	double total = service.getTotal(idMatriculaSeleccionada);
    	tfImporteFactura.setText(total + " �");
		
	}

	
	private void cargarActividadesCompletas() {
        modelActividades.setRowCount(0);
        actividadData.clear();

        List<Map<String, Object>> actividades = service.listarTodosLosCursos(); 
        if (actividades == null || actividades.isEmpty()) {
            modelActividades.addRow(new Object[]{"-", "No hay actividades disponibles", "-", "-", "-", "-", "-", "-", "-" });
            tableActividades.setEnabled(false);
            return;
        }

        LocalDate hoy = service.getFechaHoy();

        for (Map<String, Object> act : actividades) {
            int id = ((Number) act.get("id_actividad")).intValue();
            String nombre = (String) act.get("nombre");

            LocalDate inicioInscripcion = act.get("inicio_inscripcion") != null
                    ? LocalDate.parse(act.get("inicio_inscripcion").toString())
                    : null;
            LocalDate finInscripcion = act.get("fin_inscripcion") != null
                    ? LocalDate.parse(act.get("fin_inscripcion").toString())
                    : null;
            LocalDate fechaInicio = act.get("fecha_inicio") != null
                    ? LocalDate.parse(act.get("fecha_inicio").toString())
                    : null;
            LocalDate fechaFin = act.get("fecha_fin") != null
                    ? LocalDate.parse(act.get("fecha_fin").toString())
                    : null;

            int totalPlazas = act.get("total_plazas") != null ? ((Number) act.get("total_plazas")).intValue() : 0;

            Map<String, Object> detalles = service.getActividadDetalles(id);
            
            int plazasDisponibles = 0;

            if (detalles != null && detalles.get("plazas_disponibles") != null) {
                plazasDisponibles = ((Number) detalles.get("plazas_disponibles")).intValue();
            } else {
                plazasDisponibles = totalPlazas;
            }



            boolean isClosed = act.get("isClosed") != null && ((Number) act.get("isClosed")).intValue() == 1;
            boolean isCancelada = act.get("isCancelada") != null && ((Number) act.get("isCancelada")).intValue() == 1;

            String estado;
            if (isClosed) {
                estado = "Cerrada";
            } else if (isCancelada) {
                estado = "Cancelada";
            } else if (inicioInscripcion != null && finInscripcion != null &&
                       !hoy.isBefore(inicioInscripcion) && !hoy.isAfter(finInscripcion)) {
                estado = "Periodo de inscripción";
            } else if (fechaInicio != null && fechaFin != null &&
                       !hoy.isBefore(fechaInicio) && !hoy.isAfter(fechaFin)) {
                estado = "En curso";
            } else if (finInscripcion != null && fechaInicio != null &&
                       hoy.isAfter(finInscripcion) && hoy.isBefore(fechaInicio)) {
                estado = "Por empezar";
            } else if (fechaFin != null && hoy.isAfter(fechaFin)) {
                estado = "Cursada";
            } else {
                estado = "Sin actividad";
            }

            modelActividades.addRow(new Object[]{
            	id,
                nombre,
                fechaInicio != null ? fechaInicio.toString() : "-",
                fechaFin != null ? fechaFin.toString() : "-",
                estado
            });

            actividadData.put(id, act);
        }

        tableActividades.setEnabled(modelActividades.getRowCount() > 0);
    }
	
	private void cargarInscripcionesPendientes() {
    	modelInscripciones.setRowCount(0);
        inscripcionData.clear();
        idMatriculaSeleccionada = -1;

        if (idActividadSeleccionada == -1) return;

        Map<String, Object> actividad = service.getActividadDetalles(idActividadSeleccionada);
        if (actividad == null) return;

        @SuppressWarnings("unchecked")
		List<Map<String, Object>> inscripciones = (List<Map<String, Object>>) actividad.get("inscripciones");
        if (inscripciones == null || inscripciones.isEmpty()) return;

        for (Map<String, Object> ins : inscripciones) {
            int idMatricula = ((Number) ins.get("id_matricula")).intValue();
            String nombreCompleto = (String) ins.get("nombre_alumno");
            String[] partes = nombreCompleto.split(" ", 2);
            String nombre = partes.length > 0 ? partes[0] : "";
            String apellido = partes.length > 1 ? partes[1] : "";
            String estado;
            boolean facturada = service.getFacturada(idMatricula);
            if(facturada) estado = "Facturado";
            else estado = "Sin facturar";
            
            double totalPagado = service.getTotalPagado(idMatricula);
            double total = service.getTotal(idMatricula);

            modelInscripciones.addRow(new Object[]{
                idMatricula, nombre, apellido, estado,
                String.format("%.2f", totalPagado), String.format("%.2f", total)
            });


            inscripcionData.put(idMatricula, ins);

        }
    }
	
	private boolean validarSeleccion() {
        if (idActividadSeleccionada == -1) {
            mostrarError("Debe seleccionar un curso de la tabla.");
            return false;
        }
        if (idMatriculaSeleccionada == -1) {
            mostrarError("Debe seleccionar un alumno de la lista.");
            return false;
        }
        return true;
    }
	
	private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
	
	private Double validarCantidad() {
        try {
        	List<String> partes = Arrays.asList(tfImporteFactura.getText().trim().split("\\s+"));
            double cantidad = Double.parseDouble(partes.get(0));
            if (cantidad <= 0) throw new NumberFormatException();
            return cantidad;
        } catch (NumberFormatException e) {
            mostrarError("Ingrese una cantidad valida y mayor que 0.");
            return null;
        }
    }
    private LocalDate validarFecha() {
        LocalDate fechaMovimiento;
        try {
            fechaMovimiento = LocalDate.parse(tfFechaFactura.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            mostrarError("Formato de fecha invalido. Use el formato yyyy-MM-dd (por ejemplo, 2025-11-09).");
            return null;
        }

        LocalDate fechaHoy = service.getFechaHoy();
        LocalDate fechaMatricula = service.getFechaMatricula(idMatriculaSeleccionada);

        if (fechaMovimiento.isAfter(fechaHoy)) {
            mostrarError(String.format(
                "No se puede registrar una fecha futura.\n\n" +
                "Fecha introducida: %s\n" +
                "Fecha actual del sistema: %s",
                fechaMovimiento, fechaHoy
            ));
            return null;
        }

        if (fechaMovimiento.isBefore(fechaMatricula)) {
            mostrarError(String.format(
                "La fecha de la factura no puede ser anterior a la fecha de matricula.\n\n" +
                "Fecha matrícula: %s\n" +
                "Fecha introducida: %s",
                fechaMatricula, fechaMovimiento
            ));
            return null;
        }

        return fechaMovimiento;
    }
    
    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }
}

