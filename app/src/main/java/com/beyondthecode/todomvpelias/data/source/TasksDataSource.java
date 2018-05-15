package com.beyondthecode.todomvpelias.data.source;

import android.support.annotation.NonNull;

import com.beyondthecode.todomvpelias.data.Task;

import java.util.List;

/**
 * Created by jcollantes on 15/05/2018.
 */

public interface TasksDataSource {

    //ojo: muchos tasks
    interface CargarTasksCallback{

        void enTasksCargados(List<Task> tasks);
        void enDataNoDisponible();
    }

    //ojo: single task
    interface ObtenerTaskCallback{

        void enTaskCargado(Task task);
        void enDataNoDisponible();
    }

    void obtenerTasks(@NonNull CargarTasksCallback callback);

    void obtenerTask(@NonNull String taskId, @NonNull ObtenerTaskCallback callback);

    void grabarTask(@NonNull Task task);

    void completarTask(@NonNull Task task);

    void completarTask(@NonNull String taskId);

    void activarTask(@NonNull Task task);

    void activarTask(@NonNull String taskId);

    void limpiarTasksCompletados();

    void refreshTasks();

    void elimiarTodoTasks();

    void eliminarTask(@NonNull String taskId);

}
