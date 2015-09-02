package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	private static File k_filmspeicher;
	
	public static void main(String[] args) {
		launch(args); // started GUI
	}

	@Override
	public void start(Stage stage) throws Exception {
		String gui_datei = "GUI.fxml";
		String css_datei = "application.css";
		String app_titel = "Filmsuche powered by KLR";
		String daten_datei = "daten.csv";
	// GUI aus fxml einbinden
		try {
			Parent root = FXMLLoader.load(getClass().getResource(gui_datei));
			if (root != null) {
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource(css_datei).toExternalForm());
				stage.setTitle(app_titel);
				stage.getIcons().add(new Image("bilder/icon-movie_blue.png"));
				stage.setScene(scene);
				stage.show();
			}
		} catch (IllegalArgumentException ex_) {
			fehler_ausgeben("FEHLER;Einige Dateien konnte nicht gefunden werden.;");
		} catch (IOException ex_io) {
			fehler_ausgeben("FEHLER;Datei \"" + gui_datei + "\" ist fehlerhaft.;");
		} catch (NullPointerException ex_pointer) {
			fehler_ausgeben("FEHLER;Datei \"" + gui_datei + "\" befindet sich nicht im gleichen Verzeichnis.;");
		}

		try {
			k_filmspeicher = datei_anlegen(daten_datei);
			if (k_filmspeicher.exists()) {
				aus_datei_lesen(k_filmspeicher);
			} else {
				fehler_ausgeben("FEHLER;Datei \"" + daten_datei + "\" nicht vorhanden.;");
			}
		} catch (Exception e) {
			fehler_ausgeben(e.getMessage());
		}
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Dateiverwaltung															  *
	 *																			  *
	 ******************************************************************************/
	//[Datei anlegen] *************************************************************/
	private File datei_anlegen(String name) throws Exception {
		File datei = new File(name);
		
		if (!datei.exists()) {
			try {
				// erstellt Datei nur, wenn keine existiert
				datei.createNewFile();
			} catch (SecurityException sec_e) {
				SecurityException ex_security;
				ex_security = new SecurityException(
						"FEHLER;Nicht ausreichende Berechtigung beim Erzeugen der Datei \"" + datei.getName() + "\".;");
				throw ex_security;
			} catch (IOException io_e) {
				IOException ex_erzeugen;
				ex_erzeugen = new IOException(
						"FEHLER;Beim Erzeugen der Datei \"" + datei.getName() + "\" trat ein Fehler auf.;");
				throw ex_erzeugen;
			}
		} else {
			// fehler_ausgeben("STATUS;Datei vorhanden;");
		}
		
		return datei;
	}

	//[in Datei schreiben] ********************************************************/
	/* in die Datei wird durch die Einfache Methdoe write() geschrieben
	 * auf diese weise wird die komplette Liste der verwalteten Filme gespeichert
	 * der Aufwand - welcher Film hat sich verändert  / an welcher Stelle steht der Film -
	 * bleibt der programm erspart */
	private static void in_datei_schreiben(File datei, String text) throws IOException {
		// fehler_ausgeben("STATUS;Datei schreiben? "+datei.canWrite()+";");
		if (datei.canWrite()) {
			try {
				FileWriter Schreiber = new FileWriter(datei);
				Schreiber.write(text);
				Schreiber.close();
			} catch (IOException io_e) {
				IOException ex_schreibfehler;
				ex_schreibfehler = new IOException("FEHLER;Beim Schreiben in die Datei \""
						+ datei.getName() + "\" trat ein Fehler auf.;");
				throw ex_schreibfehler;
			}
		} else {
			IOException ex_lesen_nicht_erlaubt;
			ex_lesen_nicht_erlaubt = new IOException("FEHLER;Keine ausreichende Berechtigung um in die Datei \""
					+ datei.getName() + "\" zu schreiben.;");
			throw ex_lesen_nicht_erlaubt;
		}
	}

	//[aus Datei lesen] ***********************************************************/
	/* das Lesen aus der Datei erfolgt Zeilenweise */
	private static void aus_datei_lesen(File datei) throws IOException, FileNotFoundException {
		// fehler_ausgeben("STATUS;Datei lesen? "+datei.canRead()+";");
		String zeile_aus_datei = "";

		try {
			FileReader zeichen_leser = new FileReader(datei);
			BufferedReader zeilen_leser = new BufferedReader(zeichen_leser);
			while (zeilen_leser.ready()) {
				zeile_aus_datei = zeilen_leser.readLine();
				String[] film_als_array = new String[23];
				film_als_array = zeile_aus_datei.split(";");
				Film film_pro_zeile = new Film(film_als_array);
			}
			zeilen_leser.close();
			zeichen_leser.close();
		} catch (FileNotFoundException file_e) {
			FileNotFoundException ex_keine_datei;
			ex_keine_datei = new FileNotFoundException(
					"FEHLER;Datei \"" + datei.getName() + "\" ist nicht vorhanden.;");
			throw ex_keine_datei;
		} catch (IOException io_e) {
			IOException ex_lesefehler;
			ex_lesefehler = new IOException("FEHLER;Beim Lesen aus der Datei \"" + datei.getName()
					+ "\" trat ein Fehler auf.\n Daten können unvollständige sein.;");
			throw ex_lesefehler;
		}
	}

	/* Das Speichern der verwalteten Filme geschied beim Drücken der Butten
	 * Gesehen, Merken, Favorit im Bereich der Detailansicht
	 * 
	 * die toString-Methoder der Klasse wird schon auf andere Weise genutzt,
	 * deswegen wird jeder Film über ein String-Array zu einem String gewandelt
	 * Trennzeichen zwischen den Attributen => ;
	 * Trennzeichen zwischen den Filmen 	=> \n
	 * */
	public static void speichern() {
		String alle_verwalteten_filme = "";
		String[] film_als_String = new String[23];

	// Liste in String wandeln
		for (Film film : Film.get_verwaltete_filme()) {
			if (film != null) {
				film_als_String[0] = film.get_o_titel();
				film_als_String[1] = film.get_o_jahr();
				film_als_String[2] = film.get_o_rated();
				film_als_String[3] = film.get_o_erschienen_am();
				film_als_String[4] = film.get_o_laufzeit();
				film_als_String[5] = film.get_o_genre();
				film_als_String[6] = film.get_o_regisseur();
				film_als_String[7] = film.get_o_schriftsteller();
				film_als_String[8] = film.get_o_schauspieler();
				film_als_String[9] = film.get_o_kurzbeschreibung();
				film_als_String[10] = film.get_o_sprachen();
				film_als_String[11] = film.get_o_land();
				film_als_String[12] = film.get_o_auszeichnungen();
				film_als_String[13] = film.get_o_adresse_vom_plakat();
				film_als_String[14] = film.get_o_metascore();
				film_als_String[15] = film.get_o_imdb_bewertung();
				film_als_String[16] = film.get_o_imdb_stimmen();
				film_als_String[17] = film.get_o_imdb_id();
				film_als_String[18] = film.get_o_art();
				film_als_String[19] = "" + film.iso_gesehen();
				film_als_String[20] = "" + film.iso_gemerkt();
				film_als_String[21] = "" + film.iso_favorit();
				film_als_String[22] = film.get_o_bemerkung();
				for (int i = 0; i < film_als_String.length; i++) {
					alle_verwalteten_filme = alle_verwalteten_filme + film_als_String[i] + ";";
				}
	// Strings zusammenführen
				alle_verwalteten_filme = alle_verwalteten_filme + "\n";
			}
		}
	// film-string speichern
		try {
			in_datei_schreiben(k_filmspeicher, alle_verwalteten_filme);
		} catch (Exception e) {
			fehler_ausgeben(e.getMessage());
		}
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Fehlerbehandlung															  *
	 *																			  *
	 ******************************************************************************/
	/* die Handler (_Suche, _Anzeige) geben ihre Meldungen zur Ausgabe weiter
	 * die Klassen, Suchen und Film bleiben unabhängig von der Main
	 * 
	 * Fehler werden als Error, Hinweise als Information, dargestellt
	 * eine Status-Meldung erfolgt auf der Konsole
	 * 
	 * */
	public static void fehler_ausgeben(String meldung) {
		String[] fenster_inhalt = new String[2];
		fenster_inhalt = meldung.split(";");
		Alert alarm_fenster;
		
		fenster_inhalt[0] = fenster_inhalt[0].toUpperCase();
		if (fenster_inhalt[0].equals("FEHLER")) {
			alarm_fenster = new Alert(AlertType.ERROR);
			alarm_fenster.setHeaderText(null);
			alarm_fenster.setTitle(fenster_inhalt[0]);
			alarm_fenster.setContentText(fenster_inhalt[1]);
			alarm_fenster.show();
		} else if (fenster_inhalt[0].equals("HINWEIS")) {
			alarm_fenster = new Alert(AlertType.INFORMATION);
			alarm_fenster.setHeaderText(null);
			alarm_fenster.setTitle(fenster_inhalt[0]);
			alarm_fenster.setContentText(fenster_inhalt[1]);
			alarm_fenster.show();
		} else {
			System.out.println("[" + fenster_inhalt[0] + "] " + fenster_inhalt[1]);
		}
	}
}