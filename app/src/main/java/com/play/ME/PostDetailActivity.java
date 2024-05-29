package com.play.ME;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private String postId;
    private String author; // 글 작성자의 ID
    private String userId;
    private static final int REQUEST_EDIT_POST = 1;
    private Button menuButton; // Menu button reference
    private EditText etComment;
    private Button btnSubmitComment;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private DatabaseReference commentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        menuButton = findViewById(R.id.btn_menu);
        menuButton.setVisibility(View.GONE); // Initially hidden

        Intent intent = getIntent();
        if (intent != null) {
            postId = intent.getStringExtra("postId");

            userId = intent.getStringExtra("userId");
            Log.d("PostDetailActivity", "postId: " + postId);
            Log.d("PostDetailActivity", "userId: " + userId);


            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            long timestamp = intent.getLongExtra("timestamp", -1);
            String author = intent.getStringExtra("author"); // 작성자 정보 추가

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formattedDate = formatter.format(new Date(timestamp));

            TextView tvTitle = findViewById(R.id.tvTitle);
            TextView tvContent = findViewById(R.id.tvContent);
            TextView tvTimestamp = findViewById(R.id.tvTimestamp);
            TextView tvAuthor = findViewById(R.id.tvAuthor);
            tvAuthor.setText(author); // 작성자 표시

            tvTitle.setText(title);
            tvTimestamp.setText(formattedDate);
            tvContent.setText(content);

            // 현재 로그인한 유저의 ID를 가져옴
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                Log.d("PostDetailActivity", "currentUser ID: " + currentUser.getUid());

                if (currentUser.getUid().equals(userId)) {
                    // 로그인한 유저가 글 작성자와 동일할 때만 메뉴 버튼을 표시함
                    menuButton.setVisibility(View.VISIBLE);
                }
            } else {
                Log.d("PostDetailActivity", "currentUser is null");
            }
        } else {
            Toast.makeText(this, "전달된 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        etComment = findViewById(R.id.etComment);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);
        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, userId);
        rvComments.setAdapter(commentAdapter);

        commentsRef = FirebaseDatabase.getInstance().getReference("takeiteasy").child("Comments").child(postId);

        btnSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();
            }
        });

        loadComments();
    }

    private void submitComment() {
        String commentText = etComment.getText().toString();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "댓글을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        String commentId = commentsRef.push().getKey();
        String CuserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        long timestamp = System.currentTimeMillis();

        Comment comment = new Comment(commentId, userName, commentText, timestamp, CuserId);
        commentsRef.child(commentId).setValue(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        etComment.setText("");
                        Toast.makeText(PostDetailActivity.this, "댓글이 작성되었습니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostDetailActivity.this, "댓글 작성에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadComments() {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PostDetailActivity.this, "댓글을 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_edit) {
                    editPost();
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    confirmDeletePost();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    private void editPost() {
        Intent intent = new Intent(this, EditPostActivity.class);
        intent.putExtra("postId", postId);
        intent.putExtra("title", ((TextView) findViewById(R.id.tvTitle)).getText().toString());
        intent.putExtra("content", ((TextView) findViewById(R.id.tvContent)).getText().toString());
        startActivityForResult(intent, REQUEST_EDIT_POST);
    }

    private void confirmDeletePost() {
        new AlertDialog.Builder(this)
                .setTitle("게시글 삭제")
                .setMessage("정말로 이 게시글을 삭제하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void deletePost() {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("takeiteasy")
                .child("POST").child(postId); // postId를 String으로 변환하지 않고 그대로 사용
        postRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PostDetailActivity.this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostDetailActivity.this, "게시글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_POST && resultCode == RESULT_OK) {
            // Handle the result from EditPostActivity if needed
            // For example, update the post content after editing
            // String updatedContent = data.getStringExtra("updatedContent");
            // tvContent.setText(updatedContent);
            // You can implement this based on your requirements
        }
    }
}
