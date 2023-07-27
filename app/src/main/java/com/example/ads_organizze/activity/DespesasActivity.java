package com.example.ads_organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ads_organizze.R;
import com.example.ads_organizze.config.ConfiguracaoFireBase;
import com.example.ads_organizze.helper.Base64Custom;
import com.example.ads_organizze.helper.DateCustom;
import com.example.ads_organizze.model.Movimentacao;
import com.example.ads_organizze.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebasRef = ConfiguracaoFireBase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
    private Double despesasTotal;

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
        recuperarDespesaTotal();
    }

    // salvar despesas
    public void salvarDespesa(View view){

        if (validarCamposDespesas() ){

            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            movimentacao.setValor(String.valueOf(valorRecuperado));
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData( data);
            movimentacao.setTipo("d");

            Double despesaAtualizada = despesasTotal + valorRecuperado;
            atualizarDespesa(despesaAtualizada );

            movimentacao.salvar( data );

            finish();
           }
        }


    // validar campos Despesas
    public Boolean validarCamposDespesas(){

        String textValor = campoValor.getText().toString();
        String textData = campoData.getText().toString();
        String textCategoria = campoCategoria.getText().toString();
        String textDescricao = campoDescricao.getText().toString();

        // Validar se os campos foram preenchidos
        if (!textValor.isEmpty()){
            if (!textData.isEmpty()){
                if (!textCategoria.isEmpty()){
                    if (!textDescricao.isEmpty()){
                        return true;

                    } else {
                        Toast.makeText(DespesasActivity.this,
                                "Descrição não foi preenchido!!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } else {
                    Toast.makeText(DespesasActivity.this,
                            "Categoria não foi preenchido!!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                Toast.makeText(DespesasActivity.this,
                        "Data não foi preenchido!!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Toast.makeText(DespesasActivity.this,
                    "Valor não foi preenchido!!",
                    Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    // gravando e Recuparando despesas Total
    public void recuperarDespesaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );

        DatabaseReference usuarioRef = firebasRef.child("usuarios")
                .child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue( Usuario.class);
                despesasTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarDespesa( Double despesa ){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );

        DatabaseReference usuarioRef = firebasRef
                .child("usuarios")
                .child(idUsuario);
        usuarioRef.child("despesaTotal")
                  .setValue(despesa);
    }
}