package Board;

import tarik.board.control.TWUsbBoard;
import tarik.board.control.TWUsbException;
import tarik.board.control.USBBoard;

/** * @version 1.0 vom 21.02.2014 * @author jHunter */

public class klasseA {
	public klasseA() {
	}

	public void methode(int wert, boolean sta) throws TWUsbException {
		TWUsbBoard TWUsb = USBBoard.getBoard();
		try {
			// Wenn sta gleich true
			if (sta == true) {
				// 2 ist Binär 10, zusammen mit dem ODER leuchten alle Stellen mit dem Bit 1, die zweite Stelle 
				// von rechts leuchtet durch das ORDER immer
				wert = wert | 2;
				// Diese Bits werden dann auf die LEDs geschrieben				
				TWUsb.WriteAllDigital(wert);
			}
			// Wenn sta false
			if (sta == false) {
				// Dann wird dasselbe mit einem UND gemacht, das bedeutet es kann nur die zweite LED von rechts leuchten
				wert = wert & 2;
				// Diese Bits werden auf die LED geschrieben
				TWUsb.WriteAllDigital(wert);
			}
		} catch (Exception e) {
			// Gibt den Stack Trace (Aufruflog) und den Fehler aus
			e.printStackTrace();
		}
	}
}
