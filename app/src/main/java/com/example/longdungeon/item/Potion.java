package com.example.longdungeon.item;

import android.os.Parcel;
import android.os.Parcelable;

public class Potion extends Item implements Parcelable {

	// private int plusHP;// how many hp the potion restores
	// private int plusSTM;// how much stamina the potion restores
	// private int plusMGK;// how much magic the potion restores

	public final int size_of_stack = 5;

	public Potion() {
		super();
	}

	public Potion(String name, int itemType) {
		super(name, itemType);
		// this.potionType = potionType;
		switch (itemType) {
		case ITEM_HEALTH_POTION:
			statNumber = 10;
			break;
		case ITEM_STAMINA_POTION:
			statNumber = 10;
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

	// Depend on what kind of potion is, getStatPotion returns
	// heal, mana, or stamina.
	// public int getStatNumber() {
	// switch (itemType) {
	// case ITEM_HEALTH_POTION:
	// return plusHP;
	// case ITEM_MANA_POTION:
	// return plusMGK;
	// default:
	// return plusSTM;
	// }
	// }

	public String toString() {
		return statNumber + " " + statName + " " + name + " x" + size;
	}

	public boolean equals(String e) {
		return e.contains(statNumber + "") && e.contains(statName)
				&& e.contains(name);
	}

	// public String getPotionType() {
	// return potionType;
	// }
	//
	// public void setPotionType(String potionType) {
	// this.potionType = potionType;
	// }

	public Potion(Parcel in) {
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
		dest.writeInt(size);
		dest.writeInt(statNumber);
		dest.writeInt(cost);
		dest.writeString(statName);
	}

	public void readFromParcel(Parcel in) {
		name = in.readString();
		itemType = in.readInt();
		size = in.readInt();
		statNumber = in.readInt();
		cost = in.readInt();
		statName = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Potion createFromParcel(Parcel in) {
			return new Potion(in);
		}

		public Potion[] newArray(int size) {
			return new Potion[size];
		}
	};
}
