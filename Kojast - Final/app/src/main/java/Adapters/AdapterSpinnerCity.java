package Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Models.StructCity;
import Models.StructState;
import ir.ncis.kojast.App;
import ir.ncis.kojast.R;

public class AdapterSpinnerCity extends ArrayAdapter<StructCity> {
    public ArrayList<StructCity> cities;

    public AdapterSpinnerCity(Context context, int resourceId, ArrayList<StructCity> cities) {
        super(context, resourceId, cities);
        this.cities = cities;
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
        StructCity city = cities.get(position);
        View view = App.INFLATER.inflate(R.layout.adapter_simple_spinner, parent, false);
        TextView txtName = view.findViewById(R.id.txtName);
        txtName.setText(city.name);
        return view;
    }
}
