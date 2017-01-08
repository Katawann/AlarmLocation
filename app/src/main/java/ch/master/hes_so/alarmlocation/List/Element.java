package ch.master.hes_so.alarmlocation.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Element implements Parcelable{

    private int id;
    private String elementName;
    private boolean isEnabled;
    private String description;
    private String address;
    private int radius;
    private LatLng latLng;
    private boolean restaurant, supermarket, foodStore, hairdresser, bar, cafe;
    private int type;

    public Element(int _id, String _elementName, boolean _isEnabled, String _description, String _address, int _radius, LatLng _latLng, int _type){

        this.id = _id;
        this.elementName = _elementName;
        this.isEnabled = _isEnabled;
        this.description = _description;
        this.address = _address;
        this.radius = _radius;
        this.latLng = _latLng;
        this.restaurant = false;
        this.supermarket = false;
        this.foodStore = false;
        this.hairdresser = false;
        this.bar = false;
        this.cafe = false;
        this.type = _type;
    }

    public Element(String _elementName, boolean _isEnabled, String _description, String _address, int _radius, LatLng _latLng, int _type){

        this.id = -1;
        this.elementName = _elementName;
        this.isEnabled = _isEnabled;
        this.description = _description;
        this.address = _address;
        this.radius = _radius;
        this.latLng = _latLng;
        this.restaurant = false;
        this.supermarket = false;
        this.foodStore = false;
        this.hairdresser = false;
        this.bar = false;
        this.cafe = false;
        this.type = _type;
    }

    public Element(int _id, String _elementName, boolean _isEnabled, String _description, String _address, int _radius, boolean _restaurant, boolean _supermarket, boolean _foodStore, boolean _hairdresser, boolean _bar, boolean _cafe, int _type){

        this.id = _id;
        this.elementName = _elementName;
        this.isEnabled = _isEnabled;
        this.description = _description;
        this.address = _address;
        this.radius = _radius;
        this.latLng = new LatLng(0,0);
        this.restaurant = _restaurant;
        this.supermarket = _supermarket;
        this.foodStore = _foodStore;
        this.hairdresser = _hairdresser;
        this.bar = _bar;
        this.cafe = _cafe;
        this.type = _type;
    }

    public Element(String _elementName, boolean _isEnabled, String _description, String _address, int _radius, boolean _restaurant, boolean _supermarket, boolean _foodStore, boolean _hairdresser, boolean _bar, boolean _cafe, int _type){

        this.id = -1;
        this.elementName = _elementName;
        this.isEnabled = _isEnabled;
        this.description = _description;
        this.address = _address;
        this.radius = _radius;
        this.latLng = new LatLng(0,0);
        this.restaurant = _restaurant;
        this.supermarket = _supermarket;
        this.foodStore = _foodStore;
        this.hairdresser = _hairdresser;
        this.bar = _bar;
        this.cafe = _cafe;
        this.type = _type;
    }

    protected Element(Parcel in) {
        id = in.readInt();
        elementName = in.readString();
        isEnabled = in.readByte() != 0;
        description = in.readString();
        address = in.readString();
        radius = in.readInt();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        restaurant = in.readByte() != 0;
        supermarket = in.readByte() != 0;
        foodStore = in.readByte() != 0;
        hairdresser = in.readByte() != 0;
        bar = in.readByte() != 0;
        cafe = in.readByte() != 0;
        type = in.readInt();
    }

    public static final Creator<Element> CREATOR = new Creator<Element>() {
        @Override
        public Element createFromParcel(Parcel in) {
            return new Element(in);
        }

        @Override
        public Element[] newArray(int size) {
            return new Element[size];
        }
    };

    public int getId() { return id;}

    public String getElementName() {
        return elementName;
    }
    public void setElementName(String _elementName) {
        this.elementName = _elementName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String _description) {
        this.description = _description;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String _address) {
        this.address = _address;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
    public void setEnable(boolean _isEnabled) { this.isEnabled = _isEnabled; }

    public int getRadius() { return radius; }
    public void setRadius(int _radius){ this.radius = _radius; }

    public LatLng getLatLng() {
        return latLng;
    }
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public boolean getBar() {
        return bar;
    }

    public void setBar(boolean bar) {
        this.bar = bar;
    }

    public boolean getCafe() {
        return cafe;
    }

    public boolean getFoodStore() {
        return foodStore;
    }

    public boolean getHairdresser() {
        return hairdresser;
    }

    public boolean getRestaurant() {
        return restaurant;
    }

    public boolean getSupermarket() {
        return supermarket;
    }

    public void setCafe(boolean cafe) {
        this.cafe = cafe;
    }

    public void setFoodStore(boolean foodStore) {
        this.foodStore = foodStore;
    }

    public void setHairdresser(boolean hairdresser) {
        this.hairdresser = hairdresser;
    }

    public void setRestaurant(boolean restaurant) {
        this.restaurant = restaurant;
    }

    public void setSupermarket(boolean supermarket) {
        this.supermarket = supermarket;
    }

    public int getType(){
        return this.type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(elementName);
        parcel.writeByte((byte) (isEnabled ? 1 : 0));
        parcel.writeString(description);
        parcel.writeString(address);
        parcel.writeInt(radius);
        parcel.writeParcelable(latLng, i);
        parcel.writeByte((byte) (restaurant ? 1 : 0));
        parcel.writeByte((byte) (supermarket ? 1 : 0));
        parcel.writeByte((byte) (foodStore ? 1 : 0));
        parcel.writeByte((byte) (hairdresser ? 1 : 0));
        parcel.writeByte((byte) (bar ? 1 : 0));
        parcel.writeByte((byte) (cafe ? 1 : 0));
        parcel.writeInt(type);
    }
}
