package Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatMain
{
	private static final boolean isEmpfaenger = false;

	public static void main(String[] args) throws IOException
	{
		if (isEmpfaenger)
		{
			empfaenger.reciveMessage( );
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			String msg = br.readLine( );
			if (msg.equalsIgnoreCase("__exit__"))
				break;
			sender.sendMessage(msg);
			empfaenger.reciveMessage( );
		}

	}
}
