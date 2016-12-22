package tilleggselementer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class TurIndikator extends JPanel {
	
		private BufferedImage hvitKonge, svartKonge;
		boolean hentetBilder = false;
		private boolean jegErHvit, minTur, started;

		public TurIndikator() {
			setFocusable(true);
			requestFocus();
			setToolTipText("Denne brikken sin tur");
			setBackground(Color.WHITE);
		    setBorder(new LineBorder(new Color(0, 0, 0)));
		    setBounds(107, 24, 69, 50);
			if(!hentetBilder){
				try{
					hvitKonge = ImageIO.read(getClass().getResourceAsStream("/white.png"));
					svartKonge = ImageIO.read(getClass().getResourceAsStream("/black.png"));
					hentetBilder = true;
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			render(g);
			Toolkit.getDefaultToolkit().sync();
		}
		
		private void render(Graphics g) {
			if(started){
				BufferedImage brikke = null;
				if(jegErHvit && minTur|| !minTur && !jegErHvit){						
					brikke = hvitKonge;
				}
				else{
					brikke = svartKonge;
				}
				g.drawImage(brikke,9+9+(1/2),9,null);	
			}
		}

		public void jegErHvit() {
			started = true;
			jegErHvit = true;
		}
		
		public void jegErSvart(){
			started = true;
			jegErHvit = false;
		}
		
		public void updateTur(boolean minTur){
			this.minTur = minTur;
			repaint();
		}

	}