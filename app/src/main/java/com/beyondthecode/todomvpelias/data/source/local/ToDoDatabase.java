package com.beyondthecode.todomvpelias.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.beyondthecode.todomvpelias.data.Task;

/*
 *El database de Room que contiene la tabla Task */
@Database(entities = {Task.class},version=1)
public abstract class ToDoDatabase extends RoomDatabase{

    private static ToDoDatabase INSTANCIA;

    public abstract TasksDAO tasksDAO();

    private static final Object sLock = new Object();

    public static ToDoDatabase obtenerInstancia(Context context){

        synchronized (sLock){
            if(INSTANCIA == null){
                INSTANCIA = Room.databaseBuilder(context.getApplicationContext(),
                        ToDoDatabase.class, "Tasks.db")
                        .build();
            }
            return INSTANCIA;
        }
    }
}
