package bitcoinumrechner.sabel.com.bitcoinumrechner;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    private EditText et_euro, et_bitcoin;
    private Button btn_umrechnen;
    private double faktorBitcoinKursInEuro = 8919.0;
    private boolean euroLock;
    private boolean bitcoinLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                if(!euroLock) {
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
                if(!bitcoinLock) {
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
                if(et_euro.getText().toString().length() > 0){
                    double euro = Double.parseDouble(et_euro.getText().toString());
                    double ergebnis = euroBitcoinUmrechnen(euro);
                    et_bitcoin.setText(String.valueOf(ergebnis));
                } else{
                    double bitcoin = Double.parseDouble(et_bitcoin.getText().toString());
                    double ergebnis = bitcoinEuroUmrechnen(bitcoin);
                    et_euro.setText(String.valueOf(ergebnis));
                }
            }
        });

    }

    /**
     * Rechnet Euro in Bitcoin um.
     * @param betragInEuro Betrag in Euro
     * @return betragInBitcoin
     */
    private double euroBitcoinUmrechnen(double betragInEuro){
        return betragInEuro / faktorBitcoinKursInEuro;
    }

    /**
     * Rechnet Bitcoin in Euro um.
     * @param betragInBitcoin Betrag in Bitcoin
     * @return betragInEuro
     */
    private double bitcoinEuroUmrechnen (double betragInBitcoin){
        return faktorBitcoinKursInEuro * betragInBitcoin;
    }




}
