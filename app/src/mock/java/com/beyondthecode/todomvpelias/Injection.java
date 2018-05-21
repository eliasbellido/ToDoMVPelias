package com.beyondthecode.todomvpelias;


import android.content.Context;
import android.support.annotation.NonNull;
import static com.google.common.base.Preconditions.checkNotNull;

import com.beyondthecode.todomvpelias.data.FalsoTasksRemoteDataSource;
import com.beyondthecode.todomvpelias.data.source.TasksRepository;
import com.beyondthecode.todomvpelias.data.source.local.TasksLocalDataSource;
import com.beyondthecode.todomvpelias.data.source.local.ToDoDatabase;
import com.beyondthecode.todomvpelias.util.AppExecutors;

/*
* Activa la injection de las implementaciones mock para TaskDataSource
* al momento de compilar. Esto es util para testing, ya que permite usar
* una instancia falsa de la clase para isolar las dependencias y correr un test hermeticamente*/
public class Injection {

    public static TasksRepository proveerTasksRepository(@NonNull Context context){
        checkNotNull(context);
        ToDoDatabase database = ToDoDatabase.obtenerInstancia(context);
        return TasksRepository.getInstancia(FalsoTasksRemoteDataSource.obtenerInstancia(),
                TasksLocalDataSource.obtenerInstancia(new AppExecutors(),
                        database.tasksDAO()));
    }
}
