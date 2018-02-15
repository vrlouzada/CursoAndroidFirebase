package br.com.vrlsistemas.cursoandroidfirebase.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;

/**
 * Created by vlouzada on 15/02/2018.
 */

public class Prefrencias {

    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "app.preferencias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String EMAIl_USUARIO_LOGADO = "email)usuario_logado";
    private final String SENHA_USUARIO_LOGADO = "senha_usuario_logado";


    private Prefrencias(Context contextParametro){
        context = contextParametro;

        //associar o nosso preferences.edit()
        editor = preferences.edit();
    }

    public void salvarUsuarioPreferencias(String email, String senha){

        //salvar dentro do nosso aqruivo preferencias o email e senha do usuario
        editor.putString(EMAIl_USUARIO_LOGADO, email);
        editor.putString(SENHA_USUARIO_LOGADO, senha);
        editor.commit();
    }

    public String getEmailUsuarioLogado(){
        return preferences.getString(EMAIl_USUARIO_LOGADO, null);
    }

    public String getSenhaUsuarioLogado(){
        return preferences.getString(SENHA_USUARIO_LOGADO, null);
    }


}
