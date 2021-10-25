package android.apps.com.books;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search_box);
        ImageButton searchButton = findViewById(R.id.search_button);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performSearch();
                }
            });

            search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        performSearch();
                        return true;
                    }
                    return false;
                }
            });
        } else {
            // Otherwise, display error
            // Update empty state with no connection error message
            ((ImageView)findViewById(R.id.no_network)).setVisibility(View.VISIBLE);
            ((ConstraintLayout) findViewById(R.id.search_view)).setVisibility(View.INVISIBLE);
        }

    }

    private void performSearch() {
        String searchItem = search.getText().toString();
        Intent intent = new Intent(getApplicationContext(), SearchResult.class);
        intent.putExtra("item", getSearchBy() + searchItem);
        startActivity(intent);
    }

    public String getSearchBy() {
        // Is the button now checked?
        RadioGroup rg = findViewById(R.id.radio_group);
        int checked = rg.getCheckedRadioButtonId();

        // Check which radio button was clicked
        switch(checked) {
            case R.id.radio_title:
                    return "intitle:";
            case R.id.radio_author:
                    return "inauthor:";
            case R.id.radio_isbn:
                    return  "isbn:";
            default:
                return "";
        }
    }
}