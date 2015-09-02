package application;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Handler_Anzeige extends Service {
	private String film_id;
	private char listentyp;
	
	/******************************************************************************
	 * [METHODEN]																  *
	 * getter & setter															  *
	 * 																			  *
	 ******************************************************************************/
	public void set_film_id(String id) {
		film_id = id;
	}

	public void set_listentyp(char listenart) {
		listentyp = listenart;
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Override																	  *
	 * 																			  *
	 ******************************************************************************/
	@Override
	protected Task createTask() {
		return new Task<Film>() {
			
			/* Behandlung aller alsgelösten Exceptions im Händler macht die Klasse
			 * Suchen unabhängiger
			 * */
			@Override
			protected Film call() {
				Film vollstaendiger_film = null;
				
				try{
					if (listentyp == 'm' || listentyp == 'f') {
						vollstaendiger_film = Suchen.filmdetails_aus_eigener_Liste_generieren(film_id);
					} else {
						vollstaendiger_film = Suchen.filmdetails_zum_objekt_generieren(film_id);
					}
				} catch (MalformedURLException url_e) {
					Main.fehler_ausgeben(url_e.getMessage());
				} catch (IOException io_e) {
					Main.fehler_ausgeben(io_e.getMessage());
				} catch (JSONException json_e) {
					Main.fehler_ausgeben(json_e.getMessage());
				}
				
				return vollstaendiger_film;
			}
		};
	}

}
