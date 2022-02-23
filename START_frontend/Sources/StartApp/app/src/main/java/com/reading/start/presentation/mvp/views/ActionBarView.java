package com.reading.start.presentation.mvp.views;

public interface ActionBarView {

    /**
     * Raises when back button pressed
     */
    void onBackPressed();

    /**
     * Raises when home button pressed
     */
    void onHomePressed();

    /**
     * Raises when navigation menu pressed
     */
    void onNavigationMenuPressed();
}
