package Board;

import tarik.board.control.TWUsbBoard;
import tarik.board.control.TWUsbException;
import tarik.board.control.USBBoard;

public class sender
{

	public static void sendMessage(String text)
	{
		try
		{
			TWUsbBoard TWUsb = USBBoard.getBoard( );
			TWUsb.OpenDevice(TWUsbBoard.ADDRESSE_0);
			TWUsb.ClearAllDigital( );

			for (char c : text.toCharArray( ))
			{
				senden(c, TWUsb);
			}
			senden('\0', TWUsb);
			TWUsb.ClearAllDigital( );
		}
		catch (TWUsbException | InterruptedException e)
		{

		}
	}

	private static final int timingMil = 10;
	private static final int timingNano = 0;
	private static final boolean debug = false;

	public static void senden(char c, TWUsbBoard TWUsb) throws InterruptedException
	{
		// Sende DTR
		TWUsb.WriteAllDigital(0b0000_0010);

		// Warte auf DSR
		while ((TWUsb.ReadAllDigital( ) & 0b0010) == 0)
		{
			// warte
		}

		// Sende RTS
		TWUsb.WriteAllDigital(0b0000_0110);

		// Warte auf CTS
		while ((TWUsb.ReadAllDigital( ) & 0b0100) == 0)
		{
			// warte
		}

		// Setze nur DTR auf 1 => RTS und TXD sind 0
		TWUsb.WriteAllDigital(0b0000_0010);

		// Warte für ein bisschen, damit sich der Empfänger vorbereiten kann
		Thread.sleep(timingMil);
		if (debug)
			System.out.println("Sende Zeichen: " + c);

		for (int i = 0; i < 16; i++, c >>>= 1)
		{

			TWUsb.WriteAllDigital((c & 0b1 | 0b0010) & 0b011);
			Thread.sleep(timingMil);
			TWUsb.WriteAllDigital((c & 0b1) | 0b110);
			Thread.sleep(timingMil);
		}
		TWUsb.ClearAllDigital();
	}

}
