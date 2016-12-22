package spilllogikk;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


/*
* ------- Ordforklaringer ---------
* 
* Felt = Posisjonen på brettet - 0-63
* Trekk = Du flytter brikken i retning X ett felt
* Hopp = Du flytter brikken i retning X to felt, og slår ut motstanderbrikken du hopper over
* SW = South west
* SE = South east
* NE = North east
* NW = North west
* Pos = Posisjon
* Spørsmål: "Motspiller gir opp", "Motspiller vil starte på nytt","Motspiller godtar ikke å starte på nytt"
*
*
* 	------ Brikkekart -------
*  // Posisjoner
*  Posisjonen 0 i arrayet brukes til å sende informasjon mellom instansene, feks: "jeg gir opp", "vil du starte på nytt?"
*  Posisjonene 1-63 i arrayet brukes til å beskrive brikketilstanden på hvert felt
*  
*  // Betydning av verdier
*  0 = ingen brikke
*  1 = Hvit bonde
*  2 = Hvit konge
*  3 = Svart bonde
*  4 = Svart konge
*  
*  Når du sender:
*  10 = Jeg gir opp
*  20 = Jeg vil starte på nytt
*  21 = Jeg godtar ikke å starte på nytt
*  
*  Når du mottar:
*  10 = Motspiller gir opp
*  20 = Motspiller vil starte på nytt
*  21 = Motspiller vil ikke starte på nytt
*/

public class BrikkeOppsett{
	
		
		public int [] brikkekart;
		public int[][] brikkerSomBlirHoppetOver = new int[64][4];
		public int[][] lovligeHopp = new int[64][4];
		public int sumMineBrikker, sumMotspillerBrikker, motstanderSisteFraTrekk, motstanderSisteTilTrekk; // fra/til trekk er for å lage pil
		
		public boolean jegErHvit;
		public boolean tapt = false;
		public boolean vunnet = false;
		public boolean lovligeHoppFunnet = false;
		public boolean lovligeTrekkFunnet = false;
		public boolean jegVilStartePaNytt;
		public boolean motspillerVilStartePaNytt;
		public boolean funnetMotstanderSittSisteTrekk;
		
		public ArrayList<Integer> lovligeTrekk = new ArrayList<Integer>();
		public ArrayList<Integer> motstanderSinBrikkePos = new ArrayList<Integer>();
		
		public BrikkeOppsett(boolean jegErHvit) {
			nyttBrikkekart();
			lagreMotstanderSinBrikkePos();
			this.jegErHvit = jegErHvit;
		}
				
		public void resetStartPaNytt(){
			tapt = false;
			vunnet = false;
			motspillerVilStartePaNytt = false;
			jegVilStartePaNytt = false;
			brikkekart[0] = 0;
		}
		
		public void restart(){
			lagreMotstanderSinBrikkePos();
			tapt = false;
			vunnet = false;
			lovligeHoppFunnet = false;
			lovligeTrekkFunnet = false;
			jegVilStartePaNytt = false;
			motspillerVilStartePaNytt = false;
			funnetMotstanderSittSisteTrekk = false;
			lovligeTrekk.clear();
			resetLovligeHopp();
			resetStartPaNytt();
			resetBrikkerSomBlirHoppetOver();
		}
		
		public void varSjekk(){
			harJegTapt();
			harJegVunnet();
			if(!tapt && !vunnet){
				sjekkBrikkeStatus();
				sjekkLovligeHopp();
			}
		}
		
		// Teller motspillerens brikker
		public void tellMotspillersBrikker() {
			int sum = 0;
			for(int i = 0; i < brikkekart.length; i++){
				if(erDetteMotstanderenSinBrikke(i)){
					sum++;
				}
			}
			sumMotspillerBrikker = sum;
		}
		
		// Teller dine brikker
		public void tellMineBrikker() {
			int sum = 0;
			for(int i = 0; i < brikkekart.length; i++){
				if(erDetteMinBrikke(i)){
					sum++;
				}
			}
			sumMineBrikker = sum;
		}

	
		
