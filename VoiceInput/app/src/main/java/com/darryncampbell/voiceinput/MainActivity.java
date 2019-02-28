package com.darryncampbell.voiceinput;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    TextView txtProductCode;
    TextView txtQuantity;
    TextView txtDescription;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        txtProductCode = findViewById(R.id.txtProductCode);
        txtQuantity = findViewById(R.id.txtQuantity);
        txtDescription = findViewById(R.id.txtDescription);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtProductCode.setText("");
                txtQuantity.setText("");
                txtDescription.setText("");
                txtProductCode.requestFocus();
            }
        });
        txtProductCode.setOnFocusChangeListener(this);
        txtQuantity.setOnFocusChangeListener(this);
        txtDescription.setOnFocusChangeListener(this);
        btnSave.setOnFocusChangeListener(this);
        txtProductCode.requestFocus();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.txtProductCode:
                if (hasFocus)
                    SetDataWedgeConfig("voice_input",true, false, false, true);
                break;
            case R.id.txtQuantity:
                if (hasFocus)
                    SetDataWedgeConfig("voice_input",false, true, true, true);
                break;
            case R.id.txtDescription:
                if (hasFocus)
                    SetDataWedgeConfig("voice_input",false, true, false, true);
                break;
            case R.id.btnSave:
                if (hasFocus)
                    SetDataWedgeConfig("voice_input",false, true, false, false);
                break;
        }
    }

    public void SetDataWedgeConfig(String profileName, boolean bScannerInput, boolean bVoiceInput,
                                   boolean bVoiceNumericOnly, boolean setTabKey) {
        String ACTION_DATAWEDGE_FROM_6_2 = "com.symbol.datawedge.api.ACTION";
        String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";

        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", profileName);
        profileConfig.putString("PROFILE_ENABLED", "true");
        profileConfig.putString("CONFIG_MODE", "UPDATE");

        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "false");
        Bundle barcodeProps = new Bundle();
        // Can either use scanner_selection here or scanner_selection_by_identifier
        barcodeProps.putString("scanner_selection", "auto");   //  Requires DW 6.4
        if (bScannerInput)
            barcodeProps.putString("scanner_input_enabled", "true");
        else
            barcodeProps.putString("scanner_input_enabled", "false");
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SET_CONFIG, profileConfig);

        profileConfig.remove("PLUGIN_CONFIG");
        Bundle voiceConfig = new Bundle();
        voiceConfig.putString("PLUGIN_NAME", "VOICE");
        voiceConfig.putString("RESET_CONFIG", "false");
        Bundle voiceProps = new Bundle();

        if (bVoiceInput)
            voiceProps.putString("voice_input_enabled", "true");
        else
            voiceProps.putString("voice_input_enabled", "false");
        if (bVoiceNumericOnly)
            voiceProps.putString("voice_data_type", "2");
        else
            voiceProps.putString("voice_data_type", "0");
        voiceConfig.putBundle("PARAM_LIST", voiceProps);
        profileConfig.putBundle("PLUGIN_CONFIG", voiceConfig);
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SET_CONFIG, profileConfig);


        //  Set Basic Data formatting
        profileConfig.remove("PLUGIN_CONFIG");
        Bundle bdfConfig = new Bundle();
        bdfConfig.putString("PLUGIN_NAME", "BDF");
        bdfConfig.putString("RESET_CONFIG", "true");
        bdfConfig.putString("OUTPUT_PLUGIN_NAME", "KEYSTROKE");
        Bundle bdfProps = new Bundle();
        bdfProps.putString("bdf_enabled", "true");
        if (setTabKey)
            bdfProps.putString("bdf_send_tab", "true");
        else
            bdfProps.putString("bdf_send_tab", "false");
        bdfConfig.putBundle("PARAM_LIST", bdfProps);
        profileConfig.putBundle("PLUGIN_CONFIG", bdfConfig);
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SET_CONFIG, profileConfig);

    }

    private void sendDataWedgeIntentWithExtra(String action, String extraKey, Bundle extraValue)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        sendBroadcast(dwIntent);
    }

}
