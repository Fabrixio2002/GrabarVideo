package hn.uth.grabarvideo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2;
    private VideoView videoView;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);

        Button btnStartRecording = findViewById(R.id.btn_start_recording);
        btnStartRecording.setOnClickListener(view -> {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        });

        Button btnSaveVideo = findViewById(R.id.btn_save_video);
        btnSaveVideo.setOnClickListener(view -> {
            if (videoUri != null) {
                saveVideoToStorage();
            } else {
                Toast.makeText(this, "Debes grabar un video primero", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = data.getData();
            if (videoUri != null) {
                // Reproducir el video grabado en el VideoView
                videoView.setVideoURI(videoUri);
                videoView.start();
            }
        }
    }

    private void saveVideoToStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso para escribir en el almacenamiento externo si aún no está concedido
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            // Ya se ha concedido el permiso, guardar el video
            String filename = "mi_video.mp4";
            File destinationFile = new File(getExternalFilesDir(null), filename);

            try {
                InputStream in = getContentResolver().openInputStream(videoUri);
                OutputStream out = new FileOutputStream(destinationFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();
                Toast.makeText(this, "Video guardado en el almacenamiento externo", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo guardar el video", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, guardar el video
                saveVideoToStorage();
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede guardar el video.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

