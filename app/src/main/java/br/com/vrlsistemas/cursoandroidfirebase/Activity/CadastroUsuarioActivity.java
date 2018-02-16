package br.com.vrlsistemas.cursoandroidfirebase.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.vrlsistemas.cursoandroidfirebase.Classes.Usuario;
import br.com.vrlsistemas.cursoandroidfirebase.DAO.ConfiguracaoFirebase;
import br.com.vrlsistemas.cursoandroidfirebase.Helper.Preferencias;
import br.com.vrlsistemas.cursoandroidfirebase.R;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private BootstrapEditText email, senha1, senha2, nome;
    private RadioButton rbAdmin, rbAtend;
    private BootstrapButton btnCadastrar, btnCancelar;

    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        email = (BootstrapEditText)findViewById(R.id.edtCadEmail);
        senha1 = (BootstrapEditText)findViewById(R.id.edtCadSenha);
        senha2 = (BootstrapEditText)findViewById(R.id.edtCadSenha2);
        nome = (BootstrapEditText)findViewById(R.id.edtCadNome);

        rbAdmin = (RadioButton) findViewById(R.id.rbAdmin);
        rbAtend = (RadioButton)findViewById(R.id.rbAtend);

        btnCadastrar = (BootstrapButton) findViewById(R.id.btnCadastrar);
        btnCancelar = (BootstrapButton) findViewById(R.id.btnCancelar);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verificando se a senha 1 é igual a senha 2
                if(senha1.getText().toString().equals(senha2.getText().toString())){
                    usuario = new Usuario();
                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha1.getText().toString());
                    usuario.setNome(nome.getText().toString());
                    if(rbAdmin.isChecked()){
                        usuario.setTipoUsuario("Administrador");
                    } else if(rbAtend.isChecked()){
                        usuario.setTipoUsuario("Atendente");
                    }

                    //Chamada do método para cadastro do usuário
                    cadastrarUsuario();

                }else{
                    Toast.makeText(CadastroUsuarioActivity.this, "As senhas não corresnpondem!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Método responsável pela criação do usuário no Firebase (Autenticação or e-mail)
    private void cadastrarUsuario(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //Inserindo usuário no database do Firebase
                    insereUsuario(usuario);

                    finish();

                    //Deslogar ao adicionar o usuário
                    autenticacao.signOut();

                    //Para abrir a nossa tela principal após a reautenticação
                    abreTelaPrincipal();


                }else{
                    String erroExcecao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){ // Senha mais forte
                        erroExcecao = "Digite uma senha mais forte, contendo no mínimo 8 caracteres e que contenham letras e números!";
                    } catch (FirebaseAuthInvalidCredentialsException e){ // Email inválido
                        erroExcecao = "O e-mail digitado é inválido, digite um novo e-mail";
                    } catch (FirebaseAuthUserCollisionException e){ // Email já existe
                        erroExcecao = "Este e-mail já está cadastrado!";
                    }catch (Exception e) { // Erro padrão
                        erroExcecao = "Error ao efetuar o cadastro";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroUsuarioActivity.this, "Error: " + erroExcecao, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private boolean insereUsuario(Usuario usuario) {
        try{

            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            //Gerar uma chave automática (Semelhanta a primary key)
            reference.push().setValue(usuario);
            Toast.makeText(CadastroUsuarioActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show();

            return true;

        }catch (Exception e){
            Toast.makeText(CadastroUsuarioActivity.this, "Error ao gravar o usuário", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
    }

    private void abreTelaPrincipal(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);

        autenticacao.signInWithEmailAndPassword(preferencias.getEmailUsuarioLogado(), preferencias.getSenhaUsuarioLogado()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Intent intent = new Intent(CadastroUsuarioActivity.this, PrincipalActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(CadastroUsuarioActivity.this, "Falha!", Toast.LENGTH_LONG).show();
                    autenticacao.signOut();
                    Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

}
