package com.example.nonograms;

import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 게임판 구성: 세로, 가로 카운트와 버튼 배열
    private TextView[][] columnCountTextViews = new TextView[3][8]; // 세로 칸 카운트
    private TextView[][] rowCountTextViews = new TextView[5][3];    // 가로 칸 카운트
    private Cell[][] buttons = new Cell[5][5];                     // 셀 배열

    // 생명 및 체크 모드 관련
    private int life = 3;          // 초기 생명 값
    private TextView RemainedLife; // 남은 생명 출력 부분
    private ToggleButton checkModeToggle;

    // 게임 종료 관련
    private boolean gameFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 레이아웃 설정

        // 레이아웃과 텍스트뷰 초기화
        TableLayout gameTable = findViewById(R.id.gameTable);
        TableRow.LayoutParams cellLayoutParams = new TableRow.LayoutParams(125, 125);
        RemainedLife = findViewById(R.id.remainedLife);
        updateLifeDisplay();

        initColumnHeaders(gameTable, cellLayoutParams); // 세로 칸 카운트 초기화
        setupGameGrid(gameTable, cellLayoutParams);           // 게임판 초기화
        computeColumnCounts(); // 세로줄 검은 칸 계산
        computeRowCounts();    // 가로줄 검은 칸 계산

        checkModeToggle = findViewById(R.id.modeChangeButton);
        checkModeToggle.setBackgroundResource(R.drawable.cell_selector); // 토글 버튼 스타일 설정
    }

    private void initColumnHeaders(TableLayout table, TableRow.LayoutParams params) {
        for (int i = 0; i < 3; i++) {
            TableRow headerRow = new TableRow(this);
            table.addView(headerRow);

            // 빈 공간 생성
            for (int j = 0; j < 3; j++) {
                TextView placeholder = new TextView(this);
                placeholder.setLayoutParams(params);
                headerRow.addView(placeholder);
            }

            // 세로 카운트용 텍스트뷰 생성
            for (int j = 0; j < 5; j++) {
                TextView header = new TextView(this);
                header.setText("0");
                header.setTextSize(24);
                header.setTypeface(null, Typeface.BOLD);
                header.setGravity(Gravity.CENTER);
                header.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                columnCountTextViews[i][j] = header;
                headerRow.addView(header);
            }
        }
    }

    private void setupGameGrid(TableLayout table, TableRow.LayoutParams params) {
        for (int i = 0; i < 5; i++) {
            TableRow gameRow = new TableRow(this);
            table.addView(gameRow);

            // 가로 카운트용 텍스트뷰 추가
            for (int j = 0; j < 3; j++) {
                TextView rowHeader = new TextView(this);
                rowHeader.setText("0");
                rowHeader.setTextSize(24);
                rowHeader.setTypeface(null, Typeface.BOLD);
                rowHeader.setGravity(Gravity.CENTER);
                rowHeader.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                rowCountTextViews[i][j] = rowHeader;
                gameRow.addView(rowHeader);
            }

            // 셀 추가
            for (int j = 0; j < 5; j++) {
                Cell cell = new Cell(this);
                cell.setLayoutParams(params);
                cell.setOnClickListener(v -> onCellClick((Cell) v));
                buttons[i][j] = cell;
                gameRow.addView(cell);
            }
        }
    }

    private void computeRowCounts() {
        for (int i = 0; i < 5; i++) {
            List<Integer> counts = countBlackSquares(buttons[i]);
            displayCountsInRow(i, counts, rowCountTextViews);
        }
    }

    private void computeColumnCounts() {
        for (int j = 0; j < 5; j++) {
            Cell[] columnCells = extractColumnCells(j);
            List<Integer> counts = countBlackSquares(columnCells);
            displayCountsInColumn(j, counts, columnCountTextViews);
        }
    }

    private Cell[] extractColumnCells(int colIndex) {
        Cell[] column = new Cell[5];
        for (int i = 0; i < 5; i++) {
            column[i] = buttons[i][colIndex];
        }
        return column;
    }

    private List<Integer> countBlackSquares(Cell[] cells) {
        List<Integer> result = new ArrayList<>();
        int currentCount = 0;

        for (Cell cell : cells) {
            if (cell.isBlackSquare()) {
                currentCount++;
            } else if (currentCount > 0) {
                result.add(currentCount);
                currentCount = 0;
            }
        }

        if (currentCount > 0)
            result.add(currentCount);

        return result;
    }

    private void displayCountsInRow(int rowIndex, List<Integer> counts, TextView[][] targetView) {
        for (int i = 0; i < 3; i++) {
            if (i < counts.size()) {
                targetView[rowIndex][2 - i].setText(String.valueOf(counts.get(counts.size() - 1 - i)));
            } else {
                targetView[rowIndex][2 - i].setText("");
            }
        }
    }

    private void displayCountsInColumn(int colIndex, List<Integer> counts, TextView[][] targetView) {
        for (int i = 0; i < 3; i++) {
            if (i < counts.size()) {
                targetView[2 - i][colIndex].setText(String.valueOf(counts.get(counts.size() - 1 - i)));
            } else {
                targetView[2 - i][colIndex].setText("");
            }
        }
    }

    private void onCellClick(Cell cell) {
        if (gameFinished) {
            return;
        }

        if (checkModeToggle.isChecked()) {
            cell.toggleX();
        } else {
            if (!cell.markBlackSquare() && !cell.isChecked()) {
                loseLife();
            }
        }

        if (Cell.getNumBlackSquares() == 0) {
            gameResult(true);
        }
    }

    private void loseLife() {
        if (--life <= 0) {
            gameResult(false);
        } else {
            updateLifeDisplay();
        }
    }

    private void updateLifeDisplay() {
        RemainedLife.setText("Life: " + life);
    }

    private void gameResult(boolean win) {
        String message;
        gameFinished = true;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                buttons[i][j].setEnabled(false);
            }
        }

        if (win) {
            message = "GAME CLEAR";
        } else {
            message = "GAME OVER";
        }
        RemainedLife.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
