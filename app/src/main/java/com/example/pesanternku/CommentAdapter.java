package com.example.pesanternku;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {
    private Context context;
    private List<ItemComment> commentList;

    public CommentAdapter(Context context, List<ItemComment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentHolder(view);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        ItemComment comment = commentList.get(position);
        holder.tvUsername.setText(comment.getUsername());
        holder.tvComment.setText(comment.getComment());
        Toast.makeText(context, comment.getEmailComment() + "" + comment.getEmailUser(), Toast.LENGTH_SHORT).show();
        if (comment.getEmailComment().equals(comment.getEmailUser())) {
            holder.deleteComment.setVisibility(View.VISIBLE);
        }

//        holder.deleteComment.setOnClickListener(v -> {
//            db.collection("comment")
//                    .whereEqualTo("idComment", comment.getIdComment()) // Filter data berdasarkan idComment
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            for (DocumentSnapshot document : queryDocumentSnapshots) {
//                                // Dapatkan ID dokumen
//                                String documentId = document.getId();
//                                Log.d("Firestore", "Document ID: " + documentId);
//
//                                // Lakukan sesuatu dengan ID, misalnya menghapus dokumen
//                                db.collection("comment").document(documentId)
//                                        .delete()
//                                        .addOnSuccessListener(aVoid -> {
//                                            Log.d("Firestore", "Document successfully deleted!");
//                                            // Hapus item dari daftar lokal
//                                            commentList.remove(position);
//                                            Toast.makeText(context, "Komentar dihapus", Toast.LENGTH_SHORT).show();
//
//                                            // Beritahu adapter tentang perubahan
//                                            notifyItemRemoved(position);
//                                            notifyItemRangeChanged(position, commentList.size());
//                                        })
//                                        .addOnFailureListener(e -> Log.w("Firestore", "Error deleting document", e));
//                            }
//                        } else {
//                            Log.d("Firestore", "No matching documents found.");
//                        }
//                    })
//                    .addOnFailureListener(e -> Log.w("Firestore", "Error getting documents", e));
//        });

        holder.deleteComment.setOnClickListener(v -> {
            String idComment = comment.getIdComment(); // Ambil ID komentar

            // Langsung menghapus dokumen menggunakan idComment sebagai ID dokumen Firestore
            db.collection("comment").document(idComment)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Document successfully deleted!");
                        // Hapus item dari daftar lokal
                        commentList.remove(position);

                        // Beritahu adapter tentang perubahan
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, commentList.size());
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting document", e));

            Toast.makeText(context, "Komentar dihapus", Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}

