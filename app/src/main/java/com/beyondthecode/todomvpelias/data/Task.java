package com.beyondthecode.todomvpelias.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.google.common.base.Objects;
import com.google.common.base.Strings;

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

    @NonNull
    public String getmId() {
        return mId;
    }

    @Nullable
    public String getmTitulo() {
        return mTitulo;
    }

    @Nullable
    public String getmDescripcion() {
        return mDescripcion;
    }

    public boolean ismCompletado() {
        return mCompletado;
    }

    @Nullable
    public String getTituloParaLista(){
        if (!Strings.isNullOrEmpty(mTitulo)){
            return mTitulo;
        }else{
            return mDescripcion;
        }
    }

    public boolean esActivo(){
        return !mCompletado;
    }

    public boolean esVacio(){
        return Strings.isNullOrEmpty(mTitulo) && Strings.isNullOrEmpty(mDescripcion);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if(obj == null || getClass() != obj.getClass()) return false;

        Task task = (Task)obj;

        return Objects.equal(mId, task.mId) &&
                Objects.equal(mTitulo, task.mTitulo) &&
                Objects.equal(mDescripcion, task.mDescripcion);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId,mTitulo,mDescripcion);
    }

    @Override
    public String toString() {
        return "Task con titulo " + mTitulo;
    }
}
