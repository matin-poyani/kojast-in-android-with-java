package Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Models.StructState;
import ir.ncis.kojast.App;
import ir.ncis.kojast.R;

public class AdapterSpinnerState extends ArrayAdapter<StructState> {
    public ArrayList<StructState> states;

    public AdapterSpinnerState(Context context, int resourceId, ArrayList<StructState> states) {
        super(context, resourceId, states);
        this.states = states;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getRowView(position, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getRowView(position, parent);
    }

    private View getRowView(int position, ViewGroup parent) {
        StructState state = states.get(position);
        View view = App.INFLATER.inflate(R.layout.adapter_simple_spinner, parent, false);
        TextView txtName = view.findViewById(R.id.txtName);
        txtName.setText(state.name);
        return view;
    }
}
