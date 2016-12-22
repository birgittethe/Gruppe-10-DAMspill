package spillelementer;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

// Hovedvindu til applikasjonen
// Instansierer en ny DamController
public class MultiplayerMainframe extends JFrame {
	
	public MultiplayerMainframe() {
		setResizable(false);
		setVisible(true);
		setTitle("DAM - Multiplayer");
	  	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  	setBounds(100, 100, 600, 426);
		try {
			BufferedImage kroneIkon = ImageIO.read(getClass().getResourceAsStream("/kronen.png"));
		  	setIconImage(kroneIkon);
		} catch (IOException e) {}
	  	DamController brettSpill = new DamController();
	  	add(brettSpill);
	}
	  	
}
