package com.beyondthecode.todomvpelias.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import static com.google.common.base.Preconditions.checkNotNull;

import com.beyondthecode.todomvpelias.agregarEditTask.AgregarEditTaskActivity;
import com.beyondthecode.todomvpelias.R;
import com.beyondthecode.todomvpelias.data.Task;

import java.util.ArrayList;
import java.util.List;

/*
* muestra una grilla de tasks(clase). El usuario puede escoger para verlos todos, activos o tasks completados*/
public class TasksFragment extends Fragment implements TasksContract.View{

    private TasksContract.Presenter mPresenter;

    private TasksAdapter mListaAdapter;

    private View mNoTasksView;

    private ImageView mNoTaskIcono;

    private TextView mNoTaskMainView;

    private TextView mNoTaskAgregarView;

    private LinearLayout mTasksView;

    private TextView mFiltroLabelView;

    public TasksFragment(){
        //requiere contructor public vacio
    }

    public static TasksFragment nuevaInstancia(){
        return new TasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListaAdapter = new TasksAdapter(new ArrayList<Task>(0),mItemListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }


    @Override
    public void setPresenter(TasksContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.resultado(requestCode,resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_frag,container,false);

        //preparar el task view
        ListView listView = root.findViewById(R.id.tasks_list);
        listView.setAdapter(mListaAdapter);
        mFiltroLabelView = root.findViewById(R.id.filteringLabel); //txtview
        mTasksView = root.findViewById(R.id.tasksLL);

        //preparar no tasks view
        mNoTasksView = root.findViewById(R.id.noTasks);
        mNoTaskIcono = root.findViewById(R.id.noTasksIcon);
        mNoTaskMainView = root.findViewById(R.id.noTasksMain);
        mNoTaskAgregarView = root.findViewById(R.id.noTasksAdd);
        mNoTaskAgregarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarAgregarTask();
            }
        });

        //preparar floating action btn
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_task);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.agregarNuevoTask();
            }
        });

        //agregar indicador de progress
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = root.findViewById(
                R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(),R.color.colorPrimary),
                ContextCompat.getColor(getActivity(),R.color.colorAccent),
                ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark)
        );

        //prerara el scrolling view en el SwipeRrefreshLayout customizado
        swipeRefreshLayout.setmScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.cargarTasks(false);
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_clear:
                mPresenter.limpiarTasksCompletados();
                break;
            case R.id.menu_filter:
                mostrarFiltroMenuPopUp();
                break;
            case R.id.menu_refresh:
                mPresenter.cargarTasks(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu,menu);
    }

    @Override
    public void mostrarFiltroMenuPopUp() {
        PopupMenu popup = new PopupMenu(getContext(),getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks,popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.active:
                        mPresenter.setFiltro(TasksFiltroTipo.TASKS_ACTIVOS);
                        break;
                    case R.id.completed:
                        mPresenter.setFiltro(TasksFiltroTipo.TASKS_COMPLETADOS);
                        break;
                        default:
                            mPresenter.setFiltro(TasksFiltroTipo.TASKS_TODOS);
                            break;
                }
                mPresenter.cargarTasks(false);
                return true;
            }
        });

        popup.show();
    }

    /*
     * Listener para los clicks sobre los tasks en el ListView*/
    TaskItemListener mItemListener = new TaskItemListener() {
        @Override
        public void enTaskClic(Task taskClickeado) {
            mPresenter.abrirTaskDetalles(taskClickeado);
        }

        @Override
        public void enCompletarTaskClic(Task taskCompletado) {
            mPresenter.completarTask(taskCompletado);
        }

        @Override
        public void enActivarTaskClic(Task taskActivado) {
            mPresenter.activarTask(taskActivado);
        }
    };

    @Override
    public void setCargandoIndicador(final boolean activo) {
        if(getView()==null){
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);

        //asegurar de setRefreshing() es llamado despues que el layout termno de hacer todo lo demás.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(activo);
            }
        });
    }

    @Override
    public void mostrarTasks(List<Task> tasks) {
        mListaAdapter.reemplazaData(tasks);

        mTasksView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }

    @Override
    public void mostrarTasksNoActivos() {
        mostrarNoTasksViews(getResources().getString(R.string.no_tasks_active),
                R.drawable.ic_check_circle_24dp,
                false);
    }

    @Override
    public void mostrarNoTasks() {
        mostrarNoTasksViews(getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false);

    }

    @Override
    public void mostrarTasksNoCompletados() {
        mostrarNoTasksViews(getResources().getString(R.string.no_tasks_completed),
                R.drawable.ic_verified_user_24dp,
                false);
    }

    private void mostrarNoTasksViews(String textPrincipal, int iconRes, boolean mostrarAgregarView) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(textPrincipal);
        mNoTaskIcono.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTaskAgregarView.setVisibility(mostrarAgregarView ? View.VISIBLE : View.GONE);

    }

    @Override
    public void mostrarMensajeGrabadoExitosamente() {
        mostrarMensajeSnack(getString(R.string.successfully_saved_task_message));
    }

    private void mostrarMensajeSnack(String mensaje) {
        Snackbar.make(getView(),mensaje,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void mostrarFiltroActivoLabel() {
        mFiltroLabelView.setText(getResources().getString(R.string.label_active));
    }

    @Override
    public void mostrarFiltroCompletadoLabel() {
        mFiltroLabelView.setText(getResources().getString(R.string.label_completed));
    }

    @Override
    public void mostrarTodoFiltroLabel() {
        mFiltroLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void mostrarAgregarTask() {
        Intent intent = new Intent(getContext(), AgregarEditTaskActivity.class);
        startActivityForResult(intent, AgregarEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void mostrarTaskDetallesUi(String taskId) {
        // en su propio activity, ya que da mucho mas sentido y nos da mas flexibilidad
        //para mostrar un sustituo de Intent

        //ojo falta para llamar al otro activity

    }

    @Override
    public void mostrarTaskMarcadoCompleto() {
        mostrarMensajeSnack(getString(R.string.task_marked_complete));
    }

    @Override
    public void mostrarTaskMarcadoActivo() {
        mostrarMensajeSnack(getString(R.string.task_marked_active));
    }

    @Override
    public void mostrarCompletadoTasksLimpiados() {
        mostrarMensajeSnack(getString(R.string.completed_tasks_cleared));
    }

    @Override
    public void mostrarCargandoTasksError() {
        mostrarMensajeSnack(getString(R.string.loading_tasks_error));
    }

    @Override
    public boolean esActivo() {
        return esActivo();
    }

    private static class TasksAdapter extends BaseAdapter{

        private List<Task> mTasks;
        private TaskItemListener mItemListener;

        public TasksAdapter(List<Task> mTasks, TaskItemListener mItemListener) {
            setList(mTasks);
            this.mItemListener = mItemListener;
        }

        public void reemplazaData(List<Task> tasks){
            setList(mTasks);
            notifyDataSetChanged();
        }

        private void setList(List<Task> tasks){
            mTasks = checkNotNull(tasks);
        }


        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Task getItem(int i) {
            return mTasks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View filaView = view;
            if(filaView == null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                filaView = inflater.inflate(R.layout.task_item,viewGroup,false);
            }

            final Task task = getItem(i);

            TextView tvTitulo= filaView.findViewById(R.id.title);
            tvTitulo.setText(task.getTituloParaLista());

            CheckBox chkCompletar = filaView.findViewById(R.id.complete);

            //Activar/completado task UI
            chkCompletar.setChecked(task.esCompletado());
            if(task.esCompletado()) {
                filaView.setBackground(viewGroup.getContext().getResources().getDrawable(R.drawable.list_completed_touch_feedback)); //posible error aca, verificar luego por deprecado
            }else{
                filaView.setBackground(viewGroup.getContext().getResources().getDrawable(R.drawable.touch_feedback));

            }

            chkCompletar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!task.esCompletado()){
                        mItemListener.enCompletarTaskClic(task);
                    }else{
                        mItemListener.enActivarTaskClic(task);
                    }
                }
            });

            filaView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.enTaskClic(task);
                }
            });

            return filaView;
        }
    }

    public interface TaskItemListener{

        void enTaskClic(Task taskClickeado);

        void enCompletarTaskClic(Task taskCompletado);

        void enActivarTaskClic(Task taskActivado);
    }
}
