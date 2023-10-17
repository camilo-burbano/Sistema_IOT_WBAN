package com.example.sistemaiot_wban.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.sistemaiot_wban.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

    binding = FragmentGalleryBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        //final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //declarar variables
        final EditText campoIdw= binding.editTextIdw;
        final Button btnregistrar = binding.btnRegistrar;
        final TextView registroWearable = binding.textViewRegistroWearable;

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(campoIdw.getText().length() == 0){
                    Toast.makeText(getActivity().getApplicationContext(), "el id wearable no debe ser nulo", Toast.LENGTH_SHORT).show();
                }else {
                    guardarIdw(campoIdw, registroWearable);
                }
            }
        });

        cargarPrefencias(registroWearable);

        return root;
    }

    //cargar el idw si esta registrado
    private void cargarPrefencias(TextView registroWearable) {
        SharedPreferences preferences = getActivity().getSharedPreferences("idw", Context.MODE_PRIVATE);
        String idw = preferences.getString("idw","wearable no registrado");
        registroWearable.setText(idw);
    }


    private void guardarIdw(EditText campoIdw, TextView registroWearable) {

        SharedPreferences preferences = getActivity().getSharedPreferences("idw", Context.MODE_PRIVATE);

        String idw = campoIdw.getText().toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("idw",idw);

        campoIdw.setText("");
        //campoIdw.setHint("");
        registroWearable.setText(idw);
        Toast.makeText(getActivity().getApplicationContext(), "Wearable registrado", Toast.LENGTH_SHORT).show();

        editor.commit();

    }
@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}