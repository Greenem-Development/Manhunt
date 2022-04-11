package org.greenem.plugins.manhunt.classes;

public class PairStringDouble {
	private String s;
	private Double d;
	
	public PairStringDouble() {}
	
	public PairStringDouble(String s, double d) {
		this.s = s;
		this.d = d;
	}
	
	public String getString() {
		return s;
	}
	
	public double getDouble() {
		return d;
	}
	
	public void setString(String s) {
		this.s = s;
	}
	
	public void setDouble(double s) {
		this.d = d;
	}
}