		// Oppretter ett nytt brikkekart
		public void nyttBrikkekart() {
			int nyttBrikkekart[] = new int[64];
			for(int i = 0; i < 64; i++) {
					boolean partallRad = (i / 8) % 2 == 0;
					boolean partallRute = i % 2 == 0;
					int brikketype = 0;
					if(i >= 40 || i < 24) {
						if((!partallRad && partallRute) || (partallRad && !partallRute)) {
							if(i >= 40) brikketype = 1;
							if(i < 24) brikketype = 3;
						}
					}
					nyttBrikkekart[i] = brikketype;
				}
			brikkekart = nyttBrikkekart;
		}
		
		// Oppretter ett rigget brikkekart for lett gjennomføring av debugging
				
		public void nyttRiggedBrikkekart() {
			int[] nyttBrikkeKart = {
					0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,
					0,0,0,3,0,0,0,0,
					0,0,0,0,3,0,0,0,
					0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,1,
					0,0,0,0,0,0,0,0,
				};
			int[] brikkekart = {
					0,3,0,3,0,3,0,3,
					3,0,3,0,3,0,3,0,
					0,3,0,3,0,3,0,3,
					0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,
					1,0,1,0,1,0,1,0,
					0,1,0,1,0,1,0,1,
					1,0,1,0,1,0,1,0,
				};
			brikkekart = nyttBrikkeKart;
		}
		
		// Printer ut brikkestillingen for å lettere debugge 
		
		public void skrivUtBrikkekart() {
			int teller=0;
			for(int i = 0; i < brikkekart.length; i++){
				System.out.print(brikkekart[i]+" ");
				teller++;
				int asd = teller/8;
				if(asd > 0){
					System.out.println();
					teller=0;
				}
			}
			System.out.println();
		} 
		
		//Gjør om bønder til konger dersom de er på motstanderes første rad
		public void sjekkBrikkeStatus() { 
			for(int i = 1; i < brikkekart.length; i++){
				if((brikkekart[i] == 1 && i < 8) || (brikkekart[i] == 3 && i > 55)){ // Denne er true hvis du er en bonde(brikketype 1 eller 3) på motstanderens første rad
					brikkekart[i] = brikkekart[i]+1;
				}
			}
		}
			
		// Finner lovlige trekk for en gitt brikke og legger de i LovligeTrekk arrayet samt setter lovligeTrekkFunnet til true
		// Trekk NW, NE, SW og SE står for retningen til trekket
		public void finnLovligeTrekkForDenneBrikken(int position) {
			lovligeTrekk.clear();
			lovligeTrekkFunnet = false;
			int brikkeType = brikkekart[position];
			boolean brikkeKanGaOpp = brikkeKanGaOpp(brikkeType);
			boolean brikkeKanGaNed = brikkeKanGaNed(brikkeType);
			if(trekkNW(position) && brikkeKanGaOpp && feltErTomt(feltNW(position))){
				lovligeTrekk.add(feltNW(position));
				lovligeTrekkFunnet = true;
			}
			if(trekkNE(position) && brikkeKanGaOpp && feltErTomt(feltNE(position))){
				lovligeTrekk.add(feltNE(position));
				lovligeTrekkFunnet = true;
			}
			if(trekkSW(position) && brikkeKanGaNed && feltErTomt(feltSW(position))){
				lovligeTrekk.add(feltSW(position));
				lovligeTrekkFunnet = true;
			}
			if(trekkSE(position) && brikkeKanGaNed && feltErTomt(feltSE(position))){
				lovligeTrekk.add(feltSE(position));
				lovligeTrekkFunnet = true;
			}	
		}
		
		// Sjekker om brikke N har lovlige trekk
		// Trekk NW, NE, SW og SE står for retningen til trekket
		public boolean finnesDetLovligeTrekkForDenneBrikken(int position){
			boolean funnetLovligeTrekk = false;
			int brikkeType = brikkekart[position];
			boolean brikkeKanGaOpp = brikkeKanGaOpp(brikkeType);
			boolean brikkeKanGaNed = brikkeKanGaNed(brikkeType);
			if(trekkNW(position) && brikkeKanGaOpp && feltErTomt(feltNW(position))){
					funnetLovligeTrekk = true;
			}
			if(trekkNE(position) && brikkeKanGaOpp && feltErTomt(feltNE(position))){
					funnetLovligeTrekk = true;
			}
			if(trekkSW(position) && brikkeKanGaNed && feltErTomt(feltSW(position))){
					funnetLovligeTrekk = true;
			}
			if(trekkSE(position) && brikkeKanGaNed && feltErTomt(feltSE(position))){
					funnetLovligeTrekk = true;
			}
			return funnetLovligeTrekk;
		}
		
