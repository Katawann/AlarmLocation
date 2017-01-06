package ch.master.hes_so.alarmlocation.List;

import android.graphics.Bitmap;
import android.provider.Settings;

import ch.master.hes_so.alarmlocation.Globals;

abstract public class Element {

    private static int idGlobal = 0;
    private int id;
    private String elementName;
    private boolean isEnabled;
    private String description;
    private String address;
    private int radius;

    public Element(String _elementName, boolean _isEnabled, String _description, String _address, int _radius){

        this.id = idGlobal;
        idGlobal++;
        this.elementName = _elementName;
        this.isEnabled = _isEnabled;
        this.description = _description;
        this.address = _address;
        this.radius = _radius;
    }

    public int getId() { return id;}
    public void setId(int _id) { this.id = _id; }

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

    abstract public int getType();
    abstract public String getTypeName();
}
