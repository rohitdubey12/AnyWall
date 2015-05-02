package com.parse.anywall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class InfoWindowClickActivity extends Activity {

    ParseUser initiator, helper;
    AnywallPost SelectedPost;
    TextView usernameTextView;
    TextView textViewPostDesc;
    EditText postReply;
    Button offerButton;
    private int maxCharacterCount = Application.getConfigHelper().getPostMaxCharacterCount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infowindow_click);
        String postObjid = getIntent().getParcelableExtra(Application.SEARCH_SERVICE);
        //Rohit-> we have the object id of the post selected
        findUser(postObjid);

        //SET UP POST TEXT
        textViewPostDesc = (TextView) findViewById(R.id.textViewInfoClick);
        textViewPostDesc.setText(SelectedPost.getText());

        //SET USERNAME TO INITIATOR
        usernameTextView = (TextView) findViewById(R.id.textViewInfoClick2);
        usernameTextView.setText(initiator.getUsername());

        postReply = (EditText) findViewById(R.id.editTextPostClick);
        postReply.setText("Type your message here for the user here. Make sure you leave contact details and finish in 140 characters...");
        postReply.setSelectAllOnFocus(true);
        postReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePostButtonState();
                updateCharacterCountTextViewText();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        helper = ParseUser.getCurrentUser();

        offerButton = (Button) findViewById(R.id.buttonInfoClick);
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });

        updatePostButtonState();
        updateCharacterCountTextViewText();
    }


    private void post() {
        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(InfoWindowClickActivity.this);
        dialog.setMessage(getString(R.string.progress_post));
        dialog.show();

        AnywallMessage message = new AnywallMessage();
        message.setRecipient(initiator);
        message.setSender(helper);
        message.setText(postReply.getText().toString());
        ParseACL postReplyACL = new ParseACL();
        postReplyACL.setReadAccess(initiator, true);
        postReplyACL.setReadAccess(helper, true);

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (Application.APPDEBUG) {
                        Log.d(Application.APPTAG, "An error occurred while trying to Send Offer.", e);
                    }
                } else {
                    dialog.dismiss();
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_click, menu);

        return true;
    }


    public ParseUser findUser(String postObjectId) {
        ParseQuery<AnywallPost> userQuery = AnywallPost.getQuery();
        userQuery.whereContains("objectId", postObjectId);
        userQuery.include("user");
        userQuery.include("text");

        final ProgressDialog dialog = new ProgressDialog(InfoWindowClickActivity.this);
        //Rohit-> put string in R.String.
        dialog.setMessage("Retreiving Post");
        dialog.show();


        userQuery.findInBackground(new FindCallback<AnywallPost>() {
            @Override
            public void done(List<AnywallPost> list, ParseException e) {

                dialog.dismiss();
                if (e != null) {
                    if (Application.APPDEBUG) {
                        Log.d(Application.APPTAG, "An error occurred while trying to retrieve Post.", e);
                    }
                } else {
                    for (AnywallPost post : list) {
                        initiator = post.getUser();
                        SelectedPost = post;
                    }
                }
            }
        });
        return initiator;
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


    private void updatePostButtonState() {
        int length = ((EditText) findViewById(R.id.post_edittext)).getText().toString().trim().length();
        boolean enabled = length > 0 && length < maxCharacterCount;
        offerButton.setEnabled(enabled);
    }

    private void updateCharacterCountTextViewText() {
        String characterCountString = String.format("%d/%d", ((EditText) findViewById(R.id.post_edittext)).getText().toString().trim().length(), maxCharacterCount);
        ((TextView) findViewById(R.id.PostClickCharacterCount)).setText(characterCountString);
    }
}
