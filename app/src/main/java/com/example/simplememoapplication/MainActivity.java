package com.example.simplememoapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private int _noteId = -1;

    int save_select = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btDelete = findViewById(R.id.bt_delete);
        Button btSave = findViewById(R.id.bt_save);
        ListView memolist = findViewById(R.id.lv_memolist);

        btDelete.setEnabled(false);
        btSave.setEnabled(false);

        //メモリスト表示処理
        memoListDisplay();

        //リストにリスナを設定
        memolist.setOnItemClickListener(new ListItemClickListener());

    }

    //追加ボタンを押したときの処理
    public void AddButtonClick(View view){


        //タイトル欄を「new memo」に変更、その他初期化
        EditText etTitle = findViewById(R.id.et_title);
        Button btDelete = findViewById(R.id.bt_delete);
        Button btSave = findViewById(R.id.bt_save);
        EditText etNote = findViewById(R.id.et_note);

        etTitle.setText("new memo");
        etNote.setText("");
        save_select = 0;
        btDelete.setEnabled(true);
        btSave.setEnabled(true);
    }

    //保存ボタンをタップしたときの処理
    public void SaveButtonClick(View view){
        MemoDatabaseHelper _helper = new MemoDatabaseHelper(MainActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();

        //タイトルとメモ本文の内容を変数に格納
        EditText etTitle = findViewById(R.id.et_title);
        EditText etNote = findViewById(R.id.et_note);
        String title = etTitle.getText().toString();
        String memo = etNote.getText().toString();

        Button btSave = findViewById(R.id.bt_save);
        Button btDelete = findViewById(R.id.bt_delete);

        //データベース登録処理
        if(save_select == 1){
            String sqlDelete = "DELETE FROM memolist WHERE _id = ?";
            SQLiteStatement stmt = db.compileStatement(sqlDelete);
            stmt.bindLong(1, _noteId);
            stmt.executeUpdateDelete();

            String sqlInsert = "INSERT INTO memolist(_id, title, note) VALUES (?, ?, ?)";
            stmt = db.compileStatement(sqlInsert);

            stmt.bindLong(1,_noteId);
            stmt.bindString(2, title);
            stmt.bindString(3, memo);

            stmt.executeInsert();
            db.close();

        }else{
            String sqlInsert = "INSERT INTO memolist (title, note) VALUES(?, ?)";
            SQLiteStatement stmt = db.compileStatement(sqlInsert);
            stmt.bindString(1, title);
            stmt.bindString(2, memo);
            stmt.executeInsert();
            db.close();
        }


        //入力欄を空欄に、保存ボタンと削除ボタンを押せなくする
        etTitle.setText("");
        etNote.setText("");
        btSave.setEnabled(false);
        btDelete.setEnabled(false);

        //メモリスト表示処理
        memoListDisplay();

    }

    //削除ボタンをタップしたときの処理
    public void DeleteButtonClick(View view){
        MemoDatabaseHelper _helper = new MemoDatabaseHelper(MainActivity.this);
        SQLiteDatabase db = _helper.getReadableDatabase();

        EditText etNote = findViewById(R.id.et_note);
        EditText etTitle = findViewById(R.id.et_title);

        String sqlDelete = "DELETE FROM memolist WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sqlDelete);
        stmt.bindLong(1, _noteId);
        stmt.executeUpdateDelete();

        db.close();

        etTitle.setText("");
        etNote.setText("");

        memoListDisplay();
    }

    //データベースから内容を取得し、リストに表示させる処理
    private void memoListDisplay(){
        MemoDatabaseHelper _helper = new MemoDatabaseHelper(MainActivity.this);
        SQLiteDatabase db = _helper.getReadableDatabase();

        ListView memolist = findViewById(R.id.lv_memolist);

        String sql = "SELECT _id,title FROM memolist";
        Cursor cursor = db.rawQuery(sql, null);
        String[] from = {"title"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,cursor,from,to,0);
        memolist.setAdapter(adapter);

        db.close();
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        Button btSave = findViewById(R.id.bt_save);
        Button btDelete = findViewById(R.id.bt_delete);

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Log.i("debug", "onItemClick position="+position+" id="+id);
            save_select = 1;
            _noteId = (int)id;

            btSave.setEnabled(true);
            btDelete.setEnabled(true);

            MemoDatabaseHelper _helper = new MemoDatabaseHelper(MainActivity.this);
            SQLiteDatabase db = _helper.getReadableDatabase();

            String sql = "SELECT title,note FROM memolist WHERE _id = "+ _noteId;
            Cursor cursor = db.rawQuery(sql, null);
            Log.i("debug","cursor:"+cursor.toString());
            String note = "";
            String title = "";
            while(cursor.moveToNext()){
                int idxNote = cursor.getColumnIndex("note");
                Log.i("debug","idxNote="+idxNote+" note="+note);
                note = cursor.getString(idxNote);

                int idxTitle = cursor.getColumnIndex("title");
                Log.i("debug", "idxtitle:"+idxTitle);
                title = cursor.getString(idxTitle);
                Log.i("debug","idxtitle="+idxTitle+" title"+title);

                EditText etNote = findViewById(R.id.et_note);
                etNote.setText(note);

                EditText etTitle = findViewById(R.id.et_title);
                etTitle.setText(title);

                db.close();
            }
        }

    }
}




