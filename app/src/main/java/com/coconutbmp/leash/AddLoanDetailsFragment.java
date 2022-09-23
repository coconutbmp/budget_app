package com.coconutbmp.leash;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Fragment for the loan section for the liability page
 */
public class AddLoanDetailsFragment extends Fragment implements LiabilityDetails {

    EditText principal_amt_edit;
    EditText interest_rate_edit;
    TextView begin_date_label, end_date_label;
    EditText repayment_amount_edit;

    Spinner interest_type_spinner;
    Spinner calculation_freq_spinner;
    Spinner payment_freq_spinner;

    Button end_button, start_button;
    DatePicker end_dp, start_dp;

    Switch custom_repayment_switch;


    /**
     * Constructor
     */
    public AddLoanDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loan_details, container, false);

        principal_amt_edit = view.findViewById(R.id.principal_amt_edit);
        interest_rate_edit = view.findViewById(R.id.interest_rate_edit);
        interest_type_spinner = view.findViewById(R.id.interest_type_spinner);
        calculation_freq_spinner = view.findViewById(R.id.calculation_freq_spinner);
        begin_date_label = view.findViewById(R.id.loan_start_label);
        end_date_label = view.findViewById(R.id.loan_end_label);
        repayment_amount_edit = view.findViewById(R.id.loan_repayment_edit);
        payment_freq_spinner = view.findViewById(R.id.loan_payment_freq_spinner);
        start_button = view.findViewById(R.id.start_button);
        end_button = view.findViewById(R.id.end_button);
        start_dp = view.findViewById(R.id.loan_start_dp);
        end_dp = view.findViewById(R.id.loan_end_dp);

        String[] type = getResources().getStringArray(R.array.interest_types);
        String[] freq = getResources().getStringArray(R.array.calculation_frequencies);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, type);
        ArrayAdapter<String> freqAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, freq);

        interest_type_spinner.setPopupBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.color.smokey_white));
        calculation_freq_spinner.setPopupBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.color.smokey_white));
        payment_freq_spinner.setPopupBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.color.smokey_white));

        interest_type_spinner.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.black), PorterDuff.Mode.SRC_ATOP);
        calculation_freq_spinner.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.black), PorterDuff.Mode.SRC_ATOP);
        payment_freq_spinner.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.black), PorterDuff.Mode.SRC_ATOP);

        interest_type_spinner.setAdapter(typeAdapter);
        calculation_freq_spinner.setAdapter(freqAdapter);
        payment_freq_spinner.setAdapter(freqAdapter);

        start_button.setOnClickListener(view1 -> UXFunctions.select_date(start_button, start_dp, begin_date_label)); // link the button to the date picker and label
        end_button.setOnClickListener(view1 -> UXFunctions.select_date(end_button, end_dp, end_date_label)); // link the button to the date picker and label

        custom_repayment_switch = view.findViewById(R.id.custom_value_switch);

        custom_repayment_switch.setOnClickListener(view1 -> {
            if(custom_repayment_switch.isChecked()){
                repayment_amount_edit.setEnabled(true);
            }
            else{
                repayment_amount_edit.setEnabled(false);
            }
        });

        return view;
    }

    /**
     * parse class to json
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject getJSONRepresentation() throws Exception{
        JSONObject rep = new JSONObject();
        rep.put("principle", Double.parseDouble(String.valueOf(principal_amt_edit.getText())));
        rep.put("interest_type", interest_type_spinner.getSelectedItem());
        rep.put("rate", Double.parseDouble(String.valueOf(interest_rate_edit.getText())));
        rep.put("calc_freq", calculation_freq_spinner.getSelectedItem());
        rep.put("start", begin_date_label.getText());
        rep.put("end", begin_date_label.getText());
        rep.put("pay_freq", payment_freq_spinner.getSelectedItem().toString());
        return rep;
    }
}