package com.mrhid6.engine.launcher;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.mrhid6io.utils.Utils;

public class InstallerGUI extends JFrame{

	private static final long serialVersionUID = 5305360620013039537L;

	private JProgressBar progressBar;
	private JLabel titleText;
	private JLabel infoText;
	private JButton installButton;
	private JLabel myversion;
	private JLabel newversion;
	

	public InstallerGUI() {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		SwingUtilities.updateComponentTreeUI(this);
		
		setTitle(Launcher.TITLE + " Installer");
		setSize(300, 200);
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

		titleText = new JLabel(Launcher.TITLE + " Installer ");
		titleText.setBounds(10, 5, 220, 25);
		
		infoText = new JLabel("A newer version is avaliable to install..");
		infoText.setBounds(10, 22, 220, 25);
		infoText.setFont(new Font("Arial", Font.PLAIN, 10));
		
		progressBar = new JProgressBar(0,1);
		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 80, 260, 30);
		progressBar.setString("0.0%");
		
		installButton = new JButton("Install");
		installButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!Installer.getInstance().isDone()){
					Installer.getInstance().downloadFiles();
				}else{
					System.out.println("open launcher!");
				}
			}
		});
		installButton.setBounds(90, 120, 100, 30);
		
		GameVersion cv = Launcher.getCurrentVersion();
		GameVersion sv = Launcher.getServerVersion();
		
		myversion = new JLabel("Current Version: " + cv.displayVersion());
		newversion = new JLabel("New Version: " + sv.displayVersion());
		
		myversion.setBounds(10, 42, 120, 25);
		newversion.setBounds(10, 55, 120, 25);
		
		myversion.setFont(new Font("Arial", Font.PLAIN, 10));
		newversion.setFont(new Font("Arial", Font.PLAIN, 10));
		
		add(progressBar);
		add(titleText);
		add(infoText);
		add(installButton);
		add(myversion);
		add(newversion);
		pack();

		setVisible(true);
	}

	public void setProgressBarMax(int max){
		progressBar.setMaximum(max);
	}

	public void setProgressBarVal(int val){
		progressBar.setValue(val);
		float percent = 0;
		if(progressBar.getMaximum()>0 && progressBar.getValue()>0){
			percent = ((float)progressBar.getValue() / (float)progressBar.getMaximum())*100.0F;
			percent = Utils.round(percent, 1);
		}
		progressBar.setString(percent+"%");
	}
	
	public void setDone(){
		installButton.setText("Finish");
	}
}
