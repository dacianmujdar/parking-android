package com.android.app.parkinglots;

/*
    Interface for observable
    Must implement public method update - the update function will be called when the observer triggers the signal
 */
public interface Observable {

    void registerObserver(Observer observable);
    void removeObserver(Observer observable);
    void notifyObservers(boolean value);
}