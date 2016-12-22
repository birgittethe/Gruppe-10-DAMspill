package tilleggselementer;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

// Brukes til å spille av en "flytt"-lyd ved utførelse av trekk og hopp
public class Lydeffekt {

	// Spiller av "flytt"-lyden
	public void flytt(){
		try {
			AudioInputStream a = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("move.wav"));
			Clip c = AudioSystem.getClip();
	        c.open(a);
	        c.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
}
