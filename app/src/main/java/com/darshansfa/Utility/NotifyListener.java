package com.darshansfa.Utility;

/**
 * Created by Nikhil on 09-05-2017.
 */

public interface NotifyListener {

    public void onStart();

    public void onCompleted(String id);

    public void onError(String error);
}
