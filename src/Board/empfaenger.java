package Board;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import tarik.board.control.TWUsbBoard;
import tarik.board.control.TWUsbException;
import tarik.board.control.USBBoard;

public class empfaenger
{

	public static void reciveMessage( )
	{
		try
		{
			TWUsbBoard TWUsb = USBBoard.getBoard( );
			TWUsb.OpenDevice(TWUsbBoard.ADDRESSE_0);

			TWUsb.ClearAllDigital( );
			String text = "";
			while (true)
			{
				char zeichen = empfangen(TWUsb);
				if (zeichen == '\0')
				{
					break;
				}
				text += zeichen;
				if(debug)
				System.out.println("Zeichen: " + zeichen);
			}
			System.out.println( );
			if (text.contains("http") || text.contains("www."))
			{
				System.out.println("Öffne: " + text);
				java.awt.Desktop.getDesktop( ).browse(new URI(text));
			}
			else
			{
				System.out.println("Neue Nachricht: " + text);
			}
			TWUsb.ClearAllDigital( );

		}
		catch (TWUsbException | InterruptedException | IOException | URISyntaxException e)
		{}

	}

	private static final int timingMil = 10;
	private static final int timingNano = 0;
	private static final boolean debug = false;

	public static char empfangen(TWUsbBoard TWUSB) throws InterruptedException
	{

		// Warte auf DSR Signal (Betriebsbereit)
		if (debug)
			System.out.println("Warte auf DSR");
		while ((TWUSB.ReadAllDigital( ) & 0b00010) == 0)
		{
			// Warten
		}

		// Sende DTR Signal (DEE Bereit)
		if (debug)
			System.out.println("sende DTR");
		TWUSB.WriteAllDigital(0b0000_0010);

		// Warte auf CTS Signal (Sendebereitschaft)
		if (debug)
			System.out.println("Warte auf CTS");
		while ((TWUSB.ReadAllDigital( ) & 0b00100) == 0)
		{
			// Warten
		}
		// Temporäre Variablen für das abspeichern der Daten
		int binaryChar = 0;
		int bit = 0;

		// Sende RTS Signal
		if (debug)
			System.out.println("Sende RTS");
		TWUSB.WriteAllDigital(0b0000_0110);

		// Warte für ein bisschen, damit sich der Sender vorbereiten kann
		Thread.sleep(timingMil);

		for (int i = 0; i < 16; i++)
		{
			// Solange RTS = 0
			while ((TWUSB.ReadAllDigital( ) & 0b100) == 0)
			{
				// warte
			}

			// Lese RXD
			bit = TWUSB.ReadAllDigital( ) & 0b00001;
			// System.out.println("Gelesenes Bit: " + bit);
			binaryChar = binaryChar | (bit << i);

			// Solange Rts = 1
			while ((TWUSB.ReadAllDigital( ) & 0b100) != 0)
			{
				// warte
			}
		}
		// Setze Board-Output zurück
		TWUSB.ClearAllDigital( );

		// Dekodiere Zeichen
		return (char) binaryChar;
	}

}
