package com.beyondthecode.todomvpelias.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.beyondthecode.todomvpelias.data.Task;
import com.beyondthecode.todomvpelias.data.source.TasksDataSource;
import com.beyondthecode.todomvpelias.util.AppExecutors;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

/*
 * Implementación concreate de una data source como db*/
public class TasksLocalDataSource implements TasksDataSource{

    private static volatile  TasksLocalDataSource INSTANCIA;

    private TasksDAO mTasksDAO;

    private AppExecutors mAppExecutors;

    //previene instanciación directa
    private TasksLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull TasksDAO tasksDAO){
        mAppExecutors = appExecutors;
        mTasksDAO = tasksDAO;
    }

    public static TasksLocalDataSource obtenerInstancia(@NonNull AppExecutors appExecutors,
                                                        @NonNull TasksDAO tasksDAO){
        if(INSTANCIA == null){
            synchronized (TasksLocalDataSource.class){
                if(INSTANCIA == null){
                    INSTANCIA = new TasksLocalDataSource(appExecutors,tasksDAO);
                }
            }
        }

        return INSTANCIA;
    }

    /*
     * Nota {@link CargarTasksCallback#enDataNoDisponible()} es disparado si la bd no existe
     * o la tabla está vacia*/

    @Override
    public void obtenerTasks(@NonNull final CargarTasksCallback callback) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Task> tasks = mTasksDAO.obtenerTasks();
                mAppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(tasks.isEmpty()){
                            //esto será llamado si la tabla es nueva o solo vacía
                            callback.enDataNoDisponible();
                        }else{
                            callback.enTasksCargados(tasks);
                        }
                    }
                });
            }
        };

        mAppExecutors.getDiskIO().execute(runnable);
    }

    /*
     * Nota: ObtenerTaskcallback#enDataNoDisponible() es lanzado si la clase Task
     * no es encontrada*/

    @Override
    public void obtenerTask(@NonNull final String taskId, @NonNull final ObtenerTaskCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Task task = mTasksDAO.obtenerTaskPorId(taskId);

                mAppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(task != null){
                            callback.enTaskCargado(task);
                        }else{
                            callback.enDataNoDisponible();
                        }
                    }
                });
            }
        };

        mAppExecutors.getDiskIO().execute(runnable);
    }


    @Override
    public void grabarTask(@NonNull final Task task) {

        checkNotNull(task);
        Runnable grabarRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDAO.insertarTask(task);
            }
        };
        mAppExecutors.getDiskIO().execute(grabarRunnable);


    }

    @Override
    public void completarTask(@NonNull final Task task) {
        Runnable completarRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDAO.actualizarCompletado(task.getId(),true);
            }
        };
        mAppExecutors.getDiskIO().execute(completarRunnable);
    }

    @Override
    public void completarTask(@NonNull String taskId) {
        //No es necesario para el local data source porque TasksRepository maneja
        //la conversión de un taskId a task usando su data en cache
    }

    @Override
    public void activarTask(@NonNull final Task task) {

        Runnable activarRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDAO.actualizarCompletado(task.getId(),false);
            }
        };

        mAppExecutors.getDiskIO().execute(activarRunnable);
    }

    @Override
    public void activarTask(@NonNull String taskId) {
        //No es necesario para el local data source porque TasksRepository maneja
        //la conversión de un taskId a task usando su data en cache

    }

    @Override
    public void limpiarTasksCompletados() {
        Runnable limpiarTasksRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDAO.eliminarTasksCompletados();
            }
        };
        mAppExecutors.getDiskIO().execute(limpiarTasksRunnable);
    }

    @Override
    public void refreshTasks() {
        //No es necesario porque TasksRepository maneja
        //la logica de refreshing de los tasks de todas las data sources disponibles

    }

    @Override
    public void elimiarTodoTasks() {
        Runnable eliminarRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDAO.eliminarTasks();
            }
        };
        mAppExecutors.getDiskIO().execute(eliminarRunnable);
    }

    @Override
    public void eliminarTask(@NonNull final String taskId) {
        Runnable eliminarRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDAO.eliminarTaskPorId(taskId);
            }
        };
        mAppExecutors.getDiskIO().execute(eliminarRunnable);

    }

    @VisibleForTesting
    static void limpiarInstancia(){
        INSTANCIA = null;
    }
}
