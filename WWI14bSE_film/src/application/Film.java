package application;

public class Film implements Comparable<Film> {
	/******************************************************************************
	 * [ATTRIBUTE] 																  *
	 * Instanze-Attribute											  			  *
	 * 																			  *
	 ******************************************************************************/
	// Atrribute aus dem JSON der omdbappi
	private String o_titel;
	private String o_jahr;
	private String o_rated;
	private String o_erschienen_am;
	private String o_laufzeit; // min
	private String o_genre;
	private String o_regisseur;
	private String o_schriftsteller;
	private String o_schauspieler = "";
	private String o_kurzbeschreibung;
	private String o_sprachen;
	private String o_land;
	private String o_auszeichnungen;
	private String o_adresse_vom_plakat;
	private String o_metascore;
	private String o_imdb_bewertung;
	private String o_imdb_stimmen;
	private String o_imdb_id;
	private String o_art;
	// weitere Attribute
	private boolean o_gesehen = false;
	private boolean o_gemerkt = false;
	private boolean o_favorit = false;
	private String o_bemerkung = "false";

	/******************************************************************************
	 * [ATTRIBUTE] 																  *
	 * Klassen-Attribute														  *
	 * 																			  *
	 ******************************************************************************/
	private static int k_anzahl_verwalteter_filme = 0;
	private static Film k_verwaltete_filme[] = new Film[10]; // Anmerkung @ Methode array_veraendern()

	/******************************************************************************
	 * [METHODEN]																  *
	 * Konstruktoren															  *
	 * 																			  *
	 ******************************************************************************/
	public Film() {
		set_o_titel("default");
	}
	
	// Beim Erzeugen des JSON
	public Film(String titel, String jahr, String id) {
		set_o_titel(titel);
		set_o_jahr(jahr);
		set_o_imdb_id(id);
	}

	// mit allen Parametern in einem Array, beim Laden aus der DB und der Datei
	public Film(String[] film_als_array) {
		set_o_titel(film_als_array[0]);
		set_o_jahr(film_als_array[1]);
		set_o_rated(film_als_array[2]);
		set_o_erschienen_am(film_als_array[3]);
		set_o_laufzeit(film_als_array[4]);
		set_o_genre(film_als_array[5]);
		set_o_regisseur(film_als_array[6]);
		set_o_schriftsteller(film_als_array[7]);
		set_o_schauspieler(film_als_array[8]);
		set_o_kurzbeschreibung(film_als_array[9]);
		set_o_sprachen(film_als_array[10]);
		set_o_land(film_als_array[11]);
		set_o_auszeichnungen(film_als_array[12]);
		set_o_adresse_vom_plakat(film_als_array[13]);
		set_o_metascore(film_als_array[14]);
		set_o_imdb_bewertung(film_als_array[15]);
		set_o_imdb_stimmen(film_als_array[16]);
		set_o_imdb_id(film_als_array[17]);
		set_o_art(film_als_array[18]);
		if (film_als_array[19].equals("true")) {
			set_o_gesehen(true);
		} else {
			set_o_gesehen(film_status_pruefen('g'));
		}
		if (film_als_array[20].equals("true")) {
			set_o_gemerkt(true);
		} else {
			set_o_gemerkt(film_status_pruefen('m'));
		}
		if (film_als_array[21].equals("true")) {
			set_o_favorit(true);
		} else {
			set_o_favorit(film_status_pruefen('f'));
		}
		set_o_bemerkung(film_als_array[22]);
	}

	/* wird bisher nicht benötigt
	 * mit Objekt = Kopierkonstruktor
	 * keine setter, weil Prüfungen beim Erzeugen eines Film_objektes stattfanden
	public Film(Film f) {
		o_titel = f.get_o_titel();
		o_jahr = f.get_o_jahr();
		o_rated = f.get_o_rated();
		o_erschienen_am = f.get_o_erschienen_am();
		o_laufzeit = f.get_o_laufzeit();
		o_genre = f.get_o_genre();
		o_regisseur = f.get_o_regisseur();
		o_schriftsteller = f.get_o_schriftsteller();
		o_schauspieler = f.get_o_schauspieler();
		o_kurzbeschreibung = f.get_o_kurzbeschreibung();
		o_sprachen = f.get_o_sprachen();
		o_land = f.get_o_land();
		o_auszeichnungen = f.get_o_auszeichnungen();
		o_adresse_vom_plakat = f.get_o_adresse_vom_plakat();
		o_metascore = f.get_o_metascore();
		o_imdb_bewertung = f.get_o_imdb_bewertung();
		o_imdb_stimmen = f.get_o_imdb_stimmen();
		o_imdb_id = f.get_o_imdb_id();
		o_art = f.get_o_art();
		o_gesehen = f.iso_gesehen();
		o_gemerkt = f.iso_gemerkt();
		o_favorit = f.iso_favorit();
		o_bemerkung = f.get_o_bemerkung();
	}
	 */

