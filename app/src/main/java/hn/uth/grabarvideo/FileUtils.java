package hn.uth.grabarvideo;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static boolean copyFileToExternalStorage(Context context, String sourceFilePath, String destinationFileName) {
        try {
            File sourceFile = new File(sourceFilePath);
            File destinationDir = new File(Environment.getExternalStorageDirectory(), "MyVideos");

            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }

            File destinationFile = new File(destinationDir, destinationFileName);
            if (destinationFile.exists()) {
                destinationFile.delete();
            }

            FileChannel source = new FileInputStream(sourceFile).getChannel();
            FileChannel destination = new FileOutputStream(destinationFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            return true;
        } catch (IOException e) {
            Log.e("FileUtils", "Error al copiar el archivo: " + e.getMessage());
            return false;
        }
    }
}
