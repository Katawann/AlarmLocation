package ch.master.hes_so.alarmlocation.List;

import javax.microedition.khronos.opengles.GL;

import ch.master.hes_so.alarmlocation.Globals;

/**
 * Created by quent on 02/01/2017.
 */
public class Position extends Element {

    public Position(int _id, String _elementName, boolean _isEnabled, String _description, String _address, int _radius) {
        super(_id, _elementName, _isEnabled, _description, _address, _radius);
    }

    public Position(String _elementName, boolean _isEnabled, String _description, String _address, int _radius) {
        super(-1, _elementName, _isEnabled, _description, _address, _radius);
    }

    @Override
    public int getType() {
        return Globals.TYPE_POSITION;
    }

    @Override
    public String getTypeName() {
        return "Position";
    }


}
