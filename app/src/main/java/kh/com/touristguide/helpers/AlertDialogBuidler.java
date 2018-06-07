package kh.com.touristguide.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.ArrayAdapter;


public class AlertDialogBuidler extends AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener {

    private boolean[] checkedItems = null;
    private String[] items = null;

    private ArrayAdapter<String> adapter;

    public AlertDialogBuidler(Context context) {
        super(context);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Please select!!!");
        builder.setMultiChoiceItems(items, checkedItems, this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
        return super.performClick();
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        checkedItems[which] = isChecked;
    }

    public void setItems(String[] items){
        this.items = items;
    }

}
