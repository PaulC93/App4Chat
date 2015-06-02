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

import com.parse.ParseUser;
import com.parse.LogInCallback;
import com.parse.ParseException;

/**
 * Created by Paul on 12/12/2014.
 */
public class LoginFragment extends Fragment {

    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputUsername;
    EditText inputPassword;
    TextView status;
    FragmentsListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        /**
         * Inflate the layout for this fragment
         */
        View view= inflater.inflate(
                R.layout.login_layout, container, false);

        // Importing all assets like buttons, text fields
        inputUsername = (EditText) view.findViewById(R.id.loginUsername);
        inputPassword = (EditText) view.findViewById(R.id.loginPassword);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) view.findViewById(R.id.btnLinkToRegister);
        status = (TextView) view.findViewById(R.id.status);

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onLinkToRegisterClick();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hide the keyboard
                EditText loginEditText = (EditText) getView().findViewById(R.id.loginPassword);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginEditText.getWindowToken(), 0);

                status.setText("Processing...");

                ParseUser.logInInBackground(inputUsername.getText().toString(), inputPassword.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            status.setText("Successfully logged in");
                            mCallback.onSuccess();
                        } else {
                            // Sign-up failed. Look at the ParseException to see what happened.
                            status.setText("Login Failed, reason: "+e.getMessage());
                            e.printStackTrace();
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