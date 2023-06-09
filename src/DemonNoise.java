import java.io.ByteArrayInputStream;

import javax.sound.sampled.*;


public class DemonNoise {
	
	
	public static void main(String[] args) {
		try {
			// Open the audio file
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(PlayClip.class.getResourceAsStream("res/c-note.wav"));
			
			
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
            long startPositionInBytes = (long)(44100 * 0.05); // Set clip start time
            long bytesSkipped = originalAudioStream.skip(startPositionInBytes);
            if (bytesSkipped != startPositionInBytes) {
                throw new IllegalStateException("Unable to skip to specified start position in audio stream");
            }
            
            // Create a buffer for the section of the clip to be played repeatedly
            byte[] loopBuffer = new byte[(int) (format.getFrameSize() * format.getFrameRate() * 0.3)]; // buffer size for 0.5 seconds
            originalAudioStream.read(loopBuffer); // read the section of the clip to be looped
            
            //Playback
            while (true) {
	            int bytesRead = 0;
	            int totalBytesRead = 0;
	            while (totalBytesRead < loopBuffer.length) {
	                int bytesToWrite = Math.min(loopBuffer.length - totalBytesRead, dataLine.available());
	                dataLine.write(loopBuffer, totalBytesRead, bytesToWrite);
	                totalBytesRead += bytesToWrite;
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
