package com.example.test.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class ComposingNumber extends AppCompatActivity {

    private int score = 0;
    private int incorrectAttempts = 0;
    private TextView[] numberTextViews = new TextView[4];
    private final TextView[] questionbarTextViews = new TextView[3];
    private CountDownTimer timer;
    private boolean shouldNavigateBack = true;
    private TextView scoreTextView;
    private TextView timerTextView;
    private int composingNumber1;
    private int composingNumber2;
    private int targetNumber;
    private final ImageView[] lifeImages = new ImageView[3];
    ArrayList<Integer> numbers = new ArrayList<>(4);
    private Stack<Integer> undo = new Stack<>();
    private ImageView backButton;
    private ImageView undoButton;
    private long remainingTimeMillis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.composing_number);

        numberTextViews[0] = findViewById(R.id.cpans1);
        numberTextViews[1] = findViewById(R.id.cpans2);
        numberTextViews[2] = findViewById(R.id.cpans3);
        numberTextViews[3] = findViewById(R.id.cpans4);

        questionbarTextViews[0] = findViewById(R.id.cpq1);
        questionbarTextViews[1] = findViewById(R.id.cpq2);
        questionbarTextViews[2] = findViewById(R.id.cpq3);

        lifeImages[0] = findViewById(R.id.cplife1);
        lifeImages[1] = findViewById(R.id.cplife2);
        lifeImages[2] = findViewById(R.id.cplife3);

        backButton = findViewById(R.id.cpback);

        undoButton = findViewById(R.id.cpundo);

        timerTextView = findViewById(R.id.cptimer);

        scoreTextView = findViewById(R.id.cptotalScore);

        gameInstruct();

        goBack(backButton);

        undo(undoButton);

        clickHandler(numberTextViews);

        generateNumbers();

    }

    private void clickHandler(TextView[] numberTextViews){
        for (int i = 0; i < 4; i++) {
            final int index = i;
            numberTextViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleNumberClick(index);
                }
            });
        }
    }

    private void undo(ImageView undoButton){
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoLastMove();
            }
        });
    }

    private void undoLastMove() {
        if (!undo.isEmpty()) {
            // Pop the last index from the undo stack
            int questionbarIndex = undo.pop();
            int numberIndex = undo.pop();
            // Move the number back to its original numberTextView
            numberTextViews[numberIndex].setText(questionbarTextViews[questionbarIndex].getText());
            questionbarTextViews[questionbarIndex].setText(""); // Clear the questionbarTextView
        }
    }

    private void goBack(ImageView backButton){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGame();
            }
        });
    }

    private void handleNumberClick(int index) {
        // Check if the numberTextView is already empty
        if (numberTextViews[index].getText().toString().isEmpty()) {
            return; // Do nothing if already empty
        }

        // Find the first empty questionbarTextView
        int emptyIndex = -1;
        for (int i = 1; i < 3; i++) {
            if (questionbarTextViews[i].getText().toString().isEmpty()) {
                emptyIndex = i;
                break;
            }
        }

        if (emptyIndex != -1) {
            // Move the number from numberTextView to questionbarTextView
            questionbarTextViews[emptyIndex].setText(numberTextViews[index].getText());
            numberTextViews[index].setText(""); // Clear the numberTextView
            undo.push(index);
            undo.push(emptyIndex);
        }

        // Check if all questionbarTextViews are filled
        boolean allFilled = true;
        for (int i = 1; i < 3; i++) {
            if (questionbarTextViews[i].getText().toString().isEmpty()) {
                allFilled = false;
                break;
            }
        }

        if (allFilled) {
            // Check the answer
            checkAnswer();
        }
    }

    private void gameInstruct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.composing_number_popup, null);
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
        Random rand = new Random();

        // Generate a random number between 1 and 999 for cpq1
        targetNumber = rand.nextInt(999) + 1;
        questionbarTextViews[0].setText(String.valueOf(targetNumber));

        // Generate two random numbers for composing
        composingNumber1 = rand.nextInt(targetNumber);
        composingNumber2 = targetNumber - composingNumber1;

        // Generate four random numbers for cpans1, cpans2, cpans3, and cpans4
        numbers = game.generateUniqueRandomNumbers(rand, 4);

        // Insert the composing numbers into the list of random numbers
        int composingIndex1 = rand.nextInt(4); // Randomly select the index for the first composing number
        int composingIndex2;
        do {
            composingIndex2 = rand.nextInt(4); // Randomly select the index for the second composing number
        } while (composingIndex2 == composingIndex1); // Ensure the second index is different from the first

        numbers.set(composingIndex1, composingNumber1);
        numbers.set(composingIndex2, composingNumber2);

        // Display the numbers in TextViews
        game.putInNumberTextViews(numberTextViews,numbers);
    }

    private void checkAnswer() {

        int ans1 = Integer.parseInt(questionbarTextViews[1].getText().toString());
        int ans2 = Integer.parseInt(questionbarTextViews[2].getText().toString());

        // Check if the composing numbers add up to the target number
        if ((ans1 + ans2) == targetNumber) {
            score += 2;
            extendTime(4);
            undo.clear();
            scoreTextView.setText(String.valueOf(score));
            game.emptyQuestionbarTextViews(questionbarTextViews,1,3);
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            generateNumbers();
        } else {
            incorrectAttempts++;
            lifeImages[3 - incorrectAttempts].setVisibility(View.GONE);
            undo.clear();
            if (incorrectAttempts >= 3) {
                endGame();
            }
            Toast.makeText(this, "Incorrect! Try again.", Toast.LENGTH_SHORT).show();
            // Clear questionbarTextViews for user to rearrange
            game.emptyQuestionbarTextViews(questionbarTextViews,1,3);
            game.putInNumberTextViews(numberTextViews,numbers);
        }
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

        SharedPreferences preferences = getSharedPreferences("HighestScore2", MODE_PRIVATE);
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

        endGame();

        if (shouldNavigateBack && !isFinishing()) {
            super.onBackPressed();
        }
    }
}


