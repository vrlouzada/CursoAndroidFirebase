package br.com.vrlsistemas.cursoandroidfirebase.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import br.com.vrlsistemas.cursoandroidfirebase.DAO.ConfiguracaoFirebase;
import br.com.vrlsistemas.cursoandroidfirebase.R;

public class UploadFotoActivity extends AppCompatActivity {


    private BootstrapButton btnUpload, btnCancelar;
    private ImageView  imageView;

    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth autenticacao;
    private String emailUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_foto);

        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        imageView = (ImageView)findViewById(R.id.imageCadFotoPerfil);

        carregaImagempadrao();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem!"), 123);
            }
        });

        btnUpload = (BootstrapButton) findViewById(R.id.btnUpload);
        btnCancelar = (BootstrapButton) findViewById(R.id.btnCancelar);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastroFotoPeril();
            }
        });

    }

    private void cadastroFotoPeril(){
        StorageReference montaImagemReferencia = storageReference.child("fotoPerfilUsuario/" + emailUsuarioLogado + ".jpg");

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Bitmap bitmap = imageView.getDrawingCache();

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte[] data = byteArray.toByteArray();

        UploadTask uploadTask = montaImagemReferencia.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri dowloadUrl = taskSnapshot.getDownloadUrl();
                carregaImagempadrao();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int heigth = 300;
        final int width = 300;

        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                Uri imageSelecionada = data.getData();
                Picasso.with(UploadFotoActivity.this).load(imageSelecionada.toString()).resize(400, 400).centerCrop().into(imageView);
            }
        }
    }

    private void carregaImagempadrao(){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        final StorageReference storageReference = storage.getReferenceFromUrl("gs://cursoandroidfirebase-f0bb0.appspot.com/fotoPerfil.png");

        final int heigth = 300;
        final int width = 300;

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(UploadFotoActivity.this).load(uri.toString()).resize(width, heigth).centerCrop().into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}
