package org.hpdroid.util;


import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by paul on 16/10/11.
 */

public class SnackHelper {


    public static void showSnack(String msg){
        ImageView imageView = new ImageView(CtxHelper.getApp());
        Snackbar snackBar = Snackbar.make(imageView, "是否撤销删除？", Snackbar.LENGTH_SHORT);
        snackBar.setAction("YES", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackBar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                Log.e("HPG","dismiss");
            }
        });
        snackBar.show();
    }

}
