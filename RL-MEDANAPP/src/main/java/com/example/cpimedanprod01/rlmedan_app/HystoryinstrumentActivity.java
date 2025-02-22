package com.example.cpimedanprod01.rlmedan_app;

import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class HystoryinstrumentActivity extends AppCompatActivity {

    static SurfaceView surfaceView;
    static BarcodeDetector barcodeDetector;
    static CameraSource cameraSource;
    static final int REQUEST_CAMERA_PERMISSION = 201;
    static ToneGenerator toneGen1;
    static TextView barcodeText;
    static String barcodeData;

    static Button btnbarc,btnmtn;
    static TextView textusage;
    static String ipadd,usernm;
    static TextView result;
    static RadioGroup radioGroup;
    static RadioButton radiobtn,okeck,rusakck;
    static String selectedRbText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hystoryinstrument);
    }
}
