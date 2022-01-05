package com.sozonovalexander.inputs.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.sozonovalexander.inputs.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Autocomplete<T> extends LinearLayout {
    private TextInputLayout _inputLayout;
    private EditText _editText;
    private @NotNull List<T> _items = new ArrayList<>();
    private IInputDisplay<T> _inputDisplay;
    private @Nullable
    T _selectedItem;
    private IErrorWatcher _errorWatcher;
    private List<IInputValidator> _validators;
    private IInputValueWatcher<T> _valueWatcher;
    private PopupWindow _popup;
    private RecyclerView _listView;
    private DataListAdapter _adapter;

    public void set_valueWatcher(IInputValueWatcher<T> _valueWatcher) {
        this._valueWatcher = _valueWatcher;
    }

    public void set_validators(List<IInputValidator> _validators) {
        this._validators = _validators;
    }

    public void set_errorWatcher(IErrorWatcher _errorWatcher) {
        this._errorWatcher = _errorWatcher;
    }

    public void initView(@NotNull List<T> _items, IInputDisplay<T> _inputDisplay) {
        this._inputDisplay = _inputDisplay;
        this._items = _items;
        var list = new ArrayList<String>();
        _items.stream().map(_inputDisplay::display).forEach(list::add);
        _adapter.updateDataSet(list);
    }

    public void set_item(@Nullable T item) {
        _editText.setText(_inputDisplay.display(item));
        _selectedItem = item;
        _valueWatcher.onValueChange(item);
    }

    public T get_item() {
        return _selectedItem;
    }

    private void setAdapter() {
        var list = new ArrayList<String>();
        _items.stream().map(it -> _inputDisplay.display(it)).forEach(list::add);
        _adapter = new DataListAdapter(list, value -> {
            var itemStream = _items.stream().filter(it -> _inputDisplay.display(it).equals(value)).findFirst();
            itemStream.ifPresent(this::set_item);
        });
    }
    private void handleTap(T item){
        _editText.setText(_inputDisplay.display(item));

    }

    public Autocomplete(Context context) {
        super(context);
        init(context, null);
    }

    public Autocomplete(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Autocomplete(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.autocomplete, this);
        initComponents();
        if (attrs != null)
            setAttrs(attrs);
    }

    private void initComponents() {
        _inputLayout = (TextInputLayout) findViewById(R.id.inputTextLayout);
        _editText = _inputLayout.getEditText();
        setAdapter();
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.autocomplete_menu_layout, null);
        _listView = (RecyclerView) popupView.findViewById(R.id.listView);
        _listView.setAdapter(_adapter);
        _listView.setLayoutManager(new LinearLayoutManager(getContext()));
        _popup = new PopupWindow(popupView, 0, 0, false);
        _editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!_popup.isShowing()) {
                    int width = _inputLayout.getMeasuredWidth();
                    _popup.setWidth(width);
                    Display display = getDisplay();
                    DisplayMetrics outMetrics = new DisplayMetrics();
                    display.getMetrics(outMetrics);
                    _popup.setHeight(outMetrics.heightPixels / 4);
                    _popup.showAsDropDown(_editText, 0, 10);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                _adapter.updateDataSet(filterValue(charSequence.toString()).stream().map(it -> _inputDisplay.display(it)).collect(Collectors.toList()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private List<T> filterValue(String value) {
        if (value == null || value.isEmpty()) {
            return _items;
        } else {
            var filteredList = new ArrayList<T>();
            _items.stream().filter(it -> _inputDisplay.display(it).toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT))).forEach(filteredList::add);
            return filteredList;
        }
    }

    private void setAttrs(@NotNull AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.Autocomplete);
        ta.recycle();
    }
}


