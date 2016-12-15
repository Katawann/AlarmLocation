package ch.master.hes_so.alarmlocation.List;

import android.graphics.Bitmap;

/**
 * Created by Pascal Bruegger on 08/01/16.
 */
public class Element {

    private String elementName;
    private boolean isEnabled;

    public Element(String _elementName, boolean _isEnabled){

        this.elementName = _elementName;
        this.isEnabled = _isEnabled;
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
}
