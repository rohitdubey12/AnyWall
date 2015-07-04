package com.parse.anywall;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;


public class InboxActivity extends Activity {

    ListView messagesView;
    // Adapter for the Parse query
    private ParseQueryAdapter<AnywallMessage> messagesQueryAdapter;
    private String selectedMessageObjectId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);




        // Set up a customized query
        ParseQueryAdapter.QueryFactory<AnywallMessage> factory =
                new ParseQueryAdapter.QueryFactory<AnywallMessage>() {
                    public ParseQuery<AnywallMessage> create() {
                        ParseQuery<AnywallMessage> query = AnywallMessage.getQuery();
                        query.include("sender");
                        query.orderByDescending("createdAt");
                        query.whereEqualTo("recipient", ParseUser.getCurrentUser());
                        return query;
                    }
                };

        // Set up the query adapter
        messagesQueryAdapter = new ParseQueryAdapter<AnywallMessage>(this, factory) {
            @Override
            public View getItemView(AnywallMessage message, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.inbox_item, null);
                }
                TextView contentView = (TextView) view.findViewById(R.id.message_view);
                TextView usernameView = (TextView) view.findViewById(R.id.sender_view);
                contentView.setText(message.getText());
                usernameView.setText(message.getSender().getUsername());
                return view;
            }
        };
/*
        // Disable automatic loading when the adapter is attached to a view.
        messagesQueryAdapter.setAutoload(false);

        // Disable pagination, we'll manage the query limit ourselves
        messagesQueryAdapter.setPaginationEnabled(false);
*/
        // Attach the query adapter to the view
        messagesView = (ListView)findViewById(R.id.inboxlistView);
        messagesView.setAdapter(messagesQueryAdapter);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