		// Sjekker om brikke N kan gå opp/ned - true/false
		public boolean brikkeKanGaOpp(int brikkeType){
			return (brikkeType > 0 && brikkeType !=3);
		}
		
		public boolean brikkeKanGaNed(int brikkeType){
			return (brikkeType > 0 && brikkeType !=1);
		}
		
		// trekk(XX)	= Er feltet du trekker til en del av brettet?
		// hopp(XX) 	= Er feltet du hopper til en del av brettet?
		// felt(XX) 	= Feltet du går til dersom trekk(XX) er en del av brettet
		// hoppFelt(XX) = Feltet du hopper til dersom hopp(XX) er en del av brettet
		public boolean trekkNW(int position){
			int vannrett = position%8-1;
			int loddrett = position/8-1;
			return (loddrett >= 0 && vannrett >= 0 && loddrett < 8 && vannrett < 8);
		}
		
		public boolean trekkNE(int position){
			int vannrett = position%8+1;
			int loddrett = position/8-1;
			return (loddrett >= 0 && vannrett >= 0 && loddrett < 8 && vannrett < 8);
		}
		
		public boolean trekkSW(int position){
			int vannrett = position%8-1;
			int loddrett = position/8+1;
			return (loddrett >= 0 && vannrett >= 0 && loddrett < 8 && vannrett < 8);
		}
		
		public boolean trekkSE(int position){
			int vannrett = position%8+1;
			int loddrett = position/8+1;
			return (loddrett >= 0 && vannrett >= 0 && loddrett < 8 && vannrett < 8);
		}
		
		public boolean hoppNW(int position){
			int vannrett = position%8-2;
			int loddrett = position/8-2;
			return (loddrett >= 0 && vannrett >= 0 && loddrett < 8 && vannrett < 8);
		}
		
		public boolean hoppNE(int position){
			int vannrett = position%8+2;
			int loddrett = position/8-2;
			return (loddrett >= 0 && vannrett >= 0 && loddrett < 8 && vannrett < 8);
		}
		
		public boolean hoppSW(int position){
			int vannrett = position%8-2;
			int loddrett = position/8+2;
			return (loddrett >= 0 && vannrett >= 0 && loddrett < 8 && vannrett < 8);
		}
		
		public boolean hoppSE(int position){
			int vannrett = position%8+2;
			int loddrett = position/8+2;
			return (loddrett >= 0 && vannrett >= 0 && loddrett < 8 && vannrett < 8);
		}
		
		public int feltNW(int position){
			return (((position/8)-1)*8)+(position%8)-1;
		}
		
		public int feltNE(int position){
			return (((position/8)-1)*8)+(position%8)+1;
		}
		
		public int feltSW(int position){
			return (((position/8)+1)*8)+(position%8)-1;
		}
		
		public int feltSE(int position){
			return (((position/8)+1)*8)+(position%8)+1;
		}
		
		public int hoppFeltNW(int position){
			return (((position/8)-2)*8)+(position%8)-2;
		}
		
		public int hoppFeltNE(int position){
			return (((position/8)-2)*8)+(position%8)+2;
		}
		
		public int hoppFeltSW(int position){
			return (((position/8)+2)*8)+(position%8)-2;
		}
		
		public int hoppFeltSE(int position){
			return (((position/8)+2)*8)+(position%8)+2;
		}

		// Sjekker om felt X ikke har noen brikke, altså at feltet er tomt
		public boolean feltErTomt(int position){
			return (brikkekart[position] == 0);
		}
		
		//Sjekker om brikken på felt X er min
		public boolean erDetteMinBrikke(int felt) {
			if(jegErHvit && (brikkekart[felt] == 2 || brikkekart[felt] == 1)){
				return true;
			}
			if(!jegErHvit && (brikkekart[felt] == 4 ||brikkekart[felt] == 3)){
				return  true;
			}
			return false;
		}
		
