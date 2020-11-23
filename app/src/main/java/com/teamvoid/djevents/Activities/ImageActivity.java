package com.teamvoid.djevents.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;
import com.teamvoid.djevents.Utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "ImageActivity";

    // Elements
    private ImageButton ibBack;
    private ImageView ivImage;
    private Button btnSaveImage;
    private ProgressBar progressBar;

    // Variables
    private boolean image, isPost, isEvent, isEdit, isCamera;
    private final int REQUEST_CODE = 123, PICK_IMAGE = 124, REQUEST_CAMERA = 125, CAMERA = 126;
    private Uri tempPhotoUri, photoUri;
    private String tempFilePath, photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Data Binding
        init();

        Intent callingIntent = getIntent();
        if (callingIntent.hasExtra(Constants.POSTS)) {
            isPost = true;
            isCamera = callingIntent.hasExtra(Constants.CAMERA);
        } else if (callingIntent.hasExtra(Constants.EVENTS))
            isEvent = true;
        else if (callingIntent.hasExtra(Constants.EDIT_PROFILE))
            isEdit = true;
        else Toast.makeText(this, "Must define calling intent", Toast.LENGTH_SHORT).show();

        if (isCamera) checkCameraPermission();
        else checkReadStoragePermission();

        // Clicks
        ibBack.setOnClickListener(view -> this.onBackPressed());

        btnSaveImage.setOnClickListener(view -> {
            Log.d(TAG, "onCreate: image: " + image);
            Log.d(TAG, "onCreate: Path: " + photoPath);
            if (image && photoPath != null) {
                if (isEdit) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(Constants.PHOTO_PATH, photoPath);
                    setResult(RESULT_OK, resultIntent);
                    this.finish();
                }

                Intent intent;
                if (isPost)
                    intent = new Intent(ImageActivity.this, AddPostActivity.class);
                else if (isEvent)
                    intent = new Intent(ImageActivity.this, AddEventActivity.class);
                else return;
                intent.putExtra(Constants.PHOTO_PATH, photoPath);
                startActivity(intent);
                this.finish();
            } else Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbarImage);
        setSupportActionBar(toolbar);
        ibBack = findViewById(R.id.ibImageBack);
        ivImage = findViewById(R.id.ivImage);
        btnSaveImage = findViewById(R.id.btnImageSave);
        progressBar = findViewById(R.id.progressImage);
    }

    private void checkCameraPermission() {
        boolean cameraPermission = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionsList = new ArrayList<>();
        if (!cameraPermission) permissionsList.add(Manifest.permission.CAMERA);
        if (!storagePermission)
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionsList.size() == 0) {
            dispatchCameraIntent();
            return;
        }

        String[] permissions = new String[permissionsList.size()];
        for (int i = 0; i < permissionsList.size(); i++)
            permissions[i] = permissionsList.get(i);

        requestPermissions(permissions, REQUEST_CAMERA);
    }

    private void checkReadStoragePermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else pickImageFromGallery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        }
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Please grant all the permissions", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
            }
            dispatchCameraIntent();
        } else this.finish();
    }

    private void pickImageFromGallery() {
        Log.d(TAG, "pickImageFromGallery: Getting photo...");
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select image"), PICK_IMAGE);
    }

    private void dispatchCameraIntent() {
        // Create camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "dispatchCameraIntent: Creating a temporary file...");
            // Create a temporary file
            File tempFile = createFile();

            if (tempFile != null) {
                // Temporary file is created successfully
                Log.d(TAG, "dispatchCameraIntent: Temporary File created successfully");

                // Get path of temporary file
                tempFilePath = tempFile.getAbsolutePath();
                tempPhotoUri = FileProvider.getUriForFile(ImageActivity.this, Constants.AUTHORITY, tempFile);

                // Give the temporary file uri to the camera
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri);

                // Start the camera
                startActivityForResult(cameraIntent, CAMERA);
            } else {
                Log.d(TAG, "dispatchCameraIntent: Temporary file could not be created");
            }
        } else Toast.makeText(this, "No Package Manager", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d(TAG, "onActivityResult: Got photo...");
            FileUtils fileUtils = new FileUtils(this);
            photoUri = data.getData();
            photoPath = fileUtils.getPath(photoUri);
            Log.d(TAG, "onActivityResult: Uri: " + photoUri);
            Log.d(TAG, "onActivityResult: Path: " + photoPath);

            // Set image
            ivImage.setImageURI(photoUri);
            image = true;
        } else if (requestCode == CAMERA && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: Temporary File Uri: " + tempPhotoUri);
            Log.d(TAG, "onActivityResult: Temporary File Path:" + tempFilePath);
            new ImageCompressionAsyncTask().execute(tempFilePath);
        } else {
            Log.d(TAG, "onActivityResult: Could not get photo");
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private File createFile() {
        @SuppressLint("SimpleDateFormat")
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDirectory = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + File.separator + "DJ Events");

        boolean directoryExists = true;

        Log.d(TAG, "createFile: Storage directory: " + storageDirectory.getPath());
        if (!storageDirectory.exists())
            directoryExists = storageDirectory.mkdirs();

        if (directoryExists) {
            File image = null;
            try {
                image = File.createTempFile(name, ".jpg", storageDirectory);
            } catch (IOException e) {
                Log.d(TAG, "createFile: Exception: " + e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return image;
        } else {
            Toast.makeText(this, "Folder could not be created", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Start the progress bar
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            return compressImage(strings[0]);
        }

        public String compressImage(String imageUri) {
            String filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 1500.0f;
            float maxWidth = 1130.0f;
            float imgRatio = (float) actualWidth / (float) actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            assert scaledBitmap != null;
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - (float) bmp.getWidth() / 2, middleY - (float) bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return filename;

        }

        public String getFilename() {
            File file = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + File.separator + "Team Zoom");
            if (!file.exists()) {
                if (file.mkdirs())
                    return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
                else return null;
            }
            return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = Math.min(heightRatio, widthRatio);

            }
            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
            return inSampleSize;
        }

        @Override
        protected void onPostExecute(String compressedFileName) {
            super.onPostExecute(compressedFileName);

            // Stop the progress bar
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            Log.d(TAG, "onPostExecute: Compressed File Name: " + compressedFileName);
            photoPath = getRealPathFromURI(compressedFileName);

            // Get the temporary file from file path and delete it
            if (isCamera) {
                File tempFile = new File(tempFilePath);
                if (tempFile.delete())
                    Log.d(TAG, "onPostExecute: File deleted...");
                else Log.d(TAG, "onPostExecute: File not deleted...");
            }

            photoUri = Uri.parse(photoPath);
            ivImage.setImageURI(photoUri);
            image = true;
        }
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath = cursor.getString(idx);
            cursor.close();
            return realPath;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isEdit)
            setResult(RESULT_CANCELED, new Intent());
        else if (image) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to go back?")
                    .setMessage("All unsaved changes will be lost")
                    .setPositiveButton("Yes", (dialogInterface, i) -> this.finish())
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
            this.finish();
        }
    }
}