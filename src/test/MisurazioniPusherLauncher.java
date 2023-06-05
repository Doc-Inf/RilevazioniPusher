package test;

import javax.swing.SwingUtilities;

import view.RilevazioniPusherGUI;

public class MisurazioniPusherLauncher {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(()-> new RilevazioniPusherGUI());

	}

}
