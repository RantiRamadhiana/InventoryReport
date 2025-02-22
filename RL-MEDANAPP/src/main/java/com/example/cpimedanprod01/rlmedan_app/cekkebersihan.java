package com.example.cpimedanprod01.rlmedan_app;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class cekkebersihan extends Fragment {
    View rootView;
    CheckBox ranalis,radmin,rtimbang,rasam,roven,rdinding;
    Button save;
    static String ipadd,usernm;
    TextView ipadd2;
    public String ranalistxt,radmintxt,rtimbangtxt,rasamtxt,roventxt,dindingtxt,usertxt;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_cekkebersihan, container, false);
        ranalis = rootView.findViewById(R.id.ranalisa);
        radmin = rootView.findViewById(R.id.radministrasi);
        rtimbang = rootView.findViewById(R.id.rtimbang);
        rasam = rootView.findViewById(R.id.rasam);
        roven = rootView.findViewById(R.id.roven);
        rdinding = rootView.findViewById(R.id.dinding);

        ranalis.setBackgroundColor(Color.rgb(204,229,255));
        radmin.setBackgroundColor(Color.rgb(204,229,255));
        rtimbang.setBackgroundColor(Color.rgb(204,229,255));
        rasam.setBackgroundColor(Color.rgb(204,229,255));
        roven.setBackgroundColor(Color.rgb(204,229,255));
        rdinding.setBackgroundColor(Color.rgb(204,229,255));

        ipadd = getArguments().getString("ipaddress");
        usernm = getArguments().getString("user");

        ipadd2 = rootView.findViewById(R.id.ipaddtxt);
        ipadd2.setText(ipadd);

        save = rootView.findViewById(R.id.btnsave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savecekkebersihan();
            }
        });

        return  rootView;
    }

    private void savecekkebersihan(){
        if(ranalis.isChecked()){
            ranalistxt = "V";
        }else{
            ranalistxt = "";
        }

        if(radmin.isChecked()){
            radmintxt = "V";
        }else{
            radmintxt = "";
        }

        if(rtimbang.isChecked()){
            rtimbangtxt = "V";
        }else{
            rtimbangtxt = "";
        }

        if(rasam.isChecked()){
            rasamtxt = "V";
        }else{
            rasamtxt = "";
        }

        if(roven.isChecked()){
            roventxt = "V";
        }else{
            roventxt = "";
        }

        if(rdinding.isChecked()){
            dindingtxt = "V";
        }else{
            dindingtxt = "";
        }

        final String ranalisst=ranalistxt;
        final String radminst=radmintxt;
        final String rtimbangst=rtimbangtxt;
        final String rasamst=rasamtxt;
        final String rovenst=roventxt;
        final String dindingst=dindingtxt;
        final String USERNM=usernm;

        class savecekkebersihan1 extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(cekkebersihan.this.getContext(),"Loading MATERIAL USAGE...","Waiting...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                if(s==""){
                    super.onPostExecute(s);
                    loading.dismiss();
                    String txt ="KONEKSI TERPUTUS! COBALAH BEBERAPA SAAT LAGI";
                    SpannableStringBuilder biggerText = new SpannableStringBuilder(txt);
                    biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, txt.length(), 0);
                    Toast.makeText(cekkebersihan.this.getContext(), biggerText, Toast.LENGTH_LONG).show();
                }
                else {
                    super.onPostExecute(s);
                    loading.dismiss();
                    SpannableStringBuilder biggerText = new SpannableStringBuilder(s);
                    biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, s.length(), 0);
                    Toast.makeText(cekkebersihan.this.getContext(), biggerText, Toast.LENGTH_LONG).show();
                    save.setEnabled(false);
                    ranalis.setChecked(false);
                    radmin.setChecked(false);
                    rtimbang.setChecked(false);
                    rasam.setChecked(false);
                    roven.setChecked(false);
                    rdinding.setChecked(false);
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("ranalis",ranalisst);
                params.put("radmin",radminst);
                params.put("rtimbang",rtimbangst);
                params.put("rasam",rasamst);
                params.put("roven",rovenst);
                params.put("rdinding",dindingst);
                params.put("user",USERNM);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest("http://"+ipadd2.getText().toString().trim()+"/"+konfigurasi.URLaddcleanliness, params);
                return res;
            }
        }
        savecekkebersihan1 ae = new savecekkebersihan1();
        ae.execute();
    }
}
