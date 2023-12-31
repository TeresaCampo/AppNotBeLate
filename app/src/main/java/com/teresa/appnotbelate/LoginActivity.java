package com.teresa.appnotbelate;

import static android.util.Log.DEBUG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.VolleyLog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MyDebug";

    Button login;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure Firebase Sign In
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //set up the log in button
        login= findViewById(R.id.bn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        //set up the eventual alert pane
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logging in...");
        builder.setMessage("Almost ready to start to use your app!");
        dialog = builder.create();
    }

    /**
     * To manage google API answer
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Display a dialog pane
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                // now the user can use the app
                startTheApplication();
            }
        }, 3000); // 3000 milliseconds = 3 seconds
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getBaseContext(),"Error in google authentication process",Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error in google authentication process", e);
            }
        }
    }

    /**
     * To authenticate in Firebase
     * @param idToken of the google account
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //if it is the first time the user log in, save info(uid and email)
                        updateUserDocument();
                        mGoogleSignInClient.signOut();

                    } else {
                        mGoogleSignInClient.signOut();
                        Toast.makeText(getBaseContext(),"Error during the authentication process",Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error in Firebase authentication process");
                    }
                });
    }

    /**
     * To not let the user come back to the previous page without logging in
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    /**
     * To save information about the user if it is a new user
     */
    void updateUserDocument() {
        Log.d(TAG, "Checking if the account is a new one... ");
        FirebaseUser currentUser= mAuth.getCurrentUser();
        String userId= currentUser.getUid();

        if (userId != null) {
            Log.d(TAG, "Current authenticated user is "+ currentUser.getUid()+" ("+currentUser.getDisplayName()+")");

            FirebaseFirestore db= FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(userId);
            docRef.get().addOnCompleteListener(task -> {
                //try to access to the file
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // If document does not exist, create a new one and store information
                    if (document != null && !document.exists()) {
                        //store data
                        Map<String, Object> user = new HashMap<>();
                        user.put("userId", currentUser.getUid());
                        user.put("email", currentUser.getEmail());
                        user.put("name",currentUser.getDisplayName());

                        db.collection("users").document(userId)
                                .set(user, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "New user, successfully created");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "New user, error while creating it", e);
                                });
                    }
                    //if document exists, do not do anything
                    else {
                        Log.d(TAG, "Existing user");
                        //startTheApplication();
                    }
                } else {
                    if (task.getException() != null) {
                        Log.e(TAG, "Error while checking the existence of the user(Firestone document)", task.getException());
                    }
                }
            });
        } else {
            Log.e(TAG, "User ID is null");
        }
    }
    private void startTheApplication() {
        Intent loginIntent = new Intent(this, MainActivity.class );
        startActivity(loginIntent);
    }

}