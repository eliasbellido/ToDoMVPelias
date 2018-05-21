package com.beyondthecode.todomvpelias.tasks;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.beyondthecode.todomvpelias.Injection;
import com.beyondthecode.todomvpelias.R;
import com.beyondthecode.todomvpelias.util.ActivityUtils;
import com.beyondthecode.todomvpelias.util.EspressoIdlingResource;

public class TasksActivity extends AppCompatActivity{

    private static final String LLAVE_FILTRO_ACTUAL = "LLAVE_FILTRO_ACTUAL";
    private DrawerLayout mDrawerLayout;
    private TasksPresenter mTasksPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R .layout.tasks_act);

        //preparar el toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //preparar el navigation drawer;
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = findViewById(R.id.nav_view);

        if(navigationView != null){
            prepararContenidoDrawer(navigationView);
        }

        TasksFragment tasksFragment = (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(tasksFragment == null){
            //crear el fragment
            tasksFragment = TasksFragment.nuevaInstancia();
            ActivityUtils.agregarFragmentalActivity(
                    getSupportFragmentManager(),tasksFragment,R.id.contentFrame);

        }

        //crear el Presenter
        mTasksPresenter = new TasksPresenter(
                Injection.proveerTasksRepository(getApplicationContext()),
                tasksFragment);

        //cargar stado grabado anteriormente, si es disponible.
        if(savedInstanceState != null){
            TasksFiltroTipo filtroActual = (TasksFiltroTipo) savedInstanceState.getSerializable(LLAVE_FILTRO_ACTUAL);
            mTasksPresenter.setFiltro(filtroActual);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(LLAVE_FILTRO_ACTUAL,mTasksPresenter.obtenerFiltro());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //abre el nav drawer cuando el icono de home es seleccionado desde el toolbar
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepararContenidoDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.list_navigation_menu_item:
                        //hacer nada, ya estamos en esa pantalla
                        break;
                    case R.id.statistics_navigation_menu_item:
                        //falta completar
                        break;
                        default:
                            break;
                }

                //cerrar el navigation drawer cuando un item es seleccionado
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @VisibleForTesting
    public IdlingResource obtenerCountingIdlingResource(){
        return EspressoIdlingResource.obtenerIdlingRecurso();
    }



}
