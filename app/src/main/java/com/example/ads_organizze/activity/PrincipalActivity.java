package com.example.ads_organizze.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.ads_organizze.adapter.AdapterMovimentacao;
import com.example.ads_organizze.config.ConfiguracaoFireBase;
import com.example.ads_organizze.helper.Base64Custom;
import com.example.ads_organizze.model.Movimentacao;
import com.example.ads_organizze.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ads_organizze.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private TextView textSaudacao, textSaldo;
    private Double despesasTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoUsuario = 0.0;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacaos = new ArrayList<>();
    private DatabaseReference movimentacaoRef;
    private String mesAnoSeleciondado;

    private DatabaseReference usuarioRef;

    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacao;
    private FirebaseAuth autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();

    private DatabaseReference firebaseRef = ConfiguracaoFireBase.getFirebaseDatabase();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("AdsOrganizze");
        setSupportActionBar(toolbar);

        textSaudacao =  findViewById(R.id.texSaudacao);
        textSaldo =  findViewById(R.id.textSaldo);
        recyclerView = findViewById(R.id.recyclerMovimentacao);
        calendarView = findViewById(R.id.viewCalender);
        configuraCalenderView();
        swipe();

        // Configurar adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacaos, this);

        // Configurar RecyclerView
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager( layoutManager );
            recyclerView.setHasFixedSize( true);
            recyclerView.setAdapter( adapterMovimentacao );

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    // metodos paar swipe
    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipFlags = ItemTouchHelper.START | ItemTouchHelper.END ;
                return makeMovementFlags(dragFlags, swipFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        new ItemTouchHelper( itemTouch ).attachToRecyclerView(recyclerView);
    }

    public void recuperarMovimentacoes(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("movimentacao")
                                     .child( idUsuario )
                                     .child(mesAnoSeleciondado);

        valueEventListenerMovimentacao = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movimentacaos.clear();

                for (DataSnapshot  dados: snapshot.getChildren() ){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacaos.add(movimentacao);


                    Log.i("Dados", "dadas " + movimentacao.getCategoria());
                }
                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // recuperando saldos Totais
    public void recuperarResumo(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
        Log.i("Evento", "evento foi adcionado!");
         valueEventListenerUsuario =  usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario =  snapshot.getValue(Usuario.class);
                despesasTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesasTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format( resumoUsuario );

                textSaudacao.setText("Olá, " + usuario.getNome());
                textSaldo.setText("R$ " + resultadoFormatado);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override // gerando Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // Deslogar usuario
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                autenticacao.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configuraCalenderView() {
        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1));
        mesAnoSeleciondado = mesSelecionado + "" + dataAtual.getYear();
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth() + 1));
                mesAnoSeleciondado =  mesSelecionado + "" + date.getYear();

                movimentacaoRef.removeEventListener( valueEventListenerMovimentacao);

                recuperarMovimentacoes();
            }
        });
    }

    public void adicionarDespesas(View view){
        startActivity(new Intent(this, DespesasActivity.class));
    }
    public void  adicionarReceitas(View view){
        startActivity(new Intent(this, ReceitaActivity.class));
    }
    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Evento", "evento foi removido");
        usuarioRef.removeEventListener( valueEventListenerUsuario );
        movimentacaoRef.removeEventListener( valueEventListenerMovimentacao);
    }
}