package com.app4chat;

/**
 * Created by Paul on 16/12/2014.
 */
public interface FragmentsListener {

    public void onLinkToLoginClick();
    public void onLinkToRegisterClick();
    public void onSuccess();
    public void onLogout();
    public void onNewMessageClick();
}