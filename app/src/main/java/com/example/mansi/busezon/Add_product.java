package com.example.mansi.busezon;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Add_product extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    ImageView image;

    // Log tag that is used to distinguish log info.
    private final static String TAG_BROWSE_PICTURE = "BROWSE_PICTURE";

    // Used when request action Intent.ACTION_GET_CONTENT
    private final static int REQUEST_CODE_BROWSE_PICTURE = 1;

    // Used when request read external storage permission.
    private final static int REQUEST_PERMISSION_READ_EXTERNAL = 2;

    // The image view that used to display user selected image.
    private ImageView selectedPictureImageView;

    // Save user selected image uri list.
    private List<Uri> userSelectedImageUriList = null;

    // Currently displayed user selected image index in userSelectedImageUriList.
    private int currentDisplayedUserSelectImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        setTitle("dev2qa.com - Android Browse Picture Example.");

        // Get display imageview component.
        selectedPictureImageView = (ImageView)findViewById(R.id.display_image);

        // Get browse image button.
        Button choosePictureButton = (Button)findViewById(R.id.buttonLoadPicture);
        choosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Because camera app returned uri value is something like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
                // So if show the camera image in image view, this app require below permission.
                int readExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(readExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
                {
                    String requirePermission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(Add_product.this, requirePermission, REQUEST_PERMISSION_READ_EXTERNAL);
                }else {
                    openPictureGallery();
                }
            }
        });

        // Get show user selected images button.
        Button showSelectedPictureButton = (Button)findViewById(R.id.show_selected_picture_button);
        // When click this button. It will choose one user selected image to display in image view.
        showSelectedPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( userSelectedImageUriList != null )
                {
                    // Get current display image file uri.
                    Uri currentUri = userSelectedImageUriList.get(currentDisplayedUserSelectImageIndex);

                    ContentResolver contentResolver = getContentResolver();

                    try {
                        // User content resolver to get uri input stream.
                        InputStream inputStream = contentResolver.openInputStream(currentUri);

                        // Get the bitmap.
                        Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);

                        // Show image bitmap in imageview object.
                        selectedPictureImageView.setImageBitmap(imgBitmap);

                    }catch(FileNotFoundException ex)
                    {
                        Log.e(TAG_BROWSE_PICTURE, ex.getMessage(), ex);
                    }

                    // Get total user selected image count.
                    int size = userSelectedImageUriList.size();

                    if(currentDisplayedUserSelectImageIndex >= (size - 1) )
                    {
                        // Avoid array index out of bounds exception.
                        currentDisplayedUserSelectImageIndex = 0;
                    }else
                    {
                        currentDisplayedUserSelectImageIndex++;
                    }
                }
            }
        });
    }

    /* Invoke android os system file browser to select images. */
    private void openPictureGallery()
    {
        // Create an intent.
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    /* When the action Intent.ACTION_GET_CONTENT invoked app return, this method will be executed. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_BROWSE_PICTURE)
        {
            if(resultCode==RESULT_OK)
            {
                // Get return image uri. If select the image from camera the uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
                // If select the image from gallery the uri like content://media/external/images/media/1316970.
                Uri fileUri = data.getData();

                // Save user choose image file uri in list.
                if(userSelectedImageUriList == null)
                {
                    userSelectedImageUriList = new ArrayList<Uri>();
                }
                userSelectedImageUriList.add(fileUri);

                //getUriRealPath(getApplicationContext(), fileUri);

                // Create content resolver.
                ContentResolver contentResolver = getContentResolver();

                try {
                    // Open the file input stream by the uri.
                    InputStream inputStream = contentResolver.openInputStream(fileUri);

                    // Get the bitmap.
                    Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);

                    // Show image bitmap in imageview object.
                    selectedPictureImageView.setImageBitmap(imgBitmap);

                    inputStream.close();
                }catch(FileNotFoundException ex)
                {
                    Log.e(TAG_BROWSE_PICTURE, ex.getMessage(), ex);
                }catch(IOException ex)
                {
                    Log.e(TAG_BROWSE_PICTURE, ex.getMessage(), ex);
                }
            }
        }
    }

    /* After user choose grant read external storage permission or not. */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_PERMISSION_READ_EXTERNAL)
        {
            if(grantResults.length > 0)
            {
                int grantResult = grantResults[0];
                if(grantResult == PackageManager.PERMISSION_GRANTED)
                {
                    // If user grant the permission then open choose image popup dialog.
                    openPictureGallery();
                }else
                {
                    Toast.makeText(getApplicationContext(), "You denied read external storage permission.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}