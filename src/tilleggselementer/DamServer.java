package tilleggselementer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class DamServer{
	
	private Socket socketForKlient;
	private String ip = "127.0.0.1";
	private int port = 6787;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ServerSocket socketForVert;
	private boolean started = false;
	private boolean erJegVert = false;
	
	public DamServer() {
	}
	
	public boolean getStarted(){
		return started;
	}
	
	public void setStarted(boolean f){
		started = f;
	}

	public boolean getErJegVert(){
		return erJegVert ;
	}
	
	public boolean koblerTilVert(){
		try{
			socketForKlient = new Socket(ip, port);
			out = new ObjectOutputStream(socketForKlient.getOutputStream());
			in = new ObjectInputStream(socketForKlient.getInputStream());
		}
		catch(IOException e){
			return false;
		}
		started = true;
		System.out.println("Motspiller er funnet - spillet er i gang!");
		return true;
	}
	
	public void startVert(){
		try{
			System.out.println("Fant ingen vert. Blir vert for et spill og venter på motspiller.");
			socketForVert = new ServerSocket(port, 8, InetAddress.getByName(ip));
			erJegVert = true;
		}
		catch(Exception ex){
			ex.printStackTrace(); 
		}
	}
	
	public void lyttEtterKlient(){
		try{
			Socket klient = socketForVert.accept();
			out = new ObjectOutputStream(klient.getOutputStream());
			in = new ObjectInputStream(klient.getInputStream());
			started = true;
		}
		catch(IOException i){
			i.printStackTrace();
		}
		System.out.println("Motspiller er funnet - spillet er i gang!");
	}
	
	public void sendInt(int[] ia) throws IOException {
		out.writeObject(ia);
		out.flush();
	}
	
	public int[] lesInt() throws IOException, ClassNotFoundException {
		int[] ia = (int[]) in.readObject();
		return ia;
			
	}
	
}