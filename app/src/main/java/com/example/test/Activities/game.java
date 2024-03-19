package com.example.test.Activities;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class game extends AppCompatActivity {

    static ArrayList<Integer> generateUniqueRandomNumbers(Random rand, int count) {
        ArrayList<Integer> numbers = new ArrayList<>();
        while (numbers.size() < count) {
            int newNumber = rand.nextInt(999) + 1;
            if (!numbers.contains(newNumber)) {
                numbers.add(newNumber);
            }
        }
        return numbers;
    }

    static void emptyQuestionbarTextViews(TextView[] questionbarTextViews, int startNum, int endNum) {
        for (int i = startNum; i < endNum; i++) {
            questionbarTextViews[i].setText("");
        }
    }

    static void putInNumberTextViews(TextView[] numberTextViews,ArrayList<Integer> numbers) {
        for (int i = 0; i < 4; i++) {
            numberTextViews[i].setText(String.valueOf(numbers.get(i)));
        }
    }
}


