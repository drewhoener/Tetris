package me.drewhoener.tetris;

import javax.swing.*;

public class MainUnit {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Tetris");
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				TetrisPanel panel = new TetrisPanel();
				frame.setContentPane(panel);
				frame.pack();
				frame.setVisible(true);
			}
		});

	}

}
