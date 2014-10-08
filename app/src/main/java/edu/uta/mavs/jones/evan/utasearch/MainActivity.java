package edu.uta.mavs.jones.evan.utasearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends Activity {

    final Context context = this;
    ArrayList<String> shortcutList;
    ArrayList<String> searchList;
    EditText toSearch;
    EditText shortcut;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        shortcutList = new ArrayList<String>();
        searchList = new ArrayList<String>();
        toSearch = (EditText) findViewById(R.id.searchField);
        shortcut = (EditText) findViewById(R.id.shortcutField);
        ListView listView = (ListView) findViewById(R.id.resultsList);

        loadPreferences();

        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, shortcutList);


        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                search(position);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){

                createBuilder(position);
                return true;
            }
        });






    }

    public void loadPreferences(){



        SharedPreferences sharedPreference = getPreferences(MODE_PRIVATE);
        String list = "";
        list = sharedPreference.getString("SearchList", "");
        String[] splitter = list.split("/");

        if (splitter.length > 0){
            for (String s: splitter)
                searchList.add(s);
        }

        list = sharedPreference.getString("ShortcutList", "");
        splitter = list.split("/");

        if (splitter.length > 0){
            for (String s: splitter)
                shortcutList.add(s);
        }
    }


    public void onClick(View v){

        shortcutList.add(shortcut.getText().toString());
        searchList.add(toSearch.getText().toString());
        adapter.notifyDataSetChanged();
        shortcut.setText("");
        toSearch.setText("");


        ((InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                shortcut.getWindowToken(), 0);

        String searchListString = "";
        String shortcutListString = "";


        for(int i = 0; i < searchList.size(); i++){
            searchListString += searchList.get(i) + "/";
            shortcutListString += shortcutList.get(i) + "/";
        }

        SharedPreferences sharedPreference = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("SearchList", searchListString);
        editor.putString("ShortcutList", shortcutListString);
        editor.commit();

    }


    public void shareListItem(int position){

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setType("text/plain");
        startActivity(i);

    }

    public void deleteListItem(int position){

        shortcutList.remove(position);
        searchList.remove(position);
        adapter.notifyDataSetChanged();

    }

    public void editListItem(int position){

        toSearch.setText(searchList.get(position));
        shortcut.setText(shortcutList.get(position));
        deleteListItem(position);
    }

    public void search(int position) {
        String url = searchList.get(position);
        url.replace(" ", "+");

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.uta.edu/search/?q=" + url));
        startActivity(i);
    }

    public void createBuilder(int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        String title = String.format("What would you like to do with \"%s\"?", shortcutList.get(position));
        String[] choices = {"Share", "Edit", "Delete"};
        alertDialogBuilder.setTitle(title);
        final int pos = position;


                       alertDialogBuilder
                             .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                                 public void onClick(DialogInterface dialog, int id){

                                 }
                             });
                        alertDialogBuilder.setItems(choices, new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){

                                    switch(which){
                                        case 0:
                                            shareListItem(pos);
                                            break;
                                        case 1:
                                            editListItem(pos);
                                            break;
                                        case 2:
                                            deleteListItem(pos);
                                            break;
                                    }
                                }
                        });


            AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }


    public void onStop(){
        super.onStop();

        String searchListString = "";
        String shortcutListString = "";


        for(int i = 0; i < searchList.size(); i++){
            searchListString += searchList.get(i) + "/";
            shortcutListString += shortcutList.get(i) + "/";
        }

        SharedPreferences sharedPreference = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("SearchList", searchListString);
        editor.putString("ShortcutList", shortcutListString);
        editor.commit();
    }
}