		// Resetter lovligeHopp-arrayet
		public void resetLovligeHopp(){
			lovligeHoppFunnet = false;
			int[][]  lovligeHopp = new int[64][4];
			for(int i = 0; i < brikkekart.length; i++){
				for(int j = 0; j < 4; j++){
					lovligeHopp[i][j] = 0;
				}
			}
			this.lovligeHopp = lovligeHopp;
		}
		
		// Resetter brikkerSomBlirHoppetOver-arrayet
		public void resetBrikkerSomBlirHoppetOver(){
			int[][]  lovligeHopp = new int[64][4];
			for(int i = 0; i < brikkekart.length; i++){
				for(int j = 0; j < 4; j++){
					lovligeHopp[i][j] = 0;
				}
			}
			this.brikkerSomBlirHoppetOver = lovligeHopp;
		}
		
		// Sjekker om det er noen lovlige hopp for dine brikker, og lagrer de i lovligeHopp og brikkerSomBlirHoppetOver
		public void sjekkLovligeHopp() {
			lovligeHoppFunnet = false;
			for(int i = 0; i < brikkekart.length; i++){
				if(erDetteMinBrikke(i)){
					if(finnesDetLovligeHoppForDenneBrikken(i)){
						lovligeHoppFunnet = true;
						int[] h = lovligeHoppForEttFeltArray(i);
						int[] f = feltSomBlirHoppetOverArray(i);
						for(int j = 0; j < 4; j++){
							lovligeHopp[i][j] = h[j];
							brikkerSomBlirHoppetOver[i][j] = f[j];
						}
					}	
				}
			}
		}
			
		// Sjekker om det er noen lovlige hopp for ett felt. Brukes for å sjekke om det er flere hopp etter et utført hopp.
		int[][][] sjekkLovligeHoppForEttFelt(int i) {
			int[][][] hoppArray = new int[2][64][4];
			if(erDetteMinBrikke(i) && finnesDetLovligeHoppForDenneBrikken(i)){
				int[] h = lovligeHoppForEttFeltArray(i);
				int[] f = feltSomBlirHoppetOverArray(i);
				for(int j = 0; j < 4; j++){
					hoppArray[0][i][j] = h[j];
					hoppArray[1][i][j] = f[j];
				}
			}
			return hoppArray;
		}
		
		// Finner og lagrer lovligeHopp for ett felt
		public int[] lovligeHoppForEttFeltArray(int i) {
			int[]a = {0,0,0,0};
				int brikkeType = brikkekart[i];
				boolean brikkeKanGaOpp = brikkeKanGaOpp(brikkeType);
				boolean brikkeKanGaNed = brikkeKanGaNed(brikkeType);
				int teller = 0;
				if(hoppNW(i) && brikkeKanGaOpp && erDetteMotstanderenSinBrikke(feltNW(i)) && feltErTomt(hoppFeltNW(i))){
					a[teller] = hoppFeltNW(i);
					teller++;
				}
				if(hoppNE(i) && brikkeKanGaOpp && erDetteMotstanderenSinBrikke(feltNE(i)) && feltErTomt(hoppFeltNE(i))){
					a[teller] = hoppFeltNE(i);
					teller++;
				}
				if(hoppSW(i) && brikkeKanGaNed && erDetteMotstanderenSinBrikke(feltSW(i)) && feltErTomt(hoppFeltSW(i))){
					a[teller] = hoppFeltSW(i);
					teller++;
				}
				if(hoppSE(i) && brikkeKanGaNed && erDetteMotstanderenSinBrikke(feltSE(i)) && feltErTomt(hoppFeltSE(i))){
					a[teller] = hoppFeltSE(i);
					teller++;
				}
			return a;
		}
		
