package com.viniciusog.mypostapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.viniciusog.mypostapp.R;
import com.viniciusog.mypostapp.adapter.AdapterMiniaturas;
import com.viniciusog.mypostapp.helper.RecyclerItemClickListener;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    //Será chamado assim que a activity for iniciada
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imagemFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private List<ThumbnailItem> listaFiltros;

    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //inicializar componentes
        listaFiltros = new ArrayList<>();
        imagemFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);

        //Configura a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtro");
        setSupportActionBar(toolbar);

        //Habilitar botão de voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //Recupera a imagem escolhida pelo usuário
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imagemFotoEscolhida.setImageBitmap(imagem);

            //Configura recyclerView de filtros
            adapterMiniaturas = new AdapterMiniaturas(listaFiltros, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFiltros.setLayoutManager(layoutManager);
            recyclerFiltros.setAdapter(adapterMiniaturas);

            //Adiciona evento de clique o recyclerView
            recyclerFiltros.addOnItemTouchListener(new RecyclerItemClickListener(
                    getApplicationContext(), recyclerFiltros, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    ThumbnailItem item = listaFiltros.get(position);

                    //Fazer a copia de um bitmap - Passando a configuração
                    //do bitmap original e true ou falso para dizer se a nova imagem
                    //pode ser editada
                    imagemFiltro = imagem.copy(imagem.getConfig(), true);
                    Filter filtro = item.filter;
                    imagemFotoEscolhida.setImageBitmap(filtro.processFilter(imagemFiltro));
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            }
            ));

            //Recuperar filtros
            recuperarFiltros();
        }
    }

    private void recuperarFiltros() {
        //limpar items
        listaFiltros.clear();
        ThumbnailsManager.clearThumbs();

        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        //listar todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filtro : filtros) {

            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterMiniaturas.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.ic_salvar_postagem: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
