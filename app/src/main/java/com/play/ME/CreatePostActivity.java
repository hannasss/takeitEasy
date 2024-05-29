package com.play.ME;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreatePostActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private Button btnSubmit, btnBack;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("takeiteasy");
        mUserRef = FirebaseDatabase.getInstance().getReference("takeiteasy").child("UserAccount").child(mAuth.getCurrentUser().getUid());

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                if (userAccount != null) {
                    userName = userAccount.getUserName();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();
                String userId = mAuth.getCurrentUser().getUid();
                String postId = mDatabaseRef.push().getKey();
                long timestamp = System.currentTimeMillis();

                Post post = new Post(postId, userName, title, content, timestamp,userId);
                mDatabaseRef.child("POST").child(postId).setValue(post);


                Toast.makeText(CreatePostActivity.this, "게시글이 작성되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PostAdapter로 이동하는 인텐트 생성
                Intent intent = new Intent(getApplicationContext(), PostAdapter.class);
                // 필요한 경우에 인텐트에 데이터를 추가할 수 있습니다.

                // 현재 액티비티 종료
                finish();
            }
        });


    }
}