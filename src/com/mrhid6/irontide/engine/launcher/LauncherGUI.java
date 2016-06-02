package com.mrhid6.irontide.engine.launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.mrhid6.irontide.settings.Constants;
import com.mrhid6.irontide.settings.GameSettings;

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

		setTitle(Constants.TITLE + " Launcher - v"+GameSettings.CURRENTVERSION.displayVersion());
		setSize(800, 600);
		setPreferredSize(getSize());
		setResizable(false);

		setLayout(null);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
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
				try {
					System.out.println("play");
					
					String JarFile = GameSettings.INSTALLDIR+Constants.FS+"Irontide.jar";
					System.out.println(JarFile);
					
					ProcessBuilder pb = new ProcessBuilder(
								"java", "-jar", JarFile
							);
					Process p = pb.start();
					System.exit(0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		add(playButton);
	}
}
