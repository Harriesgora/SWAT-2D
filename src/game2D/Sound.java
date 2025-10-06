package game2D;

import javax.sound.sampled.*;
import java.io.File;

public class Sound extends Thread {

	String filename;
	boolean finished;
	Clip clip; // Needed to stop or loop later

	public Sound(String fname) {
		filename = fname;
		finished = false;
	}

	public void run() {
		try {
			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
			Thread.sleep(100);
			while (clip.isRunning()) {
				Thread.sleep(100);
			}
			clip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finished = true;
	}

	// Looping playback
	public void loop() {
		try {
			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.loop(Clip.LOOP_CONTINUOUSLY); // loop forever
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Optional: Stop playback
	public void stopSound() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
			clip.close();
		}
	}
}
