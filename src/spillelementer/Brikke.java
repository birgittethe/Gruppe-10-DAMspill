package spillelementer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

// Brukes for å håndtere brikker i DAM-spillet
// Inneholder posisjonen, brikketype, brikkebildet, X, og Y variabler til brikken
public class Brikke extends JPanel {
	
	int x;
	int y;
	BufferedImage brikkeBilde;
	int brikkeType; // Brikketype forklarer hvilken brikke brikken er; 1-4
	int position; // Koordinatene til brikken i brettarrayet - 0-63
	
	Brikke(int pos, int brikkeType){
		this.position = pos;
		y = pos/8*50;
		x = pos%8*50;
		this.brikkeType = brikkeType;
		setFocusable(true);
		requestFocus();
		setBackground(Color.WHITE);
		hentBrikkeBilde();
	}

	// Plasserer brikken midt i feltet
	public void sentrerBrikke(int pos){
		y = pos/8*50;
		x = pos%8*50;
	}
	
	// Plasserer brikken til feltet det sto på ved ulovlige trekk
	public void resetBrikke(){
		y = position/8*50;
		x = position%8*50;
	}
	
	// Henter riktig bilde
	public void hentBrikkeBilde() {
		try {
			BufferedImage hvitBonde = ImageIO.read(getClass().getResourceAsStream("/white.png"));
			BufferedImage hvitKonge = ImageIO.read(getClass().getResourceAsStream("/whiteking.png"));
			BufferedImage svartBonde = ImageIO.read(getClass().getResourceAsStream("/black.png"));;
			BufferedImage svartKonge = ImageIO.read(getClass().getResourceAsStream("/blackking.png"));
			switch(brikkeType){
				case 1 : 	brikkeBilde = hvitBonde;
							break;
				case 2 : 	brikkeBilde = hvitKonge;
							break;
				case 3 : 	brikkeBilde = svartBonde;
							break;
				case 4 : 	brikkeBilde = svartKonge;
							break;
			}	
		} catch (IOException e) {
		}
	}
	
	//Tegner brikken basert på X,Y og brikkeBilde
	public void tegnBrikke(Graphics g) {
		g.drawImage(brikkeBilde, x+(32/4), y+(32/4),null); // 32/4 fordi brikken er 32x32 pixler, og 32/4 vil midstille den i feltet
	}
}
