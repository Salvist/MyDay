package com.diary.myday;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class TicTacToe extends AppCompatActivity implements View.OnClickListener{
    TextView win_check;
    private Button[][] ttt = new Button[3][3];
    private int clickable = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ttt_game);
        checkSettings();

        ttt_game();
    }

    public void ttt_game(){
        win_check = findViewById(R.id.ttt_win);
        win_check.setText("");

        ttt[0][0] = findViewById(R.id.ttt1);
        ttt[0][1] = findViewById(R.id.ttt2);
        ttt[0][2] = findViewById(R.id.ttt3);
        ttt[1][0] = findViewById(R.id.ttt4);
        ttt[1][1] = findViewById(R.id.ttt5);
        ttt[1][2] = findViewById(R.id.ttt6);
        ttt[2][0] = findViewById(R.id.ttt7);
        ttt[2][1] = findViewById(R.id.ttt8);
        ttt[2][2] = findViewById(R.id.ttt9);

        for(int r = 0; r < 3; r++){
            for(int c = 0; c < 3; c++){
                ttt[r][c].setText("");
                ttt[r][c].setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View view){
        Button btn = (Button) view;
        btn.setText("X");
        btn.setClickable(false);
        clickable--;

        if(checkWin("X")){
            win_check.setText("You won!");
            for(int r = 0; r < 3; r++){
                for(int c = 0; c < 3; c++){
                    ttt[r][c].setClickable(false);
                }
            }
            return;
        }

        enemyMove();
        if(checkWin("O")){
            win_check.setText("You lost :(");
            for(int r = 0; r < 3; r++){
                for(int c = 0; c < 3; c++){
                    ttt[r][c].setClickable(false);
                }
            }
            return;
        }
    }

    public void enemyMove(){
        if(clickable == 0) return;

        Random rand = new Random();
        int rand1 = rand.nextInt(3);
        int rand2 = rand.nextInt(3);
        Boolean move = true;

        while(move){
            if(clickable==0) move = false;
            if(ttt[rand1][rand2].isClickable()){
                ttt[rand1][rand2].setText("O");
                ttt[rand1][rand2].setClickable(false);
                clickable--;
                move = false;
            } else {
                rand1 = rand.nextInt(3);
                rand2 = rand.nextInt(3);
            }
        }
    }

    public Boolean checkWin(String check){
        String[][] field = new String[3][3];

        for(int r = 0; r < 3; r++){
            for(int c = 0; c < 3; c++){
                field[r][c] = ttt[r][c].getText().toString();
            }
        }


        for (int r = 0; r < 3; r++) {
            if (field[r][0].equals(field[r][1]) && field[r][1].equals(field[r][2]) && field[r][2].contains(check)) {
                return true;
            }
        }
        for (int c = 0; c < 3; c++) {
            if (field[0][c].equals(field[1][c]) && field[1][c].equals(field[2][c]) && field[2][c].contains(check)) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) && field[1][1].equals(field[2][2]) && field[2][2].contains(check)) {
            return true;
        }

        if (field[0][2].equals(field[1][1]) && field[1][1].equals(field[2][0]) && field[2][0].contains(check)) {
            return true;
        }
        return false;

    }

    public void close(View v){
        finish();
    }

    public void checkSettings(){
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath();
        File subFolder = new File(folder);
        File[] list = subFolder.listFiles();

        FileInputStream fis = null;
        for(File f: list){
            if(f.getName().contains("settings.txt")) {
                System.out.println("settings.txt found");
                try {
                    fis = new FileInputStream(new File(subFolder, f.getName()));
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);

                    String text;
                    while ((text = br.readLine()) != null){
                        if (text.startsWith("Background: ")) applySettings(text.substring(12));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public void applySettings(String color){
        View current = this.findViewById(android.R.id.content);
        System.out.println("applying color: " + color);
        String[] colorNames = getResources().getStringArray(R.array.colorNames);
        for(int i=0; i<colorNames.length; i++)
        {
            //Getting the color resource id
            TypedArray ta = getResources().obtainTypedArray(R.array.colors);
            int colorToUse = ta.getResourceId(i, 0);

            if(color.contains(colorNames[i])) {
                current.setBackgroundResource(colorToUse);
                System.out.println(colorToUse);
            }
        }
    }
}
