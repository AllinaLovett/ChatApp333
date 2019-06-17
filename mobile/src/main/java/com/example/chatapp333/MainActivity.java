package com.example.chatapp333;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.text.format.DateFormat;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;



import java.io.FileReader;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<com.example.ChatApp333.ChatMessage> adapter;
    RelativeLayout activity_main;
    FloatingActionButton fab;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out)
        {

            AuthUI.getInstance().signOUt(this).addOnCompleteListener(new onCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main, "You have been signed out. Good riddance!", Snackbar.LENGTH_SHORT).show();
                    finish();
                }

            }};
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu)
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int reesultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == SIGN_IN_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            Snackbar.make(activity_main, "Successfully signed in. Now get lost!", Snackbar.LENGTH_SHORT).show();
            displayChatMessage();
        } else {
            Snackbar.make(activity_main, "We couldn't sign you in. Please don't try again!", Snackbar.LENGTH_SHORT).show();
            finish();
        }
    }
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               EditText input = (EditText)findViewById((R.id.input);
                FirebaseDatabase.getInstance().getRefrence().push().setValue(new com.example.ChatApp333.ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");

            }

                               }
        )

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            Snackbar.make(activity_main, "Welcome :)" + FirebaseAuth.getInstance().getCurrentuser().getEmail(), Snackbar.LENGTH_SHORT).show();
        }
    }

    displayChatMessage();
}

    private void displayChatMessage() {
        ListView listOfMessage = (ListView)findViewById(R.id.list_of_message);

        Query query = FirebaseDatabase.getInstance().getReference();
        FirebaseListOptions<ChatMessage> options =
            new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.list_item)
                .build();

        adapter = new FirebaseListAdapter<ChatMessage>(options){
            @Override
            protected void populateView(View v,ChatMessage model,int position){

                    TextView messageText,messageUser,messageTime;
                    messageText = (BubbleTextView)v.findViewById(R.id.message_text);
                    messageUser = (TextView)v.findViewById(R.id.message_user);
                    messageTime = (TextView)v.findViewById(R.id.message_time);

                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getMessageTime()));


            }
        };

        adapter.startListening();
        listOfMessage.setAdapter(adapter);
}

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.omStop();
}
