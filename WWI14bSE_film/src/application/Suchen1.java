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
	private static final String string = null; //TODO: was isch des?

	/*
	 * JavaScript-Bsp: OMDB-Abfrage per JSON var url = 'https://omdbapi.com';
	 * var data = {t: 'Pulp Fiction', y: '1994'}; $.getJSON(url, data,
	 * function(response){ console.log(response); });
	 */

	/******************************************************************************
	 * [METHODEN]																  *
	 * Durchsuchen der eigenen Liste(n)											  *
	 * 																			  *
	 ******************************************************************************/
	public static Film filmdetails_aus_eigener_Liste_generieren(String id) {
		Film gefundener_film = null;
		for (Film f : Film.get_verwaltete_filme()) {
			if (f != null) {
				if (f.get_o_imdb_id().equals(id)) {
					gefundener_film = f;
				}
			}
		}
		return gefundener_film;
	}

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
			for (Film f : Film.get_verwaltete_filme()) {
				if (f != null) {
					if (f.get_o_titel().toUpperCase().contains(titel.toUpperCase()) && f.iso_gemerkt()) {
						hilfsliste[anzahl_gefundene_Filme] = f;
						anzahl_gefundene_Filme++;
					}
				}
			}
		}
		if (listentyp == 'f') {
			for (Film f : Film.get_verwaltete_filme()) {
				if (f != null) {
					if (f.get_o_titel().toUpperCase().contains(titel.toUpperCase()) && f.iso_favorit()) {
						hilfsliste[anzahl_gefundene_Filme] = f;
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
		Main.fehler_ausgeben("STATUS;gefundene Filme: \n;");
		for (Film f : gesuchte_Filme) {
			if (f != null) {
				System.out.println(f.get_o_titel());
			}
		}
	*/
		return gesuchte_Filme;
	}

	/******************************************************************************
	 * [METHODEN] 																  *
	 * Abfragen der OMDB														  *
	 *																			  *
	 * @throws IOException 
	 ******************************************************************************/
	
	/* kurze Filmobjekte
	 * für die View werden nur Titel, Jahr & ID gebraucht
	 * vorteilhaft bei der Livesuche
	 * 
	 * zwei Suchanfragen werden gestellt - einmal ohne und einmal mit Stern am Ende
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
	public static Film[] filmliste_aus_DB_zusammenstellen_kurz(String suchtext) throws IOException {
		
		Film[] filme_mit_wildcard = null;
		try {
			filme_mit_wildcard = filme_aus_DB_abfragen(string_bauer(suchtext, true));
		} catch (JSONException e) {
			System.out.println("Keine Filme mit wildcard gefunden");
		}
		
		// Errorhandling an dieser Stelle: Weil so eine teilweise Suche (mit wildcard oder ohne) auch möglich ist, wenn eine einzelne Suche fehlschlägt
		Film[] filme_ohne_wildcard = null;
		try {
			filme_ohne_wildcard = filme_aus_DB_abfragen(string_bauer(suchtext, false));
		} catch (JSONException e) {
			System.out.println("Keine Filme ohne wildcard gefunden");
		}
		
		// Zusammenführen
		Film[] filme = new Film[filme_mit_wildcard.length + filme_ohne_wildcard.length];
		
		System.arraycopy(filme_mit_wildcard, 0, filme, 0, filme_mit_wildcard.length);
		System.arraycopy(filme_ohne_wildcard, 0, filme, filme_mit_wildcard.length , filme_ohne_wildcard.length);
		
		return filme;
	}
	
	public static Film filmdetails_zum_objekt_generieren(String id) throws IOException {
		Film film = new Film();
		try {
			String film_details = json_aus_DB_abfragen("http://www.omdbapi.com/?i=" + id);
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
			//TODO: Kein GUI Zeug hier, Abhängigkeiten vermeiden (Suchen sollte eine unabhängige, wiederverwendbare Klasse sein)! (exceptions nach oben weiterreichen)
//			Main.fehler_ausgeben("FEHLER;Die Abfrage nach war nicht erfolgreich;");
//			Main.fehler_ausgeben("Status;Bei zusammensetzen des Filmobjektes (JSON_lang) trat ein Fehler auf.;");
		}
		return film;
	}

	// diese Methode wird zusätzlich beim Anzeigen des Filmplakates verwendet
	public static BufferedInputStream verbindung_pruefen(String zugriffslink)
			throws IOException, MalformedURLException {
		URL link = new URL(zugriffslink);
		URLConnection verbindung = link.openConnection();
		BufferedInputStream input = new BufferedInputStream(verbindung.getInputStream());
		return input;
	}
	
	/******************************************************************************
	 * [METHODEN]																  *
	 * Hilfsmethoden															  *
	 * 																			  *
	 * @throws IOException 
	 ******************************************************************************/
	/* für DB-Abfrage wurde das Standard-Trennzeichen(Leerzeichen) durch "\A" ersetzen,
	 * somit kann die gesamte "Zeile" gelesen werden (anstatt nur bis zum Leerzeichen)
	 * 
	 * die Angabe des Charset UTF-8 für den Scanner verbessert die Suchmöglichkeiten
	 * Sonderzeichen *, +, ö etc. sind nicht nur möglich sondern auch nötig
	 */
	private static String json_aus_DB_abfragen(String zugriffslink) throws IOException {
		String json_als_string = "";
		BufferedInputStream input = verbindung_pruefen(zugriffslink);
		Scanner scanner = new Scanner(input, "UTF-8").useDelimiter("\\A");
		json_als_string = scanner.next();
		scanner.close();
		input.close();
		return json_als_string;
	}

	private static Film[] filme_aus_DB_abfragen(String url) throws JSONException, IOException {
		Film[] filme;
		
		String json_string = json_aus_DB_abfragen(url);
		JSONObject json = new JSONObject(json_string);
		
		JSONArray filmJSONArray = json.getJSONArray("Search");
		
		filme = new Film[filmJSONArray.length()]; 
		for (int i = 0; i < filmJSONArray.length(); i++) {
			JSONObject aktuellerFilm = filmJSONArray.getJSONObject(i);
			filme[i] = new Film(aktuellerFilm.getString("Title"), aktuellerFilm.getString("Year"),
					aktuellerFilm.getString("imdbID"));
		} 
		return filme;
	}

	private static String string_bauer(String suchwort, boolean wildcards) throws UnsupportedEncodingException{
		suchwort = URLEncoder.encode(suchwort, "UTF-8");
		String url = "";
		if (wildcards) {
			url = "http://www.omdbapi.com/?s=" + "*" + suchwort + "*";
		} else {
			url = "http://www.omdbapi.com/?s=" + suchwort;
		}
		return url;
	}

	
}
