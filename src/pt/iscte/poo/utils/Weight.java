package pt.iscte.poo.utils;

public enum Weight {

	LIGHT(1), HEAVY(2);

	private int value;

	Weight(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public boolean isLighterThan(Weight other) {
		return this.value < other.value;
	}

	public boolean isHeavierThan(Weight other) {
		return this.value > other.value;
	}

}
