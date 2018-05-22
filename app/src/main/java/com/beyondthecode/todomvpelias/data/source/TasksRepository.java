package com.beyondthecode.todomvpelias.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.beyondthecode.todomvpelias.data.Task;

import java.util.ArrayList;
import java.util.Iterator;
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
    public void obtenerTask(@NonNull final String taskId, @NonNull final ObtenerTaskCallback callback) {

        checkNotNull(taskId);
        checkNotNull(callback);

        Task cachedTask = obtenerTaskConId(taskId);

        //responder inmediatamente con el cache si es disponible
        if(cachedTask !=null){
            callback.enTaskCargado(cachedTask);
            return;
        }

        //cargar desde el server/persisted si es necesario

        //el task está en el local data source? si no es asó, consulta la red.
        mTasksLocalDataSource.obtenerTask(taskId, new ObtenerTaskCallback() {
            @Override
            public void enTaskCargado(Task task) {
                //hacer en la cache de la memoria update para mantener el UI del app siempre actualizado
                if(mCachedTasks==null){
                    mCachedTasks = new LinkedHashMap<>();
                }
                mCachedTasks.put(task.getId(),task);
                callback.enTaskCargado(task);
            }

            @Override
            public void enDataNoDisponible() {
                mTasksRemoteDataSource.obtenerTask(taskId, new ObtenerTaskCallback() {
                    @Override
                    public void enTaskCargado(Task task) {
                        //hacer en la cache de la memoria update para mantener el UI del app siempre actualizado
                        if(mCachedTasks == null){
                            mCachedTasks = new LinkedHashMap<>();
                        }
                        mCachedTasks.put(task.getId(),task);
                        callback.enTaskCargado(task);
                    }

                    @Override
                    public void enDataNoDisponible() {
                        callback.enDataNoDisponible();
                    }
                });
            }
        });
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void elimiarTodoTasks() {
        mTasksRemoteDataSource.elimiarTodoTasks();
        mTasksLocalDataSource.elimiarTodoTasks();

        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }

        mCachedTasks.clear();
    }

    @Override
    public void eliminarTask(@NonNull String taskId) {
        mTasksRemoteDataSource.eliminarTask(taskId);
        mTasksLocalDataSource.eliminarTask(taskId);

        mCachedTasks.remove(taskId);
    }

    @Override
    public void grabarTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.grabarTask(task);
        mTasksLocalDataSource.grabarTask(task);

        //hacer en la memoria cache update para mantener el UI del app actualizado
        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(),task);


    }

    @Override
    public void completarTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.completarTask(task);
        mTasksLocalDataSource.completarTask(task);

        Task taskCompletado = new Task(task.getTitulo(),task.getDescripcion(),task.getId(),true);

        //hacer en la memoria cache update para mantener el UI del app actualizado
        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(),taskCompletado);
    }

    @Override
    public void completarTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completarTask(obtenerTaskConId(taskId));
    }

    @Override
    public void activarTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.activarTask(task);
        mTasksLocalDataSource.activarTask(task);

        Task activarTask = new Task(task.getTitulo(),task.getDescripcion(),task.getId());

        //hacer en la memoria cache update para mantener el UI del app actualizado
        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(),activarTask);
    }

    @Override
    public void activarTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activarTask(obtenerTaskConId(taskId));
    }

    @Override
    public void limpiarTasksCompletados() {
        mTasksRemoteDataSource.limpiarTasksCompletados();
        mTasksLocalDataSource.limpiarTasksCompletados();

        //hacer en la memoria cache update para mantener el UI del app actualizado
        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String,Task>> eso = mCachedTasks.entrySet().iterator();
        while(eso.hasNext()){
            Map.Entry<String,Task> entrada = eso.next();
            if(entrada.getValue().isCompletado()){
                eso.remove();
            }
        }
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
            mCachedTasks.put(task.getId(),task);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        mTasksLocalDataSource.elimiarTodoTasks();
        for(Task task : tasks){
            mCachedTasks.put(task.getId(),task);
        }
        mCacheIsDirty = false;
    }

    @Nullable
    private Task obtenerTaskConId(@NonNull String taskId) {
        checkNotNull(taskId);
        if(mCachedTasks==null || mCachedTasks.isEmpty()){
            return null;
        }else{
            return mCachedTasks.get(taskId);
        }
    }
}
