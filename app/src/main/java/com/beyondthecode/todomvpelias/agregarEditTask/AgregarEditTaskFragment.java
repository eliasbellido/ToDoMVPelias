package com.beyondthecode.todomvpelias.agregarEditTask;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.google.common.base.Preconditions.checkNotNull;

import com.beyondthecode.todomvpelias.R;

/**
 * Created by jcollantes on 15/05/2018.
 */

public class AgregarEditTaskFragment extends Fragment implements AgregarEditTaskContract.View{

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private AgregarEditTaskContract.Presenter mPresenter;

    private TextView mTitulo;
    private TextView mDescripcion;

    public static AgregarEditTaskFragment nuevaInstancia(){
        return new AgregarEditTaskFragment();
    }

    public AgregarEditTaskFragment(){
        //Constructor publico vacio requerido.
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(AgregarEditTaskContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.grabarTask(mTitulo.getText().toString(),mDescripcion.getText().toString());

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addtask_frag, container, false);
        mTitulo = root.findViewById(R.id.add_task_title);
        mDescripcion = root.findViewById(R.id.add_task_description);
        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void mostrarVacioTaskError() {
        Snackbar.make(mTitulo,getString(R.string.empty_task_message),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void mostrarTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish(); //para que no se acumule en el backstack
    }

    @Override
    public void setTitulo(String titulo) {
        mTitulo.setText(titulo);
    }

    @Override
    public void setDescripcion(String descripcion) {
        mDescripcion.setText(descripcion);
    }

    @Override
    public boolean esActivo() {
        return isAdded();
    }
}
