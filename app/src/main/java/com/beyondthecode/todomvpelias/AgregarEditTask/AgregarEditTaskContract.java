package com.beyondthecode.todomvpelias.AgregarEditTask;

import com.beyondthecode.todomvpelias.BasePresenter;
import com.beyondthecode.todomvpelias.BaseView;

/**
 * Created by jcollantes on 14/05/2018.
 */

public interface AgregarEditTaskContract {

    interface View extends BaseView<Presenter>{

        void mostrarVacioTaskError();
        void mostrarTaskList();
        void setTitulo(String titulo);
        void setDescripcion(String descripcion);
        boolean esActivo();

    }

    interface Presenter extends BasePresenter{

        void grabarTask(String titulo, String descripcion);
        void llenarTask();
        boolean esDataFaltante();

    }
}
