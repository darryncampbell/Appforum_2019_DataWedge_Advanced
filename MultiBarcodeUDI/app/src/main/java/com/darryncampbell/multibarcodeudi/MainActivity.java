package com.darryncampbell.multibarcodeudi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String DATAWEDGE_SCAN_ACTION = "com.darryncampbell.datacapture.ACTION";
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button clearTextButton = findViewById(R.id.btnClearTextView);
        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //  todo : note, Send Enter as String required to get the carriage return working between multi barcodes
                TextView textView = findViewById(R.id.textMultiLine);
                textView.setText("");
            }
        });
        filter.addAction(DATAWEDGE_SCAN_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //  Register for broadcasts from DataWedge
        registerReceiver(dwBroadcastReceiver, filter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //  Unregister for broadcasts from DataWedge
        unregisterReceiver(dwBroadcastReceiver);
    }

    private BroadcastReceiver dwBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            for (String key : b.keySet())
            {
                Log.v("MultiBarcodeUDI", key);
            }
            if (action.equals(DATAWEDGE_SCAN_ACTION))
            {
                //  Process the scan which was received via Intent
                String decoded_mode = intent.getStringExtra("com.symbol.datawedge.decoded_mode");
                String source = intent.getStringExtra("com.symbol.datawedge.source");
                String symbology = intent.getStringExtra("com.symbol.datawedge.label_type");
                String decoded_string = intent.getStringExtra("com.symbol.datawedge.data_string");
                //  todo this is an error in the documentation - 'smart_decode_type'
                String smart_decode_type = intent.getStringExtra("com.symbol.datawedge.smart_decoded_type");
                String udi_type = intent.getStringExtra("com.symbol.datawedge.label_id");
                List<Bundle> multiple_barcodes = (List<Bundle>) intent.getSerializableExtra("com.symbol.datawedge.barcodes");
                List<Bundle> tokenized_data = (List<Bundle>) intent.getSerializableExtra("com.symbol.datawedge.tokenized_data");
                String output = "";
                output += "Decode Mode: " + decoded_mode + '\n';
                output += "Decode Source: " + source + '\n';
                output += "Decode Data: " + decoded_string + '\n';
                if (symbology != null)
                    output += "Decode Symbology: " + symbology + '\n';
                output += "Decode Type: " + smart_decode_type + '\n';
                if (multiple_barcodes != null)
                {
                    output += "Multi Barcode count: " + multiple_barcodes.size() + '\n';
                    for (int i = 0; i < multiple_barcodes.size(); i++)
                    {
                        Bundle thisBarcode = multiple_barcodes.get(i);
                        output += "Multi Barcode (" + (i+1) + ") data: " + thisBarcode.getString("com.symbol.datawedge.data_string") + '\n';
                        output += "Multi Barcode (" + (i+1) + ") symbology: " + thisBarcode.getString("com.symbol.datawedge.label_type") + '\n';

                    }
                }
                output += "UDI Type: " + udi_type + '\n';
                if (tokenized_data != null)
                {
                    output += "Tokenized data count: " + tokenized_data.size() + '\n';
                    for (int i = 0; i < tokenized_data.size(); i++)
                    {
                        Bundle thisTokenizedData = tokenized_data.get(i);
                        output += "Token (" + (i+1) + ") id:" + thisTokenizedData.getString("token_id") + '\n';
                        output += "Token (" + (i+1) + ") type:" + thisTokenizedData.getString("token_data_type") + '\n';
                        output += "Token (" + (i+1) + ") format:" + thisTokenizedData.getString("token_format") + '\n';
                        output += "Token (" + (i+1) + ") data:" + thisTokenizedData.getString("token_string_data") + '\n';
                    }
                }

                SetIntentOutputText(output);
            }
        }
    };

    private void SetIntentOutputText(String output) {
        TextView intentOutput = findViewById(R.id.txtIntentOutput);
        intentOutput.setText(output);
    }

}
