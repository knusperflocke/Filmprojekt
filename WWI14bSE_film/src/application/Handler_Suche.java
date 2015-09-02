package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.json.JSONException;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Handler_Suche extends Service {
	private String suchtext;
	
	/******************************************************************************
	 * [METHODEN]																  *
	 * getter & setter															  *
	 * 																			  *
	 ******************************************************************************/
	public void setSuchtext(String suchtext) {
		this.suchtext = suchtext;
	}

	/******************************************************************************
	 * [METHODEN]																  *
	 * Override																	  *
	 * 																			  *
	 ******************************************************************************/
	@Override
	protected Task createTask() {
		return new Task<Film[]>() {
			
			@Override
			protected Film[] call() {
				Film[] filme = null;
				try {
					filme = Suchen.filmliste_aus_DB_zusammenstellen_kurz(suchtext);
				} catch (UnsupportedEncodingException encoding_e){
					Main.fehler_ausgeben(encoding_e.getMessage());
				} catch (MalformedURLException url_e) {
					Main.fehler_ausgeben(url_e.getMessage());
				} catch (IOException io_e) {
					Main.fehler_ausgeben(io_e.getMessage());
				} catch (JSONException json_e) {
					Main.fehler_ausgeben(json_e.getMessage());
				}
				
		    	return filme;
			};
		};
	}
}