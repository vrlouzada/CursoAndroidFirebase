package br.com.vrlsistemas.cursoandroidfirebase.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class CadastroUsuarioComumActivity extends AppCompatActivity {

    private BootstrapEditText nome, cpf, rua, numero, email, senha1, senha2;
    private RadioButton rbFeminino, rbMasculino;
    private BootstrapButton btnCadastrar, btnCancelar;

    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario_comum);

        nome = (BootstrapEditText)findViewById(R.id.edtCadNome);
        cpf = (BootstrapEditText)findViewById(R.id.edtCadCPF);
        rua = (BootstrapEditText)findViewById(R.id.edtCadRua);
        numero = (BootstrapEditText)findViewById(R.id.edtCadNumero);
        email = (BootstrapEditText)findViewById(R.id.edtCadEmail);
        senha1 = (BootstrapEditText)findViewById(R.id.edtCadSenha);
        senha2 = (BootstrapEditText)findViewById(R.id.edtCadSenha2);


        rbFeminino = (RadioButton) findViewById(R.id.rbFeminino);
        rbMasculino = (RadioButton)findViewById(R.id.rbMasculino);

        btnCadastrar = (BootstrapButton) findViewById(R.id.btnCadastrar);
        btnCancelar = (BootstrapButton) findViewById(R.id.btnCancelar);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verificando se a senha 1 é igual a senha 2
                if(senha1.getText().toString().equals(senha2.getText().toString())){

                    usuario = new Usuario();
                    usuario.setNome(nome.getText().toString());
                    usuario.setCpf(cpf.getText().toString());
                    usuario.setRua(rua.getText().toString());
                    usuario.setNumero(numero.getText().toString());
                    usuario.setTipoUsuario("Comum");
                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha1.getText().toString());
                    if(rbFeminino.isChecked()){
                        usuario.setSexo("Feminino");
                    } else if(rbMasculino.isChecked()){
                        usuario.setSexo("Masculino");
                    }

                    //Chamada do método para cadastro do usuário
                    cadastrarUsuario();

                }else{
                    Toast.makeText(CadastroUsuarioComumActivity.this, "As senhas não corresnpondem!", Toast.LENGTH_LONG).show();
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
        ).addOnCompleteListener(CadastroUsuarioComumActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //Inserindo usuário no database do Firebase
                    insereUsuario(usuario);

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

                    Toast.makeText(CadastroUsuarioComumActivity.this, "Error: " + erroExcecao, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private boolean insereUsuario(Usuario usuario) {
        try{

            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");

            String key = reference.push().getKey();
            usuario.setKeyUsuario(key);

            //Gerar uma chave automática (Semelhanta a primary key)
            reference.child(key).setValue(usuario);

            Toast.makeText(CadastroUsuarioComumActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show();

            abrirLoginUsuario();

            return true;

        }catch (Exception e){
            Toast.makeText(CadastroUsuarioComumActivity.this, "Error ao gravar o usuário", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
    }

    private void abrirLoginUsuario(){

        Intent intent = new Intent(CadastroUsuarioComumActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
