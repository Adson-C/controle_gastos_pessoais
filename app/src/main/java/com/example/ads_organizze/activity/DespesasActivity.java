package com.example.ads_organizze.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.ads_organizze.R;
import com.example.ads_organizze.helper.DateCustom;
import com.example.ads_organizze.model.Movimentacao;
import com.google.android.material.textfield.TextInputEditText;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoValor =  findViewById(R.id.editValor);
        campoData =  findViewById(R.id.editData);
        campoCategoria =  findViewById(R.id.editCategoria);
        campoDescricao =  findViewById(R.id.editDescricao);

        // Preecher o campo data com a date atual
        campoData.setText(DateCustom.dataAtual());
    }

    // salvar despesas
    public void salvarDespesa(View view){
        movimentacao = new Movimentacao();
        String data = campoData.getText().toString();
        movimentacao.setValor(String.valueOf(Double.parseDouble(campoValor.getText().toString())));
        movimentacao.setCategoria(campoCategoria.getText().toString());
        movimentacao.setDescricao(campoDescricao.getText().toString());
        movimentacao.setData( data);
        movimentacao.setTipo("d");

        movimentacao.salvar( data );
    }
}