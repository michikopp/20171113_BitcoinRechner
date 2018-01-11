package bitcoinumrechner.sabel.com.bitcoinumrechner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private EditText et_euro, et_bitcoin;
    private Button btn_umrechnen, btn_kurs;
    private double faktorBitcoinKursInEuro;
    private boolean euroLock;
    private boolean bitcoinLock;
    private TextView tv_aktuellerKurs;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //SharedPreferences zum Speichern und laden von Schlüssel-Werte-Paaren
    //Schlüssel -> Wert
    //bitcoinkurs -> 11_111.75



    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Map<String, Double> map = new HashMap<>();
//        map.put("bitcoinkurs", 11111.0);
//        Double bitcoinkurs = map.get("bitcoinkurs");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        faktorBitcoinKursInEuro = sharedPreferences.getFloat(getString(R.string.bitcoinkurs), 0);
        System.out.println(faktorBitcoinKursInEuro);
        et_euro = findViewById(R.id.et_euro);
        et_bitcoin = findViewById(R.id.et_bitcoin);
        btn_umrechnen = findViewById(R.id.btn_umrechnen);
        euroLock = false;
        bitcoinLock = false;

        //et_bitcoin.setEnabled(false);
        et_euro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!euroLock) {
                    // System.out.println("Euro: Start: " + start + " Before: " + before + " Count: " + count);
                    try {
                        double euro = Double.parseDouble(charSequence.toString());
                        double ergebnis = euroBitcoinUmrechnen(euro);
                        bitcoinLock = true;
                        et_bitcoin.setText(String.valueOf(ergebnis));
                        bitcoinLock = false;
                    } catch (NumberFormatException e) {
                        bitcoinLock = true;
                        et_bitcoin.setText("");
                        bitcoinLock = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_bitcoin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //  System.out.println("Bitcoin: Start: " + start + " Before: " + before+ " Count: "+ count);
                if (!bitcoinLock) {
                    try {
                        double bitcoin = Double.parseDouble(charSequence.toString());
                        double ergebnis = bitcoinEuroUmrechnen(bitcoin);
                        euroLock = true;
                        et_euro.setText(String.valueOf(ergebnis));
                        euroLock = false;
                    } catch (NumberFormatException e) {
                        euroLock = true;
                        et_euro.setText("");
                        euroLock = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        btn_umrechnen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (et_euro.getText().toString().length() > 0) {
                    double euro = Double.parseDouble(et_euro.getText().toString());
                    double ergebnis = euroBitcoinUmrechnen(euro);
                    et_bitcoin.setText(String.valueOf(ergebnis));
                } else {
                    double bitcoin = Double.parseDouble(et_bitcoin.getText().toString());
                    double ergebnis = bitcoinEuroUmrechnen(bitcoin);
                    et_euro.setText(String.valueOf(ergebnis));
                }
            }
        });

        btn_kurs = findViewById(R.id.bnt_kurs);
        btn_kurs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new Thread(new MeinKursThread()).start();
                MyDownloadThread myDownloadThread = new MyDownloadThread();
                myDownloadThread.execute();
            }
        });

        tv_aktuellerKurs = findViewById(R.id.tv_kurs);
        new MyDownloadThread().execute();
    }

    /**
     * Rechnet Euro in Bitcoin um.
     *
     * @param betragInEuro Betrag in Euro
     * @return betragInBitcoin
     */
    private double euroBitcoinUmrechnen(double betragInEuro) {
        return betragInEuro / faktorBitcoinKursInEuro;
    }

    /**
     * Rechnet Bitcoin in Euro um.
     *
     * @param betragInBitcoin Betrag in Bitcoin
     * @return betragInEuro
     */
    private double bitcoinEuroUmrechnen(double betragInBitcoin) {
        return faktorBitcoinKursInEuro * betragInBitcoin;
    }


    public class MeinKursThread implements Runnable {

        @Override
        public void run() {
            InputStream inputStream = null;
            URLConnection urlConnection = null;
            try {
                URL url = new URL("https://bitaps.com/api/ticker/average");
                urlConnection = url.openConnection();
                inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String jsonZeile = null;
                String zeile= null;
                while ((zeile = bufferedReader.readLine()) != null) {
                    jsonZeile = zeile;
                }
                JSONObject jsonObject = new JSONObject(jsonZeile);
                JSONObject fx_rates = jsonObject.getJSONObject("fx_rates");
                faktorBitcoinKursInEuro = fx_rates.getDouble("eur");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }

    public class MyDownloadThread extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            URLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                url = new URL("https://bitaps.com/api/ticker/average");
                urlConnection = url.openConnection();
                inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String jsonZeile = null;
                String zeile= null;
                while ((zeile = bufferedReader.readLine()) != null) {
                    jsonZeile = zeile;
                }
                JSONObject jsonObject = new JSONObject(jsonZeile);
                JSONObject fx_rates = jsonObject.getJSONObject("fx_rates");
                faktorBitcoinKursInEuro = fx_rates.getDouble("eur");
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            sharedPreferences = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putFloat("bitcoinkurs", (float) faktorBitcoinKursInEuro);
            editor.commit();

            tv_aktuellerKurs.setText(getString(R.string.tv_akutellerKurs) + " " + faktorBitcoinKursInEuro);
        }
    }

}