package com.example.longdungeon.character;

import java.util.ArrayList;

import com.example.longdungeon.item.Item;

public class Mob extends Person {

	// the array list holds items quality based on normal mobs or boss.
	protected ArrayList<Item> loots;
	
	protected char type; //Normal mob or boss.

	public Mob() {
		// TODO Auto-generated constructor stub
		super();
		loots = new ArrayList<Item>();
		type = 'n'; // n for normal mob, b for boss.
		defaultStats();
	}

	public Mob(String nameNew) {
		super(nameNew);
		loots = new ArrayList<Item>();
		type = 'n';
		defaultStats();
	}
	
	private void defaultStats(){
		maxHp = 76;
		curHp = maxHp;		
		maxStm = 60;
		curStm = maxStm;
		damage = 14;
		def = 20;
		gold = 150;
		XP = 100;
	}
	
	public ArrayList<Item> getLoots() {
		return loots;
	}

	public void setLoots(ArrayList<Item> loots) {
		this.loots = loots;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}
	
	public int getMaxStm() {
		return maxStm;
	}

	public void setMaxStm(int maxStm) {
		this.maxStm = maxStm;
	}

	public int getCurStm() {
		return curStm;
	}

	public void setCurStm(int curStm) {
		this.curStm = curStm;
	}

}
