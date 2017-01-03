package ch.master.hes_so.alarmlocation.List;

import android.graphics.Bitmap;

import ch.master.hes_so.alarmlocation.Globals;

abstract public class Element {

    private String elementName;
    private boolean isEnabled;
    private int type; // 0 = position, 1 = rule

    public Element(String _elementName, boolean... _isEnabled){

        this.elementName = _elementName;
        this.isEnabled = _isEnabled.length<=0 || _isEnabled[0];
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String _elementName) {
        this.elementName = _elementName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnable(boolean _isEnabled) { this.isEnabled = _isEnabled; }

    abstract int getType();

    abstract public String getTypeName();
}
