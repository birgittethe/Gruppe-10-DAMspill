package spillelementer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

// Brukes til å tegne grafikk i "DamController"
public class Grafikk{
	
	private BufferedImage brett;

	//Fargekodene vi brukker
	private Color lovligeTrekkFarge = new Color(204, 255, 204,130);
	private Color pilFarge = new Color(255,255,0);
	private Color valgtBrikkeFarge = new Color(128, 204, 255,190);
	private Color deaktivertBrikkeFarge = new Color(0,0,0,90);
	private Color slaUtBrikkeFarge = new Color(255, 26, 26,90);

	public Grafikk(){
		try {
			// Henter brett-bildet
			brett = ImageIO.read(getClass().getResourceAsStream("/board.png"));
		} catch (IOException e) {
		}
	}

	//Tegner en pil fra felt "fra" til felt "til"
	public Graphics tegnTrekant(Graphics g, int til, int fra) {
		int x1 = (til%8)*50+25; 
		int y1 = (til/8)*50+25;
		int x2 = (fra%8)*50+25; 
		int y2 = (fra/8)*50+25;
		g.setColor(pilFarge);
		Graphics2D g2d = (Graphics2D)g;
	    g2d.setStroke(new BasicStroke(5));
		g2d.drawLine(x1, y1, x2, y2);
		Polygon trekant = lagTrekant(x1, x2, y1, y2);
		g2d.drawPolygon(trekant);
		g2d.fillPolygon(trekant);
		return g;
		
	}
	
	// Lager tuppen på pila slik at den indikerer hvilken vei trekket er gjort
	private Polygon lagTrekant(int x1, int x2, int y1, int y2) {
		Polygon t = new Polygon();
		if(x1 < x2 && y1 < y2){ // SW 
			t = new Polygon(new int[] {x1, x1, x1+15}, new int[]{y1,y1+15,y1}, 3);
		}
		if(x1 < x2 && y1 > y2){ // SE
			t = new Polygon(new int[] {x1, x1, x1+15}, new int[]{y1,y1-15,y1}, 3);		
		}
		if(x1 > x2 && y1 < y2){ // NW
			t = new Polygon(new int[] {x1, x1, x1-15}, new int[]{y1,y1+15,y1}, 3);
		}
		if(x1 > x2 && y1 > y2){ // SE
			t = new Polygon(new int[] {x1, x1, x1-15}, new int[]{y1,y1-15,y1}, 3);
		}
		if(x1 == x2 && y1 < y2){ // S
			t = new Polygon(new int[] {x1-7, x1, x1+7}, new int[]{y1+15,y1,y1+15}, 3);
		}
		if(x1 == x2 && y1 > y2){ // N
			t = new Polygon(new int[] {x1-7, x1, x1+7}, new int[]{y1-15,y1,y1-15}, 3);
		}
		if(x1 < x2 && y1 == y2){ // W
			t = new Polygon(new int[] {x1, x1+15, x1+15}, new int[]{y1,y1+7,y1+7}, 3);
		}
		if(x1 > x2 && y1 == y2){ // E
			t = new Polygon(new int[] {x1, x1-15, x1-15}, new int[]{y1,y1-7,y1-7}, 3);
		}
		return t;
	}

	//Tegner brettet
	public Graphics tegnBrett(Graphics g) {
		g.drawImage(brett, 0, 0, null);
		return g;
	}

	// Fargelegger feltet til valgtBrikke
	public Graphics tegnValgtBrikke(Graphics g, int valgtBrikke) {
		int x = valgtBrikke%8;
		int y = valgtBrikke/8;
		g.setColor(valgtBrikkeFarge);
		g.fillRect (x*50, y*50, 50, 50);
		return g;
	}

	// Fargelegger feltet til "deaktiverteBrikker"
	public Graphics tegnDeaktiverteBrikker(Graphics g, int i) {
		int x = i%8;
		int y = i/8;
		g.setColor(deaktivertBrikkeFarge);
		g.fillRect(x*50, y*50, 50, 50); 
		return g;
	}

	// Fargelegger feltene til "lovligeTrekk"
	public Graphics tegnLovligeTrekk(Graphics g, int i) {
		int x = i%8;
		int y = i/8;
		g.setColor(lovligeTrekkFarge);
		g.fillRect(x*50, y*50, 50, 50);
		return g;
	}

	// Fargelegger feltene tilhørende ett hopp
	public Graphics tegnLovligeHopp(int x1, int y1, int x2, int y2, Graphics g) {
		g.setColor(slaUtBrikkeFarge);
		g.fillRect(x1*50, y1*50, 50, 50);
		g.setColor(lovligeTrekkFarge);
		g.fillRect(x2*50, y2*50, 50, 50);
		return g;
	}	
}