package com.app4chat;

import android.app.Activity;
import android.app.DialogFragment;
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
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Paul on 16/12/2014.
 */
public class SendMessageFragment extends Fragment {


    EditText recipient, message;
    TextView messageStatus;
    Button btnSend;

    FragmentsListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.send_message_layout, container, false);

        recipient=(EditText) view.findViewById(R.id.ToText);
        message=(EditText) view.findViewById(R.id.MessageText);
        messageStatus=(TextView) view.findViewById(R.id.messageSendingStatus);
        btnSend=(Button) view.findViewById(R.id.btnSend);


        btnSend.setOnClickListener(new View.OnClickListener() {

             boolean messageProcessed=true;

            @Override
            public void onClick(View v) {

                //hide the keyboard
                EditText sendEditText = (EditText) getView().findViewById(R.id.MessageText);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendEditText.getWindowToken(), 0);

                messageStatus.setText("Processing...");

                if(!messageProcessed)
                    Toast.makeText(getActivity().getApplicationContext(),"Please wait until the current message is processed",Toast.LENGTH_LONG).show();
                else
                {

                messageProcessed=false;
                ParseObject msj=new ParseObject("Messages");
                ParseACL groupACL=new ParseACL();
                msj.put("Recipient", recipient.getText().toString());
                msj.put("Message", message.getText().toString());
                msj.put("Sender", ParseUser.getCurrentUser().getUsername());

                //the sender can read the message
                groupACL.setReadAccess(ParseUser.getCurrentUser(),true);
                //get the recipient user to allow him to read the message
                ParseUser recipientUser;
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("username",recipient.getText().toString());
                try {
                   recipientUser=query.getFirst();
                   groupACL.setReadAccess(recipientUser,true);
                   //set the reading rights
                   msj.setACL(groupACL);
                    //send the message
                    msj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                Toast.makeText(getActivity().getApplicationContext(),"Message Sent",Toast.LENGTH_SHORT).show();
                                recipient.getText().clear();
                                message.getText().clear();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "An error occurred, check your internet connection", Toast.LENGTH_SHORT);
                                }
                            messageStatus.setText("");
                            messageProcessed=true;
                        }
                    });

                    } catch (ParseException e) {
                    e.printStackTrace();
                    messageStatus.setText("");
                    Toast.makeText(getActivity().getApplicationContext(),"Invalid recipient username",Toast.LENGTH_SHORT).show();
                    messageProcessed=true;
                    }
                }
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
