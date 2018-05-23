package com.beyondthecode.todomvpelias.detalletask;

import com.beyondthecode.todomvpelias.BasePresenter;
import com.beyondthecode.todomvpelias.BaseView;

public interface DetalleTaskContract {

    interface View extends BaseView<Presenter>{

        void setCargandoIndicador(boolean activo);

        void mostrarFaltaTask();

        void ocultarTitulo();

        void mostrarTitulo(String titulo);

        void ocultarDescripcion();

        void mostrarDescripcion(String descripcion);

        void mostrarEstadoCompletado(boolean completo);

        void mostrarTaskEditar(String taskId);

        void mostrarTaskEliminado();

        void mostrarTaskMarcadoCompleto();

        void mostrarTaskMarcadoActivo();

        boolean esActivo();

    }
    interface Presenter extends BasePresenter{

        void editarTask();

        void eliminarTask();

        void completarTask();

        void activarTask();
    }
}
