package com.example.ads_organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ads_organizze.R;
import com.example.ads_organizze.config.ConfiguracaoFireBase;
import com.example.ads_organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private FirebaseAuth autenticacaoLogin;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        botaoEntrar = findViewById(R.id.buttonEntrar);

        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Autenticando usuarios
                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();

                    if (!textEmail.isEmpty()){
                        if (!textSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setEmail(textEmail);
                            usuario.setSenha(textSenha);

                            validarLogin();


                        }else {
                            Toast.makeText(LoginActivity.this, "Preencha a Senha!", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(LoginActivity.this, "Preencha o E-mail!", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }
    // validar Login
    public void validarLogin(){

        autenticacaoLogin = ConfiguracaoFireBase.getFirebaseAutenticacao();
        autenticacaoLogin.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    abrirTelaPrincipal();

                }else {
                    //  lançando Execptions
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao =  "Usuário não está cadastrada.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha não corresponde ao usuário cadastardo, tente novamente!";
                    }
                    catch (Exception e){
                        excecao =  "Erro ao tentar logar !" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}