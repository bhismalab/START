package com.reading.start.utils;

import com.reading.start.AppCore;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.SocialWorker;
import com.reading.start.domain.entity.Survey;
import com.reading.start.general.TLog;
import com.reading.start.tests.DatabaseLog;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import io.realm.Realm;

/**
 * Intended for making database data dump. Use only for testing and debug
 */
public class DatabaseLogHelper {
    private static final String TAG = DatabaseLogHelper.class.getSimpleName();

    /**
     * Make dam of all records from SocialWorker, Children, Survey
     */
    public static void makeDump() {
        BufferedWriter buffer = null;
        Realm realm = null;

        try {
            buffer = DatabaseLog.getBufferedWriter();

            if (buffer != null) {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                buffer.append("-------------------------- SOCIAL WORKERS DUMP --------------------------");
                buffer.newLine();
                buffer.append("ID | SERVER ID | USER NAME | TOKEN");
                buffer.newLine();

                if (realm != null && !realm.isClosed()) {
                    List<SocialWorker> socialWorkers = realm.where(SocialWorker.class).findAll();

                    if (socialWorkers != null && socialWorkers.size() > 0) {
                        for (SocialWorker sItem : socialWorkers) {
                            buffer.append(sItem.getId() + " | " + sItem.getIdServer() + " | " + sItem.getEmail() + " | " + sItem.getToken());
                            buffer.newLine();
                        }
                    }
                }

                buffer.newLine();
                buffer.append("-------------------------- CHILD DUMP --------------------------");
                buffer.newLine();
                buffer.append("ID | SERVER ID | SOCIAL WORKER ID | NAME | SURE NAME");
                buffer.newLine();

                if (realm != null && !realm.isClosed()) {
                    List<Children> children = realm.where(Children.class).findAll();

                    if (children != null && children.size() > 0) {
                        for (Children cItem : children) {
                            buffer.append(cItem.getId() + " | " + cItem.getIdServer() + " | " + cItem.getIdWorker() + " | " + cItem.getName() + " | " + cItem.getSurname());
                            buffer.newLine();
                        }
                    }
                }

                buffer.append("-------------------------- SURVEYS DUMP --------------------------");
                buffer.newLine();
                buffer.append("ID | SERVER ID | CHILD ID");
                buffer.newLine();

                if (realm != null && !realm.isClosed()) {
                    List<Survey> children = realm.where(Survey.class).findAll();

                    if (children != null && children.size() > 0) {
                        for (Survey sItem : children) {
                            buffer.append(sItem.getId() + " | " + sItem.getIdServer() + " | " + sItem.getChildId());
                            buffer.newLine();
                        }
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }

            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    TLog.e(TAG, e);
                }
            }
        }
    }
}
