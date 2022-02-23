package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.States;
import com.reading.start.general.TLog;
import com.reading.start.utils.BitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.realm.Realm;

public class ChildModel extends BaseModel {
    private static final String TAG = ChildModel.class.getSimpleName();

    public ChildModel(Realm realm) {
        super(realm);
    }

    /**
     * Saves child info
     */
    public Observable<Children> getSaveChildObservable(Children child,
                                                       String name, String surname, String patronymic, String state, String hand,
                                                       String address, String gender, long birthDate, String latitude,
                                                       String longitude, long addDatetime,
                                                       String addBy, long modDatetime, String modBy,
                                                       boolean isDeleted, File photoFile,
                                                       String doctorName, String diagnosis, long diagnosisDate) {
        Observable<Children> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (child.isManaged()) {
                    if (realm != null && !realm.isClosed() && child != null) {
                        try {
                            realm.beginTransaction();
                            child.setName(name);
                            child.setSurname(surname);
                            child.setPatronymic(patronymic);
                            child.setState(state);
                            child.setHand(hand);
                            child.setAddress(address);
                            child.setGender(gender);
                            child.setBirthDate(birthDate);
                            child.setLatitude(latitude);
                            child.setLongitude(longitude);
                            child.setAddDateTime(addDatetime);
                            child.setAddBy(addBy);
                            child.setModDateTime(modDatetime);
                            child.setModBy(modBy);
                            child.setIsDeleted(isDeleted);
                            child.setDiagnosisClinic(doctorName);
                            child.setDiagnosis(diagnosis);
                            child.setDiagnosisDate(diagnosisDate);

                            if (photoFile != null) {
                                child.setPhoto(BitmapUtils.fileToBase64(photoFile));
                            }

                            child.setPhotoPath(null);
                            realm.commitTransaction();
                        } catch (Exception e) {
                            realm.cancelTransaction();
                        }
                    }
                } else {
                    child.setName(name);
                    child.setSurname(surname);
                    child.setPatronymic(patronymic);
                    child.setState(state);
                    child.setHand(hand);
                    child.setAddress(address);
                    child.setGender(gender);
                    child.setBirthDate(birthDate);
                    child.setLatitude(latitude);
                    child.setLongitude(longitude);
                    child.setAddDateTime(addDatetime);
                    child.setAddBy(addBy);
                    child.setModDateTime(modDatetime);
                    child.setModBy(modBy);
                    child.setIsDeleted(isDeleted);
                    child.setPhotoPath(photoFile);
                    child.setDiagnosisClinic(doctorName);
                    child.setDiagnosis(diagnosis);
                    child.setDiagnosisDate(diagnosisDate);
                }

                if (child != null) {
                    subscriber.onNext(child);
                    subscriber.onComplete();
                } else {
                    subscriber.onError(new Exception("Unable to save object to Realm"));
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });

        return result;
    }

    public Children getChild(int childId) {
        Children result = null;
        Realm realm = null;

        try {
            realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
            if (realm != null && !realm.isClosed()) {
                result = realm.where(Children.class)
                        .equalTo(Children.FILED_ID, childId).findFirst();
            } else {
                TLog.d(TAG, "Realm closed");
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    /**
     * Gets all states
     */
    public ArrayList<States> getStates() {
        ArrayList<States> result = new ArrayList<>();
        Realm realm = null;

        try {
            realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

            if (realm != null && !realm.isClosed()) {
                List<States> states = realm.where(States.class).findAll();

                if (states != null) {
                    result.addAll(states);
                    Collections.sort(result, (o1, o2) -> {
                        if (o1 != null && o2 != null) {
                            return o1.getNameEnglish().compareTo(o2.getNameEnglish());
                        } else {
                            return 0;
                        }
                    });
                }
            } else {
                TLog.d(TAG, "Realm closed");
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    /**
     * Gets hands list
     */
    public ArrayList<Integer> getHandState() {
        return Constants.HAND_LIST;
    }
}
