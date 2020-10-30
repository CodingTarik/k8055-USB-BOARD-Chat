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

public class TankControl2 {

	public static void main(String[] args) throws TWUsbException, InterruptedException {
		TWUsb = USBBoard.getBoard();
		TWUsb.OpenDevice(TWUsbBoard.ADDRESSE_0);
		TWUsb.ClearAllDigital();
		System.out.println("Erstelle Thread");
		Thread eventThread = new Thread(TankControl2::updateTankStatusByBoard);
		eventThread.start();
	}

	private static TWUsbBoard TWUsb;
	private static boolean tanken = false;
	// private static boolean leck = false;
	private static boolean leckUnterSiebzig = false;
	private static boolean leckUeberSiebzig = false;
	private static int tankstatus = 0b00100000;
	private static int tankInProzent = 0;
	private static int leckUnterSiebzigGrenze = (int) (Math.random() * 70d);
	private static int leckUeberSiebzigGrenze = 100 - (int) (Math.random() * 30);

	private static void starteTankProzess() {
		tankstatus = tankstatus | 0b01000000;
		tankstatus = tankstatus & 0b11011111;
		tanken = true;
	}

	private static void stoppeTankProzess() {
		tankstatus = tankstatus | 0b00100000;
		tankstatus = tankstatus & 0b10111111;
		tanken = false;
	}

	private static void simuliereLeck(boolean ueberSiebzig) {
		//if ((tankstatus & 0b00100000) > 0) {
			tankstatus = tankstatus | 0b10000000;
		//}
		if (ueberSiebzig) {
			leckUeberSiebzig = true;
		} else {
			leckUnterSiebzig = true;
		}
	}

	private static void stopfeLeck() {
		leckUeberSiebzig = false;
		leckUnterSiebzig = false;
		tankstatus = tankstatus & 0b01111111;
	}

	private static void checkBoardInput() {
		int input = TWUsb.ReadAllDigital();
		if ((input & 0b00001) > 0) {
			System.out.println("Starte Tank-Prozess");
			starteTankProzess();
		} else if ((input & 0b00010) > 0) {
			System.out.println("Stoppe Tank-Prozess");
			stoppeTankProzess();
		}

		if ((input & 0b00100) > 0) {
			System.out.println("Simuliere Leck unter 70% bei " + leckUnterSiebzigGrenze + "%");
			simuliereLeck(false);
		} else if ((input & 0b01000) > 0) {
			System.out.println("Simuliere Leck über 70% bei " + leckUeberSiebzigGrenze + "%");
			simuliereLeck(true);
		} else if ((input & 0b10000) > 0) {
			System.out.println("Stopfe Leck");
			stopfeLeck();
		}
	}

	private static void checkTankStatus(int leckGrenze) {
		if (tanken) {
			tankInProzent += 2;
			if (tankInProzent > 100) {
				tankInProzent = 100;
			}

		}

		if (leckUnterSiebzig || leckUeberSiebzig) {
			if (tankInProzent > leckGrenze) {
				tankInProzent -= 2;
				if (tankInProzent < leckGrenze) {
					tankInProzent = leckGrenze;
				}
			}
			if (tankInProzent < 0) {
				tankInProzent = 0;
			}
		}
		int leckMask = 0b00000000;
		if (tankInProzent > 5) {
			leckMask = leckMask | 0b00000001;
		}
		if (tankInProzent > 25) {
			leckMask = leckMask | 0b00000011;
		}
		if (tankInProzent > 50) {
			leckMask = leckMask | 0b00000111;
		}
		if (tankInProzent > 70) {
			leckMask = leckMask | 0b00001111;
		}
		if (tankInProzent > 95) {
			leckMask = leckMask | 0b00011111;
		}
		tankstatus = tankstatus & 0b11100000;
		tankstatus = tankstatus | leckMask;
	}

	private static void updateTankStatusByBoard() {
		System.out.println("Board aktiv...");
		try {
			boolean alarmKurz = false;
			long durchlauf = 0;
			boolean alarmLang = true;
			while (true) {
				checkBoardInput();
				checkTankStatus(leckUnterSiebzig ? leckUnterSiebzigGrenze
						: (leckUeberSiebzig ? leckUeberSiebzigGrenze : Integer.MAX_VALUE));
				int statusToWrite = tankstatus;
				// Alarm 2 (schnell)
				if ((tankstatus & 0b00100000) > 0)
					if ((tankstatus & 0b1000_0000) > 0) {
						if (alarmKurz) {
							statusToWrite = statusToWrite & 0b01111111;
						}
						alarmKurz = !alarmKurz;
					}
				// Alarm 1 (langsam)
				if ((tankstatus & 0b0100_0000) > 0) {
					if (durchlauf % 3 == 0) {
						alarmLang = !alarmLang;
					}
					if (alarmLang) {
						statusToWrite = statusToWrite & 0b10111111;
					} else {
						play();
					}
				}
				durchlauf++;
				if (durchlauf > Integer.MAX_VALUE) {
					durchlauf = 0L;
				}
				System.out.println("Tankstatus: " + Integer.toBinaryString(statusToWrite) + " das entspricht: "
						+ tankInProzent + "%");
				TWUsb.WriteAllDigital(statusToWrite);
				Thread.sleep(1000);

			}
		} catch (Exception ex) {

		}
	}

	public static void play() {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream audioIn = AudioSystem
					.getAudioInputStream(TankControl2.class.getResourceAsStream("/sounds/alarm.wav"));
			clip.open(audioIn);
			clip.start();
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
	}

	private static void haltStopEsIstObstImHaus() throws InterruptedException {
		Thread.sleep(700);
	}
}
