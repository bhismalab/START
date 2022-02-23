package com.reading.start.tests.test_parent.data;

import android.content.Context;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_parent.domain.entity.ParentTestContent;
import com.reading.start.tests.test_parent.domain.entity.ParentTestContentChoices;
import com.reading.start.tests.test_parent.domain.entity.TestData;
import com.reading.start.tests.test_parent.domain.entity.TestDataItem;
import com.reading.start.tests.test_parent.domain.entity.TestDataItemChoices;
import com.reading.start.tests.test_parent.domain.entity.TestDataVideoItem;

import java.util.List;
import java.util.Locale;

import io.realm.Realm;

public class ParentTestTrainingModel {
    private static final String TAG = ParentTestTrainingModel.class.getSimpleName();

    public void fillTestDate(Context context, TestData value) {
        if (value != null) {
            try {
                value.getItems().clear();
                Realm realm = DataProvider.getInstance(context).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<ParentTestContent> results = realm.where(ParentTestContent.class).findAll();

                    if (results != null && results.size() > 0) {

                        for (ParentTestContent item : results) {
                            List<ParentTestContentChoices> resultChoices = realm.where(ParentTestContentChoices.class)
                                    .equalTo(ParentTestContentChoices.FILED_ID_CONTENT, item.getId()).findAll();

                            TestDataItemChoices choices = new TestDataItemChoices();

                            for (ParentTestContentChoices cItem : resultChoices) {
                                choices.getChoicesEnglish().add(cItem.getChoiceEnglish());
                                choices.getChoicesHindi().add(cItem.getChoiceHindi());
                            }

                            if (item.getType().equals("video")) {
                                value.getVideoItems().add(new TestDataVideoItem(item.getQuestion(), item.getQuestion_hindi(), item.getVideo1(), item.getVideo2(), choices));
                            } else {
                                value.getItems().add(new TestDataItem(item.getQuestion(), item.getQuestion_hindi(), choices));
                            }
                        }
                    }
                }

                realm.close();
            } catch (Exception e) {
                TestLog.e(TAG, e);
            }
        }
    }
}
