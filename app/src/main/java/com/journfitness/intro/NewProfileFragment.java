package com.journfitness.intro;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.journfitness.DAO.DAOProfile;
import com.journfitness.DAO.Profile;
import com.journfitness.DatePickerDialogFragment;
import com.journfitness.MainActivity;
import com.journfitness.R;
import com.journfitness.utils.DateConverter;
import com.journfitness.enums.Gender;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.onurkaganaldemir.ktoastlib.KToast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NewProfileFragment extends SlideFragment {

    private EditText mName;
    private TextView mBirthday;
    private final OnDateSetListener dateSet = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            mBirthday.setText(DateConverter.dateToLocalDateStr(year, month, day, getContext()));
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mBirthday.getWindowToken(), 0);
        }
    };
    private Button mBtCreate;
    private RadioButton mRbMale;
    private RadioButton mRbFemale;
    private RadioButton mRbOtherGender;
    private boolean mProfilCreated = false;
    private final View.OnClickListener clickCreateButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Initialisation des objets DB
            DAOProfile mDbProfiles = new DAOProfile(v.getContext());

            if (mName.getText().toString().isEmpty()) {
                //Toast.makeText(getActivity().getBaseContext(), R.string.fillAllFields, Toast.LENGTH_SHORT).show();
                KToast.warningToast(getActivity(), getResources().getText(R.string.fillNameField).toString(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
            } else {
                int lGender = Gender.UNKNOWN;
                if (mRbMale.isChecked()) {
                    lGender = Gender.MALE;
                } else if (mRbFemale.isChecked()) {
                    lGender = Gender.FEMALE;
                } else if (mRbOtherGender.isChecked()) {
                    lGender = Gender.OTHER;
                }

                Profile p = new Profile(mName.getText().toString(), 0, DateConverter.localDateStrToDate(mBirthday.getText().toString(), getContext()), lGender);
                // Create the new profil
                mDbProfiles.addProfile(p);
                //Toast.makeText(getActivity().getBaseContext(), R.string.profileCreated, Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(p.getName())
                        .setContentText(getContext().getResources().getText(R.string.profileCreated).toString())
                        .setConfirmClickListener(sDialog -> nextSlide())
                        .show();
                mProfilCreated = true;
            }
        }
    };
    private DatePickerDialogFragment mDateFrag = null;
    private MainActivity motherActivity;

    public NewProfileFragment() {
        // Required empty public constructor
    }

    public static NewProfileFragment newInstance() {
        return new NewProfileFragment();
    }

    private void showDatePickerFragment() {
        if (mDateFrag == null) {
            mDateFrag = DatePickerDialogFragment.newInstance(dateSet);
        }

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        mDateFrag.show(ft, "dialog");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.introfragment_newprofile, container, false);

        mName = view.findViewById(R.id.profileName);
        mBirthday = view.findViewById(R.id.profileBirthday);
        mBtCreate = view.findViewById(R.id.create_newprofil);
        mRbMale = view.findViewById(R.id.radioButtonMale);
        mRbFemale = view.findViewById(R.id.radioButtonFemale);
        mRbOtherGender = view.findViewById(R.id.radioButtonOtherGender);

        mBirthday.setOnClickListener(v -> showDatePickerFragment());

        /* Initialisation des boutons */
        mBtCreate.setOnClickListener(clickCreateButton);

        getIntroActivity().addOnNavigationBlockedListener((position, direction) -> {
            //Slide slide = getIntroActivity().getSlide(position);

            if (position == 4) {
                mBtCreate.callOnClick();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean canGoForward() {
        return mProfilCreated;
    }

    public MainIntroActivity getIntroActivity() {
        if (getActivity() instanceof MainIntroActivity) {
            return (MainIntroActivity) getActivity();
        } else {
            throw new IllegalStateException("SlideFragments must be attached to MainIntroActivity.");
        }
    }
}
