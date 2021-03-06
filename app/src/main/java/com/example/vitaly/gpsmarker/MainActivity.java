package com.example.vitaly.gpsmarker;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int INPUT_RESULT = 1;

    int SelectPosition = 0;

    DataBase db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // открываем подключение к БД
        db = new DataBase(this);
        db.open();

        // получаем курсор
        cursor = db.getAllData();
        startManagingCursor(cursor);

        // формируем столбцы сопоставления
        String [] from = new String[] { DataBase.COLUMN_NAME, DataBase.COLUMN_LOC };
        int [] to = new int[]{ R.id.name_txt, R.id.location_txt };

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        ListView listView = (ListView) findViewById(R.id.listView); // объявъяление и  нахождение ListView
        listView.setAdapter(scAdapter);

        registerForContextMenu(listView); // присвоение контексного меню для ListVew

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                SelectPosition = position;

                return false;
            }
        });

        Button btn_Add = (Button) findViewById(R.id.btn_add);// объявление и нахождение кнопки "Добавить"
        btn_Add.setOnClickListener(this);                    // присвоение слушателя на кнопку "Добавить"
    }

    // Метод обработки кнопки "Добавить"
    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this,InpActivity.class);

        startActivityForResult(intent, INPUT_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent inten_data) {
        super.onActivityResult(requestCode, resultCode, inten_data);

        if(requestCode == INPUT_RESULT) {

            if(resultCode == RESULT_OK) {

                String name = inten_data.getStringExtra(InpActivity.MARK_INPUT);

                String crd = inten_data.getStringExtra(InpActivity.MARK_LOC);

                db.addRec(name, crd + (cursor.getCount() + 1));

                // обновляем курсор
                cursor.requery();
            }
        }
    }

    // Метод создания контексного меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(0, CM_SHOW_ID, 0, R.string.show_record);
    }

    private static final int CM_DELETE_ID = 11;
    private static final int CM_SHOW_ID = 12;
    // Метод обработки нажатия на пункты контекстного меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // получаем из пункта контекстного меню данные по пункту списка
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){

            case CM_DELETE_ID:

            // извлекаем id записи и удаляем соответствующую запись в БД
            db.delRec(acmi.id);

            // обновляем курсор
            cursor.requery();

                break;

            case CM_SHOW_ID :

            String [] message = db.getPoint(acmi.id);

                String name = message [1];
                String latitude = message [2].split(" ")[0];
                String longitude = message [2].split(" ")[1];

            Intent intent = new Intent(this,MapsActivity.class);

                intent.putExtra("name", name);
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);

                startActivity(intent);

                break;
        }
        return super .onContextItemSelected(item);

    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
