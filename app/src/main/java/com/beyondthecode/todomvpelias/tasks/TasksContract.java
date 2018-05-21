package com.beyondthecode.todomvpelias.tasks;

import android.support.annotation.NonNull;

import com.beyondthecode.todomvpelias.BasePresenter;
import com.beyondthecode.todomvpelias.BaseView;
import com.beyondthecode.todomvpelias.data.Task;

import java.util.List;

public interface TasksContract {

    interface View extends BaseView<Presenter>{

        void setCargandoIndicador(boolean activo);

        void mostrarTasks(List<Task> tasks);

        void mostrarAgregarTask();

        void mostrarTaskDetallesUi(String taskId);

        void mostrarTaskMarcadoCompleto();

        void mostrarTaskMarcadoActivo();

        void mostrarCompletadoTasksLimpiados();

        void mostrarCargandoTasksError();

        void mostrarNoTasks();

        void mostrarFiltroActivoLabel();

        void mostrarFiltroCompletadoLabel();

        void mostrarTodoFiltroLabel();

        void mostrarTasksNoActivos();

        void mostrarTasksNoCompletados();

        void mostrarMensajeGrabadoExitosamente();

        boolean esActivo();

        void mostrarFiltroMenuPopUp();


    }


    interface Presenter extends BasePresenter{

        void resultado(int requestCode, int resultCode);

        void cargarTasks(boolean fozarUpdate);

        void agregarNuevoTask();

        void abrirTaskDetalles(@NonNull Task requestedTask);

        void completarTask(@NonNull Task taskCompletado);

        void activarTask(@NonNull Task taskActivo);

        void limpiarTasksCompletados();

        void setFiltro(TasksFiltroTipo requestTipo);

        TasksFiltroTipo obtenerFiltro();
    }

}
