package android.apps.com.books;

import android.apps.com.books.databinding.ActivityBookInfoBinding;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class BookInfo extends AppCompatActivity {
    private ActivityBookInfoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Book Details");

        binding = ActivityBookInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        int position = getIntent().getIntExtra("Book", 0);
        Book book = SearchResult.getBook(position);

        binding.title.setText(book.getTitle());
        binding.ratingBar.setRating(book.getRatings());
        binding.descrption.setText(book.getDescription());
        binding.pageCount.setText(String.valueOf(book.getPageCount()));
        binding.publisher.setText(book.getPublisher());
        binding.publishedDate.setText(book.getPublishedDate());

        if (!book.getPreviewLink().isEmpty()) {
            String preview = "<a href='" + book.getPreviewLink() + "'> Preview </a>";
            binding.previewLink.setText(Html.fromHtml(preview));
            binding.previewLink.setMovementMethod(LinkMovementMethod.getInstance());
        }

        String authors = TextUtils.join(", ", book.getAuthors());
        binding.authors.setText(authors);

        String identifiers = TextUtils.join("\n", book.getIdentifiers());
        binding.identifiers.setText(identifiers);

        Glide.with(this)
                .load(book.getImageLink())
                .placeholder(R.drawable.no_cover)
                .into(binding.bookImage);
    }
}
