package com.mrhid6.engine.launcher;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class LauncherGUI extends JFrame{

	private static final long serialVersionUID = -5962678664114846434L;

	private MainPanel panel;

	public LauncherGUI() throws Exception{
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		SwingUtilities.updateComponentTreeUI(this);

		panel = new MainPanel();
		panel.setSize(getSize());
		panel.setPreferredSize(getSize());

		setTitle(Launcher.TITLE + " Launcher");
		setSize(800, 600);
		setPreferredSize(getSize());
		setResizable(false);

		setLayout(null);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				int i=JOptionPane.showConfirmDialog(null, "Are You Sure You Want To Close This?");
				if(i==0)
					System.exit(0);
			}
		});



		setContentPane(panel);

		setVisible(true);
	}
}

class MainPanel extends JPanel{

	private static final long serialVersionUID = -2291801537060598641L;
	
	public MainPanel() {
		setLayout(null);
		setBounds(0, 0, 800, 600);
		JButton playButton = new JButton("Play");
		playButton.setBounds(660, 500, 110, 50);
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		add(playButton);
	}
}
