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

public class ReceitaActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebasRef = ConfiguracaoFireBase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();

    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);

        campoValor =  findViewById(R.id.editValor);
        campoData =  findViewById(R.id.editData);
        campoCategoria =  findViewById(R.id.editCategoria);
        campoDescricao =  findViewById(R.id.editDescricao);

        // Preecher o campo data com a date atual
        campoData.setText(DateCustom.dataAtual());
        recuperarReceitaTotal();
    }
    public void salvarReceitas(View view){

        if (validarCamposReceitas() ){

            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            movimentacao.setValor(String.valueOf(valorRecuperado));
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData( data);
            movimentacao.setTipo("r");

            Double receitaAtualizar = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizar );

            movimentacao.salvar( data );
        }
    }
    public Boolean validarCamposReceitas(){

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
                        Toast.makeText(ReceitaActivity.this,
                                "Descrição não foi preenchido!!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } else {
                    Toast.makeText(ReceitaActivity.this,
                            "Categoria não foi preenchido!!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                Toast.makeText(ReceitaActivity.this,
                        "Data não foi preenchido!!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Toast.makeText(ReceitaActivity.this,
                    "Valor não foi preenchido!!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarReceitaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );

        DatabaseReference usuarioRef = firebasRef.child("usuarios")
                .child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue( Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void atualizarReceita( Double despesa ){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );

        DatabaseReference usuarioRef = firebasRef
                .child("usuarios")
                .child(idUsuario);
        usuarioRef.child("receitaTotal")
                .setValue(despesa);
    }
}