package com.example.opencvtextrecognizing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button captureButton,detecttextButton;
    private TextView text;
    private ImageView Image;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureButton=findViewById(R.id.captureImage);
        detecttextButton=findViewById(R.id.detecttext);
        text=findViewById(R.id.text);
        Image=findViewById(R.id.imageView);


    }
    public void imageclick(View v){
        dispatchTakePictureIntent();

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            Image.setImageBitmap(imageBitmap);
        }

    }
    public void detect(View v){
    if(imageBitmap!=null) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displaytext(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "error in detect" + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("error in detect", e.getMessage());


            }
        });
    }
    else{
        Toast.makeText(this," bit image is null", Toast.LENGTH_LONG).show();

    }

    }

    private void displaytext(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList=firebaseVisionText.getBlocks();
        if(blockList.size()==0){
            Toast.makeText(this,"no text in the image", Toast.LENGTH_LONG).show();

        }
        else{
            for(FirebaseVisionText.Block block : firebaseVisionText.getBlocks()){
                String t=block.getText();
                text.setText(t);
            }
        }

    }
}