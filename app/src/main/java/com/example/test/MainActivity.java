package com.example.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.test.Activities.CompareNumber;
import com.example.test.Activities.ComposingNumber;
import com.example.test.Activities.NumberOrdering;

public class MainActivity extends AppCompatActivity {
    private boolean shouldNavigateBack = true;
    CardView composingNumber, orderingOfNumber, compareNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        composingNumber = findViewById(R.id.composing_number);
        orderingOfNumber = findViewById(R.id.ordering_of_numbers);
        compareNumbers = findViewById(R.id.compare_numbers);

        compareNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CompareNumber.class);
                startActivity(intent);

            }
        });

        orderingOfNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NumberOrdering.class);
                startActivity(intent);

            }
        });

        composingNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ComposingNumber.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        shouldNavigateBack = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.quit, null);
        builder.setView(customLayout);

        AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_backg);

        alertDialog.setCancelable(false);

        // Set the positive button and its click listener directly on the alertDialog instance
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", (DialogInterface.OnClickListener) null);

        // Show the AlertDialog
        alertDialog.show();

        // Check if the default behavior should be executed
        if (shouldNavigateBack && !isFinishing()) {
            // Call super.onBackPressed() to navigate back only if shouldNavigateBack is true
            Toast.makeText(this, "Error here", Toast.LENGTH_LONG).show();
            super.onBackPressed();
        }

    }

}



