package com.example.nonograms;

import android.content.Context;
import android.graphics.Color;
import android.widget.TableRow;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Random;

public class Cell extends AppCompatButton {
    private boolean blackSquare;
    private boolean checked;
    private static int numBlackSquares = 0;
    private static final Random random = new Random();  // 랜덤 객체 (blackSquare 배치에 사용)

    public Cell(@NonNull Context context) {
        super(context);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(150, 150);
        layoutParams.setMargins(0, 0, 0, 0);
        setLayoutParams(layoutParams);

        // 배경 리소스 설정: cell_selector는 기본 배경 모양
        setBackgroundResource(R.drawable.cell_selector);

        // blackSquare가 될 확률을 랜덤으로 결정
        this.blackSquare = random.nextBoolean();  // 랜덤하게 true/false 결정
        if (blackSquare) {
            numBlackSquares++;  // blackSquare가 생성되면 전체 검은 사각형 수 증가
        }
    }

    // 해당 셀이 blackSquare인지 확인하는 메서드
    public boolean isBlackSquare() {
        return blackSquare;
    }

    // 셀이 X 표시 상태인지를 확인하는 메서드
    public boolean isChecked() {
        return checked;
    }

    // 전체 blackSquare 수를 반환하는 메서드
    public static int getNumBlackSquares() {
        return numBlackSquares;
    }

    // blackSquare를 마킹하는 메서드 (게임에서 성공적인 클릭 처리)
    public boolean markBlackSquare() {
        if (checked) {
            return true;  // X 표시가 이미 되어 있으면 아무 동작도 하지 않음
        }

        if (blackSquare) {
            // blackSquare이면 배경색을 검정으로 변경하고 클릭할 수 없게 비활성화
            setBackgroundColor(Color.BLACK);
            setEnabled(false);  // 클릭 비활성화
            numBlackSquares--;  // blackSquare 수 1 감소
            return true;  // 성공적으로 마킹
        } else {
            return false;  // blackSquare가 아니면 실패
        }
    }

    // X 표시를 토글하는 메서드 (X를 표시하거나 표시를 제거)
    public boolean toggleX() {
        checked = !checked;  // X 표시 상태를 반전

        if (checked) {
            // X 표시가 켜지면 X 아이콘을 배경으로 설정
            setBackgroundResource(R.drawable.x_icon);
        } else {
            // X 표시가 꺼지면 기본 셀 배경으로 복귀
            setBackgroundResource(R.drawable.cell_selector);
        }
        return checked;  // X 표시 상태 반환
    }
}
