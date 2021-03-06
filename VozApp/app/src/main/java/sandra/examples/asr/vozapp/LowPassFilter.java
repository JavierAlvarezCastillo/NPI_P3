package sandra.examples.asr.vozapp;

public class LowPassFilter {

	
	static final float SmoothFactorCompass = 0.2f;
	static final float SmoothThresholdCompass = 150.0f;
	static final float oldCompass = 0.0f;
	
	
    static final float ALPHA = 0.15f;
    
	
    public static float[] filter2D( float[] input, float[] output ) {
            if ( output == null ) return input;

            for ( int i=0; i<input.length; i++ ) {
                    output[i] = output[i] + ALPHA * (input[i] - output[i]);
            }
            return output;
    }
	
	public static float filter3D(float newCompass, float oldCompass){
		if (Math.abs(newCompass - oldCompass) < 180) {
		    if (Math.abs(newCompass - oldCompass) > SmoothThresholdCompass) {
		        oldCompass = newCompass;
		    }
		    else {
		        oldCompass = oldCompass + SmoothFactorCompass * (newCompass - oldCompass);
		    }
		}
		else {
		    if (360.0 - Math.abs(newCompass - oldCompass) > SmoothThresholdCompass) {
		        oldCompass = newCompass;
		    }
		    else {
		        if (oldCompass > newCompass) {
		            oldCompass = (oldCompass + SmoothFactorCompass * ((360 + newCompass - oldCompass) % 360) + 360) % 360;
		        } 
		        else {
		            oldCompass = (oldCompass - SmoothFactorCompass * ((360 - newCompass + oldCompass) % 360) + 360) % 360;
		        }
		    }
		}
		return oldCompass;
	}
}
