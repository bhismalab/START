package com.reading.start.presentation.mvp.views;

import com.reading.start.domain.entity.Children;

public interface MySurveysView {

    /**
     * Raises when pressed add new child
     */
    void onAddChild();

    /**
     * Raises when pressed by child
     */
    void onChildInformationOpen(boolean restore, Children child);

    /**
     * Raises when pressed navigation menu
     */
    void onNavigationMenuPressed();

    /**
     * Raises when caused error
     */
    void onError(String message);
}
