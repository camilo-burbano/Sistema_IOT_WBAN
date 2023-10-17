package com.example.sistemaiot_wban.ui.slideshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.sistemaiot_wban.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

    binding = FragmentSlideshowBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView nombreRed = binding.textViewNombreRed;
        final TextView contrasena = binding.textViewContrasena;
       // final TextView textView = binding.textView3;
       // slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        cargarPrefencias(nombreRed, contrasena);

        return root;
    }

    //cargar el idw si esta registrado
    private String cargarPrefencias(TextView nombreRed, TextView contrasena) {
        SharedPreferences preferences = getActivity().getSharedPreferences("idw", Context.MODE_PRIVATE);

        String idw = preferences.getString("idw","wearable no registrado");
        nombreRed.setText("Nombre de la red: "+idw);
        contrasena.setText("Contraseña: "+idw+idw);

        return idw;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}