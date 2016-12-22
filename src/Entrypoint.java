import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import spillelementer.MultiplayerMainframe;

public class Entrypoint extends JFrame {

	 private static  JFrame f;
	 private static JPanel p;
	 private static JButton b1;
	 private static JLabel lab;
	
	public static void main(String[] args) {
		  f = new JFrame("DAM");
		  f.setIconImage(Toolkit.getDefaultToolkit().getImage("bin/kronen.png"));
		  f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  JMenuBar mb = new JMenuBar();
		  ImageIcon avsluttImage = new ImageIcon("res/avslutt.png");
		  JMenu file = new JMenu("fil");
		  file.setMnemonic(KeyEvent.VK_F);
		  JMenuItem eMenuItem = new JMenuItem("avslutt", avsluttImage);
		  eMenuItem.setMnemonic(KeyEvent.VK_E);
		  eMenuItem.setToolTipText("Avslutt DAM");
		  eMenuItem.addActionListener((ActionEvent event) -> {
			  System.exit(0);
		  });
		  file.add(eMenuItem);
		  mb.add(file);
		  f.setJMenuBar(mb);
		  lab = new JLabel("<html>Velkommen Til spillet DAM.<br> Trykk på Multiplayer for og starte et spill!</html>", SwingConstants.CENTER);
		  lab.setPreferredSize(new Dimension(600, 350));
		  f.getContentPane().add(lab, BorderLayout.CENTER);
		  f.pack();
		  f.setVisible(true);
		  f.setResizable(false);
		  p = new JPanel();
		  p.setBackground(Color.white);
		  ImageIcon flerspillerImage = new ImageIcon("res/flerspiller.png");
		  b1 = new JButton("Multiplayer", flerspillerImage);
		  p.add(b1);
		  f.add(p);
		  f.setLocationRelativeTo(null);
		  b1.setFont(new Font("Serif", Font.CENTER_BASELINE, 20));
		  b1.setForeground(Color.DARK_GRAY);
		  b1.setPreferredSize(new Dimension(200, 100));
		  b1.setOpaque(true);
		  b1.addActionListener(
		  new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  f.setVisible(false);
				  MultiplayerMainframe damspill = new MultiplayerMainframe();
				  damspill.setVisible(true);
			  }
		  });
		}
	
}

