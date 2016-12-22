package spillelementer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import spilllogikk.BrikkeOppsett;
import tilleggselementer.DamServer;
import tilleggselementer.Lydeffekt;
import tilleggselementer.Timer;
import tilleggselementer.TurIndikator;

public class DamController extends JPanel implements Runnable, MouseListener, MouseMotionListener{
	
	 //OBJEKTER;
	private TurIndikator turviser;
	private DamServer con;
	private BrikkeOppsett brikkeOppsett;
	private Timer timer;
	private Lydeffekt lydeffekt;
	private Grafikk grafikk;
	
	private int valgtBrikke = 0;
	private int antallFeilmeldinger;
	
	private boolean minTur = false; // Svarer på om det er din tur
	private boolean jegErHvit = false; // Svarer på om du spiller hvit
	private boolean funnetMotspiller = false; //  Svarer på om du har funnet en motspiller 
	private boolean mistetMotspiller = false; //  Svarer på om du har mistet motspilleren
	private boolean visHopp = false;  //  Svarer på om du skal highlighte lovlige hopp
	private boolean startServer = false;  //  Svarer på om du skal starte en server
	
	private JLabel duSpillerSomLabel; // Skriver hvilken brikke du spiller
	private Thread thread; // Tråden til spillet
	private JPanel mainframe; // Hovedvinduet til DamController
	
	
	private Brikke[] brikkeListe = new Brikke[64]; // Array med alle brikke-instanser
	
	
	private boolean brikkeFlyttes; // Boolean; blir en brikke flyttet
	
