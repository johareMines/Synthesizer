import java.io.ByteArrayInputStream;

import javax.sound.sampled.*;


public class Synthesizer {
	public static int makeIntEven(int input) {
		if (input % 2 == 1) {
			System.out.println("mIE " + (input - 1));
			return input - 1;
		}
		return input;
	}
	public static long makeLongEven(long input) {
		if (input % 2 == 1) {
			System.out.println("mIE " + (input - 1));
			return input - 1;
		}
		return input;
	}
	
	public static double[] fft(double[] x) {
	    int N = x.length;
	    double[] X = new double[N];

	    // base case
	    if (N == 1) {
	        X[0] = x[0];
	        return X;
	    }

	    // recursive case
	    double[] even = new double[N/2];
	    double[] odd = new double[N/2];
	    for (int i = 0; i < N/2; i++) {
	        even[i] = x[2*i];
	        odd[i] = x[2*i+1];
	    }

	    double[] Y_even = fft(even);
	    double[] Y_odd = fft(odd);

	    for (int k = 0; k < N/2; k++) {
	        double temp_re = Math.cos(-2*Math.PI*k/N)*Y_odd[k] + Math.cos(-2*Math.PI*k/N)*Y_even[k];
	        double temp_im = Math.sin(-2*Math.PI*k/N)*Y_odd[k] + Math.sin(-2*Math.PI*k/N)*Y_even[k];
	        X[k] = Y_even[k] + temp_re;
	        X[k+N/2] = Y_odd[k] - temp_im;
	    }

	    return X;
	}
	
//	// Fast Fourier Transform (FFT) algorithm implementation
//    public static double[] fft(double[] x) {
//        int N = x.length;
//
//        // Base case
//        if (N == 1) {
//            return new double[]{x[0]};
//        }
//
//        // Recursive case
//        double[] even = new double[N / 2];
//        double[] odd = new double[N / 2];
//        for (int i = 0; i < N / 2; i++) {
//            even[i] = x[2 * i];
//            odd[i] = x[2 * i + 1];
//        }
//        double[] q = fft(even);
//        double[] r = fft(odd);
//        double[] y = new double[N];
//        for (int i = 0; i < N / 2; i++) {
//            double w = 2 * i * Math.PI / N;
//            double cos = Math.cos(w);
//            double sin = Math.sin(w);
//            y[i] = q[i] + cos * r[i] + sin * r[i + N / 2];
//            y[i + N / 2] = q[i] - cos * r[i] - sin * r[i + N / 2];
//        }
//        return y;
//    }
	
	public static void main(String[] args) {
		try {
			// Open the audio file
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(PlayClip.class.getResourceAsStream("res/c-note.wav"));
			float audioInputStreamStart = (float) 0.2;
			float audioInputStreamPlaylength = (float) 0.0005;
			
			// Get the audio format
            AudioFormat format = audioInputStream.getFormat();

            // Set up the data line for playback
            SourceDataLine dataLine = AudioSystem.getSourceDataLine(format);
            dataLine.open(format);
            dataLine.start();

            int bytesInOneSecond = (int) (audioInputStream.getFrameLength() * format.getFrameSize());
            byte[] playbackBuffer = new byte[bytesInOneSecond]; // Plan on storing 5 in this queue *was /10
            audioInputStream.read(playbackBuffer);

            // Input stream holding original audio
            ByteArrayInputStream originalAudioStream = new ByteArrayInputStream(playbackBuffer);

            // Skip to the start position in the stream
            
            // Set the start position in the stream to skip to
            System.out.println(44100 * 0.0);
            long startPositionInBytes = (long)(44100 * audioInputStreamStart); // Set clip start time
            startPositionInBytes = makeLongEven(startPositionInBytes);
            System.out.println(startPositionInBytes);
            
            long bytesSkipped = originalAudioStream.skip(startPositionInBytes);
            if (bytesSkipped != startPositionInBytes) {
                throw new IllegalStateException("Unable to skip to specified start position in audio stream");
            }
            
            // Create a buffer for the section of the clip to be played repeatedly
            int oneSecondBytes = (int) (format.getFrameSize() * format.getFrameRate());
            int loopBufferSize = (int) (oneSecondBytes * audioInputStreamPlaylength);
            
            loopBufferSize = makeIntEven(loopBufferSize);
            
            System.out.println("oSB: " + oneSecondBytes + " | lBS: " + loopBufferSize);
            byte[] loopBuffer = new byte[loopBufferSize]; // buffer size for playback length
            originalAudioStream.read(loopBuffer); // read the section of the clip to be looped
            
            //Playback
            while (true) {
	            int bytesRead = 0;
	            int totalBytesRead = 0;
	            while (totalBytesRead < loopBuffer.length) {
	                int bytesToWrite = Math.min(loopBuffer.length - totalBytesRead, dataLine.available());
	                dataLine.write(loopBuffer, totalBytesRead, bytesToWrite);
	                totalBytesRead += bytesToWrite;
	                
	                //Record the data to plug into a Fourier transform, in order to display the played frequency
	                
	                // Convert the audio signal from the time domain to the frequency domain using FFT
	                double[] signal = new double[loopBuffer.length / 2];
	                for (int i = 0; i < signal.length; i++) {
	                    signal[i] = (double) (loopBuffer[i * 2] | loopBuffer[i * 2 + 1] << 8) / 32768.0;
	                }
	                double[] spectrum = fft(signal);
	                // Find the peak frequency in the spectrum
	                int peakIndex = 0;
	                for (int i = 1; i < spectrum.length / 2; i++) {
	                    if (spectrum[i] > spectrum[peakIndex]) {
	                        peakIndex = i;
	                    }
	                }
	                double frequency = (double) peakIndex * format.getFrameRate() / spectrum.length;
	                System.out.println("Peak frequency: " + frequency);
	                
	            }
            }
//            audioInputStream.close();
//	          dataLine.drain();
//	          dataLine.close();
      

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
