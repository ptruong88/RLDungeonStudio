package com.example.longdungeon.character;

public class Person {

	protected String name;
	protected int XP;
	protected int gold;
	protected int maxHp;
	protected int curHp;
	protected int def;
	protected int damage;
	protected int maxStm;
	protected int curStm;
	
	public Person() {
		name = "Invisible";
		defaultStats();
	}

	public Person(String nameNew) {
		name = nameNew;
		defaultStats();
	}
	
	private void defaultStats() {
		// TODO Auto-generated method stub
		XP = 0;
		gold = 100;
		maxHp = 120;
		curHp = 120;
		maxStm = 0;
		curStm = maxStm;
		def = 40;
		damage = 35;
	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getXP() {
		return XP;
	}

	public void setXP(int xP) {
		XP = xP;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getCurHp() {
		return curHp;
	}

	public void setCurHp(int curHp) {
		this.curHp = curHp;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

}
