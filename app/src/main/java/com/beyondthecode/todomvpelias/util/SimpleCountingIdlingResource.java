package com.beyondthecode.todomvpelias.util;

/*
* Una simple implementación decontador del IdlingResource que determina el idleness
* por mantener un contador interno. Cuando el contador es 0 - es considerado que está
* en estado idle, cuando es diferente a zero, no está en idle. Es muy similar al compartamiento que va
* java.util.concurrent.Semaphore.
*
* Esta clase puede luego ser usada para juntar operaciones que mientras están en progreso
* deberian bloquear tests en acceder a la UI*/

import android.support.test.espresso.IdlingResource;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleCountingIdlingResource implements IdlingResource{

    private final String mRecursoNombre;

    private final AtomicInteger contador = new AtomicInteger(0);

    //escrito desde el main thread, leido desde cualquier thread
    private volatile ResourceCallback recursoCallback;

    /*
    * crea un simple contador IdlingResource
    *
    * el parametro recursoNombre, este recurso deberia reporta al Espresso*/
    public SimpleCountingIdlingResource(String recursoNombre){
        mRecursoNombre = checkNotNull(recursoNombre);
    }
    @Override
    public String getName() {
        return mRecursoNombre;
    }

    @Override
    public boolean isIdleNow() {
        return contador.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback recursoCallback) {
        this.recursoCallback = recursoCallback;
    }

    /*
    * Incrementa el contador de transacciones en curso al recurso que esta siendo monitoreado.
    */
    public void incrementar(){
        contador.getAndIncrement();
    }

    /*
    Decrementa el contador de transacciones en curso al recurso que esta siendo monitoreado.

    Si la operacion resulta en el contador cayendo debajo de 0 - una excepcion se levanta.
    *
    * @throws IllegalStateException si el contador es debajo de 0.*/
    public void decrementar(){
        int contadorVal = contador.decrementAndGet();
        if(contadorVal == 0){
            //hemos ido de diferente de cero a cero. Significa que estamos en estado idle. Decirle a Espresso.
            if(null != recursoCallback){
                recursoCallback.onTransitionToIdle();
            }
        }

        if(contadorVal<0){
            throw new IllegalArgumentException("El contador ha sido corrompido/dañado!");
        }
    }
}