		//Finner og lagrer feltene lovligeHopp hopper over for ett felt
		public int[] feltSomBlirHoppetOverArray(int i) {
			int[]a ={0,0,0,0};
				int brikkeType = brikkekart[i];
				boolean brikkeKanGaOpp = brikkeKanGaOpp(brikkeType);
				boolean brikkeKanGaNed = brikkeKanGaNed(brikkeType);
				int teller = 0;
				if(hoppNW(i) && brikkeKanGaOpp && erDetteMotstanderenSinBrikke(feltNW(i)) && feltErTomt(hoppFeltNW(i))){
					a[teller] = feltNW(i);
					teller++;
				}
				if(hoppNE(i) && brikkeKanGaOpp && erDetteMotstanderenSinBrikke(feltNE(i)) && feltErTomt(hoppFeltNE(i))){
					a[teller] = feltNE(i);
					teller++;
				}
				if(hoppSW(i) && brikkeKanGaNed && erDetteMotstanderenSinBrikke(feltSW(i)) && feltErTomt(hoppFeltSW(i))){
					a[teller] = feltSW(i);
					teller++;			}
				if(hoppSE(i) && brikkeKanGaNed && erDetteMotstanderenSinBrikke(feltSE(i)) && feltErTomt(hoppFeltSE(i))){
					a[teller] = feltSE(i);
				}
			return a;
		}
		
		// Etter utført hopp sjekkes det om det finnes flere lovlige hopp fra det feltet
		public boolean finnesDetLovligeHoppForDenneBrikken(int position){
			boolean funnetLovligeHopp = false;
			int brikkeType = brikkekart[position];
			boolean brikkeKanGaOpp = brikkeKanGaOpp(brikkeType);
			boolean brikkeKanGaNed = brikkeKanGaNed(brikkeType);			
			if(hoppNW(position) && brikkeKanGaOpp && erDetteMotstanderenSinBrikke(feltNW(position)) && feltErTomt(hoppFeltNW(position))){
				funnetLovligeHopp = true;
			}
			if(hoppNE(position) && brikkeKanGaOpp && erDetteMotstanderenSinBrikke(feltNE(position)) && feltErTomt(hoppFeltNE(position))){
				funnetLovligeHopp = true;
			}
			if(hoppSW(position) && brikkeKanGaNed && erDetteMotstanderenSinBrikke(feltSW(position)) && feltErTomt(hoppFeltSW(position))){
				funnetLovligeHopp = true;
			}
			if(hoppSE(position) && brikkeKanGaNed && erDetteMotstanderenSinBrikke(feltSE(position)) && feltErTomt(hoppFeltSE(position))){
				funnetLovligeHopp = true;
			}
			return funnetLovligeHopp;
		}
		
		// Sjekker om brikken på felt X er motstanderen sin
		public boolean erDetteMotstanderenSinBrikke(int felt) {
			if(!jegErHvit && (brikkekart[felt] == 2 || brikkekart[felt] == 1)){
				return  true;
			}
			if(jegErHvit && (brikkekart[felt] == 4 || brikkekart[felt] == 3)){
				return  true;
			}
			return false;
		}
		
		// Gjør om X og Y koordinater til felt i Brikkekart-arrayet, hvert felt er 50x50
		public int hentPosition(int x, int y){
			return (y / 50)*8+(x / 50);
		}
		
		// Gjør om X og Y koordinater fra en MouseEvent til felt i Brikkekart-arrayet, hvert felt er 50x50
		public int positionE(MouseEvent e) {
			return (e.getY() / 50)*8+(e.getX() / 50);
		}
		
		// Sjekker om jeg har tapt
		public void harJegTapt(){
			tellMineBrikker();
			int hoppFunnet = 0;
			if(sumMineBrikker > 0){
				for(int i = 0; i < brikkekart.length; i++){
					if(erDetteMinBrikke(i) && (finnesDetLovligeHoppForDenneBrikken(i) || finnesDetLovligeTrekkForDenneBrikken(i))){
						hoppFunnet++;
					}
				}
				if(hoppFunnet==0){
					tapt=true;
				}
			}
			else{
				tapt = true;
			}
		}
		
		// Sjekker om jeg har vunnet
		public void harJegVunnet(){
			tellMotspillersBrikker();
			int hoppFunnet = 0;
			sjekkLovligeHopp();
			if(!lovligeHoppFunnet){
				if(sumMotspillerBrikker > 0){
					for(int i = 0; i < brikkekart.length; i++){
						if(erDetteMotstanderenSinBrikke(i)){
							if(finnesDetLovligeHoppForDenneBrikken(i) || finnesDetLovligeTrekkForDenneBrikken(i)){
								hoppFunnet++;
							}	
						}
					}
					if(hoppFunnet == 0){
						vunnet=true;
					}
				}
				else{
					vunnet = true;
				}
			}
		}
		
