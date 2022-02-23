package com.reading.start.data;

import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.Consent;
import com.reading.start.domain.entity.Languages;
import com.reading.start.domain.entity.Parent;
import com.reading.start.domain.entity.SocialWorker;
import com.reading.start.domain.entity.States;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;

import io.realm.annotations.RealmModule;

/**
 * Represent module for Realm.
 */
@RealmModule(classes = {Children.class, Parent.class, SocialWorker.class, Survey.class,
        States.class, Languages.class, Consent.class, SurveyStatus.class})
public class StartAppModule {
}
