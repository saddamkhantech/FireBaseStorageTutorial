package com.example.firebasestoragetutorial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {
        private FirebaseStorage firebaseStorage;
        private StorageReference mref;
        private Boolean externalStoragepermissionstatus=false;
        private static final  int REQ_CODE=07;
        private static final  int PICK_IMAGE_REQUEST=1001;
        private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //inizalizing
        firebaseStorage=FirebaseStorage.getInstance();
        mref=firebaseStorage.getReference("docs");
        progressBar=findViewById(R.id.progressBarid);
        //it get reference of folder and if docs folder not exist it create it the return refrence
    }

    public void runCode(View view)
    {
                //here we write in abc.txt file
//        StorageReference child=mref.child("office/abc.txt");
//        String data="Hello i am here i am abc";
//        UploadTask uploadTask = child.putBytes(data.getBytes());
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(MainActivity.this,"File Uploaded Succesfully",Toast.LENGTH_LONG).show();
//            }
//        });


        //using putBytes();
        //now image upload
        //first image ko lake aate h
//        Bitmap bitmap=readdata();
//        //now create byte array output stream
//        ByteArrayOutputStream baos=new ByteArrayOutputStream();
//        //now compress image and then put on baos
//        bitmap.compress(Bitmap.CompressFormat.JPEG,10,baos);
//        //now upload time
//        mref.child("images/friends.jpg")
//                .putBytes(baos.toByteArray())
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(MainActivity.this,"Image Uploaded",Toast.LENGTH_LONG).show();
//                    }
//                });

        //Now using putStream
        //first create input stream and load image from file
//        InputStream inputStream=null;
//        try {
//             inputStream=new FileInputStream(new File(getCacheDir(),"birthday2.mp3"));
//        }catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//              UploadTask uploadTask=mref.child("video/birthday2.mp3").putStream(inputStream);
//        final InputStream finalInputStream=inputStream;
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//             try {
//                 if(finalInputStream !=null)
//                 {
//                     finalInputStream.close();
//                 }
//             }catch (IOException e)
//             {
//                 e.printStackTrace();
//             }
//                Toast.makeText(MainActivity.this,"Image Uploaded",Toast.LENGTH_LONG).show();
//
//            }
//        });

        //now using PutFile()
        //we can get image from external storage
        //so first take permission
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if(!externalStoragepermissionstatus)
                {
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_CODE);
                        return;

                    }
                }

            }


       // startActivity(intent);
        Intent intent=new Intent();
            intent.setType("image/*" );
            intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent a=intent.createChooser(intent,"Choose your image");
        startActivityForResult(a,PICK_IMAGE_REQUEST);


//        if(intent.resolveActivity(getPackageManager())!=null)
//        {
//            intent.resolveActivity(getPackageManager());
//            startActivity(intent);
//            //startActivityForResult(Intent.createChooser(intent,"Choose Image"),PICK_IMAGE_REQUEST);
//            Toast.makeText(MainActivity.this,"I give package manager",Toast.LENGTH_LONG).show();
//
//        }
//        else {
//            //startActivityForResult(Intent.createChooser(intent,"Choose Image"),PICK_IMAGE_REQUEST);
//            startActivity(intent);
//            Toast.makeText(MainActivity.this,"I not working",Toast.LENGTH_LONG).show();
//
//        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                externalStoragepermissionstatus = true;
            }
            else
            {
                Toast.makeText(MainActivity.this, "Permission Not Granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && data !=null)
        {
            Uri imageUri=data.getData();
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Uploading");
           // progressDialog.show();
            UploadTask uploadTask=mref.child("images/"+imageUri.getLastPathSegment()).putFile(imageUri);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(false);//age hum ise false nhi krnge to ye infinite time k liye run hoga
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    //now calculate progress in % by using formula 100*transferedbyte/total no size of file
                    double progress=100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int)progress,true);

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this,"Image uploaded",Toast.LENGTH_LONG).show();
                   // progressDialog.dismiss();
                }
            });
        }
    }

    private Bitmap readdata()
    {
        InputStream inputStream=null;
        try {
            {
                inputStream=getAssets().open("friends.jpg");
                BitmapDrawable bitmapDrawable=(BitmapDrawable) Drawable.createFromStream(inputStream,null);
                return bitmapDrawable.getBitmap();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream !=null)
        {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
