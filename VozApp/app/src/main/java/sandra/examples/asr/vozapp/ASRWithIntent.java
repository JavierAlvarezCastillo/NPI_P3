/*
 *  Copyright 2013 Zoraida Callejas and Michael McTear
 * 
 *  This file is part of the Sandra (Speech ANDroid Apps) Toolkit, from the book:
 *  Voice Application Development for Android, Michael McTear and Zoraida Callejas, 
 *  PACKT Publishing 2013 <http://www.packtpub.com/voice-application-development-for-android/book>,
 *  <http://lsi.ugr.es/zoraida/androidspeechbook>
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>. 
 */

package sandra.examples.asr.vozapp;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * ASRWithIntent: Basic app with ASR using a RecognizerIntent
 * 
 * Simple demo in which the user speaks and the recognition results
 * are showed in a list along with their confidence values
 * 
 * The code for this app is self-contained: it uses an <code>Intent</code> 
 * for speech recognition. The rest of the apps in the book employ a special 
 * <code>ASR</code> library (<code>ASRLib</code>).
 * 
 * @author Zoraida Callejas
 * @author Michael McTear
 * @version 1.7, 01/22/14
 *
 */

public class ASRWithIntent extends Activity implements SensorEventListener {

	// Default values for the language model and maximum number of recognition results
	// They are shown in the GUI when the app starts, and they are used when the user selection is not valid
	private final static int DEFAULT_NUMBER_RESULTS = 10;
	private final static String DEFAULT_LANG_MODEL = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM; 
	

	private int numberRecoResults = DEFAULT_NUMBER_RESULTS; 
	private String languageModel = DEFAULT_LANG_MODEL; 
	
	private static final String LOGTAG = "ASRBEGIN";
	private static int ASR_CODE = 123;

	TextView tvHeading;
	private ImageView image;

	private float currentDegree =0f;
	private float gradosDeseados =0f;

	private SensorManager mSensorManager;
	private String[] entrada = new String[2];
	/**
	 * Sets up the activity initializing the GUI
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vozapp);
		entrada[0] = "lugar deseado";
		tvHeading = (TextView) findViewById(R.id.tvHeading);
		image=(ImageView) findViewById(R.id.imageView);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		//Shows in the GUI the default values for the language model and the maximum number of recognition results
		showDefaultValues(); 

		setSpeakButton();
	}
	
	/**
	 * Initializes the speech recognizer and starts listening to the user input
	 */
	private void listen()  {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify language model
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, languageModel);

		// Specify how many results to receive
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, numberRecoResults);  

		// Start listening
		startActivityForResult(intent, ASR_CODE);


    }
	
	
	/**
	 * Shows in the GUI the default values for the language model (checks radio button)
	 * and the maximum number of recognition results (shows the number in the text field)
	 */
	private void showDefaultValues() {
		//Show the default number of results in the corresponding EditText
		//((EditText) findViewById(R.id.numResults_editText)).setText(""+DEFAULT_NUMBER_RESULTS);
		
		//Show the language model
		//if(DEFAULT_LANG_MODEL.equals(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM))
		//	((RadioButton) findViewById(R.id.langModelFree_radio)).setChecked(true);
		//else
		//	((RadioButton) findViewById(R.id.langModelFree_radio)).setChecked(true);
	}
	
	/**
	 * Reads the values for the language model and the maximum number of recognition results
	 * from the GUI
	 */
	private void setRecognitionParams()  {
		numberRecoResults = 1;
		
		languageModel = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;

	}
	
	/**
	 * Sets up the listener for the button that the user
	 * must click to start talking
	 */
	@SuppressLint("DefaultLocale")
	private void setSpeakButton() {
		//Gain reference to speak button
		Button speak = (Button) findViewById(R.id.speech_btn);

		//Set up click listener
		speak.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//Speech recognition does not currently work on simulated devices,
					//it the user is attempting to run the app in a simulated device
					//they will get a Toast
					if("generic".equals(Build.BRAND.toLowerCase())){
						Toast toast = Toast.makeText(getApplicationContext(),"ASR is not supported on virtual devices", Toast.LENGTH_SHORT);
						toast.show();
						Log.d(LOGTAG, "ASR attempt on virtual device");						
					}
					else{
						setRecognitionParams(); //Read speech recognition parameters from GUI
						listen(); 				//Set up the recognizer with the parameters and start listening
						image.setImageResource(R.drawable.brujula);
					}
				}
		});
	}

	/**
	 *  Shows the formatted best of N best recognition results (N-best list) from
	 *  best to worst in the <code>ListView</code>.
	 *  For each match, it will render the recognized phrase and the confidence with
	 *  which it was recognized.
	 */
	@SuppressLint("InlinedApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ASR_CODE)  {
			if (resultCode == RESULT_OK)  {
				if(data!=null) {
					//Retrieves the N-best list and the confidences from the ASR result
					ArrayList<String> nBestList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					entrada = nBestList.get(0).split(" ");
				}
			}
			else {
				//Reports error in recognition error in log
				Log.e(LOGTAG, "Recognition was not successful");
			}
		}
	}

	/**
	 * Includes the recognition results in the list view
	 * @param nBestView list of matches
	 */
	private void setListView(ArrayList<String> nBestView){

		// Instantiates the array adapter to populate the listView
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nBestView);
		ListView listView = (ListView) findViewById(R.id.nbest_listview);
		listView.setAdapter(adapter);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// to stop the listener and save battery
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// get the angle around the z-axis rotated
		float degree = Math.round(event.values[0]);

		tvHeading.setText("Mueva el teléfono hacia el " + entrada[0].toLowerCase());

		if(entrada[0].toLowerCase().equals("norte"))
		{
			gradosDeseados = 360;
			if (Math.abs(Math.abs((int)currentDegree)-Math.abs((int)gradosDeseados)) <= Integer.parseInt(entrada[1]) || Math.abs(Math.abs((int)currentDegree)-Math.abs((int)gradosDeseados)) >= (360-Integer.parseInt(entrada[1])) ){
				tvHeading.setText("Está mirando hacia el " + entrada[0]);
			}
		}else if(entrada[0].toLowerCase().equals("sur"))
		{
			gradosDeseados = 180;
			if (Math.abs(Math.abs((int)currentDegree)-Math.abs((int)gradosDeseados)) <= Integer.parseInt(entrada[1])){
				tvHeading.setText("Está mirando hacia el " + entrada[0]);
			}
		}else if(entrada[0].toLowerCase().equals("este"))
		{
			gradosDeseados = 90;
			if (Math.abs(Math.abs((int)currentDegree)-Math.abs((int)gradosDeseados)) <= Integer.parseInt(entrada[1])){
				tvHeading.setText("Está mirando hacia el " + entrada[0]);
			}
		}else if(entrada[0].toLowerCase().equals("oeste"))
		{
			gradosDeseados = 270;
			if (Math.abs(Math.abs((int)currentDegree)-Math.abs((int)gradosDeseados)) <= Integer.parseInt(entrada[1])){
				tvHeading.setText("Está mirando hacia el " + entrada[0]);
			}
		}




		// create a rotation animation (reverse turn degree degrees)
		RotateAnimation ra = new RotateAnimation(
				currentDegree,
				-degree,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF,
				0.5f);

		// how long the animation will take place
		ra.setDuration(210);

		// set the animation after the end of the reservation status
		ra.setFillAfter(true);

		// Start the animation
		image.startAnimation(ra);
		currentDegree = -degree;

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not in use
	}
}