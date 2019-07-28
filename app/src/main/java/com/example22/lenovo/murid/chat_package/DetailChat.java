package com.example22.lenovo.murid.chat_package;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example22.lenovo.murid.ApiService;
import com.example22.lenovo.murid.R;
import com.example22.lenovo.murid.SessionManager;
import com.example22.lenovo.murid.chat_package.DetailChat;
import com.example22.lenovo.murid.helper.Upload;
import com.example22.lenovo.murid.notif.Client;
import com.example22.lenovo.murid.notif.Data;
import com.example22.lenovo.murid.notif.MyResponse;
import com.example22.lenovo.murid.notif.Sender;
import com.example22.lenovo.murid.notif.Token;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailChat extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 1;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton, addButton, imgPreview;
    EditText messageArea;

    ScrollView scrollView;
    Firebase reference1, reference2;
    private ProgressBar uploadProgress;

    private SessionManager sessionManager ;
    private Uri imgUrl;
    String username;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    StorageReference fileReference;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sessionManager = new SessionManager(this);

        username = getIntent().getStringExtra("username");

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        addButton = findViewById(R.id.addButton);
        uploadProgress = findViewById(R.id.progressBar);
        imgPreview = findViewById(R.id.previewImage);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        uploadProgress.setVisibility(View.GONE);
        getString(R.string.project_id);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://guruku2-572b2.firebaseio.com/messages/" + sessionManager.getNama()+ "_" + username);
        reference2 = new Firebase("https://guruku2-572b2.firebaseio.com/messages/" + username + "_" +sessionManager.getNama());

        mStorageRef = FirebaseStorage.getInstance().getReference("image_chat");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("image_chat");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(DetailChat.this);
                View sheetView = DetailChat.this.getLayoutInflater().inflate(R.layout.pop_up_image_camera, null);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();

                LinearLayout galeri = (LinearLayout) sheetView.findViewById(R.id.fragment_history_bottom_sheet_edit);
                LinearLayout camera = (LinearLayout) sheetView.findViewById(R.id.fragment_history_bottom_sheet_delete);
                galeri.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Edit code here;
                        showFileChoose();
                        mBottomSheetDialog.dismiss();

                    }
                });

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Delete code here;
                    }
                });

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("") || (imgPreview == null)){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("username", sessionManager.getNama());
                    map.put("imageschat", "noimage");

                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                    showNotif(username,sessionManager.getNama(),messageText);
//                    Toast.makeText(DetailChat.this, username, Toast.LENGTH_SHORT).show();
                }
                if ((imgPreview != null) && (!messageText.equals(""))){
                    uploadImage();
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("username").toString();
                String imgChat = map.get("imageschat").toString();

                if(userName.equals(sessionManager.getNama())){
                    addMessageBox("You:-\n" + message, imgChat, 1);
                }
                else{
                    addMessageBox(UserDetails.chatWith + ":-\n" + message, imgChat, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String message, String imgUrl, int type){
        TextView textView = new TextView(DetailChat.this);
        textView.setText(message);
        ImageView imgChat = new ImageView(DetailChat.this);
        imgChat.setMaxWidth(50);
        imgChat.setMaxHeight(80);


        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            if(!imgUrl.equalsIgnoreCase("noimage")){
                Picasso.with(getApplicationContext())
                        .load(imgUrl)
                        .placeholder(R.drawable.imagepreview)
                        .fit()
                        .centerCrop()
                        .into(imgChat);
                imgChat.setBackgroundResource(R.drawable.bubble_in);
                textView.setBackgroundResource(R.drawable.bubble_in);
            }else {
                textView.setBackgroundResource(R.drawable.bubble_in);
            }

        }
        else{
            lp2.gravity = Gravity.LEFT;
            if(!imgUrl.equalsIgnoreCase("noimage")){
                Picasso.with(getApplicationContext())
                        .load(imgUrl)
                        .placeholder(R.drawable.imagepreview)
                        .fit()
                        .centerCrop()
                        .into(imgChat);
                imgChat.setBackgroundResource(R.drawable.bubble_out);
                textView.setBackgroundResource(R.drawable.bubble_out);
            }else {
                textView.setBackgroundResource(R.drawable.bubble_out);
            }

        }
        textView.setLayoutParams(lp2);
        imgChat.setLayoutParams(lp2);
        layout.addView(imgChat);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void showFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUrl = data.getData();
            imgPreview.setVisibility(View.VISIBLE);
            Picasso.with(this).load(imgUrl).into(imgPreview);
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        if (imgUrl != null) {
            fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imgUrl));

            uploadProgress.setVisibility(View.VISIBLE);
            mUploadTask = fileReference.putFile(imgUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    uploadProgress.setProgress(0);
                                }
                            }, 500);
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Upload upload = new Upload(messageArea.getText().toString().trim(), uri.toString());
                                    String uploadID = reference1.push().getKey();

                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("message", messageArea.getText().toString());
                                    map.put("username", UserDetails.username);
                                    map.put("imageschat", upload.getImgUrl());
                                    reference1.push().setValue(map);
                                    reference2.push().setValue(map);
                                    messageArea.setText("");
                                    imgPreview.clearFocus();

//                                    reference1.child(uploadID).setValue(upload);
                                    Toast.makeText(DetailChat.this, "Upload Image Success", Toast.LENGTH_LONG).show();
                                    imgPreview.setImageResource(R.drawable.imagepreview);
                                    messageArea.setText("");
                                    uploadProgress.setVisibility(View.GONE);
                                    imgPreview.setVisibility(View.GONE);
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailChat.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            uploadProgress.setProgress((int) progress);
                        }
                    });
        } else {
//            Toast.makeText(DetailChat.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotif(final String username, final String pengirim, final String messageText) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance() ;
        DatabaseReference databaseReference  = firebaseDatabase.getReference("Token_User");

//        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

        //  databaseReference.orderByChild("")

        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                Token token = dataSnapshot.getValue(Token.class);
                Data data = new Data(pengirim,username,R.mipmap.ic_launcher,pengirim +":"+messageText,"new Message","");

                try {
                    Sender sender = new Sender(token.getToken(), data);

                    ApiService apiService = Client.getClient("http://fcm.googleapis.com/").create(ApiService.class);

                    apiService.sendNotif(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            Log.d("send notif", response.body().toString());

                            //sign up baru ya pak di device
                            //suara bapak ilang

                            if (response.isSuccessful()) {

                                int success = response.body().success;

                                if (success == 200) {


//                                    Toast.makeText(DetailChat.this, "succes", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                            Log.d("eror notif", t.getLocalizedMessage());

                        }
                    });

                }catch (NullPointerException e){

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}