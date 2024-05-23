package com.play.ME;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditPostActivity extends AppCompatActivity {

    private String postId;
    private EditText etTitle;
    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);

        Intent intent = getIntent();
        if (intent != null) {
            postId = intent.getStringExtra("postId");
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");

            etTitle.setText(title);
            etContent.setText(content);
        }

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
            }
        });

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void savePost() {
        String updatedTitle = etTitle.getText().toString();
        String updatedContent = etContent.getText().toString();

        if (postId != null && !postId.isEmpty()) {
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("takeiteasy")
                    .child("POST").child(postId);

            postRef.child("title").setValue(updatedTitle);
            postRef.child("content").setValue(updatedContent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditPostActivity.this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("updatedTitle", updatedTitle);
                            resultIntent.putExtra("updatedContent", updatedContent);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditPostActivity.this, "게시글 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "유효하지 않은 게시글 ID입니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
