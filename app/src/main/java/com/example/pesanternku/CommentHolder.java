package com.example.pesanternku;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentHolder extends RecyclerView.ViewHolder {
    TextView tvUsername, tvComment, deleteComment;
    RelativeLayout parentLayout;

    public CommentHolder(@NonNull View itemView) {
        super(itemView);
        parentLayout = itemView.findViewById(R.id.list_comment);
        tvUsername = itemView.findViewById(R.id.username);
        tvComment = itemView.findViewById(R.id.comment);
        deleteComment = itemView.findViewById(R.id.deleteComment);
    }
}

