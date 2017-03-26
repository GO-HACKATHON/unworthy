package gohackathon.unworthy;


import android.content.ClipData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private View currFragmentView;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currFragmentView = inflater.inflate(R.layout.fragment_detail, container, false);
        /*currFragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getActionMasked()==MotionEvent.ACTION_MOVE){
                    int potinterIndex = MotionEventCompat.getActionIndex(motionEvent);
                    float x = MotionEventCompat.getX(motionEvent,potinterIndex);
                    float y = MotionEventCompat.getY(motionEvent,potinterIndex);
                    float dx = view.getX() - motionEvent.getRawX();
                    float dy = view.getY() - motionEvent.getRawY();
                    currFragmentView.animate().x(motionEvent.getRawX()+dx).y(motionEvent.getRawY()+dy).setDuration(0).start();
                    Log.v("StringDebuging","onTouch x:"+x+" y:"+y);
                }
                return true;
            }
        });
        */
        currFragmentView.animate().y(1875f);

        currFragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getActionMasked() == MotionEvent.ACTION_MOVE){
                    if(currFragmentView.getY()>=1080)
                        currFragmentView.animate().y(-20.f);
                }
                return true;
            }
        });

        ScrollView scrView = (ScrollView) currFragmentView.findViewById(R.id.scrollMenu);
        scrView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                Log.v("StringDebug","scroll y:"+i1);
                if(i1==0)
                    currFragmentView.animate().y(1875f);
            }
        });

        return currFragmentView;
    }

}
