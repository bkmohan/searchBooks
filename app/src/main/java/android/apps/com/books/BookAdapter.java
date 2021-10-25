package android.apps.com.books;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    /*
     * An on-click handler that is defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private ListItemClickListener mOnClickListener;
    private Context context;
    private List<Book> books;

    public BookAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
        mOnClickListener = (ListItemClickListener) context;
    }

    public void addAll(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_layout, parent, false);

        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.titleTextView.setText(book.getTitle());

        String author = TextUtils.join(", ", book.getAuthors());
        holder.authorTextView.setText(author);
        Glide.with(this.context)
                .load(books.get(position).getImageLink())
                .placeholder(R.drawable.no_cover)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void clear() {
        books.clear();
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cellContainer;
        private ImageView thumbnail;
        private TextView titleTextView;
        private TextView authorTextView;

        public BookViewHolder(@NonNull View view) {
            super(view);

            cellContainer = view.findViewById(R.id.card_layout);
            thumbnail = view.findViewById(R.id.thumbnail);
            titleTextView = view.findViewById(R.id.book_title);
            authorTextView = view.findViewById(R.id.book_author);

            view.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
