package com.beyondthecode.todomvpelias.detalletask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.beyondthecode.todomvpelias.Injection;
import com.beyondthecode.todomvpelias.R;
import com.beyondthecode.todomvpelias.util.ActivityUtils;

public class DetalleTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.taskdetail_act);

        //preparar el toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //obtener el id del task solicitado
        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        DetalleTaskFragment detalleTaskFragment = (DetalleTaskFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (detalleTaskFragment == null) {
            detalleTaskFragment = DetalleTaskFragment.nuevaInstancia(taskId);

            ActivityUtils.agregarFragmentalActivity(getSupportFragmentManager(), detalleTaskFragment, R.id.contentFrame);
        }
        //crear el presentador
        new DetalleTaskPresenter(taskId,
                Injection.proveerTasksRepository(getApplicationContext()),
                detalleTaskFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
