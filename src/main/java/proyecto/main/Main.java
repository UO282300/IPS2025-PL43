package proyecto.main;

import java.awt.EventQueue;

import javax.swing.UIManager;

import proyecto.view.VentanaPrincipal;

public class Main {
	public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
					
					new VentanaPrincipal();
					
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			}
		});
    }
}