package com.beyondthecode.todomvpelias.util;

import android.support.test.espresso.IdlingResource;

/*
* Contiene una referencia statica a IdlingResource, solo disponible en el
* tipo de build 'mock'.*/
public class EspressoIdlingResource {

    private static final String RECURSO = "GLOBAL";

    private static  SimpleCountingIdlingResource mCountingIdlingResource =
            new SimpleCountingIdlingResource(RECURSO);

    public static void incrementar(){
        mCountingIdlingResource.incrementar();
    }

    public static  void decrementar(){
        mCountingIdlingResource.decrementar();
    }

    public static IdlingResource obtenerIdlingRecurso(){
        return mCountingIdlingResource;
    }
}
