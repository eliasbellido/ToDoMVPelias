package com.beyondthecode.todomvpelias.agregarEditTask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.beyondthecode.todomvpelias.Injection;
import com.beyondthecode.todomvpelias.R;
import com.beyondthecode.todomvpelias.util.ActivityUtils;
import com.beyondthecode.todomvpelias.util.EspressoIdlingResource;

public class AgregarEditTaskActivity extends AppCompatActivity{

    public static final int REQUEST_ADD_TASK = 1;

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";

    private AgregarEditTaskPresenter mAgregarEditTaskPresenter;

    private ActionBar mActionBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);

        //setear el toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        AgregarEditTaskFragment agregarEditTaskFragment = (AgregarEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String taskId = getIntent().getStringExtra(AgregarEditTaskFragment.ARGUMENT_EDIT_TASK_ID);

        setToolbarTitulo(taskId);

        if(agregarEditTaskFragment == null){
            agregarEditTaskFragment = AgregarEditTaskFragment.nuevaInstancia();

            if(getIntent().hasExtra(AgregarEditTaskFragment.ARGUMENT_EDIT_TASK_ID)){
                Bundle bundle = new Bundle();
                bundle.putString(AgregarEditTaskFragment.ARGUMENT_EDIT_TASK_ID,taskId);
                agregarEditTaskFragment.setArguments(bundle);
            }

            ActivityUtils.agregarFragmentalActivity(getSupportFragmentManager(),
                    agregarEditTaskFragment,R.id.contentFrame);

        }

        boolean deberiaCargarDataDelRepo = true;

        //evita que el PResenter cargue la data desde mi repositorio si este es un cambio de configuracion
        if(savedInstanceState != null){

            //La data podria no ser cargado  cuando el cambio de configuracion sucedió, por lo que acá grabamos el estado.
            deberiaCargarDataDelRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY);
        }

        //crear el presentador
        mAgregarEditTaskPresenter = new AgregarEditTaskPresenter(
                taskId,
                Injection.proveerTasksRepository(getApplicationContext()),
                agregarEditTaskFragment,
                deberiaCargarDataDelRepo
        );
      

    }

    private void setToolbarTitulo(@Nullable String taskId){
        if(taskId == null){
            mActionBar.setTitle(R.string.add_task);
        }else{
            mActionBar.setTitle(R.string.edit_task);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //grabar el estado para que la proxima vez sepamos si necesitamos refresh la data
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY,mAgregarEditTaskPresenter.esDataFaltante());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @VisibleForTesting
    public IdlingResource obtenerCountingIdlingResource(){
        return EspressoIdlingResource.obtenerIdlingRecurso();
    }

}


