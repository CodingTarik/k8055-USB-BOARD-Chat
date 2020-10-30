package tanksystem;

import tarik.board.control.TWUsbBoard;

public class TankSystem implements TankWatch {

	private boolean wartungsmodus;
	private int tankstatus;

	@Override
	public boolean getInWartungsModus() {
		return wartungsmodus;
	}

	@Override
	public int getTankStatus() {
		return wartungsmodus ? tankstatus & 0b00 : tankstatus;
	}

	@Override
	public Tankstatus getTankStatusForTank(int tankIndex) {
		if (tankIndex > 2 || tankIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		int mask = 0b11000000 >>> (tankIndex * 2);
		int status = (getTankStatus() & mask) >>> (tankIndex * 2 + 2);
		return Tankstatus.parseInt(status);
	}

	@Override
	public void setWartungsModus(boolean wartung) {
		this.wartungsmodus = wartung;
	}

	@Override
	public boolean getAkustikSignal() {
		int mask = 0b01;
		return (getAlarmBits() & mask) != 0;
	}

	@Override
	public boolean getOptikSignal() {
		int mask = 0b10;
		return (getAlarmBits() & mask) != 0;
	}

	/*
	 * @Override public void setTankStatus(int tankIndex, Tankstatus status) {
	 * if(tankIndex > 2 || tankIndex < 0) { throw new IndexOutOfBoundsException(); }
	 * System.out.println("Change of tankstatus: " +
	 * Integer.toBinaryString(this.tankstatus)); switch(status) { case leer:
	 * this.tankstatus = this.tankstatus & (0b0011111111) >> tankIndex * 2;
	 * setAlarm(); return; case einDrittel: // TEST resetAlarm(); return; case
	 * zweiDrittel: // TEST resetAlarm(); return; case voll: this.tankstatus
	 * setAlarm(); return; } }
	 */

	private void setAlarm() {
		this.tankstatus = this.tankstatus | 0b11;
	}

	private void resetAlarm() {
		this.tankstatus = this.tankstatus & 0b11111100;
	}

	 

	private int getAlarmBits() {
		int mask = 0b11;
		if (this.getInWartungsModus()) {
			mask = 0b00;
		}
		return this.tankstatus & mask;
	}

	private boolean getAlarm() {
		if (getAlarmBits() != 0) {
			return true;
		}
		return false;
	}

	@Override
	public void setTankStatus(int tank, Tankstatus status) {
		// TODO Auto-generated method stub

	}

}
