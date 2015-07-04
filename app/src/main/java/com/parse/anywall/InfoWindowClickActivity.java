package com.parse.anywall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;


public class InfoWindowClickActivity extends Activity {

    ParseUser initiator, helper;
    AnywallPost SelectedPost;
    TextView usernameTextView;
    TextView textViewPostDesc;
    EditText postReply;
    Button offerButton;
    private int maxCharacterCount = Application.getConfigHelper().getPostMaxCharacterCount();

    String postUserId;
    String postUserName;
    String postText;


    /*
        intent.putExtra("UserId", selectedPost.getUser().getObjectId());
        intent.putExtra("UserName", selectedPost.getUser().getUsername());
        intent.putExtra("PostText",selectedPost.getText());
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infowindow_click);


    	if(savedInstanceState==null){
		    Bundle extras = getIntent().getExtras();
  		    if(extras == null) {
            		postUserId= null;
    		} else {
                postUserId= extras.getString("UserId");
                postUserName=extras.getString("UserName");
                postText=extras.getString("PostText");
            }
	    } else {
    		postUserId= (String) savedInstanceState.getSerializable("PostId");
            postUserName=(String) savedInstanceState.getSerializable("UserName");
            postText=(String) savedInstanceState.getSerializable("PostText");
    	}


        usernameTextView = (TextView)findViewById(R.id.textViewInfoClick2);
        textViewPostDesc=(TextView) findViewById(R.id.textViewInfoClick);
        postReply = (EditText) findViewById(R.id.editTextPostClick);
        offerButton = (Button) findViewById(R.id.buttonInfoClick);

        usernameTextView.setText(postUserName);
        textViewPostDesc.setText(postText);

        //SET UP POST TEXT
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

        //attempt to retrieve initiator/recipient
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(postUserId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(e==null){
                    initiator=parseUser;
                }else{
                    //something went wrong
                }
            }
        });

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


        //send push notification to recipient
        String pushText= helper.getUsername() + "Just Offered to help you";

        //insecure way
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", initiator);
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage(pushText);
        push.sendInBackground();

        /*
        HashMap<String, Object> params= new HashMap<String, Object>();
        params.put("recipientId",initiator);
        params.put("message", pushText);
        ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
            @Override
            public void done(String s, ParseException e) {
                Log.v("FIN","Push notification sent");
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_click, menu);

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


    private void updatePostButtonState() {
        int length = postReply.getText().toString().trim().length();
        boolean enabled = length > 0 && length < maxCharacterCount;
        offerButton.setEnabled(enabled);
    }

    private void updateCharacterCountTextViewText() {
        //String characterCountString = String.format("%d/%d", ((EditText) findViewById(R.id.post_edittext)).getText().toString().trim().length(), maxCharacterCount);

        String characterCountString = String.format("%d/%d", postReply.getText().toString().trim().length(), maxCharacterCount);
        ((TextView) findViewById(R.id.PostClickCharacterCount)).setText(characterCountString);
    }
}
