package com.sozonovalexander.view.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
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
import com.sozonovalexander.view.R;
import com.sozonovalexander.view.adapters.DataListAdapter;
import com.sozonovalexander.view.api.IInputDisplay;
import com.sozonovalexander.view.api.IInputValidator;
import com.sozonovalexander.view.api.IInputValueWatcher;
import com.sozonovalexander.view.api.InputModes;
import com.sozonovalexander.view.api.InputResult;

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
    private List<IInputValidator> _validators = new ArrayList<>();
    private IInputValueWatcher<T> _valueWatcher;
    private PopupWindow _popup;
    private RecyclerView _listView;
    private DataListAdapter _adapter;
    private InputModes _mode = InputModes.HARD_AUTOCOMPLETE;
    @Nullable
    private CharSequence _inputLabel;
    @Nullable
    private CharSequence _inputHelperText;
    private int _inputHeight = 0;
    private int _inputType = 0;

    public void set_valueWatcher(IInputValueWatcher<T> _valueWatcher) {
        this._valueWatcher = _valueWatcher;
    }

    public void set_validators(List<IInputValidator> _validators) {
        this._validators = _validators;
    }

    public void initView(@NotNull List<T> _items, @NotNull IInputDisplay<T> _inputDisplay, @Nullable InputModes _mode) {
        if (_mode != null) this._mode = _mode;
        this._inputDisplay = _inputDisplay;
        this._items = _items;
        var list = new ArrayList<String>();
        _items.stream().map(_inputDisplay::display).forEach(list::add);
        _adapter.updateDataSet(list);
    }

    public void set_item(@Nullable T item) {
        String text;
        if (item != null) {
            text = _inputDisplay.display(item);
            _editText.setText(text);
            _editText.setSelection(text.length());
            _valueWatcher.onValueChange(new InputResult<>(item, text));
        } else {
            text = _editText.getText().toString();
            if (_mode != InputModes.SELECT && _mode != InputModes.HARD_AUTOCOMPLETE)
                _valueWatcher.onValueChange(new InputResult<>(null, text));
        }
        _selectedItem = item;

    }

    public InputResult<T> get_result() {
        return new InputResult<>(_selectedItem, _editText.getText().toString());
    }

    private void setAdapter() {
        _adapter = new DataListAdapter(new ArrayList<>(), this::findAndSetItem);
    }

    private void findAndSetItem(String value) {
        if (isValid(value)) {
            var itemStream = _items.stream().filter(it -> _inputDisplay.display(it).equals(value)).findFirst();
            _inputLayout.setError(null);
            if (itemStream.isPresent()) {
                set_item(itemStream.get());
            } else {
                if (_mode == InputModes.SOFT_AUTOCOMPLETE)
                    set_item(null);
                if (_mode == InputModes.HARD_AUTOCOMPLETE)
                    _editText.setText(null);
            }
            _popup.dismiss();
            hideKeyboard();
        } else {
            _popup.dismiss();
            var errors = _validators.stream().filter(it -> !it.isValid(value)).map(IInputValidator::getError).collect(Collectors.toList());
            if (errors.size() == 1) {
                _inputLayout.setError(errors.get(0));
            } else {
                _inputLayout.setError("Возникли ошибки ввода");
            }
        }
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
        if (attrs != null)
            setAttrs(attrs);
        initComponents();
    }

    private void showPopup() {
        int width = _inputLayout.getMeasuredWidth();
        _popup.setWidth(width);
        Display display = getDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        _popup.setHeight(outMetrics.heightPixels / 4);
        _popup.showAsDropDown(_editText, 0, 0);
    }

    private void initComponents() {
        _inputLayout = findViewById(R.id.inputTextLayout);
        _editText = _inputLayout.getEditText();
        assert _editText != null;
        if (_inputHeight != 0) {
            _editText.setHeight(_inputHeight);
        }
        if (_inputHelperText != null) {
            _inputLayout.setHelperText(_inputHelperText);
        }
        if (_inputLabel != null) {
            _inputLayout.setHint(_inputLabel);
        }
        if (_mode == InputModes.SELECT) {
            _editText.setCursorVisible(false);
            _editText.setShowSoftInputOnFocus(false);
        }
        _editText.setOnFocusChangeListener((view, b) -> {
            if (b) {
                showPopup();
            } else {
                var text = ((EditText) view).getText().toString();
                findAndSetItem(text);
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
        if (ta.hasValue(R.styleable.Autocomplete_inputLabel)) {
            _inputLabel = ta.getText(R.styleable.Autocomplete_inputLabel);
        }
        if (ta.hasValue(R.styleable.Autocomplete_inputHelperText)) {
            _inputHelperText = ta.getText(R.styleable.Autocomplete_inputHelperText);
        }
        if (ta.hasValue(R.styleable.Autocomplete_inputHeight)) {
            _inputHeight = ta.getDimensionPixelSize(R.styleable.Autocomplete_inputHeight, 0);
        }
        if (ta.hasValue(R.styleable.Autocomplete_inputType)) {
            var inputType = ta.getInt(R.styleable.Autocomplete_inputMode, 0);
            switch (inputType) {
                case 0:
                    _inputType = InputType.TYPE_CLASS_TEXT;
                case 1:
                    _inputType = InputType.TYPE_CLASS_NUMBER;
            }
        }
        if (ta.hasValue(R.styleable.Autocomplete_inputMode)) {
            var mode = ta.getInt(R.styleable.Autocomplete_inputMode, InputModes.SOFT_AUTOCOMPLETE.ordinal());
            _mode = InputModes.values()[mode];
        }
        ta.recycle();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    private Boolean isValid(String value) {
        return _validators.size() > 0 && _validators.stream().allMatch(it -> it.isValid(value));
    }
}


