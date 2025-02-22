package com.example.cpimedanprod01.rlmedan_app;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.NavigationView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {
    SharedPreferences prf;
    public NavigationView nav_view;
    public Menu menu;
    FrameLayout frameLayout;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    Toolbar toolbars;
    View header;

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
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("BERANDA ( " + prf.getString("username",null) + " )");
        }

        setContentView(R.layout.activity_main);
        prf = getSharedPreferences("user_details",MODE_PRIVATE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        result = findViewById(R.id.user2);
        result.setText(prf.getString("username",null));
        ipadd = prf.getString("ipaddress",null);

        toolbars = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbars);
        getSupportActionBar().setTitle("Regional Lab Medan ("+ prf.getString("username",null)+")");

        dl =(DrawerLayout) findViewById(R.id.dl);
        nav_view = (NavigationView)findViewById(R.id.nav_view);
        header = nav_view.getHeaderView(0);
        frameLayout = (FrameLayout) findViewById(R.id.container);

        abdt = new ActionBarDrawerToggle(this,dl,toolbars,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();

        menu = nav_view.getMenu();
        for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
            MenuItem menuItem = menu.getItem(menuItemIndex);
        }

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.logout){
                    Toast.makeText(MainActivity.this,"logout",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginForm.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SharedPreferences.Editor editor = prf.edit();
                    editor.clear();
                    editor.commit();
                    startActivity(intent);
                }

                if(id == R.id.barcode){
                    initialiseDetectorsAndSources();
                    btnbarc.setEnabled(true);
                    dl.closeDrawer(GravityCompat.START);
                    barcodeText.setText("");
                    loadFragment(new Homefragment());
                }

                if(id == R.id.materialusage){
                    loadFragment(new materialusage());
                    dl.closeDrawer(GravityCompat.START);
                }

                if(id == R.id.cleanlinesscek){
                    loadFragment(new cekkebersihan());
                    dl.closeDrawer(GravityCompat.START);
                }
                if(id==R.id.hystory){
                    loadFragment(new HystoryPemeliharaanFrag());
                    dl.closeDrawer(GravityCompat.START);
                }

                return true;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        Bundle bundle = new Bundle();
        bundle.putString("ipaddress", ipadd);
        bundle.putString("user", result.getText().toString().trim());
        fragment.setArguments(bundle);

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, LoginForm.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SharedPreferences.Editor editor = prf.edit();
            editor.clear();
            editor.commit();
            startActivity(intent);
        }
        if (id == R.id.scanbarcode) {
            initialiseDetectorsAndSources();
            btnbarc.setEnabled(true);
            barcodeText.setText("");
            loadFragment(new Homefragment());
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,     100);
            surfaceView = rootView.findViewById(R.id.surface_view);
            barcodeText = rootView.findViewById(R.id.barcode_text);
            btnbarc = rootView.findViewById(R.id.btnbarcode2);
            textusage = rootView.findViewById(R.id.usagetxt);

            radioGroup = rootView.findViewById(R.id.groupstat);
            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            radiobtn = rootView.findViewById(selectedRadioButtonId);
            okeck = rootView.findViewById(R.id.okchc);
            rusakck = rootView.findViewById(R.id.notokechc);

            btnbarc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(barcodeText.getText().toString().isEmpty()){
                        String txt ="KODE ALAT TIDAK BOLEH KOSONG";
                        SpannableStringBuilder biggerText = new SpannableStringBuilder(txt);
                        biggerText.setSpan(new RelativeSizeSpan(2.35f), 0, txt.length(), 0);
                        Toast.makeText(getActivity(), biggerText, Toast.LENGTH_LONG).show();
                    }
                    else if(textusage.getText().toString().isEmpty()){
                        String txt ="Tujuan Penggunaan tidak boleh kosong!";
                        SpannableStringBuilder biggerText = new SpannableStringBuilder(txt);
                        biggerText.setSpan(new RelativeSizeSpan(2.35f), 0, txt.length(), 0);
                        Toast.makeText(getActivity(), biggerText, Toast.LENGTH_LONG).show();
                    }
                    else {
                        addinstrumenusage(barcodeText.getText().toString().trim());
                    }
                }
            });
            btnmtn = rootView.findViewById(R.id.btnmtn);
            btnmtn.setVisibility(View.INVISIBLE);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(btnmtn.getVisibility()==View.INVISIBLE){
                        btnmtn.setVisibility(View.VISIBLE);
                        btnmtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadFragment(new HystoryPemeliharaanFrag2());
                            }
                        });
                    }
                }
            });

            return rootView;
        }

        private void loadFragment(Fragment fragment) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            Bundle bundle = new Bundle();
            bundle.putString("ipaddress", ipadd);
            bundle.putString("user", result.getText().toString().trim());
            bundle.putString("idalat", barcodeText.getText().toString().trim());
            fragment.setArguments(bundle);
            fragmentTransaction.commit(); // save the changes
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        private void addinstrumenusage(String idbarcode3){
            if(okeck.isChecked()){
                selectedRbText = "Oke";
            }
            else{
                selectedRbText = "Rusak";
            }

            final String idalat=idbarcode3;
            final String usage=textusage.getText().toString().trim();
            final String status=selectedRbText;
            final String userlab=result.getText().toString().trim();

            class Addinstrumenusage1 extends AsyncTask<Void,Void,String> {
                ProgressDialog loading;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loading = ProgressDialog.show(getActivity(),"Loading LOG BOOK...","Waiting...",false,false);
                }

                @Override
                protected void onPostExecute(String s) {
                    if(s==""){
                        super.onPostExecute(s);
                        loading.dismiss();
                        String txt ="KONEKSI TERPUTUS! COBALAH BEBERAPA SAAT LAGI";
                        SpannableStringBuilder biggerText = new SpannableStringBuilder(txt);
                        biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, txt.length(), 0);
                        Toast.makeText(getActivity(), biggerText, Toast.LENGTH_LONG).show();
                    }
                    else {
                        super.onPostExecute(s);
                        loading.dismiss();
                        //Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
                        SpannableStringBuilder biggerText = new SpannableStringBuilder(s);
                        biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, s.length(), 0);
                        Toast.makeText(getActivity(), biggerText, Toast.LENGTH_LONG).show();
                        textusage.setText("");
                        btnbarc.setEnabled(false);
                    }
                }

                @Override
                protected String doInBackground(Void... v) {
                    HashMap<String,String> params = new HashMap<>();
                    params.put("idalat",idalat);
                    params.put("usage",usage);
                    params.put("status",status);
                    params.put("userlab",userlab);
                    RequestHandler rh = new RequestHandler();
                    String res = rh.sendPostRequest("http://"+ipadd+"/"+konfigurasi.URLaddlogbook, params);
                    return res;
                }
            }
            Addinstrumenusage1 ae = new Addinstrumenusage1();
            ae.execute();
        }
    }

    public final void initialiseDetectorsAndSources() {
        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
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
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            }
                        }
                    });
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }
}