	/******************************************************************************
	 * [METHODEN] 																  *
	 * getter & setter															  *
	 * 																			  *
	 ******************************************************************************/
	public String get_o_titel() {
		return o_titel;
	}

	private void set_o_titel(String title) {
		o_titel = title;
	}

	public String get_o_jahr() {
		return o_jahr;
	}

	/*
	 * der Wert für das Jahr kann sehr unterschiedlich sein, eine Darstellung
	 * der ersten und letzten Jahreszahl führt nicht immer zum Erfolg deswegen
	 * werden nur die ersten 4 Zeichen dargestellt sind mehr Zeichen enthalten,
	 * so wird dies mit einem plus signalisiert
	 */
	private void set_o_jahr(String jahr) {
		if (jahr.length() > 4) {
			jahr = jahr.substring(0, 4).concat("+");
			// jahr = jahr.substring(0, 4).concat("...").concat(jahr.substring(jahr.length() - 4, jahr.length()));
		}
		else{
			jahr = jahr+" ";
		}
		o_jahr = jahr;
	}

	public String get_o_rated() {
		return o_rated;
	}

	private void set_o_rated(String rated) {
		o_rated = rated;
	}

	public String get_o_erschienen_am() {
		return o_erschienen_am;
	}

	private void set_o_erschienen_am(String erschienen_am) {
		o_erschienen_am = erschienen_am;
	}

	public String get_o_laufzeit() {
		return o_laufzeit;
	}

	private void set_o_laufzeit(String laufzeit) {
		o_laufzeit = laufzeit;
	}

	public String get_o_genre() {
		return o_genre;
	}

	private void set_o_genre(String genre) {
		o_genre = genre;
	}

	public String get_o_regisseur() {
		return o_regisseur;
	}

	private void set_o_regisseur(String regisseur) {
		o_regisseur = regisseur;
	}

	public String get_o_schriftsteller() {
		return o_schriftsteller;
	}

	private void set_o_schriftsteller(String schriftsteller) {
		o_schriftsteller = schriftsteller;
	}

	public String get_o_schauspieler() {
		return o_schauspieler;
	}

	private void set_o_schauspieler(String schauspieler) {
		o_schauspieler = schauspieler;
	}

	public String get_o_kurzbeschreibung() {
		return o_kurzbeschreibung;
	}

	private void set_o_kurzbeschreibung(String kurzbeschreibung) {
		o_kurzbeschreibung = kurzbeschreibung;
	}

	public String get_o_sprachen() {
		return o_sprachen;
	}

	private void set_o_sprachen(String sprachen) {
		o_sprachen = sprachen;
	}

	public String get_o_land() {
		return o_land;
	}

	private void set_o_land(String land) {
		o_land = land;
	}

	public String get_o_auszeichnungen() {
		return o_auszeichnungen;
	}

	private void set_o_auszeichnungen(String auszeichnungen) {
		o_auszeichnungen = auszeichnungen;
	}

	public String get_o_adresse_vom_plakat() {
		return o_adresse_vom_plakat;
	}

	private void set_o_adresse_vom_plakat(String adresse_vom_plakat) {
		if (adresse_vom_plakat.equals("N/A")) {
			o_adresse_vom_plakat = "bilder/default_poster.jpg";
		} else
			o_adresse_vom_plakat = adresse_vom_plakat;
	}

	public String get_o_metascore() {
		return o_metascore;
	}

	private void set_o_metascore(String metascore) {
		o_metascore = metascore;
	}

	public String get_o_imdb_bewertung() {
		return o_imdb_bewertung;
	}

