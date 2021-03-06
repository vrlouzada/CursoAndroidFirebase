package br.com.vrlsistemas.cursoandroidfirebase.Activity;

import android.Manifest;
import android.content.Intent;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.vrlsistemas.cursoandroidfirebase.Classes.Usuario;
import br.com.vrlsistemas.cursoandroidfirebase.DAO.ConfiguracaoFirebase;
import br.com.vrlsistemas.cursoandroidfirebase.Helper.Preferencias;
import br.com.vrlsistemas.cursoandroidfirebase.R;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth autenticacao;
    private BootstrapEditText edtEmail, edtSenha;
    private BootstrapButton btnLogin;
    private Usuario usuario;
    private TextView txtAbreCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = (BootstrapEditText)findViewById(R.id.edtEmail);
        edtSenha = (BootstrapEditText)findViewById(R.id.edtSenha);
        btnLogin = (BootstrapButton)findViewById(R.id.btnLogin);
        txtAbreCadastro = (TextView)findViewById(R.id.txtAbreCadastro);

        //Solicita as permissões para acesso completo do sistema
        permission();

        //Verifica se o usuário já está logado
        if(usuarioLogado())
        {
            Intent intentMinhaConta = new Intent(MainActivity.this, PrincipalActivity.class);
            abrirNovaActivity(intentMinhaConta);

        }
        else
        {
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

        txtAbreCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CadastroUsuarioComumActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    //Valida se o email e senha está cadastrado no Firebase
    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail().toString(), usuario.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Preferencias preferencias = new Preferencias(MainActivity.this);
                    preferencias.salvarUsuarioPreferencias(usuario.getEmail(), usuario.getSenha());


                    abrirTelaPrincipal();
                    Toast.makeText(MainActivity.this, "Login efetuado com sucesso", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Usuario ou senha inválidos! Tente novamente.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Abre a tela de Casdatro de Usuário
    private void abrirTelaPrincipal(){


        Intent intentPrincipalActivity = new Intent(MainActivity.this, PrincipalActivity.class);
        finish();
        abrirNovaActivity(intentPrincipalActivity);
    }

    //Verifica se o usuário já esta logado no sistema
    public Boolean usuarioLogado(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            return true;
        }else{
            return  false;
        }
    }

    //Método generic para iniciar nova activity
    public void abrirNovaActivity(Intent intent){
        startActivity(intent);
    }


    public void permission(){
        int PERMISSION_ALL = 1;

        String[] PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_ALL);
    }
}
