package com.viniciusog.mypostapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.viniciusog.mypostapp.R;
import com.viniciusog.mypostapp.fragment.FeedFragment;
import com.viniciusog.mypostapp.fragment.PerfilFragment;
import com.viniciusog.mypostapp.fragment.PesquisaFragment;
import com.viniciusog.mypostapp.fragment.PostagemFragment;
import com.viniciusog.mypostapp.helper.ConfiguracaoFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configura a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Post App");
        setSupportActionBar(toolbar);

        //Configurar bottom navigation
        configuraBottomNavigation();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Carregar o Feed assim que executar a main activity
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();


    }

    //Método responsável pela configuração do bottom navigation view
    private void configuraBottomNavigation() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        //Faz as configurações iniciais do bottom navigation
        bottomNavigationViewEx.setTextVisibility(true);

        //Habilitar navegação
        habilitarNavegacao(bottomNavigationViewEx);

        //Configura item selecionado inicialmente
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    /**
     *Método responsável por tratar o clique na  bottomNavigationViewEx
     * @param viewEx
     */
    private void habilitarNavegacao(BottomNavigationViewEx viewEx) {

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch ( menuItem.getItemId() ) {
                    case R.id.ic_home: {
                        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                        return true;
                    }
                    case R.id.ic_pesquisa: {
                        fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                        return true;
                    }
                    case R.id.ic_postagem: {
                        fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                        return true;
                    }
                    case R.id.ic_perfil: {
                        fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair: {
                deslogarUsuario();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario() {
        try {
            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            autenticacao.signOut();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Erro", "Erro ao deslogar usuário: " + e.getMessage());
        }
    }
}