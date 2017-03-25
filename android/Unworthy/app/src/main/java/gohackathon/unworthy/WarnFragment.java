package gohackathon.unworthy;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WarnFragment extends Fragment {

    private View currWebView;

    public WarnFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currWebView = inflater.inflate(R.layout.fragment_warn, container, false);
        WebView img = (WebView) currWebView.findViewById(R.id.imgWarn);
        img.loadUrl("file:///android_asset/imgWarn.html");
        img.setVerticalScrollBarEnabled(false);
        img.setHorizontalScrollBarEnabled(false);
        return currWebView;
    }

}
