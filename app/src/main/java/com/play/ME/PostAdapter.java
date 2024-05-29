package com.play.ME;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvTitle.setText(post.getTitle());

        // 게시글의 첫 부분만 표시
        String content = post.getContent();
        if (content.length() > 15) {
            content = content.substring(0, 15) + "..."; // 첫 5자만 표시하고 이후에는 "..."로 대체
        }
        holder.tvContent.setText(content);

        // 글 작성 시간을 포맷팅하여 표시
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(new Date(post.getTimestamp()));
        holder.tvTimestamp.setText(formattedDate);

        // 게시글의 작성자 정보를 설정
        holder.tvAuthor.setText(post.getAuthor());

        // 게시글 아이템을 클릭하면 전체 게시글 내용을 표시하는 액티비티로 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭한 게시글의 정보를 Intent에 추가하여 전달
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("userId", post.getuserId());
                intent.putExtra("title", post.getTitle());
                intent.putExtra("content", post.getContent());
                intent.putExtra("timestamp", post.getTimestamp());
                intent.putExtra("authorId", post.getAuthor()); // authorId 추가

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamp, tvAuthor;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            //tvAuthor.setVisibility(View.GONE);
        }
    }
}
