package com.beyondthecode.todomvpelias.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.beyondthecode.todomvpelias.data.source.TasksDataSource;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/*
* Implementacion de un remote data source con acceso static a la data para un testing fácil*/
public class FalsoTasksRemoteDataSource implements TasksDataSource {

    private static FalsoTasksRemoteDataSource INSTANCIA;

    private static final Map<String,Task> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    //prevenir instanciación directa
    private FalsoTasksRemoteDataSource(){

    }

    public static FalsoTasksRemoteDataSource obtenerInstancia(){
        if(INSTANCIA == null){
            INSTANCIA = new FalsoTasksRemoteDataSource();
        }
        return INSTANCIA;
    }

    @Override
    public void obtenerTasks(@NonNull CargarTasksCallback callback) {
        callback.enTasksCargados(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
    }

    @Override
    public void obtenerTask(@NonNull String taskId, @NonNull ObtenerTaskCallback callback) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        callback.enTaskCargado(task);
    }

    @Override
    public void grabarTask(@NonNull Task task) {
        TASKS_SERVICE_DATA.put(task.getId(),task);
    }

    @Override
    public void completarTask(@NonNull Task task) {
      Task TaskCompletado = new Task(task.getTitulo(),task.getDescripcion(),task.getId(),true);
      TASKS_SERVICE_DATA.put(task.getId(),TaskCompletado);
    }

    @Override
    public void completarTask(@NonNull String taskId) {
        //no requerido por el remote data source
    }

    @Override
    public void activarTask(@NonNull Task task) {
        Task activarTask = new Task(task.getTitulo(),task.getDescripcion(), task.getId());
        TASKS_SERVICE_DATA.put(task.getId(),activarTask);
    }

    @Override
    public void activarTask(@NonNull String taskId) {
        //no requerido por el remote data source
    }

    @Override
    public void limpiarTasksCompletados() {
        Iterator<Map.Entry<String,Task>> eso = TASKS_SERVICE_DATA.entrySet().iterator();
        while(eso.hasNext()){
            Map.Entry<String,Task> entrada = eso.next();
            if(entrada.getValue().isCompletado()){
                eso.remove();
            }
        }
    }


    public void refreshTasks() {
        //no es requerido porque @link TasksRepository maneja la logica de refreshing
        //los tasks de todas las data sources disponibles

    }

    @Override
    public void eliminarTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }

    @Override
    public void elimiarTodoTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void agregarTasks(Task... tasks){
        for(Task task : tasks){
            TASKS_SERVICE_DATA.put(task.getId(),task);
        }
    }

}
