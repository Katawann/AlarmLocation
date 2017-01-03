package ch.master.hes_so.alarmlocation.List;

import javax.microedition.khronos.opengles.GL;

import ch.master.hes_so.alarmlocation.Globals;

/**
 * Created by quent on 02/01/2017.
 */

public class Position extends Element {
    public Position(String _elementName, boolean... _isEnabled) {
        super(_elementName, _isEnabled);
    }

    @Override
    int getType() {
        return Globals.TYPE_POSITION;
    }

    @Override
    public String getTypeName() {
        return "Position";
    }


}