	DamController(){
		// INIT AV OBJEKTER:
		mainframe = this;
		lydeffekt = new Lydeffekt();
		grafikk = new Grafikk();
		turviser = new TurIndikator();
		timer = new Timer();
		con = new DamServer();
		
		//lager JPanelet
		setFocusable(true);
		requestFocus();
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(this);
	  	setBorder(new EmptyBorder(5, 5, 5, 5));
	  	setLayout(null);
	  	setBounds(0,0,400,400);
	  	JPanel SideBarPanel = new JPanel();
	  	SideBarPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	  	SideBarPanel.setBackground(Color.WHITE);
	  	SideBarPanel.setBounds(400, 0, 192, 400);
	  	add(SideBarPanel);
	  	SideBarPanel.setLayout(null);
		JButton btnNewButton = new JButton("Gi opp!");
		btnNewButton.setToolTipText("Du vil gi seieren til motstanderen ved og gi opp!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!funnetMotspiller){
					JOptionPane.showMessageDialog(mainframe,"Vent til spillet har startet.");
				}
				else{
					if(!minTur && !brikkeOppsett.getTapt() && !brikkeOppsett.getVunnet()){
						JOptionPane.showMessageDialog(mainframe,"Du må vente til det er din tur med å gi opp.");
					}
					else{
						if(minTur && !brikkeOppsett.getTapt() && !brikkeOppsett.getVunnet()){
							int knapp = JOptionPane.YES_NO_OPTION;
							int confirmGiOpp = JOptionPane.showConfirmDialog (mainframe, "Er du sikker på at du vil gi opp?","Advarsel",knapp);
							if(confirmGiOpp == JOptionPane.YES_OPTION){
								sendBrikkekart(brikkeOppsett.giOpp());
								timer.stopp();
							}
						}
						else{
							JOptionPane.showMessageDialog(mainframe,"Du kan ikke gi opp, spillet er over.");
						}
					}
				}
		
			}
		});
		btnNewButton.setBounds(45, 315, 119, 35);
		SideBarPanel.add(btnNewButton);
		JButton btnStartPNytt = new JButton("Start p\u00E5 Nytt!");
		btnStartPNytt.setToolTipText("Send foresp\u00F8rsel til motstanderen om han vil starte spillet p\u00E5 nytt!");
		btnStartPNytt.setBounds(45, 354, 119, 35);
		SideBarPanel.add(btnStartPNytt);
		btnStartPNytt.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(!funnetMotspiller){
				JOptionPane.showMessageDialog(mainframe,"Vent til spillet har startet.");
			}
			else{
				if((minTur  && !brikkeOppsett.getTapt() && !brikkeOppsett.getVunnet())){
					int knapp = JOptionPane.YES_NO_OPTION;
					int confirmGiOpp = JOptionPane.showConfirmDialog (mainframe, "Er du sikker på at du vil starte på nytt?","Advarsel",knapp);
					if(confirmGiOpp == JOptionPane.YES_OPTION){
						sendBrikkekart(brikkeOppsett.startPaNytt());
						valgtBrikke = 0;
					}
				}
				else if((!minTur && brikkeOppsett.getTapt() || brikkeOppsett.getVunnet())){
					System.out.println("begge parter har godtatt en restart");
					brikkeOppsett = new BrikkeOppsett(jegErHvit);
					minTur = false;
					if(jegErHvit){
						minTur = true;
					}
					mainframe.repaint();
					turviser.repaint();
					valgtBrikke=0;
					timer.start();
					sendBrikkekart(brikkeOppsett.brikkekart);
				}
				else{
					JOptionPane.showMessageDialog(mainframe,"Du må vente til det er din tur med å starte på nytt.");
				}
				
			}
		}
		});
		duSpillerSomLabel = new JLabel();
		duSpillerSomLabel.setHorizontalAlignment(SwingConstants.CENTER);
		duSpillerSomLabel.setFont(new Font("Ebrima", Font.BOLD, 13));
		duSpillerSomLabel.setBounds(32, 275, 144, 42);
		duSpillerSomLabel.setText("");
		SideBarPanel.add(duSpillerSomLabel);
		SideBarPanel.add(timer);
      	SideBarPanel.add(turviser);
      	
      	//Kobler til eller starter en server
		if(!con.koblerTilVert()){
			con.startVert();
			jegErHvit = true;
			minTur = true;
			while(con.getStarted() == false){
				con.lyttEtterKlient();
			}
			funnetMotspiller = true;
			turviser.jegErHvit();
			timer.start();
		}
		else{
			funnetMotspiller = true;
			turviser.jegErSvart();
			timer.start();
		}
		// Oppdaterer turviser-objektet med riktig minTur
		turviser.updateTur(minTur);
		turviser.repaint();
		//Lager brikkeOppsett-objektet
		brikkeOppsett = new BrikkeOppsett(jegErHvit);
		//Starter tråden
		thread = new Thread(this, "Checkers");
		thread.start();
		//Lager brikker-objekter
		oppdaterBrikker();
		//Lager riktig tekst i "Du spiller" - labelen
		if(jegErHvit){
			duSpillerSomLabel.setText("Du spiller hvit");
		}
		else{
			duSpillerSomLabel.setText("Du spiller svart");
		}	
		brikkeOppsett.nyttRiggedBrikkekart();
	}
	
	//Ligger i "Run", og lytter etter et nytt brikkekart og sjekker brikkekartet
	private void tick() {
		// Hvis du mister motspilleren stopper vi timeren , og starter en server for å lytte etter en ny motspiller
		if(antallFeilmeldinger > 0) {
			mistetMotspiller = true;
			startServer = true;
			timer.stopp();
		}
		// Hvis spillet er over stopper vi timeren
		if(brikkeOppsett.erSpilletOver()){
			timer.stopp();
		}
		// Prøver å motta brikkekart fra motspiller
		// For kommentarer på brikkeOppset metoder, les i BrikkeOppsett-klassen
		try{
			int[] oppdatertBrikkeKart = con.lesInt(); // Brikkekartet du mottar fra motspiller
			brikkeOppsett.setBrikkekart(oppdatertBrikkeKart);
			oppdaterBrikker();
			brikkeOppsett.sjekkSpm();
	    	brikkeOppsett.nyttBrikkekartSjekk();
			if(brikkeOppsett.motspillerVilStartePaNyttOgJegMaSvare()){ // motspiller vil restarte
				System.out.println("motspiller vil restarte - svar på spm");
				int knapp = JOptionPane.YES_NO_OPTION;
				int confirmStartPaNytt = JOptionPane.showConfirmDialog (mainframe, "Motspiller vil starte på nytt. Godtar du?","Advarsel",knapp);
				if(confirmStartPaNytt != JOptionPane.YES_OPTION){
					brikkeOppsett.resetStartPaNytt();
					brikkeOppsett.jegVilIkkeRestarte();
					sendBrikkekart(brikkeOppsett.getBrikkekart());
					repaint();
					return;
				}
				else{
					brikkeOppsett.setJegVilStartePaNytt(true);
				}
				repaint();
			}
			if(brikkeOppsett.skalJegStartePaNytt()){ // motstander vil restarte - og jeg har godtatt
				System.out.println("begge parter har godtatt en restart");
				brikkeOppsett.nyttBrikkekart();
				sendBrikkekart(brikkeOppsett.lagNyttBrikkekart());
				minTur = false;
				if(jegErHvit){
					minTur = true;
				}
				repaint();
				turviser.repaint();
				brikkeOppsett.restart();
				valgtBrikke=0;
				timer.start();
				return;
			}
			if(brikkeOppsett.getVunnet()){ // jeg har vunnet
				System.out.println("jeg har vunnet");
				minTur = false;
				timer.stopp();
				return;
			}
			if(brikkeOppsett.nyttSpill(oppdatertBrikkeKart)){ // motstander sender nytt brikkekart
				System.out.println("Mottar nytt brikkekart");
				timer.start();
				brikkeOppsett.restart();
		    	if(jegErHvit){
		    		minTur=true;
					turviser.updateTur(true);
				}
		    	if(!jegErHvit){
		    		minTur = false;
		    		turviser.updateTur(false);
		    	}
				valgtBrikke=0;
				repaint();
				return;
		    }
			if(!minTur && !mistetMotspiller){ // det er min tur, og motstander har ingen spørsmål
				System.out.println("Motstander har gjort ett trekk, og det er min tur.");
				brikkeOppsett.sjekkMotstanderSinBrikkePos();
		    	minTur = true;
				repaint();
		    	return;
			}
		}catch (ClassNotFoundException e) {
				antallFeilmeldinger++;
		}catch (IOException e) {
				antallFeilmeldinger++;
		}
	}
	
	// Metoden tråden kjører, og ligger i en "while true"
	public void run() {
		// Lager brikke-objekter basert på brikkeOppsettet
		oppdaterBrikker();
		while(true){
			turviser.updateTur(minTur);
			// Leter etter ny motspiller dersom du har mistet din motspiller
			letEtterNyMotspiller();
			tick();
			repaint();
		}
	}
	
	// Starter server hvis du er klient, og lytter etter motspiller samt starter ett nytt spill når du finner en motspiller
	private void letEtterNyMotspiller() {
		if(mistetMotspiller){
			System.out.println("Mistet forbindelsen til motspiller.");
			funnetMotspiller = false;
			// Starter en ny vert dersom du ikke var det
			if(!con.getErJegVert()){
				System.out.println("Starter server");
				con.startVert();
				repaint();
			}
			if(startServer){
				con.setStarted(false);
				startServer = false;
			}
			if(!con.getStarted()){
				System.out.println("Venter på motspiller");
				con.lyttEtterKlient();
			}
			if(con.getStarted() && mistetMotspiller){
				duSpillerSomLabel.setText("Du spiller hvit");
				minTur = true;
				jegErHvit = true;
				turviser.jegErHvit();
				funnetMotspiller = true;
				mistetMotspiller = false;
				antallFeilmeldinger = 0;
				brikkeOppsett = new BrikkeOppsett(jegErHvit);
				turviser.repaint();
				timer.start();
				System.out.println("Motspiller funnet, starter spillet.");
				repaint();
				oppdaterBrikker();
			}
		}
	}
	
	// Oppdaterer informasjonen i brikke-objektene basert på brikkekartet i BrikkeOppsett-objektet
	private void oppdaterBrikker() {
		int[] b = brikkeOppsett.getBrikkekart();
		for(int i = 1; i < b.length; i++){
			if(b[i] > 0 ) {
				if(brikkeListe[i] == null){
					Brikke brikke = new Brikke(i, b[i]);
					brikkeListe[i] = brikke;
				}
				else if(brikkeListe[i].brikkeType != b[i]){
					brikkeListe[i].brikkeType = b[i];
					brikkeListe[i].hentBrikkeBilde();
				}
			}
			else{
				brikkeListe[i] = null;
			}
		}
	}

	// Sender brikkekart til motspiller
	public void sendBrikkekart(int[] brikkeKart) {
		try{
			brikkeOppsett.lagreMotstanderSinBrikkePos();
			//Stopper timeren dersom du har tapt/vunnet
			if(brikkeOppsett.erSpilletOver()){
				timer.stopp();
			}
			repaint();
			con.sendInt(brikkeKart);
			oppdaterBrikker();
		} catch(IOException e1){
			antallFeilmeldinger++;
			e1.printStackTrace();
		}
	}
		
	public void paintComponent(Graphics g) {
		turviser.updateTur(minTur);
		super.paintComponents(g);
		render(g);
	}

	// For kommentarer til metodene i "grafikk"-objektet, les klassen
	private void render(Graphics g){
		g = grafikk.tegnBrett(g);
		if(mistetMotspiller){
			Image conlost = new ImageIcon(MultiplayerMainframe.class.getResource("/connectionlost.gif")).getImage();
			g.drawImage(conlost, 0, 0, this);
			g.setColor(Color.WHITE);
			g.setFont(new Font("TimesRoman", Font.BOLD, 40)); 
			g.drawString("Mistet motstander!", 50,300);
			return;
		}
		if(!funnetMotspiller){
			Image venter = new ImageIcon(MultiplayerMainframe.class.getResource("/venter.gif")).getImage();
			g.drawImage(venter, 0, 0, this);
			return;
		}
		if(brikkeOppsett != null){
			if(brikkeOppsett.getVunnet()){
				Image win = new ImageIcon(MultiplayerMainframe.class.getResource("/winner.gif")).getImage();
				g.drawImage(win, 0, 0, this);
				return;
			}
			if(brikkeOppsett.getTapt()){
				Image lost = new ImageIcon(MultiplayerMainframe.class.getResource("/lost.gif")).getImage();
				g.drawImage(lost, 0, 0, this);
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman", Font.BOLD, 40)); 
				g.drawString("Du har tapt", 95,300);
				return;
			}
			if(visHopp){ // Fargelegger felt basert på brikkens lovlige hopp
				for(int i = 0; i < brikkeOppsett.getLovligeHopp().length; i++){ 
					for(int j = 0; j < 4 ;j++){
						if(brikkeOppsett.getLovligeHopp()[i][j] != 0 && i == valgtBrikke){
							g = grafikk.tegnLovligeHopp(brikkeOppsett.getBrikkerSomBlirHoppetOver()[i][j] %8, brikkeOppsett.getBrikkerSomBlirHoppetOver()[i][j] /8,brikkeOppsett.getLovligeHopp()[i][j] %8,brikkeOppsett.getLovligeHopp()[i][j] /8,g);
						}
					}
				}
			}
			if(!(brikkeOppsett.getLovligeTrekk().isEmpty())){ // Fargelegger felt basert på brikkens lovlige trekk
				for(int i = 0; i < brikkeOppsett.getLovligeTrekk().size() ;i++){
					g = grafikk.tegnLovligeTrekk(g,brikkeOppsett.getLovligeTrekk().get(i));
				}
			}
			if(valgtBrikke != 0){ // Fargelegger feltet til brikken du har valgt
				g = grafikk.tegnValgtBrikke(g, valgtBrikke);
			}
			if(minTur){ // Dersom det er lovlige trekk "deaktiveres" andre brikker med ett grått filter
				for(int i = 0; i < brikkeOppsett.getBrikkekart().length; i++){
					if(brikkeOppsett.erDetteMinBrikke(i) && brikkeOppsett.getLovligeHoppFunnet()){
						boolean visLovligeHopp = true;
						for(int j = 0; j < 4; j++){
							if(brikkeOppsett.getLovligeHopp()[i][j] > 0){
								visLovligeHopp= false;
						    }
						}
						if(visLovligeHopp){
							g = grafikk.tegnDeaktiverteBrikker(g, i);
						}
					}
				}
			}
			for(Brikke brikke: brikkeListe){ // Tegner brikkene
				if(brikke != null){
					brikke.tegnBrikke(g);
				}
			}
			if(brikkeOppsett.getFunnetMotstanderSittSisteTrekk() && minTur){ // Lager pil for motstanderens forrige trekk
				g = grafikk.tegnTrekant(g,brikkeOppsett.getMotstanderSisteTilTrekk(),  brikkeOppsett.getMotstanderSisteFraTrekk());
				
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	// Setter valgtBrikke til brikken du trykker på dersom den har lovlige trekk/hopp
	public void mousePressed(MouseEvent e) {
		if(minTur && funnetMotspiller){
			brikkeOppsett.setFunnetMotstanderSittSisteTrekk(false);
			int position = brikkeOppsett.positionE(e);
			if(brikkeOppsett.getLovligeHoppFunnet()){
				if(brikkeOppsett.erDetteEttLovligHopp(position)){
					valgtBrikke = position;
					visHopp = true;
					brikkeFlyttes = true;
				}
			}
			else{
				if(brikkeOppsett.erDetteMinBrikke(position)){
					if(brikkeOppsett.finnesDetLovligeTrekkForDenneBrikken(position)){
						brikkeOppsett.finnLovligeTrekkForDenneBrikken(position);
						valgtBrikke = position;
						brikkeFlyttes = true;
					}
				}
			}
		}	
		repaint();
	}

	@Override
	// Sjekker om feltet du flytter til er ett lovlig trekk/hopp basert på valgtBrikke
	public void mouseReleased(MouseEvent e) {
		if(brikkeFlyttes){
			brikkeFlyttes = false;
		}
		if(minTur && funnetMotspiller){
			brikkeOppsett.setFunnetMotstanderSittSisteTrekk(false);
			int position = brikkeOppsett.positionE(e);
			if(brikkeOppsett.getLovligeHoppFunnet()){
				if(brikkeOppsett.kanDenneBrikkenHoppeTilPosistion(position,valgtBrikke)){
					brikkeOppsett.hopp(position,valgtBrikke);
					if(brikkeOppsett.finnesDetLovligeHoppForDenneBrikken(position)){
						lydeffekt.flytt();
						valgtBrikke = position;
						brikkeOppsett.varSjekk();
					}
					else{
						visHopp = false;
						minTur = false;
						valgtBrikke = 0;
						lydeffekt.flytt();
						sendBrikkekart(brikkeOppsett.getBrikkekart());
					}
				}
			}
			else{
				if(brikkeOppsett.getLovligeTrekkFunnet()){
					for(int i = 0; i < brikkeOppsett.getLovligeTrekk().size() ;i++){
						if(brikkeOppsett.getLovligeTrekk().get(i).equals(position)){
							brikkeOppsett.trekk(valgtBrikke,position);
							lydeffekt.flytt();
							valgtBrikke = 0;
							sendBrikkekart(brikkeOppsett.getBrikkekart());
							minTur = false;
						}
					}
				}
			}
		}
		if(brikkeListe[valgtBrikke] != null){
			brikkeListe[valgtBrikke].resetBrikke();
		}
		oppdaterBrikker();
		repaint();
	}
	//Brikken følger musepekeren
	public void mouseDragged(MouseEvent e){
		if(brikkeFlyttes){
			brikkeListe[valgtBrikke].x = e.getX()-24;
			brikkeListe[valgtBrikke].y = e.getY()-24;
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}	

