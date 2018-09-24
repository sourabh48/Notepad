package com.example.soura.notepad;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class NoteList extends AppCompatActivity {

    static SimpleCursorAdapter adapter;
    DataBase myData;
    ListView listView;
    TextView txtid;
    int count = 0;

    ArrayList<String> idList = new ArrayList<>();

    ArrayList<String> selectedItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myData = new DataBase(this);

        myData.getWritableDatabase();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Notes.class);
                startActivity(intent);
                finish();


            }
        });


        Cursor c = myData.fetchAll();

        if (c.getCount() == 0)
            Toast.makeText(getApplicationContext(), "Please Add some Note", Toast.LENGTH_SHORT).show();

        else {

            c.moveToFirst();
            String id = c.getString(c.getColumnIndex(myData.Id));
            idList.add(id);

            while (c.moveToNext()) {
                id = c.getString(c.getColumnIndex(myData.Id));
                idList.add(id);

            }


            String[] columnNames = new String[]{myData.Name, myData.Id, myData.Date, myData.Contents};

            int[] txtId = new int[]{R.id.txt_name, R.id.txt_id, R.id.txt_date, R.id.txt_content};

            adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_content, c, columnNames, txtId, 0);

            listView = (ListView) findViewById(R.id.list_item);


            listView.setAdapter(adapter);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    LinearLayout linearLayoutParent = (LinearLayout) view;
                    LinearLayout linearLayoutChild = (LinearLayout) linearLayoutParent.getChildAt(0);

                    txtid = (TextView) linearLayoutChild.getChildAt(1);


                    Bundle dataBundle = new Bundle();

                    int Id = Integer.parseInt(txtid.getText().toString());


                    dataBundle.putInt("id", Id);
                    Intent intent = new Intent(getApplicationContext(), Notes.class);

                    intent.putExtras(dataBundle);
                    startActivity(intent);
                    finish();
                }
            });


            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                    if (checked) {
                        selectedItem.add(idList.get(position));
                        count++;
                        mode.setTitle(count + " Selected");
                    } else {
                        selectedItem.remove(idList.get(position));
                        count--;
                        mode.setTitle(count + " Selected");

                    }

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                    MenuInflater menuInflater = getMenuInflater();
                    menuInflater.inflate(R.menu.select_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                    if (item.getItemId() == R.id.list_delete) {
                        int count = 0;
                        for (String i : selectedItem) {
                            DataBase myData = new DataBase(getApplicationContext());

                            int c = myData.deleteNotes(Integer.parseInt(i));

                            if (c > 0)
                                count++;

                        }
                        if (count == 0) {
                            Toast.makeText(getApplicationContext(), "Item Not Found", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), String.valueOf(count) + "Item Deleted", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                            Intent i = new Intent(getApplicationContext(), NoteList.class);
                            startActivity(i);
                            finish();
                            //onDestroyActionMode(mode);

                        }

                    }

                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                    count = 0;
                    selectedItem.clear();

                }
            });

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notelist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            Intent i = new Intent(getApplicationContext(), Notes.class);
            startActivity(i);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
