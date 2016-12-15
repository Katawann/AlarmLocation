package ch.master.hes_so.alarmlocation.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import ch.master.hes_so.alarmlocation.R;


/**
 * Created by quent on 05/10/2016.
 */
public class ElementAdapter extends ArrayAdapter<Element> {

    private ArrayList<Element> elementsList;
    private Context context;
    private int viewRes;
    private Resources res;

    /*!
        context             : activité dans laquelle on crée notre classe
        textViewResourceId  : cellule dans laquelle sera insérée nos données, avec un layout défini
                              par notre "android_version_layout.xml"
        versions            : liste de nos versions android
     */
    public ElementAdapter(Context context, int textViewResourceId, ArrayList<Element> _elements) {
        super(context, textViewResourceId, _elements);
        this.elementsList = _elements;
        this.context = context;
        this.viewRes = textViewResourceId;
        this.res = context.getResources();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(viewRes, parent, false);
        }

        final Element element_selected = elementsList.get(position);
        if (element_selected != null) {
            final TextView element_name = (TextView) view.findViewById(R.id.txt_element);
            final Switch element_enable = (Switch) view.findViewById(R.id.sw_enabled);
            element_name.setText(element_selected.getElementName());
            element_enable.setChecked(element_selected.isEnabled());
        }
        return view;
    }
}
