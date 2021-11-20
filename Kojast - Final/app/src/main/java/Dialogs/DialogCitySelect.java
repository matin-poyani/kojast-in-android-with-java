package Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import Adapters.AdapterSpinnerCity;
import Adapters.AdapterSpinnerState;
import Async.AsyncCities;
import Async.AsyncStates;
import Models.StructCity;
import Models.StructState;
import activities.MainActivity;
import ir.ncis.kojast.App;
import ir.ncis.kojast.R;

public class DialogCitySelect extends Dialog {
    private AdapterSpinnerCity adapterCity;
    private AdapterSpinnerState adapterState;

    public DialogCitySelect(@NonNull Context context) {
        super(context);
    }

    public DialogCitySelect(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_city_select);
        setCancelable(false);

        AppCompatButton btnSelect = findViewById(R.id.btnSelect);
        final AppCompatSpinner spnCity = findViewById(R.id.spnCity);
        final AppCompatSpinner spnState = findViewById(R.id.spnState);
        final ProgressBar prgLoading = findViewById(R.id.prgLoading);
        final TextView txtCity = findViewById(R.id.txtCity);

        adapterCity = new AdapterSpinnerCity(getContext(), R.layout.adapter_simple_spinner, new ArrayList<StructCity>());
        spnCity.setAdapter(adapterCity);

        adapterState = new AdapterSpinnerState(getContext(), R.layout.adapter_simple_spinner, new ArrayList<StructState>());
        spnState.setAdapter(adapterState);

        new AsyncStates()
                .setOperations(new AsyncStates.Operations() {
                    @Override
                    public void before() {
                    }

                    @Override
                    public void failure(String error) {
                        App.toast(error);
                    }

                    @Override
                    public void after(ArrayList<StructState> result) {
                        adapterState.states.clear();
                        adapterState.states.addAll(result);
                        adapterState.notifyDataSetChanged();
                        spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                StructState state = (StructState) adapterView.getItemAtPosition(i);
                                new AsyncCities()
                                        .setOperations(new AsyncCities.Operations() {
                                            @Override
                                            public void before() {
                                                txtCity.setVisibility(View.GONE);
                                                spnCity.setVisibility(View.GONE);
                                                prgLoading.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void failure(String error) {
                                                txtCity.setVisibility(View.VISIBLE);
                                                spnCity.setVisibility(View.VISIBLE);
                                                prgLoading.setVisibility(View.GONE);
                                                App.toast(error);
                                            }

                                            @Override
                                            public void after(ArrayList<StructCity> result) {
                                                adapterCity.cities.clear();
                                                adapterCity.cities.addAll(result);
                                                adapterCity.notifyDataSetChanged();
                                                txtCity.setVisibility(View.VISIBLE);
                                                spnCity.setVisibility(View.VISIBLE);
                                                prgLoading.setVisibility(View.GONE);
                                            }
                                        })
                                        .execute(state.id);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                })
                .execute();

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.cityId = ((StructCity) spnCity.getSelectedItem()).id;
                dismiss();
            }
        });
    }
}
