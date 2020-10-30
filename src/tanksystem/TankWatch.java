package tanksystem;

public interface TankWatch {
	enum Tankstatus {
		leer,
		einDrittel,
		zweiDrittel,
		voll;
		
		private static Tankstatus[] allValues = values();
		public static Tankstatus parseInt(int n) { return allValues[n]; }
	}
	public void setTankStatus(int tank, Tankstatus status);
	boolean getInWartungsModus();
	int getTankStatus();
	void setWartungsModus(boolean wartung);
	boolean getAkustikSignal();
	boolean getOptikSignal();
	Tankstatus getTankStatusForTank(int tankIndex);	 
}