	private void set_o_imdb_bewertung(String imdb_bewertung) {
		o_imdb_bewertung = imdb_bewertung;
	}

	public String get_o_imdb_stimmen() {
		return o_imdb_stimmen;
	}

	private void set_o_imdb_stimmen(String imdb_stimmen) {
		o_imdb_stimmen = imdb_stimmen;
	}

	public String get_o_imdb_id() {
		return o_imdb_id;
	}

	private void set_o_imdb_id(String imdb_id) {
		o_imdb_id = imdb_id;
	}

	public String get_o_art() {
		return o_art;
	}

	private void set_o_art(String art) {
		o_art = art;
	}

	public boolean iso_gesehen() {
		return o_gesehen;
	}

	public void set_o_gesehen(boolean gesehen) {
		o_gesehen = gesehen;
	}

	public boolean iso_gemerkt() {
		return o_gemerkt;
	}

	public void set_o_gemerkt(boolean gemerkt) {
		o_gemerkt = gemerkt;
		if (o_gemerkt) {
			einfuegen_in_verwaltung(this);
		} else {
			loeschen_aus_verwaltung(this);
		}
	}

	public boolean iso_favorit() {
		return o_favorit;
	}

	public void set_o_favorit(boolean favorit) {
		o_favorit = favorit;
		if (o_favorit) {
			einfuegen_in_verwaltung(this);
		} else {
			loeschen_aus_verwaltung(this);
		}
	}

	public String get_o_bemerkung() {
		return o_bemerkung;
	}

	public void set_o_bemerkung(String bemerkung) {
		o_bemerkung = bemerkung;
	}

	/******************************************************************************
	 * [METHODEN] 																  *
	 * für die Verwaltungs-Liste												  *
	 * 																		      *
	 ******************************************************************************/
	public static Film[] get_verwaltete_filme() {
		return k_verwaltete_filme;
	}

	public static void einfuegen_in_verwaltung(Film gemerkter_film) {
		if (!(Suchen.ist_film_in_liste(gemerkter_film.get_o_imdb_id(), k_verwaltete_filme))) {
			k_anzahl_verwalteter_filme++;
			k_verwaltete_filme[k_anzahl_verwalteter_filme - 1] = gemerkter_film;
			if (k_anzahl_verwalteter_filme > 2) {
				array_veraendern();
			}
		}
	/* Kontrollausgaben */
		// else System.out.println("STATUS;Film "+gemerkter_film.get_o_titel()+" wird schon verwaltet;");
		// ausgeben_Liste("merken");
		// ausgeben_Liste("favorit");
	}

	//nur löschen, wenn sowohl gemerkt als auch favorit false sind
	public static void loeschen_aus_verwaltung(Film gemerkter_film) {
		if (!gemerkter_film.iso_favorit() && !gemerkter_film.iso_gemerkt()) {
			int position;
			position = Suchen.positon_in_Liste(gemerkter_film.get_o_imdb_id(), k_verwaltete_filme);
			if (position > 0) {
				for (int i = position; i < k_verwaltete_filme.length - 1; i++) {
					k_verwaltete_filme[i] = k_verwaltete_filme[i + 1];
				}
				k_anzahl_verwalteter_filme--;
				if (k_anzahl_verwalteter_filme > 2) {
					array_veraendern();
				}
				// System.out.println("STATUS;film gelöscht;");
			}
	/* Kontrollausgaben */
			// else System.out.println("STATUS;Film wird nicht verwaltet;");
			// ausgeben_Liste("merken");
			// ausgeben_Liste("favorit");
		}
	}

