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

    // 게임판을 구성하는 TextView 배열들 (세로, 가로 각각 5칸)
    private TextView[][] columnCountTextViews = new TextView[3][8];  // 세로 칸 카운트 표시용
    private TextView[][] rowCountTextViews = new TextView[5][3];     // 가로 칸 카운트 표시용
    private Cell[][] buttons = new Cell[5][5];               // 게임판 셀 버튼들

    // 생명 관련 변수
    private int life = 3;  // 초기 생명 3
    private TextView lifeTextView;  // 생명 표시용 TextView

    // 체크 모드 상태를 나타내는 ToggleButton
    private ToggleButton checkModeToggle;

    // 게임 결과 변수 (true이면 게임 종료됨)
    private boolean result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 액티비티의 레이아웃 설정

        // Layout 정의: TableLayout을 가져와서 게임판을 구성
        TableLayout tableLayout = findViewById(R.id.nonogramTable);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(120, 120);  // 각 셀의 크기 (120x120)

        // 생명 관련 TextView 초기화
        lifeTextView = findViewById(R.id.textLife);
        refreshLifeText();  // 생명 정보 갱신

        // 상단의 가로 및 세로 칸 카운트 텍스트뷰 초기화
        initializeColumnCountViews(tableLayout, layoutParams);

        // 게임판(버튼, 텍스트) 초기화
        initializeGameBoard(tableLayout, layoutParams);

        // 가로 및 세로의 검은 칸 개수 계산 (게임 규칙에 맞게)
        calculateColumnBlackSquares();
        calculateRowBlackSquares();

        // 체크 모드 변경 버튼 초기화
        checkModeToggle = findViewById(R.id.toggleCheckButton);
        checkModeToggle.setBackgroundResource(R.drawable.cell_selector);  // 버튼 배경 설정
    }

    /**
     * 상단 텍스트뷰 초기화 (세로 칸 카운트 표시)
     */
    private void initializeColumnCountViews(TableLayout tableLayout, TableRow.LayoutParams layoutParams) {
        for (int i = 0; i < 3; i++) {
            TableRow tableRow = new TableRow(this);
            tableLayout.addView(tableRow);

            // 줄 맞춤을 위해 왼쪽 공백 3칸 추가
            for (int j = 0; j < 3; j++) {
                TextView emptyView = new TextView(this);
                emptyView.setLayoutParams(layoutParams);
                tableRow.addView(emptyView);
            }

            // 실제 텍스트뷰를 추가 (세로 칸 카운트 표시용)
            for (int j = 0; j < 5; j++) {
                columnCountTextViews[i][j] = new TextView(this);
                columnCountTextViews[i][j].setText("0");  // 초기 값은 0
                columnCountTextViews[i][j].setTextSize(24);  // 텍스트 크기 설정
                columnCountTextViews[i][j].setTypeface(null, Typeface.BOLD);  // 텍스트 굵게 설정
                columnCountTextViews[i][j].setGravity(Gravity.CENTER);  // 텍스트 중앙 정렬
                columnCountTextViews[i][j].setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));  // 비율로 크기 설정

                tableRow.addView(columnCountTextViews[i][j]);
            }
        }
    }

    /**
     * 게임판 초기화 (실제 셀 버튼 및 텍스트뷰 초기화)
     */
    private void initializeGameBoard(TableLayout tableLayout, TableRow.LayoutParams layoutParams) {
        for (int i = 0; i < 5; i++) {
            TableRow tableRow = new TableRow(this);
            tableLayout.addView(tableRow);

            // 각 칸에 대한 텍스트뷰 추가 (가로 칸 카운트 표시용)
            for (int j = 0; j < 3; j++) {
                rowCountTextViews[i][j] = new TextView(this);
                rowCountTextViews[i][j].setText("0");  // 초기 값은 0
                rowCountTextViews[i][j].setTextSize(24);  // 텍스트 크기 설정
                rowCountTextViews[i][j].setTypeface(null, Typeface.BOLD);  // 텍스트 굵게 설정
                rowCountTextViews[i][j].setGravity(Gravity.CENTER);  // 텍스트 중앙 정렬
                rowCountTextViews[i][j].setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));  // 비율로 크기 설정

                tableRow.addView(rowCountTextViews[i][j]);
            }

            // 각 칸에 대한 버튼 추가 (실제 게임 진행 버튼)
            for (int j = 0; j < 5; j++) {
                buttons[i][j] = new Cell(this);
                buttons[i][j].setLayoutParams(layoutParams);
                buttons[i][j].setOnClickListener(v -> onCellClick((Cell) v));  // 버튼 클릭 시 처리
                tableRow.addView(buttons[i][j]);
            }
        }
    }

    /**
     * 각 가로줄에서 검은 칸의 개수 계산하여 표시
     */
    private void calculateRowBlackSquares() {
        for (int i = 0; i < 5; i++) {
            List<Integer> counts = getCounts(buttons[i]);  // 각 줄에 있는 검은 칸 개수 계산

            // 세로로 텍스트 뷰에 숫자 표시
            for (int col = 0; col < 3; col++) {
                int countText = 0;
                if (col < counts.size()) {
                    countText = counts.size() - 1 - col;
                    rowCountTextViews[i][2 - col].setText(String.valueOf(counts.get(countText)));
                } else {
                    rowCountTextViews[i][2 - col].setText("");  // 빈 칸은 텍스트 비우기
                }
            }
        }
    }

    /**
     * 각 세로줄에서 검은 칸의 개수 계산하여 표시
     */
    private void calculateColumnBlackSquares() {
        for (int j = 0; j < 5; j++) {
            Cell[] colTempButtons = new Cell[5];
            for (int i = 0; i < 5; i++) {
                colTempButtons[i] = buttons[i][j];  // 세로줄에 해당하는 버튼들을 모은 배열
            }
            List<Integer> counts = getCounts(colTempButtons);  // 각 세로줄에 있는 검은 칸 개수 계산
            for (int row = 0; row < 3; row++) {
                if (row < counts.size()) {
                    int countText = counts.size() - 1 - row;
                    columnCountTextViews[2 - row][j].setText(String.valueOf(counts.get(countText)));
                } else {
                    columnCountTextViews[2 - row][j].setText("");  // 빈 칸은 텍스트 비우기
                }
            }
        }
    }

    /**
     * 각 셀에서 검은 칸의 개수를 계산하여 반환하는 함수
     */
    private List<Integer> getCounts(Cell[] buttonRow) {
        List<Integer> counts = new ArrayList<>();
        int count = 0;

        // 각 셀을 순차적으로 검사하여 검은 칸의 연속된 개수를 계산
        for (Cell cell : buttonRow) {
            if (cell.isBlackSquare()) {
                count++;
            } else {
                if (count > 0) {
                    counts.add(count);  // 연속된 검은 칸 개수를 counts 리스트에 추가
                    count = 0;  // 카운트를 리셋
                }
            }
        }
        if (count > 0) counts.add(count);  // 마지막에 남은 연속된 검은 칸 개수 추가

        return counts;
    }

    /**
     * 셀 클릭 시 처리하는 함수
     */
    private void onCellClick(Cell cell) {
        if (result) {
            return;  // 게임이 끝났다면 클릭 처리하지 않음
        }

        // 체크모드가 켜져 있으면 X 표시 모드, 아니면 검은 칸 표시 모드
        if (checkModeToggle.isChecked()) {
            cell.toggleX();  // X 표시 토글
        } else {
            // 검은 칸 찾기 모드
            boolean success = cell.markBlackSquare();  // 검은 칸 표시 시도
            if (!success && !cell.isChecked()) {
                loseLife();  // 실패하면 생명 차감
            }
        }

        // 게임 종료 조건 체크
        isGameComplete();
    }

    /**
     * 생명 감소
     */
    private void loseLife() {
        life--;  // 생명 1 감소
        refreshLifeText();  // 생명 표시 갱신

        // 생명이 0이 되면 게임 종료
        if (life <= 0) {
            finishGame(false, Cell.getNumBlackSquares());
        }
    }

    /**
     * 생명 텍스트 갱신
     */
    private void refreshLifeText() {
        lifeTextView.setText("Life: " + life);
    }

    /**
     * 게임 종료 체크 (모든 검은 칸을 마킹했으면 게임 종료)
     */
    private void isGameComplete() {
        if (Cell.getNumBlackSquares() == 0) {
            finishGame(true, 0);  // 검은 칸이 모두 마킹되었으면 게임 종료
        }
    }

    /**
     * 모든 셀을 비활성화
     */
    private void deactivateAllCells() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                buttons[i][j].setEnabled(false);  // 셀 비활성화
            }
        }
    }

    /**
     * 게임 종료 처리 및 결과 표시
     */
    private void finishGame(boolean hasWon, int remain) {
        result = true;
        deactivateAllCells();  // 모든 셀 비활성화

        String message = hasWon ? "You Win!!" : "GAME OVER \nYou Lose" + "\n못 찾은 개수 : " + remain + "개";
        String toastMessage = hasWon ? "You Win!!" : "You Lose";
        lifeTextView.setText(message);  // 게임 결과 텍스트로 표시
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();  // 게임 결과 메시지 표시
    }
}
