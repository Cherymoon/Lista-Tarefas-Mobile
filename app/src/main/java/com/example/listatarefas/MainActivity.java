package com.example.listatarefas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText meutexto;
    private ListView minhaLista;
    private Button meuBotao;

    private ArrayAdapter<String> itemsAdaptador;
    private ArrayList<Integer> ids;
    private ArrayList<String> itens;

    private SQLiteDatabase bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        meutexto = findViewById(R.id.meuTexto);
        minhaLista = (ListView) findViewById(R.id.minhaLista);
        meuBotao = (Button) findViewById(R.id.meuBotao);


        carregaTarefas();

        minhaLista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                alertaApagarTarefa(position);
                return false;
            }
        });

        meuBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionaNovaTarefa(meutexto.getText().toString());
            }
        });

    }

    private void carregaTarefas() {

        try{
            bancoDados = openOrCreateDatabase("ToDoList", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS minhastarefas(id integer primary key autoincrement, tarefa varchar)");
                // String novaTarefa = meutexto.getText().toString();
                // bancoDados.execSQL("INSERT INTO minhastarefas(tarefa) VALUES('" + novaTarefa + "')");

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM minhastarefas ORDER BY id DESC", null);

            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            itemsAdaptador = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2, android.R.id.text1
            , itens);

            minhaLista.setAdapter(itemsAdaptador);

            cursor.moveToFirst();

            while(cursor != null) {
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(cursor.getInt(indiceColunaId));
                cursor.moveToNext();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void adicionaNovaTarefa(String novaTarefa) {
        try {
            if(novaTarefa.equals(null) || novaTarefa.trim().equals("")) {
                Toast.makeText(this, "Insira uma tarefa.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tarefa" + novaTarefa + "inserida.", Toast.LENGTH_SHORT).show();
                meutexto.setText("");
                bancoDados.execSQL("INSERT INTO minhastarefas(tarefa) VALUES('" + novaTarefa + "')");
                carregaTarefas();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void apagarTarefa(Integer id) {
        try{
            bancoDados.execSQL("DELETE FROM minhastarefas where id="+id);
            carregaTarefas();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void alertaApagarTarefa(Integer idSelecionado) {
        String tarefaSeleciona = itens.get(idSelecionado);
        final Integer numeroId = idSelecionado;

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Aviso")
                .setMessage("Deseja apagar a tarefa:" + tarefaSeleciona +"?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apagarTarefa(ids.get(numeroId));
                    }
                })
                .setNegativeButton("Não", null).show();
    }
}
