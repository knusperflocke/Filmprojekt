package application;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.*;

public abstract class Suchen {
	/******************************************************************************
	 * [METHODEN]																  *
	 * Durchsuchen der eigenen Liste(n)											  *
	 * 																			  *
	 ******************************************************************************/
	/* unabhängig von Groß-/ Kleinschreibung durch toUpperCase()
	 * die max. Länge des zurückgegeben Arrays ist im worst-case
	 * gleich der Anzahl der verwalteten Film, deswegen wird die
	 * Liste abgeschnitten
	 */
	public static Film[] film_in_eigener_Liste_suchen(String titel, char listentyp) {
		Film[] hilfsliste = new Film[Film.get_verwaltete_filme().length];
		int anzahl_gefundene_Filme = 0;
		
	// Liste befüllen
		if (listentyp == 'm') {
			for (Film film_germerkt : Film.get_verwaltete_filme()) {
				if (film_germerkt != null) {
					if (film_germerkt.get_o_titel().toUpperCase().contains(titel.toUpperCase()) && film_germerkt.iso_gemerkt()) {
						hilfsliste[anzahl_gefundene_Filme] = film_germerkt;
						anzahl_gefundene_Filme++;
					}
				}
			}
		}
		if (listentyp == 'f') {
			for (Film film_favorit : Film.get_verwaltete_filme()) {
				if (film_favorit != null) {
					if (film_favorit.get_o_titel().toUpperCase().contains(titel.toUpperCase()) && film_favorit.iso_favorit()) {
						hilfsliste[anzahl_gefundene_Filme] = film_favorit;
						anzahl_gefundene_Filme++;
					}
				}
			}
		}
	// Liste abschneiden
		Film[] gesuchte_Filme = new Film[anzahl_gefundene_Filme];
		for (int j = 0; j < anzahl_gefundene_Filme; j++) {
			gesuchte_Filme[j] = hilfsliste[j];
		}
	/* Kontrollausgabe
		System.out.println("STATUS;gefundene Filme: \n;");
		for (Film film : gesuchte_Filme) {
			if (film != null) {
				System.out.println(film.get_o_titel());
			}
		}*/

		return gesuchte_Filme;
	}

	public static Film filmdetails_aus_eigener_Liste_generieren(String id) {
		Film gefundener_film = new Film();
		
		for (Film film : Film.get_verwaltete_filme()) {
			if (film != null) {
				if (film.get_o_imdb_id().equals(id)) {
					gefundener_film = film;
				}
			}
		}
		
		return gefundener_film;
	}

	/******************************************************************************
	 * [METHODEN] 																  *
	 * Durchsuchen der OMDB														  *
	 *																			  *
	 ******************************************************************************/
	/* zwei Suchanfragen werden gestellt - einmal ohne und einmal mit Stern am Ende
	 * auf diese Weise werden Film angezeigt, welche das Suchwort in gleicher Länge
	 * im Titel beherbergen
	 * und Film, bei denen das Suchwort nur zum Teil im titel vorkommt
	 * 
	 * die Datenbank(DB) gibt max. 10 Film zurück, deswegen hat der zurückgegebene
	 * Film[] eine Länge von 20
	 * durch den KeyEvent ist das Tippen schneller als die Suche, somit
	 * die Arrays filme_ohne_stern sowie filme_mit_stern zum Zeitpunkt des Zusammenführens
	 * nicht immer aktuell. Um einen "ArrayOutOfBounds"-Fehler zu vermeinden, wird
	 * der endgültige Array mit möglichen Leerstellen erzeugt, diese werden beim
	 * Verbinden der ObservableList mit dem ListView herausgefiltert
	 */
	public static Film[] filmliste_aus_DB_zusammenstellen_kurz(String suchwort)
			throws UnsupportedEncodingException, MalformedURLException, IOException, JSONException {
		Film[] filme = null, filme_ohne_stern = null, filme_mit_stern = null;
		
		try {
			filme_ohne_stern = filme_aus_DB_abfragen(abfragestring_erzeugen(suchwort, false));
		} catch (JSONException json_e) {
			JSONException ex_json = new JSONException("HINWEIS;Die Abfrage nach " + suchwort + " war nicht erfolgreich;");
			throw ex_json;
		}
		
		try{
			filme_mit_stern = filme_aus_DB_abfragen(abfragestring_erzeugen(suchwort, true));
		} catch (JSONException json_e) {
			JSONException ex_json = new JSONException("HINWEIS;Die Abfrage nach " + suchwort + " war nicht erfolgreich;");
			throw ex_json;
		}
		
		// Zusammenführen
		filme = new Film[filme_mit_stern.length + filme_ohne_stern.length];
		
		System.arraycopy(filme_mit_stern, 0, filme, 0, filme_ohne_stern.length);
		System.arraycopy(filme_ohne_stern, 0, filme, filme_mit_stern.length , filme_ohne_stern.length);
		
	
//	// Arrays zusammenführen
//		filme = new Film[20];
//		if (filme_ohne_stern != null) {
//			System.arraycopy(filme_ohne_stern, 0, filme, 0, filme_ohne_stern.length);
//		}
//		if (filme_mit_stern != null) {
//			System.arraycopy(filme_mit_stern, 0, filme, 10, filme_mit_stern.length);
//		}
//		if (filme != null){
//			filme = liste_abgleichen(filme);
//		}
		
		return filme;
	}

