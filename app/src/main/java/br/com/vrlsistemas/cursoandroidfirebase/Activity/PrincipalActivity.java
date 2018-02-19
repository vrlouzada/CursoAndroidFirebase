package br.com.vrlsistemas.cursoandroidfirebase.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.vrlsistemas.cursoandroidfirebase.Classes.Usuario;
import br.com.vrlsistemas.cursoandroidfirebase.R;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference referenciaFirebase;
    private TextView tipoUsuario;
    private Usuario usuario;
    private String tipoUsuarioEmail;

    private Menu menu1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        tipoUsuario = (TextView)findViewById(R.id.txtTipoUsuario);

        //Recupera a instância do Firebase
        autenticacao = FirebaseAuth.getInstance();
        referenciaFirebase = FirebaseDatabase.getInstance().getReference();


    }


    //Adicionando o menu criado na Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();

        this.menu1 = menu;

        //Recebedo o e-mail do usuário logado no momento
        String email = autenticacao.getCurrentUser().getEmail().toString();

        referenciaFirebase.child("usuarios").orderByChild("email").equalTo(email.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    tipoUsuarioEmail = postSnapshot.child("tipoUsuario").getValue().toString();

                    tipoUsuario.setText(tipoUsuarioEmail.toString());

                    menu1.clear();

                    if (tipoUsuarioEmail.equals("Administrador")){
                        getMenuInflater().inflate(R.menu.menu_admin, menu1);
                    }else if (tipoUsuarioEmail.equals("Atendente")){
                        getMenuInflater().inflate(R.menu.menu_atend, menu1);
                    }else if (tipoUsuarioEmail.equals("Comum")){
                        getMenuInflater().inflate(R.menu.menu_atend, menu1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        }else if (id == R.id.action_sair_atend) {
            deslogarUsuario();
        } else if (id == R.id.action_cad_perfil_atend){
            uploadFotoPerfil();
        }else if(id == R.id.action_cardapio){
            abrirTelaCardapio();
        }

        return super.onOptionsItemSelected(item);
    }

    //Método para abertura da tela ed cadastro de usuário
    private void abrirTelaCardapio(){
        Intent intent = new Intent(PrincipalActivity.this, CardapioActivity.class);
        startActivity(intent);
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

    private void uploadFotoPerfil(){
        Intent intent = new Intent(PrincipalActivity.this, UploadFotoActivity.class);
        startActivity(intent);
    }
}