		// Sjekker om mottatt brikkekart er et nytt spill
		public boolean nyttSpill(int[] oppdatertBrikkeKart) {
			int[] startPosBrikkekart = lagNyttBrikkekart();
			for(int i = 0; i < brikkekart.length; i++){
				if(startPosBrikkekart[i] != oppdatertBrikkeKart[i]){
					return false;
				}
			}
			lovligeHoppFunnet = false;
			lovligeTrekkFunnet = false;
			resetLovligeHopp();
			resetBrikkerSomBlirHoppetOver();
			return true;
		}
		
		// Lager og returner ett nytt Brikkekart
		public int[] lagNyttBrikkekart(){
			int nyttBrikkeKart[] = new int[64];
			for(int i = 0; i < brikkekart.length; i++){
				boolean partallRad = (i / 8) % 2 == 0;
				boolean partallRute = i % 2 == 0;
				int brikketype = 0;
				if(i >= 40 || i < 24){
					if((!partallRad && partallRute) || (partallRad && !partallRute)){
						if(i >= 40) brikketype = 1;
						if(i < 24) brikketype = 3;
					}
				}
				nyttBrikkeKart[i] = brikketype;
			}
			return nyttBrikkeKart;
		}

		// Utfører ett trekk fra felt x til x
		public void trekk(int valgtBrikke, int position) {
			int valgtBrikketype = brikkekart[valgtBrikke];
			brikkekart[position]=valgtBrikketype;
			brikkekart[valgtBrikke]=0;
			nyttBrikkekartSjekk();
		}
		
		// Gjør en sjekk på mottatt brikkekart
		public void nyttBrikkekartSjekk() {
			lovligeTrekk.clear();
			resetLovligeHopp();
			resetBrikkerSomBlirHoppetOver();
			varSjekk();			
		}
		
		//Sjekker om felt X har et lovlig hopp
		public boolean erDetteEttLovligHopp(int felt) {
			for(int i = 0; i < lovligeHopp.length; i++){
				for(int j = 0; j < 4 ;j++){
					if(lovligeHopp[i][j] > 0){
						if(i == felt){
							return true;
						}
					}
				}
			}
			return false;
		}
		
		// Sjekker om valgtBrikke kan hoppe til felt X
		public boolean kanDenneBrikkenHoppeTilPosistion(int felt, int valgtBrikke) {
			for(int i = 0; i < lovligeHopp.length; i++){
				for(int j = 0; j < 4 ;j++){
					if(lovligeHopp[i][j] > 0){
						if(lovligeHopp[i][j] == felt && valgtBrikke != 99 && valgtBrikke == i){
							return true;
						}
					}
				}
			}
			return false;
		}
		
		// Brikken hopper fra felt X til N og slår ut brikken på feltet mellom X og N
		public void hopp(int position, int valgtBrikke) {
			int valgtBrikkeType = brikkekart[valgtBrikke];
			brikkekart[position]=valgtBrikkeType; // feltet brikken hopper til
			brikkekart[valgtBrikke]=0; // Feltet brikken hoppet fra
			brikkekart[((position - valgtBrikke)/2)+valgtBrikke] = 0; // Brikken du hoppet over
			sjekkBrikkeStatus();
			varSjekk();
		}
		
		// Jeg gir opp
		public int[] giOpp() {
			nyttBrikkekart();
			tapt = true;
			System.out.println("Jeg gir opp");
			brikkekart[0] = 10;
			return brikkekart;
		}
		
		//Sjekker spørsmål hver gang jeg mottar ett nytt brikkekart
		public void sjekkSpm() {
			vunnet = false;
			tapt = false;
			motspillerVilStartePaNytt = false;
			if(brikkekart[0] == 10){ // Motspiller gir opp
				vunnet = true;
				System.out.println("Motspiller gir opp");
			}
			if(brikkekart[0] == 20){ // Jeg godtar en restart
				motspillerVilStartePaNytt = true;
				System.out.println("Motspiller vil restarte");
			}
			if(brikkekart[0] == 21){ // Jeg godtar ikke en restart
				resetStartPaNytt();
				System.out.println("Motspiller vil ikke restarte");
			}
			tomSpm();
		}
			
		// Resetter pos 0 i brikkekart-arrayet
		public void tomSpm() {
			brikkekart[0] = 0;
		}
		
