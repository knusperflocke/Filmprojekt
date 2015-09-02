package application;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class LayoutController implements Initializable {
	/******************************************************************************
	 * [ATTRIBUTE]																  *
	 * 																			  *
	 * 																			  *
	 ******************************************************************************/
	private Handler_Suche o_suchhilfe;
	private Handler_Anzeige o_anzeigehilfe;
	private Film o_angezeigter_Film;

	/******************************************************************************
	 * [ATTRIBUTE]																  *
	 * Elemente des GUI															  *
	 * 																			  *
	 * Hinweis: 																  *
	 * die ersten 2 Stellen deuten auf den Typ des Elements 					  *
	 * ein A am Ende weist auf ein anzeigendes Element 							  *
	 ******************************************************************************/
	// [Elemente Tab DB-Suche] ****************************************************/
	@FXML
	private Button bt_suche_datenbank;
	@FXML
	private TextField tf_suche_datenbank;
	@FXML
	private ListView<Film> lv_datenbank;
	private ObservableList<Film> ol_datenbank_liste = FXCollections.<Film> observableArrayList();
	// [Elemente Tab Merkliste] ***************************************************/
	@FXML
	private Button bt_suche_m;
	@FXML
	private TextField tf_suche_m;
	@FXML
	private ListView<Film> lv_merken;
	private ObservableList<Film> ol_merkliste = FXCollections.<Film> observableArrayList();
	// [Elemente Tab Favoriten] ***************************************************/
	@FXML
	private Button bt_suche_f;
	@FXML
	private TextField tf_suche_f;
	@FXML
	private ListView<Film> lv_favoriten;
	private ObservableList<Film> ol_favoritenliste = FXCollections.<Film> observableArrayList();
	// [Elemente Detailansicht] ***************************************************/
	@FXML
	private AnchorPane ap_detail;
	@FXML
	private Label lb_titel_A;
	@FXML
	private Label lb_erscheinungsjahr;
	@FXML
	private Label lb_erscheinungsjahr_A;
	@FXML
	private Label lb_laufzeit;
	@FXML
	private Label lb_laufzeit_A;
	@FXML
	private Label lb_genre;
	@FXML
	private Label lb_genre_A;
	@FXML
	private Label lb_regisseur;
	@FXML
	private Label lb_regisseur_A;
	@FXML
	private Label lb_schauspieler;
	@FXML
	private TextArea ta_schauspieler_A;
	@FXML
	private Label lb_kurzbeschreibung;
	@FXML
	private TextArea ta_kurzbeschreibung_A;
	@FXML
	private Label lb_awards;
	@FXML
	private Label lb_awards_A;
	@FXML
	private ImageView iv_plakat;
	@FXML
	private Label lb_bewertung;
	@FXML
	private Label lb_bewertung_A;
	// [Buttons] ******************************************************************/
	@FXML
	private Button bt_gesehen;
	@FXML
	private Button bt_merken;
	@FXML
	private Button bt_favorit;
	@FXML
	private Button bt_notiz;
	@FXML
	private Tooltip tt_merken;
	@FXML
	private Tooltip tt_favorit;
	@FXML
	private Tooltip tt_gesehen;
	@FXML
	private Tooltip tt_notiz;
	/* [Bilder für die Buttons] ***************************************************
	 * Icons made by "http://www.flaticon.com/authors/bryn-taylor"
	 * from ="http://www.flaticon.com" is licensed by
	 * "http://creativecommons.org/licenses/by/3.0/" CC BY 3.0
	 */
	private Image auge_schwarz_weiss = new Image("bilder/eye_blocked_grey.png");
	private Image auge_bunt = new Image("bilder/eye_green.png");
	private ImageView Vauge = new ImageView();
	private Image herz_schwarz_weiss = new Image("bilder/heart_grey.png");
	private Image herz_bunt = new Image("bilder/heart_red.png");
	private ImageView Vherz = new ImageView();
	private Image stern_schwarz_weiss = new Image("bilder/star_grey.png");
	private Image stern_bunt = new Image("bilder/star_yellow.png");
	private ImageView Vstern = new ImageView();
	private Image notiz_schwarz_weiss = new Image("bilder/note_grey.png");
	private Image notiz_bunt = new Image("bilder/note_blue.png");
	private ImageView Vnotiz = new ImageView();
	/* weitere Inhalte des JSON ***************************************************
	@FXML
	private Label lb_rated;
	@FXML
	private Label lb_schriftsteller;
	@FXML
	private Label lb_sprachen;
	@FXML
	private Label lb_land;
	@FXML
	private Label lb_metascore;
	@FXML
	private Label lb_imdb_stimmen;
	@FXML
	private Label lb_imdb_id;
	@FXML
	private Label lb_art;
	 */
	
	/******************************************************************************
	 * [METHODEN]																  *
	 *																			  *
	 * Abkürzungen: g = gesehen, m = merken, f = favorit, db = datenbank		  *
	 ******************************************************************************/

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		lv_datenbank.getSelectionModel().selectedItemProperty().addListener(MeinListener -> liste_DB_geklickt());
		lv_merken.getSelectionModel().selectedItemProperty().addListener(MeinListener -> liste_M_geklickt());
		lv_favoriten.getSelectionModel().selectedItemProperty().addListener(MeinListener -> liste_F_geklickt());
		this.o_suchhilfe = new Handler_Suche();
		this.o_anzeigehilfe = new Handler_Anzeige();
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Klicken auf Buttons														  *
	 * 																			  *
	 ******************************************************************************/
	// [Gesehen - Button]
	@FXML
	private void klicken_Button_G(ActionEvent event) {
		if ((o_angezeigter_Film != null) && (o_angezeigter_Film.iso_gemerkt() || o_angezeigter_Film.iso_favorit())) {
			o_angezeigter_Film.set_o_gesehen(!(o_angezeigter_Film.iso_gesehen()));
			button_an_status_anpassen(o_angezeigter_Film.iso_gesehen(), 'g');
			Main.speichern();
		} else {
			Main.fehler_ausgeben("HINWEIS;Der Film " + o_angezeigter_Film.get_o_titel()
					+ " wird zur zeit nicht verwaltet.\nEr ist in keiner Liste;");
		}
	}

	// [Merken - Button]
	@FXML
	private void klicken_Button_M(ActionEvent event) {
		if (o_angezeigter_Film != null) {
			o_angezeigter_Film.set_o_gemerkt(!o_angezeigter_Film.iso_gemerkt());
			button_an_status_anpassen(o_angezeigter_Film.iso_gemerkt(), 'm');
			Main.speichern();
		}
	}

	// [Favorit - Button]
	@FXML
	private void klicken_Button_F(ActionEvent event) {
		if (o_angezeigter_Film != null) {
			o_angezeigter_Film.set_o_favorit(!o_angezeigter_Film.iso_favorit());
			button_an_status_anpassen(o_angezeigter_Film.iso_favorit(), 'f');
			Main.speichern();
		}
	}

	// [Notiz - Button]
	@FXML
	private void klicken_Button_B(ActionEvent event) {
	// Prüfen ob Film in der Merkliste steht
		if (o_angezeigter_Film != null) {
			if (o_angezeigter_Film.iso_gemerkt()) {
				String eintrag = "hinzufügen";
				if (!o_angezeigter_Film.get_o_bemerkung().equals("false")) {
					eintrag = o_angezeigter_Film.get_o_bemerkung();
				}
				TextInputDialog dialog = new TextInputDialog(eintrag);
				dialog.setTitle("eigene Bemerkungen");
				dialog.setHeaderText(null);
				dialog.setContentText("eigene Bemerkung:");

				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					o_angezeigter_Film.set_o_bemerkung(result.get());
				}
				Main.speichern();
			} else {
				Main.fehler_ausgeben("HINWEIS;Film ist nicht in der Merkliste");
			}
		}
	}

	/* die Such-Button werden bei der Live-Suche überflüssig */
	// [Suchen-Button @ Datenbank]
	@FXML
	private void bt_suche_DB_geklickt(ActionEvent event) {
		String suchetext = tf_suche_datenbank.getText();
		if (suchetext.length() > 1) {
			hilfe_beim_suchen(suchetext);
		}
	}

	// [Suchen-Button @ Merkliste]
	@FXML
	private void bt_suche_M_geklickt(ActionEvent event) {
		Film[] filmliste = Suchen.film_in_eigener_Liste_suchen(tf_suche_m.getText(), 'm');
		filme_in_view_einfügen(filmliste, lv_merken, ol_merkliste);
	}

	// [Suchen-Button @ Favoriten]
	@FXML
	private void bt_suche_F_geklickt(ActionEvent event) {
		Film[] filmliste = Suchen.film_in_eigener_Liste_suchen(tf_suche_f.getText(), 'f');
		filme_in_view_einfügen(filmliste, lv_favoriten, ol_favoritenliste);
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Tippen in Textfeldern													  *
	 * 																  			  *
	 ******************************************************************************/
	// Textfeld_suchen_DB
	@FXML
	private void tf_suche_DB_veraendert(KeyEvent event) {
		String suche = tf_suche_datenbank.getText();
		if (suche.length() > 1) {
			hilfe_beim_suchen(suche);
		}
	}

	// Textfeld_suchen_M
	@FXML
	private void tf_suche_M_veraendert(KeyEvent event) {
		Film[] filmliste = Suchen.film_in_eigener_Liste_suchen(tf_suche_m.getText(), 'm');
		filme_in_view_einfügen(filmliste, lv_merken, ol_merkliste);
	}

	// Textfeld_suchen_F
	@FXML
	private void tf_suche_F_veraendert(KeyEvent event) {
		Film[] filmliste = Suchen.film_in_eigener_Liste_suchen(tf_suche_f.getText(), 'f');
		filme_in_view_einfügen(filmliste, lv_favoriten, ol_favoritenliste);
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Klicken auf Listen														  *
	 * 																			  *
	 ******************************************************************************/
	private void liste_DB_geklickt() {
		film_aus_liste_geklickt(lv_datenbank, 'd');
	}

	private void liste_M_geklickt() {
		film_aus_liste_geklickt(lv_merken, 'm');
	}

	private void liste_F_geklickt() {
		film_aus_liste_geklickt(lv_favoriten, 'f');
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Detailansicht - Film anzeigen											  *
	 * 																			  *
	 ******************************************************************************/
	private void film_in_detailansicht_anzeigen(Film gefunden) {
		if (gefunden != null) {
			String url_des_bildes;
			Image poster;
			
			o_angezeigter_Film = gefunden;
			lb_titel_A.setText(o_angezeigter_Film.get_o_titel());
			lb_erscheinungsjahr_A.setText(o_angezeigter_Film.get_o_erschienen_am());
			lb_laufzeit_A.setText("" + o_angezeigter_Film.get_o_laufzeit());
			lb_genre_A.setText(o_angezeigter_Film.get_o_genre());
			lb_regisseur_A.setText(o_angezeigter_Film.get_o_regisseur());
	// Schauspieler-String zu einer Liste umbrechen
			ta_schauspieler_A.setText(o_angezeigter_Film.get_o_schauspieler().replaceAll(", ", "\n"));
			ta_kurzbeschreibung_A.setText(o_angezeigter_Film.get_o_kurzbeschreibung());
			lb_awards_A.setText(o_angezeigter_Film.get_o_auszeichnungen());
	// leeres Filmplakat verhindern
			try {
				BufferedInputStream url_check = Suchen.verbindung_pruefen(o_angezeigter_Film.get_o_adresse_vom_plakat());
				poster = new Image(o_angezeigter_Film.get_o_adresse_vom_plakat());
			} catch (Exception e) {
				if (o_angezeigter_Film.get_o_adresse_vom_plakat().startsWith("bilder")) {
					url_des_bildes = o_angezeigter_Film.get_o_adresse_vom_plakat();
				} else {
					url_des_bildes = "bilder/error_poster.jpg";
				}
				poster = new Image(url_des_bildes);
			}
			iv_plakat.setImage(poster);
			lb_bewertung_A.setText("" + o_angezeigter_Film.get_o_imdb_bewertung());
	// Buttonbilder setzen
			button_an_status_anpassen(o_angezeigter_Film.iso_gesehen(), 'g');
			bt_gesehen.setGraphic(Vauge);
			button_an_status_anpassen(o_angezeigter_Film.iso_gemerkt(), 'm');
			bt_merken.setGraphic(Vstern);
			button_an_status_anpassen(o_angezeigter_Film.iso_favorit(), 'f');
			bt_favorit.setGraphic(Vherz);
			if (!o_angezeigter_Film.get_o_bemerkung().equals("false"))
				Vnotiz.setImage(notiz_bunt);
			else
				Vnotiz.setImage(notiz_schwarz_weiss);
			bt_notiz.setGraphic(Vnotiz);
	// Elemente anzeigen
			ap_detail.setVisible(true);
		}
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Hilfsmethoden															  *
	 * 																			  *
	 ******************************************************************************/
	/* nach der Suche bzw. durch Ändern der ListView wird die Methode aufgerufen
	 * dabei können die Film-Arrays nicht vollständig gefüllt sein
	 * 
	 * die Listenausgabe erfolgt geformt mittels der toString-Methode und sortiert
	 * durch die comparTo-Methode der Film-Klasse
	 */
	private static void filme_in_view_einfügen(Film[] liste, ListView<Film> lv, ObservableList<Film> ol) {
		ol.clear();
		if (liste.length != 0) {
			for (Film film : liste) {
				if (film != null) {
				// nutzt die toString-Methoden von Film
					ol.add(film);
				}
			}
		} else {
			Main.fehler_ausgeben("STATUS;kein Film gefunden;");
			ol.clear();
		}
		Collections.sort(ol, (Film f1, Film f2) -> f1.compareTo(f2));
		lv.setItems(ol);
	}

	/* nach dem Klicken auf die Liste (db) werden die restlichen Informationen zum
	 * Film aus der Datenbank abgefragt
	 */
	private void film_aus_liste_geklickt(ListView<Film> lv, char listentyp) {
		Film ausgewaehlter_film = lv.getSelectionModel().getSelectedItem();
		if (ausgewaehlter_film != null) {
			hilfe_beim_Anzeigen(ausgewaehlter_film.get_o_imdb_id(), listentyp);
			
			//film_in_detailansicht_anzeigen(ausgewaehlter_film);
		}
	}

	/* Aufruf nach dem Klicken der Statusbuttons sowie beim Anzeigen eines
	 * Films in der Detailansicht
	 */
	private void button_an_status_anpassen(boolean status, char button) {
		switch (button) {
		case 'g':
			if (status) {
				Vauge.setImage(auge_bunt);
				tt_gesehen.setText("Film gesehen");
			} else {
				Vauge.setImage(auge_schwarz_weiss);
				tt_gesehen.setText("Film noch nicht gesehen");
			}
			break;

		case 'm':
			if (status) {
				Vstern.setImage(stern_bunt);
				tt_favorit.setText("Film aus Favoritenliste entfernen");
			} else {
				Vstern.setImage(stern_schwarz_weiss);
				tt_favorit.setText("Film zu Favoriten hinzufügen");
			}
			break;

		case 'f':
			if (status) {
				Vherz.setImage(herz_bunt);
				tt_merken.setText("Film aus Merkliste entfernen");
			} else {
				Vherz.setImage(herz_schwarz_weiss);
				tt_merken.setText("Film zur Merkliste hinzufügen");
			}
			break;
		}

	}
	/* nutzen eines Services für die Live-suche und Anzeige
	 * Nach dem Ausgelösen werden aktuelle Handlungen unterbrochen
	 * und neu erzeugt
	 */
	private void hilfe_beim_suchen(String suchtext) {
		this.o_suchhilfe.cancel();
		this.o_suchhilfe.reset();
		this.o_suchhilfe.setSuchtext(suchtext);
		this.o_suchhilfe.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				Film[] filme_aus_DB;
				filme_aus_DB = (Film[]) event.getSource().getValue();
				if (filme_aus_DB != null) {
					filme_in_view_einfügen(filme_aus_DB, lv_datenbank, ol_datenbank_liste);
				} else {
					Main.fehler_ausgeben("HINWEIS;Keine Filme in der OMDB zu " + suchtext + " gefunden;");
				}
			}
		});
		this.o_suchhilfe.start();
	}
	
	private void hilfe_beim_Anzeigen(String film_id, char listentyp) {
		this.o_anzeigehilfe.cancel();
		this.o_anzeigehilfe.reset();
		this.o_anzeigehilfe.set_film_id(film_id);
		this.o_anzeigehilfe.set_listentyp(listentyp);
		this.o_anzeigehilfe.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				Film anzuzeigender_film = (Film) event.getSource().getValue();
				film_in_detailansicht_anzeigen(anzuzeigender_film);
			}
		});
		this.o_anzeigehilfe.start();
	}

}