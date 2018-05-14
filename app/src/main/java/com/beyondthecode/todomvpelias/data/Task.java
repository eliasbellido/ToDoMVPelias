package com.beyondthecode.todomvpelias.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;



import java.util.UUID;

/**
 * Clase de modelo inmutable para un Task
 */

@Entity(tableName = "tasks")
public final class Task {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entradaId")
    private final String mId;

    @Nullable
    @ColumnInfo(name = "titulo")
    private final String mTitulo;

    @Nullable
    @ColumnInfo(name = "descripcion")
    private final String mDescripcion;

    @ColumnInfo(name = "completado")
    private final boolean mCompletado;


    /*
    Usar este constructor para especificar un task completado si el task ya tiene un id
    (copia de otro task)
    * */

    public Task(@Nullable String titulo, @Nullable String descripcion, @NonNull String id, boolean completado){

        mId = id;
        mTitulo = titulo;
        mDescripcion = descripcion;
        mCompletado = completado;

    }

    /*
    * Usar este constructor para crear un nuevo finalizado o completado Task.
    * */
    @Ignore
    public Task(@Nullable String titulo, @Nullable String descripcion, boolean completado){
        this(titulo,descripcion,UUID.randomUUID().toString(),completado);
    }

    /*
    * Usar este constructor para crear un task activo si el task ya tiene un id
    * (copia de otro task)
    * */
    @Ignore
    public Task(@Nullable String titulo, @Nullable String descipcion, @NonNull String id){
        this(titulo,descipcion,id,false);
    }

    /*
    * Usar este constructor para crear un nuevo activo Task
    * */

    @Ignore
    public Task(@Nullable String titulo, @Nullable String descripcion){
        this(titulo,descripcion,UUID.randomUUID().toString(),false);
    }





}
