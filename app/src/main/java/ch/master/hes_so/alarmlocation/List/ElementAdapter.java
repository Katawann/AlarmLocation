package ch.master.hes_so.alarmlocation.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
    private LayoutInflater layoutInflater;
    private Resources res;

    /*!
        context             : activité dans laquelle on crée notre classe
        textViewResourceId  : cellule dans laquelle sera insérée nos données, avec un layout défini
                              par notre "android_version_layout.xml"
        versions            : liste de nos versions android
     */
    public ElementAdapter(Context context, ArrayList<Element> _elements) {
        super(context, 0, _elements);
        this.elementsList = _elements;
        this.context = context;
        this.res = context.getResources();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_button, parent, false);
        }

        //get The tag (attribute's data) of the view to display
        ElementListViewHolder viewHolder = (ElementListViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new ElementListViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_element);
            viewHolder.enabled = (Switch) convertView.findViewById(R.id.sw_enabled);
            convertView.setTag(viewHolder); //Set the tag gotten from xml
        }

        Element element = getItem(position);
        //set the attributes for the views of the recycled view
        viewHolder.name.setText(element.getElementName());
        viewHolder.enabled.setChecked(element.isEnabled());

        return convertView;
    }
}
