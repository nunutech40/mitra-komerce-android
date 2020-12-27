package id.android.kmabsensi.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

public class RupiahTextWatcher implements TextWatcher {

    private EditText edt;
    private String current = "";
    private OnAfterTextChanged mListener;

    public RupiahTextWatcher(EditText edt) {
        this.edt = edt;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    public void setListener(OnAfterTextChanged listener){
        this.mListener = listener;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (!editable.toString().equals(current)) {
            edt.removeTextChangedListener(this);

            Locale local = new Locale("id", "id");
            String replaceable = String.format("[Rp,.\\s]",
                    NumberFormat.getCurrencyInstance().getCurrency()
                            .getSymbol(local));
            String cleanString = editable.toString().replaceAll(replaceable,
                    "");

            double parsed;
            try {
                parsed = Double.parseDouble(cleanString);
            } catch (NumberFormatException e) {
                parsed = 0.00;
            }

            NumberFormat formatter = NumberFormat
                    .getCurrencyInstance(local);
            formatter.setMaximumFractionDigits(0);
            formatter.setParseIntegerOnly(true);
            String formatted = formatter.format((parsed));

            String replace = String.format("[Rp\\s]",
                    NumberFormat.getCurrencyInstance().getCurrency()
                            .getSymbol(local));
//            String clean = "Rp. "+formatted.replaceAll(replace, "");
            String clean = formatted.replaceAll(replace, "");
            if (mListener != null){
                mListener.afterTextChanged(clean);
            }

            current = formatted;
            if (clean.equals("0")){
                edt.setText("0");
                edt.addTextChangedListener(this);
            } else {
                edt.setText(clean);
                edt.setSelection(clean.length());
                edt.addTextChangedListener(this);
            }

        }
    }
}
