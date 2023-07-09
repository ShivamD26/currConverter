package com.example.currconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String baseCurrency = "EUR";
    String convertedToCurrency = "USD";
    float conversionRate = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerSetup();
        textChangedStuff();
    }

    private void textChangedStuff() {
        EditText et_firstConversion = findViewById(R.id.et_firstConversion);
        et_firstConversion.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    getApiResult();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Type a value", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Main", "Before Text Changed");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("Main", "OnTextChanged");
            }
        });
    }

    private void getApiResult() {
        EditText et_firstConversion = findViewById(R.id.et_firstConversion);
        EditText et_secondConversion = findViewById(R.id.et_secondConversion);

        if (et_firstConversion != null && !et_firstConversion.getText().toString().isEmpty() &&
                !et_firstConversion.getText().toString().trim().isEmpty()) {

            //String API = "https://api.ratesapi.io/api/latest?base=" + baseCurrency + "&symbols=" + convertedToCurrency;
            String on = "https://free.currconv.com/api/v7/convert?q="+baseCurrency+"_"+convertedToCurrency+"&compact=ultra&apiKey=a5f60fb6663d1a79e9a7";
            if (baseCurrency.equals(convertedToCurrency)) {
                Toast.makeText(getApplicationContext(), "Please pick a currency to convert", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> {
                    try {
                        URL url = new URL(on);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.connect();

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = bufferedReader.readLine()) != null) {
                            response.append(inputLine);
                        }
                        bufferedReader.close();

                        JSONObject jsonObject = new JSONObject(response.toString());
                        conversionRate = Float.parseFloat(jsonObject.getJSONObject("rates").getString(convertedToCurrency));

                        Log.d("Main", String.valueOf(conversionRate));
                        Log.d("Main", response.toString());

                        runOnUiThread(() -> {
                            String text = String.valueOf(Float.parseFloat(et_firstConversion.getText().toString()) * conversionRate);
                            et_secondConversion.setText(text);
                        });
                    } catch (Exception e) {
                        Log.e("Main", e.toString());
                    }
                }).start();
            }
        }
    }

    private void spinnerSetup() {
        Spinner spinner = findViewById(R.id.spinner_firstConversion);
        Spinner spinner2 = findViewById(R.id.spinner_secondConversion);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.currencies2, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                baseCurrency = parent.getItemAtPosition(position).toString();
                getApiResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convertedToCurrency = parent.getItemAtPosition(position).toString();
                getApiResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }


}


