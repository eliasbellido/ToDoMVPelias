package com.beyondthecode.todomvpelias.AgregarEditTask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.beyondthecode.todomvpelias.R;
import com.beyondthecode.todomvpelias.util.ActivityUtils;

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

        setToolbarTitle(taskId);

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
      

    }

    private void setToolbarTitle(@Nullable String taskId) {
        if(taskId == null){
            mActionBar.setTitle(R.string.add_task);
        }else{
            mActionBar.setTitle(R.string.edit_task);
        }
    }
}
