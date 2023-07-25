package com.example.ads_organizze.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.ads_organizze.R;
import com.example.ads_organizze.helper.DateCustom;
import com.google.android.material.textfield.TextInputEditText;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;

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
}