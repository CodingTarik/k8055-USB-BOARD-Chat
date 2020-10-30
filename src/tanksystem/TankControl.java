package tanksystem;

import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import tanksystem.TankWatch.Tankstatus;
import tarik.board.control.TWUsbBoard;
import tarik.board.control.TWUsbException;
import tarik.board.control.USBBoard;

public class TankControl {
	public static void main(String[] args) throws TWUsbException, InterruptedException {
		TWUsbBoard TWUsb = USBBoard.getBoard();
		TWUsb.OpenDevice(TWUsbBoard.ADDRESSE_0);
		TWUsb.ClearAllDigital();
		System.out.println("Erstelle Thread");
		Thread eventThread = new Thread(() -> {
			while (true) {
				try {
					updateTankStatusByBoard(TWUsb);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		eventThread.start();
	}

	private static boolean wartung = false;

	private static void updateTankStatusByBoard(TWUsbBoard board) throws InterruptedException {
		boolean alarm = false;
		while (true) {
			int input = board.ReadAllDigital() << 3;
			if ((input & 0b111111100) != 0) {
				if (!wartung) {
					input = input | 0b11;
					if (alarm) {
						input = input & 0b11111101;
						//URL url = TankControl.class.getResource("/sounds/alarm.wav");
						play();
					}
					alarm = !alarm;
				}
			}
			System.out.println(Integer.toBinaryString(input));
			haltStopEsIstObstImHaus();
			board.WriteAllDigital(input);
		}
	}
	public static void play()
	{
	    try
	    {
	        Clip clip = AudioSystem.getClip();
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(TankControl.class.getResourceAsStream("/sounds/alarm.wav"));
	        clip.open(audioIn);
	        clip.start();
	    }
	    catch (Exception exc)
	    {
	        exc.printStackTrace(System.out);
	    }
	}
	private static void haltStopEsIstObstImHaus() throws InterruptedException {
		Thread.sleep(700);
	}
}
