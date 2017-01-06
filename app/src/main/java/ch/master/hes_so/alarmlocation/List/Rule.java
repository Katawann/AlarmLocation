package ch.master.hes_so.alarmlocation.List;

import ch.master.hes_so.alarmlocation.Globals;

/**
 * Created by quent on 02/01/2017.
 */

public class Rule extends Element {

    public Rule(String _elementName, boolean _isEnabled, String _description, String _address, int _radius) {
        super(_elementName, _isEnabled, _description, _address, _radius);
    }

    @Override
    public int getType() {
        return Globals.TYPE_RULE;
    }

    @Override
    public String getTypeName() {
        return "Rule";
    }


}
