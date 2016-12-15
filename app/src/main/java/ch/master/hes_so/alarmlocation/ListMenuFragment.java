package ch.master.hes_so.alarmlocation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ch.master.hes_so.alarmlocation.List.Element;
import ch.master.hes_so.alarmlocation.List.ElementAdapter;

/**
 * Created by quent on 03/11/2016.
 */

public class ListMenuFragment extends Fragment {

    private ArrayList<Element> itemsOfElement = new ArrayList<Element>();

    public ListMenuFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        itemsOfElement.add(new Element("Cupcake", true));
        itemsOfElement.add(new Element("Donut", false));
        itemsOfElement.add(new Element("Eclair", true));
        itemsOfElement.add(new Element("Kit kat", false));
        itemsOfElement.add(new Element("Lolipop", false));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menulist_layout, container, false);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        //Lie l'adapteur à notre liste en appliquant la cellule customisée
        ElementAdapter adapter = new ElementAdapter(getContext(), R.layout.button_format, itemsOfElement);
        ListView customListView = (ListView) getView().findViewById(R.id.lst_Element);
        customListView.setAdapter(adapter);

        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Element selectedItem = (Element) adapter.getItemAtPosition(position);
                Log.v("CustomAdapterExemple", "Element selectionne : " + selectedItem.getElementName());
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
}
