package com.example.cpimedanprod01.rlmedan_app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class materialusage extends Fragment {
    View rootView;
    static SurfaceView surfaceView;
    static BarcodeDetector barcodeDetector;
    static CameraSource cameraSource;
    static final int REQUEST_CAMERA_PERMISSION = 201;
    static ToneGenerator toneGen1;

    static TextView barcodeText,totalpakai,hargaterima,tglterima2,jlhterima2,namabrg2,ipadd2,idmat2;
    static String barcodeData;
    static Button btnbarc;
    static TextView textusage;
    static String ipadd,usernm;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_materialusage, container, false);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,     100);
        surfaceView = rootView.findViewById(R.id.surface_view2);
        barcodeText = rootView.findViewById(R.id.barcode_text2);
        btnbarc = rootView.findViewById(R.id.btnbarcode2);

        textusage = rootView.findViewById(R.id.total);
        hargaterima = rootView.findViewById(R.id.harga);
        tglterima2 = rootView.findViewById(R.id.tglterima);
        jlhterima2 = rootView.findViewById(R.id.jlhterima);
        namabrg2 = rootView.findViewById(R.id.namamaterial);
        ipadd2 = rootView.findViewById(R.id.ipaddress2);
        idmat2 = rootView.findViewById(R.id.idmat);
        textusage.setText("0");
        tglterima2.setText("-");
        jlhterima2.setText("-");
        hargaterima.setText("-");

        btnbarc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addmaterialusage();
            }
        });
        btnbarc.setEnabled(true);

        ipadd = getArguments().getString("ipaddress");
        usernm = getArguments().getString("user");
        ipadd2.setText(ipadd);
        return rootView;
    }

    private void addmaterialusage(){
        final String idbarang=idmat2.getText().toString().trim();
        final String total=textusage.getText().toString().trim();
        final String tglterima=tglterima2.getText().toString().trim();
        final String jlhterima=jlhterima2.getText().toString().trim();
        final String harga=hargaterima.getText().toString().trim();

        class Addmaterialusage1 extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(materialusage.this.getContext(),"Loading MATERIAL USAGE...","Waiting...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                if(s==""){
                    super.onPostExecute(s);
                    loading.dismiss();
                    String txt ="KONEKSI TERPUTUS! COBALAH BEBERAPA SAAT LAGI";
                    SpannableStringBuilder biggerText = new SpannableStringBuilder(txt);
                    biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, txt.length(), 0);
                    Toast.makeText(materialusage.this.getContext(), biggerText, Toast.LENGTH_LONG).show();
                }
                else {
                    super.onPostExecute(s);
                    loading.dismiss();
                    //Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
                    SpannableStringBuilder biggerText = new SpannableStringBuilder(s);
                    biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, s.length(), 0);
                    Toast.makeText(materialusage.this.getContext(), biggerText, Toast.LENGTH_LONG).show();
                    barcodeText.setText("");
                    textusage.setText("0");
                    tglterima2.setText("");
                    jlhterima2.setText("");
                    hargaterima.setText("");
                    namabrg2.setText("");
                    idmat2.setText("");
                    btnbarc.setEnabled(false);
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("idbarang",idbarang);
                params.put("totalusage",total);
                params.put("tglterima",tglterima);
                params.put("jlhterima",jlhterima);
                params.put("harga",harga);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest("http://"+ipadd2.getText().toString().trim()+"/"+konfigurasi.URLaddmaterial, params);
                return res;
            }
        }
        Addmaterialusage1 ae = new Addmaterialusage1();
        ae.execute();
    }

    public final void initialiseDetectorsAndSources() {
        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this.getContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this.getContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(materialusage.this.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeText.post(new Runnable() {
                        @Override
                        public void run() {
                        if (barcodes.valueAt(0).email != null) {
                            barcodeText.removeCallbacks(null);
                            barcodeData = barcodes.valueAt(0).email.address;
                            barcodeText.setText(barcodeData);
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            btnbarc.setEnabled(true);
                        } else {

                            barcodeData = barcodes.valueAt(0).displayValue;
                            barcodeText.setText(barcodeData);
                            ontagreader(barcodeText.getText().toString().trim());
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            btnbarc.setEnabled(true);
                        }
                        }
                    });
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
    @Override
    public void onPause() {
        super.onPause();
        cameraSource.release();
    }

    public void ontagreader(String idmaterial) {
        final String idlap2 = idmaterial;
        class ReadTag extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(materialusage.this.getContext(), "Loading...", "Waiting...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                if (s == "") {
                    super.onPostExecute(s);
                    loading.dismiss();
                    String txt = "KONEKSI TERPUTUS! COBALAH BEBERAPA SAAT LAGI!";
                    SpannableStringBuilder biggerText = new SpannableStringBuilder(txt);
                    biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, txt.length(), 0);
                    Toast.makeText(materialusage.this.getContext(), biggerText, Toast.LENGTH_LONG).show();
                    tglterima2.setText("-");
                    jlhterima2.setText("-");
                    hargaterima.setText("-");
                    namabrg2.setText("-");
                    idmat2.setText("");
                } else {
                    super.onPostExecute(s);
                    loading.dismiss();
                    //String txt = "Data ditemukan...";
                    //SpannableStringBuilder biggerText = new SpannableStringBuilder(txt);
                   // biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, txt.length(), 0);
                   // Toast.makeText(materialusage.this.getContext(), biggerText, Toast.LENGTH_LONG).show();
                    try {
                        JSONArray array1 = new JSONObject(s).getJSONArray("result");
                        for (int i = 0; i < array1.length(); i++) {
                            JSONObject object = array1.getJSONObject(i);
                            if (object.isNull("tglterima")) {
                                tglterima2.setText("-");
                            } else {
                                tglterima2.setText(object.getString("tglterima"));
                            }

                            if (object.isNull("jlhterima")) {
                                jlhterima2.setText("-");
                            } else {
                                jlhterima2.setText(object.getString("jlhterima"));
                            }

                            if (object.isNull("harga")) {
                                hargaterima.setText("-");
                            } else {
                                hargaterima.setText(object.getString("harga"));
                            }

                            if (object.isNull("namabrg")) {
                                namabrg2.setText("-");
                            } else {
                                namabrg2.setText(object.getString("namabrg").trim());
                            }

                            if (object.isNull("idbarang")) {
                                idmat2.setText("-");
                            } else {
                                idmat2.setText(object.getString("idbarang").trim());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            protected String doInBackground (Void...v){
                HashMap<String, String> params = new HashMap<>();
                params.put("idmaterial", idlap2);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest("http://"+ipadd2.getText().toString().trim()+"/" + konfigurasi.URLselectmaterial, params);
                return res;
            }
        }
        ReadTag ae = new ReadTag();
        ae.execute();
    }
}
