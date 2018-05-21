package com.beyondthecode.todomvpelias.tasks;

/*
* usado con un spinner de filtro en la lista de tasks*/
public enum TasksFiltroTipo {

    //no filtrar tasks
    TASKS_TODOS,

    //Filtrar solo los activos tasks(no los completados aun)
    TASKS_ACTIVOS,

    //Filtrar solo los tasks completados
    TASKS_COMPLETADOS
}