		//Godtar en restart
		public int[] startPaNytt() {
			System.out.println("Jeg vil restarte");
			brikkekart[0] = 20;
			jegVilStartePaNytt = true;
			System.out.println(brikkekart[0]);
			return brikkekart;
		}
		
		//Godtar ikke en restart
		public void jegVilIkkeRestarte() {
			brikkekart[0] = 21;
		}
		
		//Lagrer motstanderen sin brikkepos
		public void lagreMotstanderSinBrikkePos() {
			if(!motstanderSinBrikkePos.isEmpty()){
				motstanderSinBrikkePos.clear();
			}
			for(int i = 1; i < brikkekart.length; i++){
				if(erDetteMotstanderenSinBrikke(i)){
					motstanderSinBrikkePos.add(i);
				}
			}
		}
		
		// Finner motstanderen sitt siste trekk ved å sammenligne gammel og ny brikkepos
		public void sjekkMotstanderSinBrikkePos(){
			if(!motstanderSinBrikkePos.isEmpty()){
				ArrayList<Integer> motspillerSinNyeBrikkePos = new ArrayList<Integer>();
				for(int g = 0; g < brikkekart.length; g++){
					if(erDetteMotstanderenSinBrikke(g)){
						motspillerSinNyeBrikkePos.add(g);
					}
				}
				boolean til = false;
				boolean fra = false;
				if(motstanderSinBrikkePos.size() == motspillerSinNyeBrikkePos.size()){
					for(int i = 0; i < motstanderSinBrikkePos.size(); i++){
						if(!motspillerSinNyeBrikkePos.contains(motstanderSinBrikkePos.get(i))){
							motstanderSisteFraTrekk = motstanderSinBrikkePos.get(i);
							fra = true;
						}
						if(!motstanderSinBrikkePos.contains(motspillerSinNyeBrikkePos.get(i))){
							motstanderSisteTilTrekk = motspillerSinNyeBrikkePos.get(i);
							til = true;
						}
					}
				}
				if(fra && til){
					funnetMotstanderSittSisteTrekk = true;
					motstanderSinBrikkePos.clear();
				}
			}
		}
		
		// Metoder for å hente variabler
		
		public void setFunnetMotstanderSittSisteTrekk(boolean b) {
			funnetMotstanderSittSisteTrekk = false;
		}
		
		public boolean getFunnetMotstanderSittSisteTrekk(){
			return funnetMotstanderSittSisteTrekk;
		}
		public int getMotstanderSisteTilTrekk(){
			return motstanderSisteTilTrekk;
		}
		
		public int getMotstanderSisteFraTrekk(){
			return motstanderSisteFraTrekk;
		}
		
		public boolean skalJegStartePaNytt(){
			return jegVilStartePaNytt && motspillerVilStartePaNytt;
		}
		
		public boolean motspillerVilStartePaNyttOgJegMaSvare(){
			return (motspillerVilStartePaNytt  == true && jegVilStartePaNytt == false);
		}

		public int getSumMineBrikker(){
			return sumMineBrikker;
		}
		public int getSumMotstanderBrikker(){
			return sumMotspillerBrikker;
		}
		public ArrayList<Integer> getLovligeTrekk() {
			return lovligeTrekk;
		}
		
		public int[] getBrikkekart(){
			return brikkekart;
		}
		
		public int[][] getLovligeHopp(){
			return lovligeHopp;
		}
		
		public boolean getTapt(){
			return tapt;
		}
		
		public boolean getVunnet(){
			return vunnet;
		}
		
		public boolean getLovligeHoppFunnet() {
			return lovligeHoppFunnet;
		}
		public boolean getLovligeTrekkFunnet() {
			return lovligeTrekkFunnet;
		}
		
		public int[][] getBrikkerSomBlirHoppetOver(){
			return brikkerSomBlirHoppetOver;
		}
		
		public void setBrikkekart(int[] brikkeKart){
			this.brikkekart = brikkeKart;
		}

		public void setJegVilStartePaNytt(boolean jegVilStartePaNytt) {
			this.jegVilStartePaNytt = jegVilStartePaNytt;
		}
		
		public boolean erSpilletOver(){
			return (tapt || vunnet);
		}
}


		