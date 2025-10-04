package proyecto.main;

import java.awt.EventQueue;

import proyecto.view.VentanaPrincipal;

public class Main {
	public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new VentanaPrincipal();
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			}
		});
    }
}
