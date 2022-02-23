package com.reading.start.tests;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import io.fabric.sdk.android.Fabric;

/**
 * Intended for communication main application and tests modules.
 * Provide all test for the main application.
 */
public class TestsProvider {
    private static final String TAG = TestsProvider.class.getSimpleName();

    private static TestsProvider sInstance = null;

    private static Context sContext = null;

    private static ILanguage sILanguage = null;

    /**
     * Get current language of UI.
     */
    public static ILanguage getLanguage() {
        return sILanguage;
    }

    /**
     * Set current language of UI.
     */
    public static void setLanguage(ILanguage language) {
        sILanguage = language;
    }

    public static TestsProvider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TestsProvider();
            Fabric.with(context, new Crashlytics());
        }

        sContext = context;
        return sInstance;
    }

    private ArrayList<ITestModule> mAllDisplayingTestModules = null;

    private ArrayList<ITestModule> mAllProcessingTestModules = null;

    /**
     * Get all tests modules.
     */
    public ArrayList<ITestModule> getAllDisplayingTestModules() {
        if (mAllDisplayingTestModules == null) {
            ArrayList<ITestModule> result = null;

            try {
                result = new ArrayList<>();
                ArrayList<Class> candidates = new ArrayList<>();
                PathClassLoader classLoader = (PathClassLoader) sContext.getClassLoader();
                String packageCodePath = sContext.getPackageCodePath();
                DexFile dexFile = new DexFile(packageCodePath);
                Enumeration<String> entries = dexFile.entries();

                while (entries.hasMoreElements()) {
                    String entry = entries.nextElement();
                    Class<?> entryClass = dexFile.loadClass(entry, classLoader);

                    if (entryClass != null) {
                        Class<?>[] interfaces = entryClass.getInterfaces();

                        if (interfaces != null && interfaces.length > 0) {
                            for (int i = 0; i < interfaces.length; i++) {
                                if (interfaces[i] == ITestModule.class) {
                                    candidates.add(entryClass);

                                    try {
                                        Constructor constructor = entryClass.getDeclaredConstructor(Context.class);
                                        constructor.setAccessible(true);
                                        ITestModule module = (ITestModule) constructor.newInstance(sContext);
                                        result.add(module);
                                    } catch (Exception e) {
                                        TestLog.e(TAG, e);
                                    }
                                }
                            }
                        }
                    }
                }

                if (result != null && result.size() > 0) {
                    Collections.sort(result, (o1, o2) -> Integer.compare(o1.getIndexDisplaying(), o2.getIndexDisplaying()));
                    mAllDisplayingTestModules = result;
                }
            } catch (Exception e) {
                TestLog.e(TAG, e);
            }
        }

        return mAllDisplayingTestModules;
    }

    public ArrayList<ITestModule> getAllProcessingTestModules() {
        if (mAllProcessingTestModules == null) {
            ArrayList<ITestModule> displaying = getAllDisplayingTestModules();

            if (displaying != null && displaying.size() > 0) {
                mAllProcessingTestModules = new ArrayList<>();
                mAllProcessingTestModules.addAll(displaying);
                Collections.sort(mAllProcessingTestModules, (o1, o2) -> Integer.compare(o1.getIndexProcessing(), o2.getIndexProcessing()));
            }
        }

        return mAllProcessingTestModules;
    }

    /**
     * Get next test module.
     *
     * @param test current test module.
     */
    public ITestModule getNextTest(ITestModule test) {
        ITestModule result = null;

        if (test != null && mAllDisplayingTestModules != null && mAllDisplayingTestModules.size() > 0) {
            int index = mAllDisplayingTestModules.indexOf(test);

            if (index > -1 && index < mAllDisplayingTestModules.size() - 1) {
                result = mAllDisplayingTestModules.get(index + 1);
            }
        }

        return result;
    }

    /**
     * Check is module last in the list.
     */
    public boolean isLastTest(ITestModule test) {
        boolean result = false;

        if (test != null && mAllDisplayingTestModules != null && mAllDisplayingTestModules.size() > 0) {
            int index = mAllDisplayingTestModules.indexOf(test);

            if (index == mAllDisplayingTestModules.size() - 1) {
                result = true;
            }
        }

        return result;
    }
}
