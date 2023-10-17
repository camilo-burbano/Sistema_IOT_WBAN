package com.example.sistemaiot_wban.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistemaiot_wban.R;
import com.example.sistemaiot_wban.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarException;

public class HomeFragment extends Fragment {

    //declarar objeto
    RequestQueue requestQueue;

    //private ProgressBar cargando;

private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

    binding = FragmentHomeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        //declarar variables

        //seleccionar direccion
        final ImageButton imgBtnIzq = binding.imgBtnIzq;
        final ImageButton imgBtnCen = binding.imgBtnCen;
        final ImageButton imgBtnDer = binding.imgBtnDer;

        final TextView resultado = binding.textViewResultado;
        final TextView wearable = binding.textViewWearable;
        final ProgressBar cargando = binding.progressBar;

        String idw = cargarPrefencias(wearable);

        imgBtnIzq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operacionGeneral(wearable,resultado, cargando,"1");
            }
        });

        imgBtnCen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operacionGeneral(wearable,resultado, cargando,"2");
            }
        });

        imgBtnDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operacionGeneral(wearable,resultado, cargando,"3");
            }
        });
        return root;
    }

    //operacion general
    private void operacionGeneral(TextView wearable, TextView resultado,ProgressBar cargando,String direccion){
        String idw = cargarPrefencias(wearable);
        if(idw == "wearable no registrado"){
            Toast.makeText(getActivity().getApplicationContext(), "Registre el wearable", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            encenderWearable("http://192.168.1.6/sistemaIoTWBAN/cambiarEstado.php", idw);
            ejecutarAlarma();

            cargando.setVisibility(View.VISIBLE);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    evaluar("http://192.168.1.6/sistemaIoTWBAN/evaluacion.php",idw, direccion,resultado);
                }
            };

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(runnable,16000);

            Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                    cambiarEvaluados("http://192.168.1.6/sistemaIoTWBAN/cambiarEvaluados.php?",idw);
                    cargando.setVisibility(View.GONE);
                }
            };
            Handler handler1 = new Handler(Looper.getMainLooper());
            handler1.postDelayed(runnable1,22000);


        } catch (Exception e){
            Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }


    //activar la alarma
    private void ejecutarAlarma(){
        final MediaPlayer alarma;
        alarma = MediaPlayer.create(getActivity(),R.raw.inicio);
        alarma.start();
    }

    //cargar el idw si esta registrado
    private String cargarPrefencias(TextView wearable) {
        SharedPreferences preferences = getActivity().getSharedPreferences("idw", Context.MODE_PRIVATE);

        String idw = preferences.getString("idw","wearable no registrado");
        wearable.setText(idw);

        return idw;
    }

    //encender el wearable
    private void encenderWearable(String URL,String idw){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Wearable encendido", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<String, String>();

                parametros.put("idw",idw);
                parametros.put("encender","1");

                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void cambiarEvaluados(String URL,String idw){
        //declarar objeto
        //RequestQueue requestQueue;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Evaluacion Realizada", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<String, String>();
                parametros.put("idw",idw);
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    //realizar evaluacion
    private void evaluar(String URL,String idw, String direccion,TextView resultado){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity().getApplicationContext(), "1111", Toast.LENGTH_SHORT).show();
                resultado.setText(response);
                //Toast.makeText(getActivity().getApplicationContext(), "2222", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<String, String>();

                parametros.put("idw",idw);
                parametros.put("direccion",direccion);
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    //ver la calificacion no funciona
    private void verEvaluacion(String URL,String idw, EditText resultado){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,URL, null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0;i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        resultado.setText(jsonObject.getString("eva"));
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "error de conexion",Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }








@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }






}