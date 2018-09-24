package com.example.soura.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

/**
 * Created by Soura on 30-05-2017
 */

public class Notes extends AppCompatActivity {

    EditText title, content;

    TextView Nam,cont,date;

    int value;
    Menu myMenu;
    boolean flagT = false , flagC  = false;

    String Name,Contents,Date;

    static   String changedTitle,changedContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.edit_note);

        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.txtcontent);


        DataBase myData = new DataBase(this);


        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
           setContentView(R.layout.note_display);

            Nam = (TextView) findViewById(R.id.note_name);
            cont = (TextView) findViewById(R.id.note_content);
            date = (TextView) findViewById(R.id.note_date);



            value = extras.getInt("id");

            if(value > 0)
            {

               Cursor c = myData.getData(String.valueOf(value));


                while(c.moveToNext())
                {
                     Name = c.getString(c.getColumnIndex(myData.Name));
                     Contents = c.getString(c.getColumnIndex(myData.Contents));
                    Date = c.getString(c.getColumnIndex(myData.Date));

                }



                if(!c.isClosed())
                {
                    c.close();
                }

               Nam.setText(Name);
                cont.setText(Contents);
                date.setText(Date);

                title.setText((CharSequence) Name);
                content.setText((CharSequence) Contents);


            }

        }






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        myMenu = menu;

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            getMenuInflater().inflate(R.menu.display_note_menu, menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.edit_note_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.




        String Title, Content, Date = "";

        Title = title.getText().toString();
        Content = content.getText().toString();

        boolean flag = false;

        DataBase myData = new DataBase(this);

        Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        Date = dateFormat.format(date);


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id)
        {
            case R.id.Save:

                if (Title.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter the name of note!", Toast.LENGTH_SHORT).show();

                } else {
                    boolean r = myData.insertNotes(Title, Date, Content);

                    if (r) {
                        Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(),NoteList.class);
                        startActivity(i);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

                    }

                }

                return true;


            case R.id.edit:

                Menu menu ;

               setContentView(R.layout.edit_note);



                MenuItem menuItem = (MenuItem) myMenu.findItem(R.id.save2);
                menuItem.setVisible(true);
                menuItem = myMenu.findItem(R.id.edit);
                menuItem.setVisible(false);
                menuItem = myMenu.findItem(R.id.delete);
                menuItem.setVisible(false);


                title = (EditText) findViewById(R.id.title);
                content = (EditText) findViewById(R.id.txtcontent);

                title.setText(Name);
                content.setText(Contents);

                title.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        changedTitle = s.toString();
                        flagT = true;


                    }
                });


                content.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        changedContent = s.toString();
                        flagC = true;

                    }
                });

                return  true;



            case  R.id.save2:

                if(flagT & !flagC)
                {
                   flag = myData.updateNotes(value,changedTitle,Date,"");
                }

                if(flagC & !flagT)
                {
                    flag = myData.updateNotes(value,"",Date,changedContent);
                }

                if(flagC & flagT)
                {
                    flag = myData.updateNotes(value,changedTitle,Date,changedContent);
                }

                if(flag)
                {
                    Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();;
                }

                Intent intent = new Intent(getApplicationContext(),NoteList.class);

                startActivity(intent);

                finish();



                return  true;

            case R.id.delete:


                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete!")
                        .setMessage("Are you sure you want to delete this ?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DataBase myData = new DataBase(getApplicationContext());

                                int count = myData.deleteNotes(value);

                                if(count == 0)
                                {
                                    Toast.makeText(getApplicationContext(),"Item Not Found",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                    NoteList.adapter.notifyDataSetChanged();

                                    Intent i = new Intent(getApplicationContext(),NoteList.class);

                                    startActivity(i);
                                    finish();

                                }

                            }
                        });

                        builder.setNegativeButton("No", null);


                AlertDialog alert = builder.create();
                        alert.show();


                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.BLACK);
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.BLACK);

                return  true;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Write your code here

        DataBase myData = new DataBase(this);
        boolean flag = false;

        if(flagT & !flagC)
        {
            flag = myData.updateNotes(value,changedTitle,Date,"");
        }

        if(flagC & !flagT)
        {
            flag = myData.updateNotes(value,"",Date,changedContent);
        }

        if(flagC & flagT)
        {
            flag = myData.updateNotes(value,changedTitle,Date,changedContent);
        }

        if(flag)
        {
            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();;
        }

        Intent intent = new Intent(getApplicationContext(),NoteList.class);

        startActivity(intent);

        finish();

        super.onBackPressed();
    }




}