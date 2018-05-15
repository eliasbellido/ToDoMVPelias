package com.beyondthecode.todomvpelias.AgregarEditTask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beyondthecode.todomvpelias.data.Task;
import com.beyondthecode.todomvpelias.data.source.TasksDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by jcollantes on 14/05/2018.
 */

public class AgregarEditTaskPresenter implements AgregarEditTaskContract.Presenter,
        TasksDataSource.ObtenerTaskCallback {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private final AgregarEditTaskContract.View mAgregarTaskView;

    @Nullable
    private String mTaskId;

    private boolean mEsDataPerdida;


    public AgregarEditTaskPresenter(@Nullable String mTaskId, @NonNull TasksDataSource mTasksRepository, @NonNull AgregarEditTaskContract.View mAgregarTaskView, boolean deberiaCargarDataDelRepo) {

        this.mTaskId = mTaskId;
        this.mTasksRepository = checkNotNull(mTasksRepository);
        this.mAgregarTaskView = checkNotNull(mAgregarTaskView);
        this.mEsDataPerdida = deberiaCargarDataDelRepo;

        this.mAgregarTaskView.setPresenter(this);
    }

    @Override
    public void start() {

        if(!esNuevaTarea() && mEsDataPerdida){
            llenarTask();
        }
    }

    @Override
    public void grabarTask(String titulo, String descripcion) {

        if (esNuevaTarea()) {
            crearTask(titulo, descripcion);
        }else{
            actualizarTask(titulo,descripcion);
        }

    }

    @Override
    public void llenarTask() {

        if(esNuevaTarea()){
            throw new RuntimeException("llenarTask() fue llamado pero el task es nuevo");
        }

        mTasksRepository.obtenerTask(mTaskId,this);
    }

    @Override
    public void enTaskCargado(Task task) {
        //la vista podria ya no ser capaz de manejar updates del UI
        if(mAgregarTaskView.esActivo()){
            mAgregarTaskView.setTitulo(task.getmTitulo());
            mAgregarTaskView.setDescripcion(task.getmDescripcion());
        }
        mEsDataPerdida = false;

    }

    @Override
    public void enDataNoDisponible() {
        //la vista podria ya no ser capaz de manejar updates del UI
        if(mAgregarTaskView.esActivo()){
            mAgregarTaskView.mostrarVacioTaskError();
        }


    }

    @Override
    public boolean esDataFaltante() {
        return false;
    }

    private boolean esNuevaTarea(){
        return mTaskId == null;
    }

    private void crearTask(String titulo, String descripcion) {
        Task nuevaTarea = new Task(titulo,descripcion);
        if(nuevaTarea.esVacio()){
            mAgregarTaskView.mostrarVacioTaskError();
        }else{
            mTasksRepository.grabarTask(nuevaTarea);
            mAgregarTaskView.mostrarTasksList();
        }
    }

    private void actualizarTask(String titulo, String descripcion){
        if(esNuevaTarea()){
            throw new RuntimeException("actualizarTask() fue llamado pero el task es nuevo.");
        }
        mTasksRepository.grabarTask(new Task(titulo,descripcion,mTaskId));
        mAgregarTaskView.mostrarTasksList(); // despues de editar, regresar a la lista

    }

}
