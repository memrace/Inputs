package com.sozonovalexander;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sozonovalexander.view.R;
import com.sozonovalexander.view.api.IInputValidator;
import com.sozonovalexander.view.databinding.ActivityMainBinding;
import com.sozonovalexander.view.ui.Autocomplete;
import com.sozonovalexander.view.validators.ValueRequired;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        List<Person> items = new ArrayList<>();
        items.add(new Person("Alex"));
        items.add(new Person("Sveta"));
        items.add(new Person("Gera"));
        items.add(new Person("Leva"));
        var main = findViewById(R.id.activityMain);
        Autocomplete<Person> autocomplete = main.findViewById(R.id.autocomplete);
        Autocomplete<Person> autocomplete2 = main.findViewById(R.id.autocomplete2);
        Autocomplete<Person> autocomplete3 = main.findViewById(R.id.autocomplete3);
        autocomplete.initView(items, item -> item.name, null);
        autocomplete2.initView(items, item -> item.name, null);
        autocomplete3.initView(items, item -> item.name, null);
        List<IInputValidator> validators = new ArrayList<>();
        validators.add(new ValueRequired());
        autocomplete.set_validators(validators);
        autocomplete2.set_validators(validators);
        autocomplete3.set_validators(validators);
        autocomplete.set_valueWatcher(result -> {
            if (result != null)
                if (result.item != null)
                    Toast.makeText(this, result.item.name, Toast.LENGTH_LONG).show();
                else Toast.makeText(this, result.value, Toast.LENGTH_LONG).show();

        });
        autocomplete2.set_valueWatcher(result -> {
            if (result != null)
                if (result.item != null)
                    Toast.makeText(this, result.item.name, Toast.LENGTH_LONG).show();
                else Toast.makeText(this, result.value, Toast.LENGTH_LONG).show();

        });
        autocomplete3.set_valueWatcher(result -> {
            if (result != null)
                if (result.item != null)
                    Toast.makeText(this, result.item.name, Toast.LENGTH_LONG).show();
                else Toast.makeText(this, result.value, Toast.LENGTH_LONG).show();

        });
        super.onStart();
    }
}

