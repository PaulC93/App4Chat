package com.app4chat;


import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class MainActivity extends Activity implements FragmentsListener{

    Fragment f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Enable Local Datastore.
            Parse.enableLocalDatastore(this);
            Parse.initialize(this, "Aw4xuCV6ru5CGQyLUVD3yjRjRyUONezNQTSkxKK1", "Cz5cSvEZmI5CiOW8fh2S2at1e5rp5QOpulFoWxHe");
        }
        catch (RuntimeException e)
        {
            //do nothing
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            f = new ViewMessagesFragment();
        } else {
            // show the login screen
            f = new LoginFragment();
        }

        fragmentTransaction.replace(android.R.id.content,f);
        fragmentTransaction.commit();
    }

    @Override
    public void onLinkToRegisterClick() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        f = new RegisterFragment();
        fragmentTransaction.replace(android.R.id.content,f);
        fragmentTransaction.commit();
    }

    @Override
    public void onLinkToLoginClick() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        f = new LoginFragment();
        fragmentTransaction.replace(android.R.id.content,f);
        fragmentTransaction.commit();
    }

    @Override
    // on Successful Registration or on Successful Login
    public void onSuccess() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        f = new ViewMessagesFragment();
        fragmentTransaction.replace(android.R.id.content,f);
        fragmentTransaction.commit();
    }

    @Override
    public void onLogout() {
       try {
            ParseObject.unpinAll();
       } catch (ParseException e) {
            e.printStackTrace();
       }
       FragmentManager fragmentManager = getFragmentManager();
       FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
       f=new LoginFragment();
       fragmentTransaction.replace(android.R.id.content,f);
       fragmentTransaction.commit();
    }

    @Override
    public void onNewMessageClick() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        f =new SendMessageFragment();
        fragmentTransaction.replace(android.R.id.content,f);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
