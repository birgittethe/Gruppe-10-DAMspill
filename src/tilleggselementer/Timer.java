package tilleggselementer;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

// Brukes til å håndtere tidtaking i applikasjonen
public class Timer extends JPanel {

	private JLabel time;
	private boolean started = false;
	private long startTime = 0;
	private String statusTid;

	public Timer() {
		statusTid= "00:00";
    	// JPanel og JLabel-innstillinger
		setBackground(Color.WHITE);
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBounds(21, 24, 76, 50);
    	setLayout(null);
    	time = new JLabel("");
    	time.setBackground(Color.WHITE);
    	time.setHorizontalAlignment(SwingConstants.CENTER);
    	time.setBounds(0, 0, 77, 50);
    	time.setFont(new Font("Ebrima", Font.BOLD, 22));
    	add(time);
    	//starter tråd som oppdaterer tid
    	new Thread(new Runnable() {
    		public void run() { 
    			try {
    				updateTime(); 
    			} 
    			catch (Exception ie) {}
    		}
    		}).start();
   
		}

	// Oppdaterer timeren til riktig tid
   public void updateTime() {
	   try {
		   while(true) {
			   //henter tid i min sek format
			   time.setText(hentTid());
			   Thread.currentThread();
			   //1 sek
			   Thread.sleep(1000);
		   }
	   }
	   catch (Exception e) {}
   	}

   // Starter timeren
   public void start(){
	   started=true;
	   startTime = System.currentTimeMillis()/1000;
   }
   
   // Stopper tiden
   public void stopp(){
	   started = false;
	   long elapsedTime = System.currentTimeMillis()/1000 - startTime;
	   String s = Integer.toString((int)(elapsedTime % 60));
	   String m = Integer.toString((int)((elapsedTime % 3600) / 60));
	   if (s.length() < 2){
		   s = "0" + s;
	   }
	   if (m.length() < 2){
		   m = "0" + m;
	   }
	   statusTid = m+":"+s;
	 }
   
   // Henter riktig tid
   public String hentTid() {
	   if(started){		   
		   long tidSidenStart = System.currentTimeMillis()/1000 - startTime;
		   String s = Integer.toString((int)(tidSidenStart % 60));
		   String m = Integer.toString((int)((tidSidenStart % 3600) / 60));
		   if (s.length() < 2){
			   s = "0" + s;
		   }
		   if (m.length() < 2){
			   m = "0" + m;
		   }
		   return m+":"+s;
	   }
	   else{
		   return statusTid;
	   }
   }
   
}