package com.app4chat;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Paul on 16/12/2014.
 */
public class ViewMessagesFragment extends Fragment {

    Button btnNewMessage;
    Button btnCheckNewMessage;
    Button btnLogout;
    FragmentsListener mCallback;
    ParseObject lastMessage;
    LinearLayout messagesContainer;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.view_messages_layout, container, false);

        btnNewMessage = (Button) view.findViewById(R.id.btnNewMessage);
        btnCheckNewMessage = (Button) view.findViewById(R.id.btnCheckNewMessage);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        messagesContainer = (LinearLayout) view.findViewById(R.id.messagesContainer);


        //load messages from local datastore
        ParseQuery<ParseObject> recipientQuery = ParseQuery.getQuery("Messages");
        recipientQuery.whereEqualTo("Recipient", ParseUser.getCurrentUser().getUsername());
        ParseQuery<ParseObject> senderQuery = ParseQuery.getQuery("Messages");
        senderQuery.whereEqualTo("Sender", ParseUser.getCurrentUser().getUsername());
        List<ParseQuery<ParseObject>> queryList =new ArrayList<ParseQuery<ParseObject>>() ;
        queryList.add(recipientQuery);
        queryList.add(senderQuery);
        ParseQuery<ParseObject> query=ParseQuery.or(queryList);
        query.orderByAscending("createdAt");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> MessageList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < MessageList.size(); i++) {
                        ParseObject msjObj = MessageList.get(i);
                        createNewMessageBox((String) msjObj.get("Recipient"), (String) msjObj.get("Sender"), msjObj.getCreatedAt(), (String) msjObj.get("Message"));
                    }
                    if (MessageList.size()>0) {
                        lastMessage=MessageList.get(MessageList.size() - 1);
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "An error occurred while retrieving messages from cache", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                mCallback.onLogout();
            }
        });

        btnNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onNewMessageClick();
            }
        });

        btnCheckNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity().getApplicationContext(), "Checking Messages ...", Toast.LENGTH_SHORT).show();

                ParseQuery<ParseObject> newMessagesQuery =ParseQuery.getQuery("Messages");
                if (lastMessage!=null) {
                    newMessagesQuery.whereGreaterThan("createdAt", lastMessage.getCreatedAt());
                }

                newMessagesQuery.orderByAscending("createdAt");
                newMessagesQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> MessageList, ParseException e) {
                        if (e == null) {
                            if (MessageList.size()==0)  Toast.makeText(getActivity().getApplicationContext(), "No new messages", Toast.LENGTH_SHORT).show();
                            else lastMessage=MessageList.get(MessageList.size() - 1);
                            for (int i = 0; i < MessageList.size(); i++) {
                                ParseObject msjObj = MessageList.get(i);
                                createNewMessageBox((String) msjObj.get("Recipient"), (String) msjObj.get("Sender"), msjObj.getCreatedAt(), (String) msjObj.get("Message"));
                            }
                            ParseObject.pinAllInBackground(MessageList); //save new messages
                        } else {
                            // Log.d("score", "Error: " + e.getMessage());
                            Toast.makeText(getActivity().getApplicationContext(), "An error occurred while retrieving messages", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return view;
    }

    public void createNewMessageBox(String recipient, String sender, Date dateAndTime, String message) {

        TextView viewSenderOrRecipient = new TextView(getActivity());
        TextView viewDateAndTime = new TextView(getActivity());
        TextView viewMessage = new TextView(getActivity());
        View line = new View(getActivity());
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5));
        line.setBackgroundColor(Color.parseColor("#B3B3B3"));

        RelativeLayout messageBox = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams messageBoxParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        messageBox.setLayoutParams(messageBoxParams);
        if (sender.equals(ParseUser.getCurrentUser().getUsername())){
            messageBox.setBackgroundColor(Color.parseColor("#0db467"));
            viewSenderOrRecipient.setText("To: "+recipient);
        }
        else {
            messageBox.setBackgroundColor(Color.parseColor("#0da6b4"));
            viewSenderOrRecipient.setText("From: "+sender);
        }

        GregorianCalendar calendar=new GregorianCalendar();
        calendar.setTime(dateAndTime);
        Integer year=calendar.get(Calendar.YEAR);
        Integer month=calendar.get(Calendar.MONTH)+1; //+1 because January=0 not 1 as expected
        Integer day=calendar.get(Calendar.DAY_OF_MONTH);
        Integer hours=calendar.get(Calendar.HOUR_OF_DAY);
        Integer minutes=calendar.get(Calendar.MINUTE);
        String minutesString=minutes.toString();
        if (minutes<10) minutesString="0"+minutesString;
        viewDateAndTime.setText(hours.toString()+":"+minutesString+" "+day.toString()+"/"+month.toString()+"/"+year.toString().replace("20",""));

        viewMessage.setText(message);
        viewMessage.setTextColor(Color.WHITE);

        RelativeLayout.LayoutParams senderParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        senderParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        senderParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        viewSenderOrRecipient.setLayoutParams(senderParams);

        viewDateAndTime.setId(generateViewId()); //generateViewID()=View.generateViewId() for lower than 17 APIs
        RelativeLayout.LayoutParams dateAndTimeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dateAndTimeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        dateAndTimeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        viewDateAndTime.setLayoutParams(dateAndTimeParams);

        RelativeLayout.LayoutParams messageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        messageParams.addRule(RelativeLayout.BELOW, viewDateAndTime.getId());
        viewMessage.setLayoutParams(messageParams);

        messageBox.addView(viewSenderOrRecipient);
        messageBox.addView(viewDateAndTime);
        messageBox.addView(viewMessage);
        messageBox.addView(line);
        messagesContainer.addView(messageBox);

        final ScrollView messagesScroll= (ScrollView) getView().findViewById(R.id.messagesScroll);
        messagesScroll.postDelayed(new Runnable() {
            public void run() {
                messagesScroll.fullScroll(View.FOCUS_DOWN);
            }
        },250);

    }

    @Override
    public void onStart() {
        super.onStart();
        final ScrollView messagesScroll= (ScrollView) getView().findViewById(R.id.messagesScroll);
        messagesScroll.postDelayed(new Runnable() {
            public void run() {
                messagesScroll.fullScroll(View.FOCUS_DOWN);
            }
        },250);
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

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
