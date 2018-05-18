package com.beyondthecode.todomvpelias.data.source;

import android.support.annotation.NonNull;
import static com.google.common.base.Preconditions.checkNotNull;

import com.beyondthecode.todomvpelias.data.Task;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCIA = null;

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

    //esta variable tiene un paquete de visibilidad local para que pueda se accedido desde los tests.
    Map<String, Task> mCachedTasks;

    /*marca el cache como invalido, para forzar un update la proxima vez que la data es solicitada.
    Esta variable tiene un paquete de visibilidad local para que pueda ser accedido desde los tests.
    */
    boolean mCacheIsDirty = false;

    //previene instanciación directa


    public TasksRepository(@NonNull TasksDataSource mTasksRemoteDataSource,
                           @NonNull TasksDataSource mTasksLocalDataSource) {
        this.mTasksRemoteDataSource = mTasksRemoteDataSource;
        this.mTasksLocalDataSource = mTasksLocalDataSource;
    }

    /*
    * retorna la unica instancia de esta clase, creandolo si es necesario
    * */
    public static TasksRepository getInstancia(TasksDataSource mTasksRemoteDataSource,
                                               TasksDataSource mTasksLocalDataSource){
        if(INSTANCIA == null){
            INSTANCIA = new TasksRepository(mTasksRemoteDataSource,mTasksLocalDataSource);
        }
        return INSTANCIA;
    }

    /*
    * Usado para forzar  {@link #getInstance(TasksDataSource, TasksDataSource)} crear una nueva instancia
    * la proxima vez es llamado.
    */
    public static void destruirInstancia(){
        INSTANCIA = null;
    }


    /*
    * Obtiene tasks desde el cache, local data source (SQLite) o remote data source,
    * cualquiera que es disponible primero.
    *  /* * <p>
     * Note: {@link LoadTasksCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     * */




    @Override
    public void obtenerTasks(@NonNull final CargarTasksCallback callback) {

        checkNotNull(callback);

        //responde inmediatamente con el cache si es disponible y no esta sucio
        if(mCachedTasks !=null && !mCacheIsDirty){
            callback.enTasksCargados(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        if(mCacheIsDirty){
            //si el cache está sucio necesitamos recuperar la nueva data desde la red.
            obtenerTasksDesdeRemoteDataSource(callback);
        }else{
            //consultar el almacenamineto local si es disponible. Si no, consultar la red.
            mTasksLocalDataSource.obtenerTasks(new CargarTasksCallback() {
                @Override
                public void enTasksCargados(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.enTasksCargados(new ArrayList<>(mCachedTasks.values()));

                }

                @Override
                public void enDataNoDisponible() {
                    obtenerTasksDesdeRemoteDataSource(callback
                    );
                }
            });
        }
    }



    @Override
    public void obtenerTask(@NonNull String taskId, @NonNull ObtenerTaskCallback callback) {

    }

    @Override
    public void grabarTask(@NonNull Task task) {

    }

    @Override
    public void completarTask(@NonNull Task task) {

    }

    @Override
    public void completarTask(@NonNull String taskId) {

    }

    @Override
    public void activarTask(@NonNull Task task) {

    }

    @Override
    public void activarTask(@NonNull String taskId) {

    }

    @Override
    public void limpiarTasksCompletados() {

    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void elimiarTodoTasks() {

    }

    @Override
    public void eliminarTask(@NonNull String taskId) {

    }

    private void obtenerTasksDesdeRemoteDataSource(@NonNull final CargarTasksCallback callback) {
        mTasksRemoteDataSource.obtenerTasks(new CargarTasksCallback() {
            @Override
            public void enTasksCargados(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
            }

            @Override
            public void enDataNoDisponible() {
                callback.enDataNoDisponible();
            }
        });

    }

    private void refreshCache(List<Task> tasks) {
        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for(Task task : tasks){
            mCachedTasks.put(task.getmId(),task);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        mTasksLocalDataSource.elimiarTodoTasks();
        for(Task task : tasks){
            mCachedTasks.put(task.getmId(),task);
        }
        mCacheIsDirty = false;
    }
}