	/* durch den Anzeigehändler aufgerufen */
	public static Film filmdetails_zum_objekt_wandeln(String film_id)
			throws MalformedURLException, IOException, JSONException {
		Film film = new Film();
		
		try{
			String film_details = json_aus_DB_abfragen("http://www.omdbapi.com/?i=" + film_id);
			JSONObject aktuellerFilm = new JSONObject(film_details);
			String[] film_als_array = new String[23];
		// JSON aufteilen
			film_als_array[0] = aktuellerFilm.getString("Title");
			film_als_array[1] = aktuellerFilm.getString("Year");
			film_als_array[2] = aktuellerFilm.getString("Rated");
			film_als_array[3] = aktuellerFilm.getString("Released");
			film_als_array[4] = aktuellerFilm.getString("Runtime");
			film_als_array[5] = aktuellerFilm.getString("Genre");
			film_als_array[6] = aktuellerFilm.getString("Director");
			film_als_array[7] = aktuellerFilm.getString("Writer");
			film_als_array[8] = aktuellerFilm.getString("Actors");
			film_als_array[9] = aktuellerFilm.getString("Plot");
			film_als_array[10] = aktuellerFilm.getString("Language");
			film_als_array[11] = aktuellerFilm.getString("Country");
			film_als_array[12] = aktuellerFilm.getString("Awards");
			film_als_array[13] = aktuellerFilm.getString("Poster");
			film_als_array[14] = aktuellerFilm.getString("Metascore");
			film_als_array[15] = aktuellerFilm.getString("imdbRating");
			film_als_array[16] = aktuellerFilm.getString("imdbVotes");
			film_als_array[17] = aktuellerFilm.getString("imdbID");
			film_als_array[18] = aktuellerFilm.getString("Type");
			film_als_array[19] = "false";
			film_als_array[20] = "false";
			film_als_array[21] = "false";
			film_als_array[22] = "false";
		// Film_Objekt(e) erzeugen
			film = new Film(film_als_array);
		} catch (JSONException json_e) {
			JSONException ex_json = new JSONException("HINWEIS;Die Abfrage nach des Film mittels ID war nicht erfolgreich;");
			throw ex_json;
		}
		
		return film;
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Hilfsmethoden - [OMDB-Abfrage]											  *
	 * 																			  *
	 ******************************************************************************/
	// diese Methode wird zusätzlich beim Anzeigen des Filmplakates verwendet
	public static BufferedInputStream verbindung_pruefen(String zugriffslink)
			throws MalformedURLException, IOException {
		BufferedInputStream input = null;
	
		try{
			//URL Objekt zur Erzeugung der Verbindung mit der Website 
			URL link = new URL(zugriffslink);
			URLConnection verbindung = link.openConnection();
			//Input Stream zum Einlesen der Daten von der Website
			input = new BufferedInputStream(verbindung.getInputStream());
		} catch (MalformedURLException e) {
			MalformedURLException ex_url = new MalformedURLException("FEHLER;Es wurde kein passendes Verbindungsprotokoll gefunden.;");
			throw ex_url;
		} catch (IOException e) {
			IOException ex_io = new IOException("FEHLER;Es besteht keine Verbindung zum Server der OMDB.;"); 
			throw ex_io;
		}		
			
		return input;
	}

	private static String abfragestring_erzeugen(String suchwort, boolean stern_gesetzt)
			throws UnsupportedEncodingException {
		String url = "";
		
		try {
			suchwort = URLEncoder.encode(suchwort, "UTF-8");
			if (stern_gesetzt) {
				url = "http://www.omdbapi.com/?s=" + "*" + suchwort + "*";
			} else {
				url = "http://www.omdbapi.com/?s=" + suchwort;
			}
		} catch (UnsupportedEncodingException encoding_e){
			UnsupportedEncodingException ex_encoding = new UnsupportedEncodingException("Status;Suchetext ist fehlerhaft.;");
			throw ex_encoding;
		}
			
		return url;
	}
		
	/* für DB-Abfrage wurde das Standard-Trennzeichen(Leerzeichen) durch "\A" ersetzen,
	 * somit kann die gesamte "Zeile" gelesen werden (anstatt nur bis zum Leerzeichen)
	 * 
	 * die Angabe des Charset UTF-8 für den Scanner verbessert die Suchmöglichkeiten
	 * Sonderzeichen *, +, ö etc. sind nicht nur möglich sondern auch nötig
	 */
	private static String json_aus_DB_abfragen(String zugriffslink)
			throws  IOException { //MalformedURLException,
		String json_als_string = "";
		
		// Standard Delimitter zu \\A ändern, dass gesamte Zeile gelesen wird (sonst nur bis zu Whitespace)
		BufferedInputStream input = verbindung_pruefen(zugriffslink);
		Scanner scanner = new Scanner(input, "UTF-8").useDelimiter("\\A");
		//speichern des Ausgelesenen in String Variable json als string
		json_als_string = scanner.next();
		scanner.close();
		input.close();
		
		return json_als_string;
	}
	
	/* kurze Filmobjekte
	 * für die View werden nur Titel, Jahr & ID gebraucht
	 * dem User werden durch eine schrittweise-Abfrage eher Informationen angezeigt
	 * die entstehende JSONException wird beim Aufrufer mit einer Fehlermeldung
	 * verknüpft
	 */
	public static Film[] filme_aus_DB_abfragen(String url)
			throws MalformedURLException, IOException, JSONException {
		Film[] filme = null;

		String json_als_string = json_aus_DB_abfragen(url);
		JSONObject json = new JSONObject(json_als_string);
		JSONArray filmJSONArray = json.getJSONArray("Search");
		filme = new Film[filmJSONArray.length()];
		for (int i = 0; i < filmJSONArray.length(); i++) {
			JSONObject aktuellerFilm = filmJSONArray.getJSONObject(i);
			filme[i] = new Film(aktuellerFilm.getString("Title"), aktuellerFilm.getString("Year"),
					aktuellerFilm.getString("imdbID"));
		}

		return filme;
	}
	
	/******************************************************************************
	 * [METHODEN]																  *
	 * Hilfsmethoden - [Listen bearbeitung]										  *
	 * 																			  *
	 ******************************************************************************/
	// nach dem der Film gefunden wurde, hilft das break die Schleife zu
	// verlassen
	// damit durch andere Filme, der Status nicht wieder auf false gesetzt wird
	public static boolean ist_film_in_liste(String film_id, Film[] liste) {
		boolean b = false;

		for (int i = 0; i < liste.length; i++) {
			if (liste[i] != null) {
				if (liste[i].get_o_imdb_id().equals(film_id)) {
					b = true;
					break;
				} else {
					b = false;
				}
			}
		}

		return b;
	}

	public static int positon_in_Liste(String film_id, Film[] liste) {
		int position = -1;

		for (int i = 0; i < liste.length; i++) {
			if(liste[i] != null){
				if (liste[i].get_o_imdb_id().equals(film_id)){
					position = i;
					break;
				}
			}
		}

		return position;
	}
	
	/*
	 * die Liste von der Datenbank wird gekürzt um doppelte Einträge zu
	 * vermeiden ebenfalls wird sie mit der verwalteten Liste abgeglichen, um in
	 * manchen Fällen die Status bzw. Buttons (g, m, f) korrekt wieder zu geben
	 */
	private static Film[] liste_abgleichen(Film[] liste) {
		Film[] gefilterte_filme = new Film[liste.length];
		String film_id;

		for (int i = 0; i < gefilterte_filme.length; i++) {
			if (liste[i] != null) {
				film_id = liste[i].get_o_imdb_id();
				// Prüfen ob noch nicht in der zusammengefassen Liste
				if (!ist_film_in_liste(film_id, gefilterte_filme)) {
					// Prüfen, ob in verwalteter Liste
					if (ist_film_in_liste(film_id, Film.get_verwaltete_filme())) {
						int pos = positon_in_Liste(film_id, Film.get_verwaltete_filme());
						gefilterte_filme[i] = Film.get_verwaltete_filme()[pos];
					} else {
						gefilterte_filme[i] = liste[i];
					}
				}
			}
		}

		return gefilterte_filme;
	}
}
