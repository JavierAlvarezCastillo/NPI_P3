
package sandra.examples.asr.puntomovimientosonido;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity implements SensorEventListener {


	private SensorManager sm;
	private float a,b;
	private float[] gravity = new float[3];
	MediaPlayer mediaPlayer;
	private float[] linear_acceleration = new float[3];
	public MainActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);

		//Inicializamos los sensores para que detecte que es acelerometro
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		if(sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size()!=0){
			Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			sm.registerListener(this,s, SensorManager.SENSOR_DELAY_NORMAL);
		}

		//Cargamos el sonido en un mediaPlayer
		int sonidoResource = getResources().getIdentifier("raw/hammer", null, getPackageName());
		mediaPlayer = MediaPlayer.create(this.getApplicationContext(), sonidoResource);

		setContentView(R.layout.activity_main);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;

		final float alpha = (float) 0.8;

		// Isolate the force of gravity with the low-pass filter.

		gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
		gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

		// Remove the gravity contribution with the high-pass filter.
		linear_acceleration[0] = event.values[0] - gravity[0];
		linear_acceleration[1] = event.values[1] - gravity[1];
		linear_acceleration[2] = event.values[2] - gravity[2];

		//Utilizamos la aceleracion en el eje Z como valor, comparando la anterior con la actual
		b=a;
		a = linear_acceleration[2];
		if(comprobarMovimiento(a,b)) {
			mediaPlayer.start();
		}

	}

	/*Se comprueba que la diferencia de valores esta dentro de unos limites
    para detectar movimientos rapidos en el eje Z.
     */
	public boolean comprobarMovimiento(float a, float b)
	{
		if(Math.abs(a-b) > 12)
			return true;
		else
			return false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}

