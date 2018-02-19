package br.com.vrlsistemas.cursoandroidfirebase.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.vrlsistemas.cursoandroidfirebase.Adapter.CardapioAdapter;
import br.com.vrlsistemas.cursoandroidfirebase.Classes.Cardapio;
import br.com.vrlsistemas.cursoandroidfirebase.R;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView mRecycleViewCardapios;
    private CardapioAdapter adapter;
    private List<Cardapio> cardapios;
    private DatabaseReference referenciaFirebase;
    private Cardapio todosCardapios;
    private LinearLayoutManager mLayoutManagerTodosProdutos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        mRecycleViewCardapios = (RecyclerView)findViewById(R.id.recycleViewtODOSpRODUTOS);

        carregarTodosProdutos();

    }

    private void carregarTodosProdutos() {

        mRecycleViewCardapios.setHasFixedSize(true);

        mLayoutManagerTodosProdutos = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecycleViewCardapios.setLayoutManager(mLayoutManagerTodosProdutos);

        cardapios = new ArrayList<>();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        referenciaFirebase.child("cardapios").orderByChild("nomePrato").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    todosCardapios = postSnapshot.getValue(Cardapio.class);

                    cardapios.add(todosCardapios);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new CardapioAdapter(cardapios, this);

        mRecycleViewCardapios.setAdapter(adapter);

    }
}
