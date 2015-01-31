package com.example.longdungeon.item;

import android.os.Parcel;
import android.os.Parcelable;

public class Equipment extends Item implements Parcelable {

	// private int damage;
	// private int defend;
	// private int mana;
//	private boolean equipped;
	private byte equipped;

	public Equipment() {
		super();
	}

	public Equipment(String name, int itemType) {
		super(name, itemType);
		switch (itemType) {
		case ITEM_SWORD:
			statNumber = 15;
			break;
		case ITEM_RING:
			statNumber = 60;
			break;
		default:
			statNumber = 10;
			break;
		}
		cost = statNumber;
	}

	// public void setStatNumber(int statNumber) {
	// this.statNumber = statNumber;
	// cost = statNumber;
	// }

	// Depend on what item is, getStat will get damage, defend, or stamina.
	// public int getStatNumber() {
	// switch (itemType) {
	// case ITEM_SWORD:
	// return damage;
	// case ITEM_RING:
	// return mana;
	// default:
	// return defend;
	// }
	// }

	/**
	 * Get value 1 if the item is equipped, otherwise 0.
	 * @return 1 if the item is equipped, otherwise 0.
	 */
	public byte isEquipped() {
		return equipped;
	}

	/**
	 * Set value to an item to be equipped and non-equipped.
	 * @param equipped - 1 is equipped, otherwise 0 for non-equipped.
	 */
	public void setEquipped(byte equipped) {
		this.equipped = equipped;
	}

	public String toString() {
		return statNumber + " " + statName + " " + name
				+ (equipped != 0 ? " E" : "");
	}

	public Equipment(Parcel in) {
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
		dest.writeInt(statNumber);
		dest.writeInt(cost);
		dest.writeString(statName);
		dest.writeByte(equipped);
	}

	public void readFromParcel(Parcel in) {
		name = in.readString();
		itemType = in.readInt();
		statNumber = in.readInt();
		cost = in.readInt();
		statName = in.readString();
		equipped = in.readByte();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Equipment createFromParcel(Parcel in) {
			return new Equipment(in);
		}

		public Equipment[] newArray(int size) {
			return new Equipment[size];
		}
	};
}
