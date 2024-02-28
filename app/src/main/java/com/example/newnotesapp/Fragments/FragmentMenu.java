package com.example.newnotesapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.newnotesapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMenu extends Fragment {

    public interface MenuFragmentInteractionListener {
        void onMenuFragmentDisplayed();
        void onMenuFragmentHidden();
    }
    CardView cardView;
    Button settings_button, main_menu_button, basket_button;
    ImageButton list_button;

    private MenuFragmentInteractionListener listener;

    public FragmentMenu() {
        super(R.layout.fragment_menu);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MenuFragmentInteractionListener) {
            listener = (MenuFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement MenuFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            listener.onMenuFragmentDisplayed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listener != null) {
            listener.onMenuFragmentHidden();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*cardView = view.findViewById(R.id.menu);
        settings_button = view.findViewById(R.id.settingsButton);
        main_menu_button = view.findViewById(R.id.mainMenuButton);
        basket_button = view.findViewById(R.id.basketButton);
        list_button = view.findViewById(R.id.listMenuButton);

        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.remove(FragmentMenu.this);

                fragmentTransaction.commit();
            }
        });

        main_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.remove(FragmentMenu.this);

                fragmentTransaction.commit();
            }
        });*/
    }
}