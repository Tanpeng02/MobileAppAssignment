package com.example.test.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class NumberOrdering extends AppCompatActivity {

    private int score = 0;
    private int incorrectAttempts = 0;
    private TextView[] numberTextViews = new TextView[4];
    private final TextView[] questionbarTextViews = new TextView[4];
    private CountDownTimer timer;
    private boolean shouldNavigateBack = true;
    private boolean isAscendingOrder;
    private TextView scoreTextView;
    private TextView timerTextView;
    private final ImageView[] lifeImages = new ImageView[3];
    ArrayList<Integer> numbers = new ArrayList<>(4);
    private Stack<Integer> undo = new Stack<>();
    private ImageView backButton;
    private ImageView undoButton;
    private long remainingTimeMillis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_ordering);

        numberTextViews[0] = findViewById(R.id.numorderans1);
        numberTextViews[1] = findViewById(R.id.numorderans2);
        numberTextViews[2] = findViewById(R.id.numorderans3);
        numberTextViews[3] = findViewById(R.id.numorderans4);

        questionbarTextViews[0] = findViewById(R.id.numorderq1);
        questionbarTextViews[1] = findViewById(R.id.numorderq2);
        questionbarTextViews[2] = findViewById(R.id.numorderq3);
        questionbarTextViews[3] = findViewById(R.id.numorderq4);

        lifeImages[0] = findViewById(R.id.numorderlife1);
        lifeImages[1] = findViewById(R.id.numorderlife2);
        lifeImages[2] = findViewById(R.id.numorderlife3);

        backButton = findViewById(R.id.numorderback);

        timerTextView = findViewById(R.id.numordertimer);

        scoreTextView = findViewById(R.id.numordertotalScore);

        undoButton = findViewById(R.id.numorderundo);

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
        for (int i = 0; i < 4; i++) {
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
        for (int i = 0; i < 4; i++) {
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
        View customLayout = getLayoutInflater().inflate(R.layout.num_ordering_popup, null);
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
        isAscendingOrder = rand.nextBoolean();
        TextView orderTextView = findViewById(R.id.order);
        ImageView apple1 = findViewById(R.id.apple1);
        ImageView apple2 = findViewById(R.id.apple2);
        ImageView arrow = findViewById(R.id.arrow);

        // Generate random numbers
        numbers.clear(); // Clear the ArrayList before generating new numbers
        numbers.addAll(game.generateUniqueRandomNumbers(rand, 4));

        // Sort the numbers in ascending or descending order based on isAscendingOrder
        if (isAscendingOrder) {
            orderTextView.setText("Ascending Order");
            apple1.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(25), dpToPx(25), 1));
            ((LinearLayout.LayoutParams) apple1.getLayoutParams()).gravity = Gravity.BOTTOM;

            arrow.setRotationX(170);

            apple2.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(30), dpToPx(30), 1));
            ((LinearLayout.LayoutParams) apple2.getLayoutParams()).gravity = Gravity.BOTTOM;
        } else {
            orderTextView.setText("Descending Order");
            apple1.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(30), dpToPx(30), 1));
            ((LinearLayout.LayoutParams) apple1.getLayoutParams()).gravity = Gravity.BOTTOM;

            arrow.setRotationX(0);

            apple2.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(25), dpToPx(25), 1));
            ((LinearLayout.LayoutParams) apple2.getLayoutParams()).gravity = Gravity.BOTTOM;
        }


        // Display the numbers in TextViews
        game.putInNumberTextViews(numberTextViews,numbers);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void checkAnswer() {
        // Convert text from questionbarTextViews to integers
        int[] answer = new int[4];
        for (int i = 0; i < 4; i++) {
            answer[i] = Integer.parseInt(questionbarTextViews[i].getText().toString());
        }

        // Check if the numbers are arranged correctly
        boolean isCorrect = true;
        for (int i = 1; i < 4; i++) {
            if ((isAscendingOrder && answer[i] < answer[i - 1]) || (!isAscendingOrder && answer[i] > answer[i - 1])) {
                isCorrect = false;
                break;
            }
        }

        if (isCorrect) {
            score+=4;
            scoreTextView.setText(String.valueOf(score));
            extendTime(4);
            undo.clear();
            game.emptyQuestionbarTextViews(questionbarTextViews,0,4);
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            generateNumbers();
        } else {
            incorrectAttempts++;
            lifeImages[3 - incorrectAttempts].setVisibility(View.GONE);
            undo.clear();
            if (incorrectAttempts >= 3) {
                endGame();
            }
            Toast.makeText(this, "Incorrect! Rearrange the numbers.", Toast.LENGTH_SHORT).show();
            // Clear questionbarTextViews for user to rearrange
            game.emptyQuestionbarTextViews(questionbarTextViews,0,4);
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

        SharedPreferences preferences = getSharedPreferences("HighestScore1", MODE_PRIVATE);
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


