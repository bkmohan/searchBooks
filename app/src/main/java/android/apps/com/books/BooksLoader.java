package android.apps.com.books;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.List;

public class BooksLoader extends AsyncTaskLoader<List<Book>> {
    private String searchQuery;

    /**
     * @param context
     */
    public BooksLoader(Context context, String searchQuery) {
        super(context);
        this.searchQuery = searchQuery;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (searchQuery.isEmpty()) {
            return null;
        }

        List<Book> books = null;
        try {
            books = QueryUtils.fetchBooks(searchQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }
}
