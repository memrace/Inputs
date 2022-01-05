package com.sozonovalexander.inputs;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sozonovalexander.inputs.databinding.ActivityMainBinding;
import com.sozonovalexander.inputs.ui.Autocomplete;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<Person> items = new ArrayList<>();
        items.add(new Person("Alex"));
        items.add(new Person("Sveta"));
        items.add(new Person("Gera"));
        items.add(new Person("Leva"));
        Autocomplete<Person> autocomplete = binding.autocomplete;
        autocomplete.initView(items, item -> item.name);
        autocomplete.set_valueWatcher(value -> {
            if (value != null)
                Toast.makeText(this, value.name, Toast.LENGTH_LONG).show();
            else binding.tv.setText("");
        });
    }
}

