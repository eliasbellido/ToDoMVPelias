package com.beyondthecode.todomvpelias.tasks;

import android.support.annotation.NonNull;

import com.beyondthecode.todomvpelias.data.Task;
import com.beyondthecode.todomvpelias.data.source.TasksDataSource;
import com.beyondthecode.todomvpelias.data.source.TasksRepository;
import com.beyondthecode.todomvpelias.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/*
* Escucha las acciones del usuario desde la UI (TasksFragment), recupera la data y actualiza
* la UI requerida*/
public class TasksPresenter implements TasksContract.Presenter{

    private final TasksRepository mTasksRepository;

    private final TasksContract.View mTasksView;

    private TasksFiltroTipo mFiltroActual = TasksFiltroTipo.TASKS_TODOS;

    private boolean mPrimeraCarga = true;

    public TasksPresenter(@NonNull TasksRepository mTasksRepository, @NonNull TasksContract.View mTasksView) {

        this.mTasksRepository = checkNotNull(mTasksRepository,"taskRepositoyry no puede ser nulo");
        this.mTasksView = checkNotNull(mTasksView,"taskView no pueede ser nulo!");

        this.mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
        cargarTasks(false);
    }

    @Override
    public void resultado(int requestCode, int resultCode) {
        //Si el task fue agregado exitosamente, mostrar snackbar
        mTasksView.mostrarMensajeGrabadoExitosamente();
    }

    @Override
    public void cargarTasks(boolean forzarUpdate) {
        //Simplificacion de ejemplo: una red recargada será forzada en la primera carga.
        cargarTasks(forzarUpdate || mPrimeraCarga, true);
    }

    /*
    * forzarUpdate, pasa true para refresh la data en el TasksDataSource
    * mostrarUICarando, pasa true para mostrar un icono de cargando en la UI
    * */
    public void cargarTasks(boolean forzarUpdate, final boolean mostrarUICargando){
        if(mostrarUICargando){
            mTasksView.setCargandoIndicador(true);
        }
        if(forzarUpdate){
            mTasksRepository.refreshTasks();
        }

        //El request de red puede ser manejado en un thread diferente asi que asegurar que
        //Espresso sepa que el app esta ocupado hasta que la respuesta sea manejada.
        EspressoIdlingResource.incrementar();//El app está ocupado hasta nuevo aviso.

        mTasksRepository.obtenerTasks(new TasksDataSource.CargarTasksCallback() {
            @Override
            public void enTasksCargados(List<Task> tasks) {
                List<Task> tasksAMostrar = new ArrayList<>();

                //Este callback puede ser llamado 2 veces, la 1ra ve para el cache y el 2do
                //para cargar la data desde el server API, por lo que revisamos antes de decrementar,
                //de otra manera lanza la excepcion "Contador ha sido corrompido/dañado!"
                if(!EspressoIdlingResource.obtenerIdlingRecurso().isIdleNow()){
                    EspressoIdlingResource.decrementar(); //set el app como idle.
                }

                //filtramos los tasks basados en el requestTipo
                for(Task task : tasks){
                    switch (mFiltroActual){
                        case TASKS_TODOS:
                            tasksAMostrar.add(task);
                            break;
                        case TASKS_ACTIVOS:
                            if(task.esActivo()){
                                tasksAMostrar.add(task);
                            }
                            break;
                        case TASKS_COMPLETADOS:
                            if(task.esCompletado()){
                                tasksAMostrar.add(task);
                            }
                            break;
                        default:
                            tasksAMostrar.add(task);
                            break;
                    }
                }
                //El view puede ya no ser capaz de manejar los updates del UI
                if(!mTasksView.esActivo()){
                    return;
                }
                if(mostrarUICargando){
                    mTasksView.setCargandoIndicador(false);
                }

                procesarTasks(tasksAMostrar);


            }

            @Override
            public void enDataNoDisponible() {
                //El view puede ya no ser capaz de manejar los updates del UI
                if(!mTasksView.esActivo()){
                    return;
                }
                mTasksView.mostrarCargandoTasksError();

            }
        });


    }

    private void procesarTasks(List<Task> tasks) {
        if(tasks.isEmpty()){
            //mostrar un mensaje indicado que no hay tasks para ese tipo de filtro.
            procesarTasksVacios();
        }else{
            //mostrar la lista de tasks
            mTasksView.mostrarTasks(tasks);

            //set el texto de filtro del label
            mostrarLabelFiltro();
        }
    }

    private void mostrarLabelFiltro() {
        switch (mFiltroActual){
            case TASKS_ACTIVOS:
                mTasksView.mostrarFiltroActivoLabel();
                break;
            case TASKS_COMPLETADOS:
                mTasksView.mostrarFiltroCompletadoLabel();
                break;
            default:
                mTasksView.mostrarTodoFiltroLabel();
                break;
        }
    }

    private void procesarTasksVacios() {
        switch (mFiltroActual){
            case TASKS_ACTIVOS:
                mTasksView.mostrarTasksNoActivos();
                break;
            case TASKS_COMPLETADOS:
                mTasksView.mostrarTasksNoCompletados();
                break;
            default:
                mTasksView.mostrarNoTasks();
                break;
        }
    }

    @Override
    public void agregarNuevoTask() {
        mTasksView.mostrarAgregarTask();
    }

    @Override
    public void abrirTaskDetalles(@NonNull Task requestedTask) {
        checkNotNull(requestedTask,"requestedTask no puede ser nulo!");
        mTasksView.mostrarTaskDetallesUi(requestedTask.getId());
    }

    @Override
    public void completarTask(@NonNull Task taskCompletado) {
        checkNotNull(taskCompletado,"taskCompletado no puede ser nulo!");
        mTasksRepository.completarTask(taskCompletado);
        mTasksView.mostrarTaskMarcadoCompleto();
        cargarTasks(false,false);
    }

    @Override
    public void activarTask(@NonNull Task taskActivo) {
        checkNotNull(taskActivo,"taskActivo no puede ser nulo!");
        mTasksRepository.activarTask(taskActivo);
        mTasksView.mostrarTaskMarcadoActivo();
        cargarTasks(false,false);

    }

    @Override
    public void limpiarTasksCompletados() {
        mTasksRepository.limpiarTasksCompletados();
        mTasksView.mostrarCompletadoTasksLimpiados();
        cargarTasks(false,false);
    }

    /*
    * sets el task actual filtrando el tipo*/
    @Override
    public void setFiltro(TasksFiltroTipo requestTipo) {
        mFiltroActual = requestTipo;
    }

    @Override
    public TasksFiltroTipo obtenerFiltro() {
        return mFiltroActual;
    }


}
