package com.ccbfm.dice.flat;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ccbfm.dice.R;

public class DiceSpinnerAdapter extends ArrayAdapter<String> {

    public DiceSpinnerAdapter(Context context, String[] numbers) {
        super(context, R.layout.simple_spinner_item, numbers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return setCentered(super.getView(position, convertView, parent));
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return setCentered(super.getDropDownView(position, convertView, parent));
    }

    private View setCentered(View view) {
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setGravity(Gravity.CENTER);
        return view;
    }
}
