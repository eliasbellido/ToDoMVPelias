package com.beyondthecode.todomvpelias.tasks;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/*
* Extiende SwipeRefreshLayout para soportar el scroll descendiente de los views
*
* SwipeRefreshLayout trabaja como lo esperado cuando un scroll view es un child directo: activa
* el refresh solo cuando la vista esta en el top. Esta clase agrega una forma a setScrollUpChild para
* definir que vsta controla este comportamiento
* */
public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout{

    private View mScrollUpChild;

    public ScrollChildSwipeRefreshLayout(Context context) {
        super(context);

    }

    public ScrollChildSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean canChildScrollUp() {
        if(mScrollUpChild !=null){
            return ViewCompat.canScrollVertically(mScrollUpChild,-1);

        }
        return super.canChildScrollUp();
    }

    public void setmScrollUpChild(View view) {
        this.mScrollUpChild = view;
    }
}
