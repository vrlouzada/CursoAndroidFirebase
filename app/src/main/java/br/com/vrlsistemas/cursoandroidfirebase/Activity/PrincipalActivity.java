package br.com.vrlsistemas.cursoandroidfirebase.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import br.com.vrlsistemas.cursoandroidfirebase.R;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //Recupera a instância do Firebase
        autenticacao = FirebaseAuth.getInstance();

    }


    //Adicionando o menu criado na Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    //Verificado o item selecionado no menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_add_usuario){
            abrirTelaCadastroUsuario();
        } else if (id == R.id.action_sair_admin) {
            deslogarUsuario();
        }

        return super.onOptionsItemSelected(item);
    }

    //Método para abertura da tela ed cadastro de usuário
    private void abrirTelaCadastroUsuario(){
        Intent intent = new Intent(PrincipalActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }

    //Método para deslogar o usuário da aplicação
    private void deslogarUsuario(){
        autenticacao.signOut();
        Intent intent = new Intent(PrincipalActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