	/*
	 * wenn das Array in Abhängigkeit der prozentuallen Befüllung (10% & 80%)
	 * die Größe verändert, ist die ein guter Kompromis aus Speicherbedarf und
	 * Aufwand
	 * arraycopy
	 * 1. Parameter: welcher Array soll kopiert werden?
	 * 2. Parameter: an welchem Index wird begonnen zu Lesen?
	 * 3. Parameter: in welches Array soll kopiert werden?
	 * 4. Parameter: ab welchem Index darf in das neue Arrray geschrieben werde?
	 * 5. Parameter: wie viele Elemente sollen kopiert werden
	 * 
	 * dadurch enthält die Liste Leerstellen k_anzahl_verwalteter_filme gibt die
	 * inhaltliche Länge des Arrays an
	 */
	private static void array_veraendern() {
		Film[] neue_Liste;
		
		if (k_anzahl_verwalteter_filme > (8 * k_verwaltete_filme.length / 10)) {
			neue_Liste = new Film[k_verwaltete_filme.length * 3];
			System.arraycopy(k_verwaltete_filme, 0, neue_Liste, 0, k_anzahl_verwalteter_filme);
			k_verwaltete_filme = neue_Liste;
		}
		if (k_anzahl_verwalteter_filme < (k_verwaltete_filme.length / 10)) {
			neue_Liste = new Film[k_verwaltete_filme.length / 3];
			System.arraycopy(k_verwaltete_filme, 0, neue_Liste, 0, k_anzahl_verwalteter_filme);
			k_verwaltete_filme = neue_Liste;
		}
	}

	/******************************************************************************
	 * [METHODEN] 																  *
	 * Override																	  *
	 * 																			  *
	 ******************************************************************************/
	// zum Anzeigen in einer ObservableList<Film>
	@Override
	public String toString() {
		// String darstellung_in_einer_view = "" + this.get_o_titel() + " @ " + this.get_o_jahr();
		String darstellung_in_einer_view = this.get_o_jahr() + " | " + this.get_o_titel();
		
		return darstellung_in_einer_view;
	}

	/* sortiert nach ungesehen = a & gesehen = b => a<b */
	@Override
	public int compareTo(Film film) {
		String wert_this, wert_film;
		
		if (this.iso_gesehen()) {
			wert_this = "b";
		} else {
			wert_this = "a";
		}
		if (film.iso_gesehen()) {
			wert_film = "b";
		} else {
			wert_film = "a";
		}
		int diff = wert_this.compareTo(wert_film);
		
		return diff;
	}

	/******************************************************************************
	 * [METHODEN] 																  *
	 * Hilfsmethoden															  *
	 * 																			  *
	 ******************************************************************************/
	/*
	 * die Status gesehen, gemerkt, favorit können unterschiedlich sein, wenn
	 * der Film aus dem Speicher (Datei) geladen oder aus der OMDB abgefragt
	 * wird, deswegen wird beim Erzeugen eines Film-objektes der jeweilige
	 * Status geprüft
	 *
	 * ist der eingehende Status true, so schon sind Informationen zum Film
	 * vorhanden und es braucht keine weitere Prüfung
	 * 
	 * ist der eingehende Status false, so könnte der Film schon in der
	 * Verwaltungs-Liste sein, dies wird hier geprüft und der entsprechende
	 * Status übernommen
	 */
	private boolean film_status_pruefen(char status) {
		boolean b = false;
		String id = this.o_imdb_id;
		int position;
		
		switch (status) {
		case 'g':
			if (Suchen.ist_film_in_liste(id, k_verwaltete_filme)) {
				position = Suchen.positon_in_Liste(id, k_verwaltete_filme);
				b = k_verwaltete_filme[position].iso_gesehen();
			}
			break;
		case 'm':
			if (Suchen.ist_film_in_liste(id, k_verwaltete_filme)) {
				position = Suchen.positon_in_Liste(id, k_verwaltete_filme);
				b = k_verwaltete_filme[position].iso_gemerkt();
			}
			break;
		case 'f':
			if (Suchen.ist_film_in_liste(id, k_verwaltete_filme)) {
				position = Suchen.positon_in_Liste(id, k_verwaltete_filme);
				b = k_verwaltete_filme[position].iso_favorit();
			}
			break;
		}
		
		return b;
	}
	
	 /*zum Testen
	private static void ausgeben_Liste(String listentyp) {
		System.out.printlnn("STATUS;Liste " + listentyp + ";");
		for (int i = 0; i < k_anzahl_verwalteter_filme; i++) {
			if ((listentyp.equals("merken") && k_verwaltete_filme[i].iso_gemerkt())
					|| (listentyp.equals("favorit") && k_verwaltete_filme[i].iso_favorit())) {
				System.out.println("Titel: " + k_verwaltete_filme[i].get_o_titel());
			}
		}
	} */
	 
}
