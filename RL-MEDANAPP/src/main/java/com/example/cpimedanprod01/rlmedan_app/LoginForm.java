package com.example.cpimedanprod01.rlmedan_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginForm extends AppCompatActivity {
    private Button sign_in_login;
    private EditText username, password,ipaddress;
    private RequestQueue requestqueue;
    private StringRequest request;
    UserSessionManager session;
    SharedPreferences pref;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_form);
        session = new UserSessionManager(getApplicationContext());

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        ipaddress = (EditText) findViewById(R.id.ipaddress);
        sign_in_login = (Button) findViewById(R.id.sign_in_login);

        ipaddress.setText("xx.xx.x.xxx"); //ip adrress

        requestqueue = Volley.newRequestQueue(this);
        username.requestFocus();

        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        intent = new Intent(LoginForm.this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(pref.contains("username") && pref.contains("password")){
            startActivity(intent);
        }

        sign_in_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        sign_in_login.setEnabled(false);

        /*final ProgressDialog progressDialog = new ProgressDialog(LoginForm.this,
                R.style.Theme_AppCompat);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();*/
        final ProgressDialog progressDialog = ProgressDialog.show(LoginForm.this,"Loading...","Waiting...",false,false);

        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        sign_in_login.setEnabled(true);
        String URL = "http://"+ipaddress.getText().toString()+"/"+konfigurasi.URL_LOGIN;

        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonobj = new JSONObject(s);
                    if (jsonobj.getString("status").equals("1")) {
                        String user = username.getText().toString();
                        String passw = password.getText().toString();
                        String ipserver = ipaddress.getText().toString();

                        session.createUserLoginSession(user,passw);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username",user);
                        editor.putString("password",passw);
                        editor.putString("ipaddress",ipserver);
                        editor.commit();
                        editor.apply();
                        Toast.makeText(getApplication(), "Welcome " + username.getText(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else if (jsonobj.getString("status").equals("2")) {

                        String user = username.getText().toString();
                        String passw = password.getText().toString();
                        String ipserver = ipaddress.getText().toString();

                        session.createUserLoginSession(user,passw);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username",user);
                        editor.putString("password",passw);
                        editor.putString("ipaddress",ipserver);
                        editor.commit();
                        editor.apply();

                        Toast.makeText(getApplication(), "Welcome " + username.getText(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else if (jsonobj.getString("status").equals("3")) {

                        String user = username.getText().toString();
                        String passw = password.getText().toString();
                        String ipserver = ipaddress.getText().toString();

                        session.createUserLoginSession(user,passw);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username",user);
                        editor.putString("password",passw);
                        editor.putString("ipaddress",ipserver);
                        editor.commit();
                        editor.apply();

                        Toast.makeText(getApplication(), "Welcome " + username.getText(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else {
                        onLoginFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError{
                HashMap<String, String> hashmap = new HashMap<String,String>();
                hashmap.put("usernm",username.getText().toString());
                hashmap.put("passwd", password.getText().toString());
                return hashmap;
            }
        };
        requestqueue.add(request);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        sign_in_login.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String user = username.getText().toString();
        String pass = password.getText().toString();

        if (user.isEmpty()) {
            username.setError("Username tidak boleh kosong!");
            valid = false;
        } else {
            username.setError(null);
        }

        if (pass.isEmpty()) {
            password.setError("Password tidak boleh kosong!");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }
}
