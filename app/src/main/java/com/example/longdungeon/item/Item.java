package com.example.longdungeon.item;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

	protected String name;
	protected String description;// some story info about the item
	protected int itemType;
	protected int cost;// gold from sell item or value from buy item.
	protected String statName;
	protected int statNumber;
	protected int size;// Size for potion
	protected byte position; // position in shopping activity.
	
	public static final int ITEM_SWORD = 0;
	public static final int ITEM_HELMET = 1;
	public static final int ITEM_SHIELD = 2;
	public static final int ITEM_CLOTH = 3;
	public static final int ITEM_RING = 4;
	public static final int ITEM_HEALTH_POTION = 5;
	public static final int ITEM_MANA_POTION = 6;
	public static final int ITEM_STAMINA_POTION = 7;

	public Item() {
		// TODO Auto-generated constructor stub
		itemType = 0;
		size = 1;
	}

	public Item(String name, int itemType) {
		this.name = name;
		this.itemType = itemType;
		this.cost = 10;
		this.position = 0;
		switch (itemType) {
		case ITEM_SWORD:
			statName = "DMG";
			break;
		case ITEM_RING:
			statName = "MANA";
			break;
		case ITEM_HEALTH_POTION:
			statName = "HP";
			break;
		case ITEM_MANA_POTION:
			statName = "MANA";
			break;
		case ITEM_STAMINA_POTION:
			statName = "STM";
			break;
		default:
			statName = "DEF";
			break;
		}
		size = 1;
	}

	public String getStatName() {
		return statName;
	}

	public int getStatNumber() {
		return statNumber;
	}

	public void setStatNumber(int statNumber) {
		this.statNumber = statNumber;
		cost = statNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
		switch (itemType) {
		case ITEM_SWORD:
			statName = "DMG";
			break;
		case ITEM_MANA_POTION:
		case ITEM_RING:
			statName = "MANA";
			break;
		case ITEM_HEALTH_POTION:
			statName = "HP";
			break;
		case ITEM_STAMINA_POTION:
			statName = "STM";
			break;
		default:
			statName = "DEF";
			break;
		}
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public byte getPosition() {
		return position;
	}

	public void setPosition(byte postion) {
		this.position = postion;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		cost = statNumber*size;
	}

	
	
	public boolean equals(String e) {
		return e.contains(statNumber+"") && e.contains(name);
	}
	
	public Item(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(itemType);
		dest.writeString(statName);
	}

	public void readFromParcel(Parcel in) {
		name = in.readString();
		itemType = in.readInt();
		statName = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Item createFromParcel(Parcel in) {
			return new Item(in);
		}

		public Item[] newArray(int size) {
			return new Item[size];
		}
	};

}
