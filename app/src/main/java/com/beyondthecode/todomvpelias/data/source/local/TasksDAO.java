package com.beyondthecode.todomvpelias.data.source.local;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.beyondthecode.todomvpelias.data.Task;

import java.util.List;

/*
 * Data access Object para la tabla de tasks*/
@Dao
public interface TasksDAO {

    /*
     * Select todas las tareas desde la tabla de tasks
     *
     * @retorna todas las tareas*/
    @Query("SELECT * FROM Tasks")
    List<Task> obtenerTasks();


    /*
     * Select un task por id*/
    @Query("SELECT * FROM Tasks WHERE entradaId = :taskId")
    Task obtenerTaskPorId(String taskId);

    /*
     * Insert un task a la bd. Si el task ya existe, reemplazarlo*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarTask(Task task);

    /*
     * Update un task
     * @return el numero de tasks actualizados. Siempre debería ser 1*/
    @Update
    int actualizarTask(Task task);

    /*
     * Update el status completo de un task*/
    @Query("UPDATE tasks SET completado = :completado WHERE entradaId = :taskId")
    void actualizarCompletado(String taskId, boolean completado);

    /*
     * eliminar un task por id
     * @return el numero de tasks eliminados. Siempre deberia ser 1*/
    @Query("DELETE FROM tasks WHERE entradaId = :taskId")
    int eliminarTaskPorId(String taskId);

    /*
     * Eliminar todos los tasks*/
    @Query("DELETE FROM tasks")
    void eliminarTasks();

    /*
     * Elimina todos los tasks completado de la tabla
     * @return el numero de tasks eliminados*/
    @Query("DELETE FROM tasks WHERE completado = 1")
    int eliminarTasksCompletados();
}
