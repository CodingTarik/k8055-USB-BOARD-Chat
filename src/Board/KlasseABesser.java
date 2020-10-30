package Board;

import tarik.board.control.TWUsbBoard;
import tarik.board.control.TWUsbException;
import tarik.board.control.USBBoard;

public class KlasseABesser {
	public static void main(String[] args) throws TWUsbException
	{
		KlasseABesser klassenObjekt = new KlasseABesser();
		klassenObjekt.applyMaskForBoard(0b11111111, true);
	}
	/**
	 * 
	 * @param binaryValue
	 * @param applyORMask IF FALSE IT WILL APPLY AND
	 * @throws TWUsbException
	 */
	public void applyMaskForBoard(int binaryValue, boolean applyORMask) throws TWUsbException {
		TWUsbBoard TWUsb = USBBoard.getBoard();
        TWUsb.OpenDevice(TWUsbBoard.ADDRESSE_0);
        TWUsb.ClearAllDigital();
		int mask = 0b10;
		try {
			// Wenn sta gleich true
			if (applyORMask) {
				// 2 ist Binär 10, zusammen mit dem ODER leuchten alle Stellen mit dem Bit 1, die zweite Stelle 
				// von rechts leuchtet durch das ORDER immer
				binaryValue = binaryValue | mask;
				// Diese Bits werden dann auf die LEDs geschrieben	
				System.out.println("Writing values " + Integer.toBinaryString(binaryValue));
				TWUsb.WriteAllDigital(binaryValue);
			}
			// Wenn sta false
			else {
				// Dann wird dasselbe mit einem UND gemacht, das bedeutet es kann nur die zweite LED von rechts leuchten
				binaryValue = binaryValue & mask;
				// Diese Bits werden auf die LED geschrieben
				System.out.println("Writing values " + Integer.toBinaryString(binaryValue));
				TWUsb.WriteAllDigital(binaryValue);
			}
		} catch (Exception e) {
			// Gibt den Stack Trace (Aufruflog) und den Fehler aus
			e.printStackTrace();
		}
	}
}
