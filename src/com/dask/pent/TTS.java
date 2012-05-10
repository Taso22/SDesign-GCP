package com.dask.pent;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;

public class TTS {
	public TextToSpeech tts;

	/**
	 * @param context Passed from main activity. Context in which the
	 * text to speech engine will be used.
	 */
	public TTS(Context context){
		tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

			public void onInit(int status) {
				// unimplemented
			}
		});
		tts.setLanguage(Locale.UK);
	}

	/**
	 * @param text String to be spoken. Method is a wrapper for the
	 * TextToSpeech speak method. 
	 */
	public void speak(String text){
//		while(tts.isSpeaking()){
//			tts.stop();
////			try {
////				Thread.sleep(500);
////			} catch (InterruptedException e) {
////				e.printStackTrace();
////			}
//		}
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
}
