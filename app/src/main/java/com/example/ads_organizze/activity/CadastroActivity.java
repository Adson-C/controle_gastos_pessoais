package com.example.ads_organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ads_organizze.R;
import com.example.ads_organizze.config.ConfiguracaoFireBase;
import com.example.ads_organizze.helper.Base64Custom;
import com.example.ads_organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button btnCadastrado;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        btnCadastrado = findViewById(R.id.btnCadastrado);

        btnCadastrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // retornar o que  usuaro digitou
                String textNome = campoNome.getText().toString();
                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();

                // Validar se os campos foram preenchidos
                if (!textNome.isEmpty()){
                    if (!textEmail.isEmpty()){
                        if (!textSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setNome(textNome);
                            usuario.setEmail(textEmail);
                            usuario.setSenha(textSenha);

                            CadastrarUsuario();

                        }else {
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha a Senha!",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o E-mail!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    // metodo para cadastrar usuario Firebase
    public void CadastrarUsuario(){

        autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String idUsuario = Base64Custom.codificarBase64( usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    finish();

                }else {
                        //  lançando Execptions
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                            excecao =  "Digite uma senha mais forte !";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                            excecao =  "Por favor digite um E-mail válido !";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao =  "Já existe uma conta com esse E-mail cadastrado !";
                    }catch (Exception e){
                        excecao =  "Erro ao cadastrar usuáro !" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}