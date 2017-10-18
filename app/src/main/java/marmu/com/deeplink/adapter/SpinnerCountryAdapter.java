package marmu.com.deeplink.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import marmu.com.deeplink.R;
import marmu.com.deeplink.model.SpinnerCountryModel;

/**
 * Created by azharuddin on 4/8/17.
 */

@SuppressWarnings({"DanglingJavadoc", "unchecked"})
public class SpinnerCountryAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<SpinnerCountryModel> spinnerCountry = new ArrayList<>();

    /*************  CustomAdapter Constructor *****************/
    public SpinnerCountryAdapter(
            Context context,
            int layoutId,
            ArrayList spinnerCountry) {
        super(context, layoutId, spinnerCountry);

        /********** Take passed values **********/
        this.context = context;
        this.spinnerCountry = spinnerCountry;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    // This function called for each row ( Called data.size() times )
    private View getCustomView(int position, ViewGroup parent) {

        /********** Inflate spinner_country.xml file for each row ( Defined below ) ************/
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_country, parent, false);

        /***** Get each Model object from Arraylist ********/
        SpinnerCountryModel countryModel = spinnerCountry.get(position);

        TextView dialCode = view.findViewById(R.id.tv_country_dial_code);
        TextView countryName = view.findViewById(R.id.tv_country_name);
        TextView countryCode = view.findViewById(R.id.tv_country_code);

        // Set values for spinner each row
        dialCode.setText(countryModel.getDialCode());
        countryName.setText(countryModel.getName());
        countryCode.setText(countryModel.getCode());
        return view;
    }

}
