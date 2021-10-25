package android.apps.com.books;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchResult extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>>,
        BookAdapter.ListItemClickListener {
    private static List<Book> booksList;
    private String searchItem;
    private RecyclerView recyclerView;
    private BookAdapter bAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;

    public static Book getBook(int i) {
        return booksList.get(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        progressBar = findViewById(R.id.indeterminateBar);

        searchItem = getIntent().getStringExtra("item");
        String[] fortitle = searchItem.split(":");
        this.setTitle("Books: \"" + fortitle[1] + "\"");

        recyclerView = findViewById(R.id.recycler_view);
        bAdapter = new BookAdapter(this, new ArrayList<Book>());

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        layoutManager = new GridLayoutManager(this, gridColumnCount);

        recyclerView.hasFixedSize();
        recyclerView.setAdapter(bAdapter);
        recyclerView.setLayoutManager(layoutManager);

        LoaderManager.getInstance(this).initLoader(1, null, this);
    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new BooksLoader(this, searchItem);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> books) {
        progressBar.setVisibility(View.GONE);
        if (books != null && !books.isEmpty()) {
            findViewById(R.id.no_books_found).setVisibility(View.INVISIBLE);
            booksList = books;
            bAdapter.addAll(books);
        } else {
            findViewById(R.id.no_books_found).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        bAdapter.clear();
    }

    /**
     * This is where we receive our callback from
     * {@link android.apps.com.books.BookAdapter.ListItemClickListener}
     * <p>
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, BookInfo.class);
        intent.putExtra("Book", clickedItemIndex);
        startActivity(intent);
    }
}
