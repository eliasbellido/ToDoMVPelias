package com.beyondthecode.todomvpelias.detalletask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import static com.google.common.base.Preconditions.checkNotNull;

import com.beyondthecode.todomvpelias.R;
import com.beyondthecode.todomvpelias.agregarEditTask.AgregarEditTaskActivity;
import com.beyondthecode.todomvpelias.agregarEditTask.AgregarEditTaskFragment;

public class DetalleTaskFragment extends Fragment implements DetalleTaskContract.View{

    @NonNull
    private static final String ARGUMENT_TASK_ID ="TASK_ID";

    @NonNull
    private static final int REQUEST_EDIT_TASK = 1;

    private DetalleTaskContract.Presenter mPresenter;

    private TextView mTituloDetalle;

    private TextView mDescripcionDetalle;

    private CheckBox mEstadoCompletadoDetalle;

    public static DetalleTaskFragment nuevaInstancia(@Nullable String taskId){

        Bundle argumentos = new Bundle();
        argumentos.putString(ARGUMENT_TASK_ID,taskId);
        DetalleTaskFragment fragment = new DetalleTaskFragment();
        fragment.setArguments(argumentos);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.taskdetail_frag,container,false);
        setHasOptionsMenu(true);
        mTituloDetalle = root.findViewById(R.id.task_detail_title);
        mDescripcionDetalle = root.findViewById(R.id.task_detail_description);
        mEstadoCompletadoDetalle = root.findViewById(R.id.task_detail_complete);

        //preparar floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.editarTask();
            }
        });

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_delete:
                mPresenter.eliminarTask();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu,menu);

    }

    @Override
    public void setCargandoIndicador(boolean activo) {
        if(activo){
            mTituloDetalle.setText("");
            mDescripcionDetalle.setText(getString(R.string.loading));
        }
    }

    @Override
    public void mostrarFaltaTask() {
        mTituloDetalle.setText("");
        mDescripcionDetalle.setText(getString(R.string.no_data));
    }

    @Override
    public void ocultarTitulo() {
        mTituloDetalle.setVisibility(View.GONE);
    }

    @Override
    public void mostrarTitulo(String titulo) {
        mTituloDetalle.setVisibility(View.VISIBLE);
        mTituloDetalle.setText(titulo);
    }

    @Override
    public void ocultarDescripcion() {
        mDescripcionDetalle.setVisibility(View.GONE);
    }

    @Override
    public void mostrarDescripcion(@NonNull String descripcion) {
        mDescripcionDetalle.setVisibility(View.VISIBLE);
        mDescripcionDetalle.setText(descripcion);
    }

    @Override
    public void mostrarEstadoCompletado(final boolean completo) {
        checkNotNull(mEstadoCompletadoDetalle);

        mEstadoCompletadoDetalle.setChecked(completo);
        mEstadoCompletadoDetalle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mPresenter.completarTask();
                }else{
                    mPresenter.activarTask();
                }
            }
        });
    }

    @Override
    public void mostrarTaskEditar(@NonNull String taskId) {
        Intent intent = new Intent(getContext(), AgregarEditTaskActivity.class);
        intent.putExtra(AgregarEditTaskFragment.ARGUMENT_EDIT_TASK_ID,taskId);
        startActivityForResult(intent,REQUEST_EDIT_TASK);
    }

    @Override
    public void mostrarTaskEliminado() {
        getActivity().finish();
    }

    @Override
    public void mostrarTaskMarcadoCompleto() {
        Snackbar.make(getView(),getString(R.string.task_marked_complete),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void mostrarTaskMarcadoActivo() {
        Snackbar.make(getView(),getString(R.string.task_marked_active),Snackbar.LENGTH_LONG).show();

    }

    @Override
    public boolean esActivo() {
        return isAdded();
    }

    @Override
    public void setPresenter(@NonNull DetalleTaskContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }


}
