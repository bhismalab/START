package com.reading.start.sdk.utils;

import android.content.ContentResolver;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.reading.start.sdk.general.SdkLog;

import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class SDCardUtils {
    private static final String TAG = SDCardUtils.class.getSimpleName();

    private static final long K = 1024;

    private static final long M = K * K;

    private static final long G = M * K;

    private static final long T = G * K;

    public static String convertToStringRepresentation(final long value) {
        String result = "0";

        try {
            final long[] dividers = new long[]{T, G, M, K, 1};
            final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};

            if (value > 0) {
                for (int i = 0; i < dividers.length; i++) {
                    final long divider = dividers[i];
                    if (value >= divider) {
                        result = format(value, divider, units[i]);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "SDCardUtils", e);
        }

        return result;
    }

    private static String format(final long value, final long divider, final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;
        return new DecimalFormat("#,##0.#").format(result) + " " + unit;
    }

    public static long dirSize(File dir) {
        long result = 0;

        try {
            Stack<File> dirlist = new Stack<File>();
            dirlist.clear();

            dirlist.push(dir);

            while (!dirlist.isEmpty()) {
                File dirCurrent = dirlist.pop();

                File[] fileList = dirCurrent.listFiles();
                for (File f : fileList) {
                    if (f.isDirectory())
                        dirlist.push(f);
                    else
                        result += f.length();
                }
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "dirSize", e);
        }

        return result;
    }

    public static boolean checkBitmapToFile(String urlImage) {
        boolean result = false;

        try {
            if (urlImage != null) {
                URL url = new URL(urlImage);

                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, bounds);
                result = true;
            }
        } catch (Exception e) {
            // no need log this error
            //SdkLog.e(TAG, "checkBitmapToFile", e);
        }

        return result;
    }

    public static boolean isAvailable() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }

        return false;
    }

    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    public static long getSdCardFreeSpace() {
        long result = 0;

        try {
            File file = new File(getSdCardPath());

            if (file != null && file.exists()) {
                result = file.getFreeSpace();
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "getSdCardFreeSpace", e);
        }

        return result;
    }

    public static int countLines(String text) {
        int count = 0;

        try {
            String[] lines = text.split("\r\n|\r|\n");
            count = lines.length;
        } catch (Exception e) {
            SdkLog.e(TAG, "countLines", e);
        }

        return count;
    }

    public static int countLines(File file) {
        int count = 0;

        try {
            if (file != null && file.exists()) {
                String text = readFileToString(file);

                if (text != null) {
                    count = countLines(text);
                }
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "countLines", e);
        }

        return count;
    }

    public static void deleteAllFile(File folder) {
        try {
            if (folder != null && folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();

                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isFile()) {
                            files[i].delete();
                        } else if (files[i].isDirectory()) {
                            deleteAllFile(files[i]);
                        }
                    }
                }

                folder.delete();
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "deleteAllFile", e);
        }
    }

    public static void deleteAllFileFirsLevel(File folder) {
        try {
            if (folder != null && folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();

                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isFile()) {
                            files[i].delete();
                        } else if (files[i].isDirectory()) {
                            deleteAllFile(files[i]);
                        }
                    }
                }

                folder.delete();
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "deleteAllFileFirsLevel", e);
        }
    }

    public static boolean isSdCardMounted() {
        return (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED));
    }

    public static boolean ensurePathExist(String path) {
        boolean creted = false;

        if (path != null) {
            File file = new File(path);

            if (!(creted = file.exists())) {
                creted = file.mkdirs();
            }
        }

        return creted;
    }

    public static byte[] readByteFile(String path) {
        byte[] result = null;

        if (path != null) {
            File file = new File(path);
            int size = (int) file.length();
            result = new byte[size];
            BufferedInputStream buf = null;

            try {
                buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(result, 0, result.length);
            } catch (FileNotFoundException e) {
                SdkLog.e(TAG, "readByteFile", e);
            } catch (IOException e) {
                SdkLog.e(TAG, "readByteFile", e);
            } finally {
                try {
                    buf.close();
                } catch (IOException e) {
                    SdkLog.e(TAG, "readByteFile", e);
                }
            }
        }

        return result;
    }

    public static boolean writeDocumentToFile(File file, Document doc) {
        boolean result = false;
        try {
            if (doc != null) {
                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();
                StringWriter writer = new StringWriter();
                StreamResult sresult = new StreamResult(writer);
                transformer.transform(new DOMSource(doc), sresult);
                String text = writer.toString();
                byte[] data = text.getBytes();
                result = writeBytesToFile(file, data);
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "writeDocumentToFile", e);
        }

        return result;
    }

    public static boolean writeStringToFile(String path, String text) {
        boolean result = false;

        if (text != null) {
            byte[] data = text.getBytes();
            result = writeBytesToFile(path, data);
        }

        return result;
    }

    public static boolean writeStringToFile(File file, String text) {
        boolean result = false;

        if (text != null) {
            byte[] data = text.getBytes();
            result = writeBytesToFile(file, data);
        }

        return result;
    }

    public static boolean writeBytesToFile(String path, byte[] data) {
        boolean result = false;

        try {
            result = writeBytesToFile(new File(path), data);
        } catch (Exception e) {
            SdkLog.e(TAG, "writeBytesToFile", e);
        }

        return result;
    }

    public static boolean writeBytesToFile(File file, byte[] data) {
        boolean result = false;
        FileOutputStream out = null;

        try {
            if (file != null && data != null) {
                if (file.exists()) {
                    file.delete();
                }

                file.createNewFile();
                out = new FileOutputStream(file);
                out.write(data);
            }
        } catch (IOException e) {
            SdkLog.e(TAG, "writeBytesToFile", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                    result = true;
                } catch (IOException e) {
                    SdkLog.e(TAG, "writeBytesToFile", e);
                }
            }

            data = null;
        }

        return result;
    }

    public static boolean ensurePathExist(File file) {
        boolean creted = false;

        if (file != null) {
            if (!(creted = file.exists())) {
                creted = file.mkdirs();
            }
        }

        return creted;
    }

    public static void removeDir(File file, boolean deleteRoot) {
        if (file.exists() && file.isDirectory()) {
            File[] list = file.listFiles();

            if (list != null && list.length > 0) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isDirectory()) {
                        removeDir(list[i].getPath(), true);
                    } else {
                        try {
                            list[i].delete();
                        } catch (Exception ex) {
                            SdkLog.e(TAG, "removeDir", ex);
                        }
                    }
                }
            }

            if (deleteRoot) {
                try {
                    file.delete();
                } catch (Exception ex) {
                    SdkLog.e(TAG, "removeDir", ex);
                }
            }
        }
    }

    public static void removeDir(String path, boolean deleteRoot) {
        if (path != null && path.length() > 0) {
            File f = new File(path);
            removeDir(f, deleteRoot);
        }
    }

    public static boolean copyFileToFile(File source, File destination) {
        boolean value = false;
        InputStream in = null;
        OutputStream out = null;

        try {
            if (source != null && source.exists() && destination != null
                    && destination.exists()) {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "copyFileToFile", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    SdkLog.e(TAG, "copyFileToFile", e);
                }
            }

            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    SdkLog.e(TAG, "copyFileToFile", e);
                }
            }

            value = true;
        }

        return value;
    }

    public static boolean copyStreamToFile(InputStream in, File destination) {
        boolean value = false;
        OutputStream out = null;

        try {
            if (in != null && destination != null) {
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "copyStreamToFile", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    SdkLog.e(TAG, "copyStreamToFile", e);
                }
            }

            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    SdkLog.e(TAG, "copyStreamToFile", e);
                }
            }

            value = true;
        }

        return value;
    }

    public static boolean copyUriToFile(Uri source, File destination, ContentResolver contentResolver) {
        boolean value = false;
        InputStream in = null;
        OutputStream out = null;

        try {
            if (contentResolver != null && source != null && destination != null
                    && destination.exists()) {
                in = contentResolver.openInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "copyUriToFile", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    SdkLog.e(TAG, "copyUriToFile", e);
                }
            }

            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    SdkLog.e(TAG, "copyUriToFile", e);
                }
            }

            value = true;
        }

        return value;
    }

    public static String readFileToString(File file) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;

            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            reader.close();
        } catch (IOException e) {
            SdkLog.e(TAG, "readFileToString", e);
        }

        return stringBuilder.toString();
    }

    public static class StorageInfo {
        public final String path;

        public final boolean readonly;

        public final boolean removable;

        public final int number;

        StorageInfo(String path, boolean readonly, boolean removable, int number) {
            this.path = path;
            this.readonly = readonly;
            this.removable = removable;
            this.number = number;
        }

        public String getDisplayName() {
            StringBuilder res = new StringBuilder();
            if (!removable) {
                res.append("Internal SD card");
            } else if (number > 1) {
                res.append("SD card " + number);
            } else {
                res.append("SD card");
            }
            if (readonly) {
                res.append(" (Read only)");
            }

            File file = new File(path);
            res.append(" (" + file.getName() + ")");

            return res.toString();
        }

        public String getPath() {
            StringBuilder res = new StringBuilder();
            res.append(path);
            return path.toString();
        }
    }

    @SuppressWarnings("unused")
    public static List<StorageInfo> getStorageList1() {
        List<StorageInfo> list = new ArrayList<StorageInfo>();
        BufferedReader buf_reader = null;

        try {
            String def_path = Environment.getExternalStorageDirectory().getPath();
            boolean def_path_removable = Environment.isExternalStorageRemovable();
            String def_path_state = Environment.getExternalStorageState();
            boolean def_path_available = def_path_state
                    .equals(Environment.MEDIA_MOUNTED)
                    || def_path_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
            boolean def_path_readonly = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED_READ_ONLY);

            HashSet<String> paths = new HashSet<String>();
            int cur_removable_number = 1;

            if (def_path_available) {
                paths.add(def_path);
                list.add(0, new StorageInfo(def_path, def_path_readonly,
                        def_path_removable, def_path_removable ? cur_removable_number++
                        : -1));
            }

            buf_reader = new BufferedReader(new FileReader("/proc/mounts"));
            String line;
            SdkLog.d(TAG, "/proc/mounts");
            while ((line = buf_reader.readLine()) != null) {
                SdkLog.d(TAG, line);
                if (line.contains("vfat") || line.contains("/mnt")) {
                    StringTokenizer tokens = new StringTokenizer(line, " ");

                    String unused = tokens.nextToken(); // device
                    String mount_point = tokens.nextToken(); // mount point
                    if (paths.contains(mount_point)) {
                        continue;
                    }
                    unused = tokens.nextToken(); // file system
                    List<String> flags = Arrays.asList(tokens.nextToken().split(",")); // flags
                    boolean readonly = flags.contains("ro");

                    if (line.contains("/dev/block/vold")) {
                        if (!line.contains("/mnt/secure") && !line.contains("/mnt/asec")
                                && !line.contains("/mnt/obb") && !line.contains("/dev/mapper")
                                && !line.contains("tmpfs")) {
                            paths.add(mount_point);
                            list.add(new StorageInfo(mount_point, readonly, true,
                                    cur_removable_number++));
                        }
                    }
                }
            }

        } catch (Exception e) {
            SdkLog.e(TAG, "getStorageList1", e);
        } finally {
            if (buf_reader != null) {
                try {
                    buf_reader.close();
                } catch (Exception e) {
                    SdkLog.e(TAG, "getStorageList1", e);
                }
            }
        }

        return list;
    }

    public static List<StorageInfo> getStorageList2() {
        List<StorageInfo> list = new ArrayList<>();
        BufferedReader buf_reader = null;

        try {
            final String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {  // we can read the External Storage...
                //Retrieve the primary External Storage:
                final File primaryExternalStorage = Environment.getExternalStorageDirectory();
                //Retrieve the External Storages root directory:
                final String externalStorageRootDir;

                if ((externalStorageRootDir = primaryExternalStorage.getParent()) == null) {  // no parent...
                    list.add(new StorageInfo(primaryExternalStorage.getAbsolutePath(), false, true, 0));
                } else {
                    final File externalStorageRoot = new File(externalStorageRootDir);
                    final File[] files = externalStorageRoot.listFiles();

                    if (files != null && files.length > 0) {
                        for (final File file : files) {
                            if (file.isDirectory() && file.canRead() && (file.listFiles().length > 0)) {  // it is a real directory (not a USB drive)...

                                if (primaryExternalStorage.getAbsolutePath().equals(file.getAbsolutePath())) {
                                    list.add(0, new StorageInfo(file.getAbsolutePath(), false, false, 0));
                                } else {
                                    int index = list.size() > 0 ? list.size() + 1 : 1;
                                    list.add(new StorageInfo(file.getAbsolutePath(), false, true, index));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            SdkLog.e(TAG, "getStorageList2", e);
        } finally {
            if (buf_reader != null) {
                try {
                    buf_reader.close();
                } catch (Exception e) {
                    SdkLog.e(TAG, "getStorageList2", e);
                }
            }
        }

        return list;
    }

    public static boolean copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation) {
        boolean result = false;

        try {
            if (sourceLocation != null && sourceLocation.exists() && targetLocation != null)
                if (sourceLocation.isDirectory()) {
                    if (!targetLocation.exists()) {
                        targetLocation.mkdir();
                    }

                    String[] children = sourceLocation.list();

                    if (children != null && children.length > 0) {
                        for (int i = 0; i < children.length; i++) {
                            if (children != null && children.length > 0) {
                                result = copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
                            }
                        }
                    }
                } else {
                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();
                    result = true;
                }
        } catch (Exception e) {
            SdkLog.e(TAG, "copyDirectoryOneLocationToAnotherLocation", e);
        }

        return result;
    }
}
