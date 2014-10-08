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

    //Class variables
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

        //initializing class variables
        shortcutList = new ArrayList<String>();
        searchList = new ArrayList<String>();
        toSearch = (EditText) findViewById(R.id.searchField);
        shortcut = (EditText) findViewById(R.id.shortcutField);
        ListView listView = (ListView) findViewById(R.id.resultsList);

        //calling loadPreferences to fill ArrayLists
        loadPreferences();

        //adapter for use in the ListView
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, shortcutList);


        listView.setAdapter(adapter);

        //OnClick listener calls search()
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                search(position);
            }
        });
        //OnLongClick listener calls createBuilder()
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){

                createBuilder(position);
                return true;
            }
        });






    } //End of onCreate

    public void loadPreferences(){



        SharedPreferences sharedPreference = getPreferences(MODE_PRIVATE);
        String list = ""; //Variable to hold both lists
        list = sharedPreference.getString("SearchList", ""); //fill list with SearchList
        String[] splitter = list.split("/"); //Splits the list by / into an array

        //Loop to fill searchList with Strings from list
        if (splitter.length > 0){
            for (String s: splitter)
                searchList.add(s);
        }

        list = sharedPreference.getString("ShortcutList", ""); //fill list with ShortcutList
        splitter = list.split("/"); //reapply split

        //Loop to fill shortcutList with Strings from list
        if (splitter.length > 0){
            for (String s: splitter)
                shortcutList.add(s);
        }
    } //End of loadPreferences


    public void onClick(View v){

        //Adds items to corresponding lists, updates adapter and empties textfields
        shortcutList.add(shortcut.getText().toString());
        searchList.add(toSearch.getText().toString());
        adapter.notifyDataSetChanged();
        shortcut.setText("");
        toSearch.setText("");

        //Hide the keyboard
        ((InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                shortcut.getWindowToken(), 0);

       sharedPrefs();

    }//End of onClick


    public void shareListItem(int position){

        //Code to share items from device via other apps
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setType("text/plain");
        startActivity(i);

    } //End of shareListItem

    public void deleteListItem(int position){

        //Removes selected item from lists and updates adapter
        shortcutList.remove(position);
        searchList.remove(position);
        adapter.notifyDataSetChanged();
        sharedPrefs();

    } //End of deleteListItem

    public void editListItem(int position){

        //Sets up the tag to edit back in the boxes and then removes it from the list
        toSearch.setText(searchList.get(position));
        shortcut.setText(shortcutList.get(position));
        deleteListItem(position);
    } //End of editListItem

    public void search(int position) {

        //Creates a string to search for, replacing spaces with + to fit URL
        String url = searchList.get(position);
        url.replace(" ", "+");

        //Intent to search the website
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.uta.edu/search/?q=" + url));
        startActivity(i);
    } //End of search

    public void createBuilder(int position){

        //Builder for alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);


        String title = String.format("What would you like to do with \"%s\"?", shortcutList.get(position));
        String[] choices = {"Share", "Edit", "Delete"}; //options for the listView
        alertDialogBuilder.setTitle(title); //sets title
        final int pos = position; // int to pass the held position to the delete, share and edit methods


                       alertDialogBuilder
                             .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                                 public void onClick(DialogInterface dialog, int id){

                                 }
                             });
                        //ListView to hold options
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

            //Create the Dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show(); //And show it
    } //End of createBuilder

    public void sharedPrefs(){

        //Strings to be passed to sharedPreferences
        String searchListString = "";
        String shortcutListString = "";

        //Adding items to Strings with / in between to split up later
        for(int i = 0; i < searchList.size(); i++){
            searchListString += searchList.get(i) + "/";
            shortcutListString += shortcutList.get(i) + "/";
        }

        //Putting the Strings to the sharedPreferences
        SharedPreferences sharedPreference = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("SearchList", searchListString);
        editor.putString("ShortcutList", shortcutListString);
        editor.commit();
    } //End of SharedPrefs

    public void onStop(){
        super.onStop();

        //Saving sharedPreferences when app is closed
        sharedPrefs();
    }
}
