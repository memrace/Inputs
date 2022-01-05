package com.sozonovalexander.inputs.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

    public void initView(@NotNull List<T> _items, IInputDisplay<T> _inputDisplay) {
        this._inputDisplay = _inputDisplay;
        this._items = _items;
        var list = new ArrayList<String>();
        _items.stream().map(_inputDisplay::display).forEach(list::add);
        _adapter.updateDataSet(list);
    }

    public void set_item(@Nullable T item) {
        var text = _inputDisplay.display(item);
        _editText.setText(text);
        _editText.setSelection(text.length());
        _selectedItem = item;
        _valueWatcher.onValueChange(item);
    }

    public T get_item() {
        return _selectedItem;
    }

    private void setAdapter() {
        var list = new ArrayList<String>();
        _items.stream().map(it -> _inputDisplay.display(it)).forEach(list::add);
        _adapter = new DataListAdapter(list, this::findAndSetItem);
    }

    private void findAndSetItem(String value) {
        var itemStream = _items.stream().filter(it -> _inputDisplay.display(it).equals(value)).findFirst();
        itemStream.ifPresent(this::set_item);
        _popup.dismiss();
        hideKeyboard();
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

    private void showPopup() {
        int width = _inputLayout.getMeasuredWidth();
        _popup.setWidth(width);
        Display display = getDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        _popup.setHeight(outMetrics.heightPixels / 4);
        _popup.showAsDropDown(_editText, 0, 10);
    }

    private void initComponents() {
        _inputLayout = findViewById(R.id.inputTextLayout);
        _editText = _inputLayout.getEditText();
        _editText.setOnFocusChangeListener((view, b) -> {
            if (b) {
                showPopup();
            } else {
                var text = ((EditText) view).getText().toString();
                if (text.isEmpty()) {
                    _popup.dismiss();
                    hideKeyboard();
                } else {
                    findAndSetItem(text);
                }

            }
        });
        setAdapter();
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.autocomplete_menu_layout, null);
        _listView = popupView.findViewById(R.id.listView);
        _listView.setAdapter(_adapter);
        _listView.setLayoutManager(new LinearLayoutManager(getContext()));
        _popup = new PopupWindow(popupView, 0, 0, false);
        _editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH ||
                    i == EditorInfo.IME_ACTION_DONE ||
                    keyEvent != null &&
                            keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                            keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (keyEvent == null || !keyEvent.isShiftPressed()) {
                    findAndSetItem(textView.getText().toString());
                    _popup.dismiss();
                    hideKeyboard();
                    return true;
                }
            }
            return false;
        });
        _editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!_popup.isShowing()) {
                    showPopup();
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

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getWindowToken(), 0);
    }
}


