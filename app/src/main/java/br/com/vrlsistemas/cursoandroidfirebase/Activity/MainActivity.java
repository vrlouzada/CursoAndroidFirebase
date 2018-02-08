package br.com.vrlsistemas.cursoandroidfirebase.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.vrlsistemas.cursoandroidfirebase.Classes.Usuario;
import br.com.vrlsistemas.cursoandroidfirebase.DAO.ConfiguracaoFirebase;
import br.com.vrlsistemas.cursoandroidfirebase.R;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth autenticacao;
    private EditText edtEmail, edtSenha;
    private Button btnLogin;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtSenha = (EditText)findViewById(R.id.edtSenha);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!edtEmail.getText().toString().equals("") && !edtSenha.getText().toString().equals("")){

                    usuario = new Usuario();
                    usuario.setEmail(edtEmail.getText().toString());
                    usuario.setSenha(edtSenha.getText().toString());

                    validarLogin();


                }else{
                    Toast.makeText(MainActivity.this, "Preencha os campos de E-mail e Senha", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail().toString(), usuario.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    abrirTelaAdministrador();
                    Toast.makeText(MainActivity.this, "Login efetuado com sucesso", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Usuario ou senha inválidos! Tente novamente.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void abrirTelaAdministrador(){
        Intent it = new Intent(MainActivity.this, CadastroUsuario.class);
        startActivity(it);
    }


}