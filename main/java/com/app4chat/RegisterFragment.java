package com.app4chat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Paul on 12/12/2014.
 */
public class RegisterFragment extends Fragment {


    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;
    Button btnLinkToLogin;
    TextView status;
   FragmentsListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
        View view=inflater.inflate(R.layout.register_layout, container, false);

        inputUsername = (EditText) view.findViewById(R.id.registerUserName);
        inputEmail = (EditText) view.findViewById(R.id.registerEmail);
        inputPassword = (EditText) view.findViewById(R.id.registerPassword);
        btnRegister = (Button) view.findViewById(R.id.btnRegister);
        btnLinkToLogin= (Button) view.findViewById(R.id.btnLinkToLogin);
        status= (TextView) view.findViewById(R.id.status);

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onLinkToLoginClick();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hide the keyboard
                EditText registerEditText = (EditText) getView().findViewById(R.id.registerPassword);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(registerEditText.getWindowToken(), 0);

                status.setText("Processing...");

                ParseUser user =new ParseUser();
                user.setUsername(inputUsername.getText().toString());
                user.setEmail(inputEmail.getText().toString());
                user.setPassword(inputPassword.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            status.setText("Successfully Registered.");
                            mCallback.onSuccess();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            status.setText("Registration Error, reason: "+e.getMessage());
                        }

                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (FragmentsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentsListener");
        }
    }
}