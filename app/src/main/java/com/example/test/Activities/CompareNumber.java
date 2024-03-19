package com.example.test.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

import java.util.Random;

public class CompareNumber extends AppCompatActivity {

    private int score = 0;
    private int incorrectAttempts = 0;
    private TextView number1TextView;
    private TextView number2TextView;
    private TextView timerTextView;
    private TextView scoreTextView;
    private CountDownTimer timer;
    private final ImageView[] lifeImages = new ImageView[3];
    private boolean shouldNavigateBack = true;
    private ImageView backButton;
    private long remainingTimeMillis;
    ImageButton lesserThanButton;
    ImageButton greaterThanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        number1TextView = findViewById(R.id.question_num1);
        number2TextView = findViewById(R.id.question_num2);

        timerTextView = findViewById(R.id.timer);

        scoreTextView = findViewById(R.id.totalScore);

        backButton = findViewById(R.id.back);

        lifeImages[0] = findViewById(R.id.life1);
        lifeImages[1] = findViewById(R.id.life2);
        lifeImages[2] = findViewById(R.id.life3);

        lesserThanButton = findViewById(R.id.option1);
        greaterThanButton = findViewById(R.id.option2);

        //Game Instruction
        gameInstruct();

        goBack(backButton);

        clickHandler(lesserThanButton,greaterThanButton);

        generateNumbers();

    }

    private void clickHandler(ImageButton lesserThanButton,ImageButton greaterThanButton){
        lesserThanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        greaterThanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
    }

    private void goBack(ImageView backButton){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGame();
            }
        });
    }

    private void gameInstruct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.compare_num_popup, null);
        builder.setView(customLayout);

        AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_backg);

        alertDialog.setCancelable(false);

        // Set the positive button and its click listener directly on the alertDialog instance
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Got It", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startTimer();
            }
        });

        // Show the AlertDialog
        alertDialog.show();
    }

    private void generateNumbers() {
        int random1, random2;
        do {
            int min = 1;
            int max = 999;
            random1 = generateRandomNumber(min, max);
            random2 = generateRandomNumber(min, max);
        } while (random1 == random2);

        number1TextView.setText(String.valueOf(random1));
        number2TextView.setText(String.valueOf(random2));
    }

    private static int generateRandomNumber(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    private void checkAnswer(boolean isLesser) {
        int num1 = Integer.parseInt(number1TextView.getText().toString());
        int num2 = Integer.parseInt(number2TextView.getText().toString());

        boolean isCorrect = isLesser ? num1 < num2 : num1 > num2;

        if (isCorrect) {
            score++;
            extendTime(1);
            scoreTextView.setText(String.valueOf(score));
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            incorrectAttempts++;
            lifeImages[3 - incorrectAttempts].setVisibility(View.GONE);
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
            if (incorrectAttempts >= 3) {
                endGame();
            }
        }

        generateNumbers();
    }

    private void startTimer() {

        timer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
                    }
                });
            }

            public void onFinish() {
                endGame();
            }
        }.start();
    }

    private void extendTime(int seconds) {
        // Cancel the current timer
        timer.cancel();

        // Start a new timer with extended time
        timer = new CountDownTimer((remainingTimeMillis + seconds * 1000), 1000) {
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished; // Update remaining time
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
                    }
                });
            }

            public void onFinish() {
                endGame();
            }
        }.start();
    }

    private void endGame() {
        shouldNavigateBack = false;
        timer.cancel();

        SharedPreferences preferences = getSharedPreferences("HighestScore", MODE_PRIVATE);
        int highestScoreRecord = preferences.getInt("highestScore", 0);

        if (score > highestScoreRecord) {
            highestScoreRecord = score;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("highestScore", highestScoreRecord);
            editor.apply();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.scoreboard, null);
        builder.setView(customLayout);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_backg);
        alertDialog.setCancelable(false);

        TextView highestScore = customLayout.findViewById(R.id.higestScore);
        TextView currentScore = customLayout.findViewById(R.id.currentScore);

        highestScore.setText(String.valueOf(highestScoreRecord));
        currentScore.setText(String.valueOf(score));

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        // Perform your custom logic
        endGame();

        // Check if the default behavior should be executed
        if (shouldNavigateBack && !isFinishing()) {
            // Call super.onBackPressed() to navigate back only if shouldNavigateBack is true
            super.onBackPressed();
        }

    }

}