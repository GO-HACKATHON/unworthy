package gohackathon.unworthy;


import android.content.ClipData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private View currFragmentView;
    private TranslateAnimation

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currFragmentView = inflater.inflate(R.layout.fragment_detail, container, false);
        currFragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getActionMasked()==MotionEvent.ACTION_MOVE){

                }
                return true;
            }
        });
        return currFragmentView;
    }

}
