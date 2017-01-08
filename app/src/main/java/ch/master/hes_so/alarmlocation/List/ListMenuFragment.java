package ch.master.hes_so.alarmlocation.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import ch.master.hes_so.alarmlocation.Globals;
import ch.master.hes_so.alarmlocation.R;

/**
 * Created by quent on 03/11/2016.
 */
public class ListMenuFragment extends Fragment implements ElementAdapter.OnSwitchStateChangedCallback{

    private ArrayList<Element> itemsOfElement = new ArrayList<Element>();
    private ElementAdapter adapter;
    OnListMenuFragmentListener mCallback;

    @Override
    public void switchState(int position, boolean state) {
        mCallback.OnInteractionListMenu(Globals.UPDATE_ELEMENT,itemsOfElement.get(position).getId(),state);
    }


    public interface OnListMenuFragmentListener{
        void OnInteractionListMenu(int fragmentCaller, int id, boolean state);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnListMenuFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnInteractionListMenu"); }
    }

    public void updateList(ArrayList<Element> _list){
        this.itemsOfElement = _list;

        if(adapter!= null){
            adapter.clear();

            for(int i=0; i<itemsOfElement.size(); i++){
                adapter.add(itemsOfElement.get(i));
            }

            adapter.notifyDataSetChanged();
        }
    }

    public ListMenuFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menulist_fragment, container, false);

        ListView customListView = (ListView) view.findViewById(R.id.lst_Elements);
        adapter = new ElementAdapter(view.getContext(), itemsOfElement);
        adapter.setCallback(this);
        customListView.setAdapter(adapter);

        customListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (itemsOfElement.get(position).getType() == Globals.TYPE_POSITION){
                    mCallback.OnInteractionListMenu(Globals.OPEN_POSITION, itemsOfElement.get(position).getId(),false);
                }else if (itemsOfElement.get(position).getType() == Globals.TYPE_RULE){
                    mCallback.OnInteractionListMenu(Globals.OPEN_RULE, itemsOfElement.get(position).getId(),false);
                }
            }
        });

        /**
         *  Boutton "+". Permet d'ajouter des nouvelles positions ou règles de déclenchement de l'alarme
         */
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_element();
            }
        });

        customListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Get task list from database
                final ArrayList<Element> listElements;
                final Element selectedElement = (Element) adapterView.getItemAtPosition(position);
                final int index = position;

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                mCallback.OnInteractionListMenu(Globals.DELETE_ELEMENT,adapter.getItem(index).getId(),false);
                                dialog.dismiss();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getString(R.string.are_you_sure) + " " + selectedElement.getElementName() + " ?")
                        .setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();

                return true;
            }
        });
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void add_element(){
        // Get task list from database
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        //builderSingle.setIcon(R.drawable.ic_menu_camera);
        builderSingle.setTitle(R.string.add_rules);

        final String[] array_choice;
        array_choice = getResources().getStringArray(R.array.add_choice);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_item, array_choice);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0){

                    mCallback.OnInteractionListMenu(Globals.ADD_POSITION,-1,false);
                    dialog.dismiss();

                }else if(which == 1){

                    mCallback.OnInteractionListMenu(Globals.ADD_RULE,-1,false);
                    dialog.dismiss();
                }
            }
        });
        builderSingle.show();
    }
}
