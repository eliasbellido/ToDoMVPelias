package com.beyondthecode.todomvpelias.detalletask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;


import com.beyondthecode.todomvpelias.data.Task;
import com.beyondthecode.todomvpelias.data.source.TasksDataSource;
import com.beyondthecode.todomvpelias.data.source.TasksRepository;
import com.google.common.base.Strings;

public class DetalleTaskPresenter implements DetalleTaskContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final DetalleTaskContract.View mDetalleTaskView;

    @Nullable
    private String mTaskId;

    public DetalleTaskPresenter(@Nullable String mTaskId,
                                @NonNull TasksRepository mTasksRepository,
                                @NonNull DetalleTaskContract.View mDetalleTaskView) {

        this.mTaskId = mTaskId;
        this.mTasksRepository = checkNotNull(mTasksRepository,"tasksRepository no pueden ser nulo!");
        this.mDetalleTaskView = checkNotNull(mDetalleTaskView,"detalleTaskView no puede ser nulo!");

        mDetalleTaskView.setPresenter(this);
    }


    @Override
    public void start() {
        abrirTask();
    }

    private void abrirTask() {

        if(Strings.isNullOrEmpty(mTaskId)){
            mDetalleTaskView.mostrarFaltaTask();
            return;
        }

        mDetalleTaskView.setCargandoIndicador(true);
        mTasksRepository.obtenerTask(mTaskId, new TasksDataSource.ObtenerTaskCallback() {
            @Override
            public void enTaskCargado(Task task) {
                //La vista puede ya no ser capaz de manejar los updates del UI
                if(!mDetalleTaskView.esActivo()){
                    return;
                }
                mDetalleTaskView.setCargandoIndicador(false);

                if(null == task){
                    mDetalleTaskView.mostrarFaltaTask();
                }else{
                    mostrarTask(task);
                }


            }

            @Override
            public void enDataNoDisponible() {
                //La vista puede ya no ser capaz de manejar los updates del UI
                if(!mDetalleTaskView.esActivo()){
                    return;
                }
                mDetalleTaskView.mostrarFaltaTask();


            }
        });
    }



    @Override
    public void editarTask() {
        if(Strings.isNullOrEmpty(mTaskId)){
            mDetalleTaskView.mostrarFaltaTask();
            return;
        }
        mDetalleTaskView.mostrarTaskEditar(mTaskId);

    }

    @Override
    public void eliminarTask() {
        if(Strings.isNullOrEmpty(mTaskId)){
            mDetalleTaskView.mostrarFaltaTask();
            return;
        }
        mTasksRepository.eliminarTask(mTaskId);
        mDetalleTaskView.mostrarTaskEliminado();

    }

    @Override
    public void completarTask() {
        if(Strings.isNullOrEmpty(mTaskId)){
            mDetalleTaskView.mostrarFaltaTask();
            return;
        }
        mTasksRepository.completarTask(mTaskId);
        mDetalleTaskView.mostrarTaskMarcadoCompleto();
    }

    @Override
    public void activarTask() {
        if(Strings.isNullOrEmpty(mTaskId)){
            mDetalleTaskView.mostrarFaltaTask();
            return;
        }
        mTasksRepository.activarTask(mTaskId);
        mDetalleTaskView.mostrarTaskMarcadoActivo();

    }

    private void mostrarTask(@NonNull Task task) {
        String titulo = task.getTitulo();
        String descripcion = task.getDescripcion();

        if(Strings.isNullOrEmpty(titulo)){
            mDetalleTaskView.ocultarTitulo();
        }else{
            mDetalleTaskView.mostrarTitulo(titulo);
        }

        if(Strings.isNullOrEmpty(descripcion)){
            mDetalleTaskView.ocultarDescripcion();
        }else{
            mDetalleTaskView.mostrarDescripcion(descripcion);
        }
        mDetalleTaskView.mostrarEstadoCompletado(task.isCompletado());

    }

}
