package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.Languages;
import com.reading.start.domain.entity.Parent;
import com.reading.start.domain.entity.States;
import com.reading.start.general.TLog;
import com.reading.start.presentation.ui.UiConstants;
import com.reading.start.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.realm.Realm;

public class ParentModel extends BaseModel {
    private static final String TAG = ParentModel.class.getSimpleName();

    public ParentModel(Realm realm) {
        super(realm);
    }

    /**
     * Returns observable for save children.
     */
    public Observable<Children> getSaveChildObservable(final Children child) {
        Observable<Children> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
                Children instance = null;

                if (realm != null && !realm.isClosed()) {
                    try {
                        realm.beginTransaction();

                        Number currentChildIdNum = realm.where(Children.class).max(Children.FILED_ID);
                        Number currentParentIdNum = realm.where(Parent.class).max(Parent.FILED_ID);
                        int nextChildId;
                        int nextParentId;

                        if (currentChildIdNum == null) {
                            nextChildId = 0;
                        } else {
                            nextChildId = currentChildIdNum.intValue() + 1;
                        }

                        if (currentParentIdNum == null) {
                            nextParentId = 1;
                        } else {
                            nextParentId = currentParentIdNum.intValue() + 1;
                        }

                        child.setId(nextChildId);
                        child.setIdWorker(AppCore.getInstance().getPreferences().getLoginWorkerId());

                        if (child.getPhotoPath() != null) {
                            child.setPhoto(BitmapUtils.fileToBase64(child.getPhotoPath()));
                            child.setPhotoPath(null);
                        }

                        if (child.getParentFirst() != null) {
                            child.getParentFirst().setChildId(nextChildId);
                            child.getParentFirst().setId(nextParentId);
                            nextParentId++;
                        }

                        if (child.getParentSecond() != null) {
                            child.getParentSecond().setChildId(nextChildId);
                            child.getParentSecond().setId(nextParentId);
                        }

                        instance = realm.copyToRealmOrUpdate(child);

                        realm.commitTransaction();
                    } catch (Exception e) {
                        realm.cancelTransaction();
                    }
                }

                if (instance != null) {
                    subscriber.onNext(instance);
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

    /**
     * Gets child by id.
     */
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
     * Save parent info
     */
    public Observable<Children> getSaveParentObservable(Children child, int idParent,
                                                        String childRelationship, String name, String surname,
                                                        String patronymic, String state, String address,
                                                        String gender, long birthDate, String spokenLanguage,
                                                        String phone, String email, long addDateTime,
                                                        String addBy, long modDateTime, String modBy,
                                                        boolean isDeleted, String preferableContact) {
        Observable<Children> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (child.isManaged()) {
                    Parent parent = idParent == UiConstants.PARENT_ID_2 ? child.getParentSecond() : child.getParentFirst();

                    if (realm != null && !realm.isClosed() && parent != null) {
                        try {
                            realm.beginTransaction();
                            parent.setChildRelationship(childRelationship);
                            parent.setName(name);
                            parent.setSurname(surname);
                            parent.setPatronymic(patronymic);
                            parent.setState(state);
                            parent.setAddress(address);
                            parent.setGender(gender);
                            parent.setBirthDate(birthDate);
                            parent.setSpokenLanguage(spokenLanguage);
                            parent.setPhone(phone);
                            parent.setEmail(email);
                            parent.setAddDateTime(addDateTime);
                            parent.setAddBy(addBy);
                            parent.setModDateTime(modDateTime);
                            parent.setModBy(modBy);
                            parent.setIsDeleted(isDeleted);
                            parent.setPreferableContact(preferableContact);
                            realm.commitTransaction();
                        } catch (Exception e) {
                            realm.cancelTransaction();
                        }
                    }
                } else {
                    Parent parent = null;

                    if (idParent == UiConstants.PARENT_ID_2) {
                        if (child.getParentSecond() == null) {
                            child.setParentSecond(new Parent());
                        }

                        parent = child.getParentSecond();
                    } else {
                        if (child.getParentFirst() == null) {
                            child.setParentFirst(new Parent());
                        }

                        parent = child.getParentFirst();
                    }

                    parent.setChildRelationship(childRelationship);
                    parent.setName(name);
                    parent.setSurname(surname);
                    parent.setPatronymic(patronymic);
                    parent.setState(state);
                    parent.setAddress(address);
                    parent.setGender(gender);
                    parent.setBirthDate(birthDate);
                    parent.setSpokenLanguage(spokenLanguage);
                    parent.setPhone(phone);
                    parent.setEmail(email);
                    parent.setAddDateTime(addDateTime);
                    parent.setAddBy(addBy);
                    parent.setModDateTime(modDateTime);
                    parent.setModBy(modBy);
                    parent.setIsDeleted(isDeleted);
                    parent.setPreferableContact(preferableContact);
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

    /**
     * Gets states
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
     * Gets languages
     */
    public ArrayList<Languages> getLanguages() {
        ArrayList<Languages> result = new ArrayList<>();
        Realm realm = null;

        try {
            realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

            if (realm != null && !realm.isClosed()) {
                List<Languages> languages = realm.where(Languages.class).findAll();

                if (languages != null) {
                    result.addAll(languages);
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
     * Gets preferable contact list
     */
    public ArrayList<Integer> getPreferableContact() {
        return Constants.PREFERABLE_CONTACT_LIST;
    }
}
