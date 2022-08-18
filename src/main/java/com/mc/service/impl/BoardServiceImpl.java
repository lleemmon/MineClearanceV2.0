package com.mc.service.impl;

import com.mc.service.BoardService;
import com.mc.vo.BoardVo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Service
public class BoardServiceImpl implements BoardService {
    private final int minNumber = 64;
    private final int maxNumber = 480;
    private final int minMineRate = 10;
    private final int maxMineRate = 30;
    private final List<Double>highWinRate =
            Arrays.asList(0.9528,0.9363,0.9222,0.9045,0.8667
        ,0.8101,0.7358,0.6427,0.5696,0.4670
        ,0.3656,0.2913,0.2182,0.1533,0.1026
        ,0.0719,0.0483,0.0330,0.0212,0.0142,0.0106);
    private final List<Double>lowWinRate =
            Arrays.asList(0.9705,0.9564,0.9387,0.9139,0.8667
        ,0.8290,0.7842,0.7394,0.6910,0.6274
        ,0.5590,0.4929,0.4340,0.3656,0.3019
        ,0.2465,0.2017,0.1627,0.1262,0.1014,0.0802);

    @Override
    public Integer getLevelRank(Integer row, Integer col, Integer mineNumber) {
        int area = row * col;
        double mineRate = 100.0 * mineNumber / area;
        int LeftIndex = (int)Math.floor(mineRate) - minMineRate;
        int RightIndex = (int)Math.ceil(mineRate) - minMineRate;
        double areaRate = 1.0 * (area - minNumber) / (maxNumber - minNumber);
        double leftRate = mineRate - (int)Math.floor(mineRate);
        double lowLeftRate = lowWinRate.get(LeftIndex);
        double lowRightRate = lowWinRate.get(RightIndex);
        double highLeftRate = highWinRate.get(LeftIndex);
        double highRightRate = highWinRate.get(RightIndex);
        double lowMiddleRate = lowLeftRate * leftRate + lowRightRate * (1 - leftRate);
        double highMiddleRate = highLeftRate * leftRate + highRightRate * (1 - leftRate);
        double finalRate = lowMiddleRate * (1 - areaRate) + highMiddleRate * areaRate;
        return (int)Math.floor(1000 / finalRate);
    }

    @Override
    public BoardVo createNewBoardByRank(Integer rank) {
        BoardVo boardVo = new BoardVo();
        while (true) {
            int rc = (int) (Math.random() * 13 + 8);
            int minMineNumber = (int) (Math.ceil(rc * rc * 0.1));
            int maxMineNumber = (int) (Math.floor(rc * rc * 0.3));
            int mineNumber = (int) (Math.random() * (maxMineNumber - minMineNumber + 1) + minMineNumber);
            int score = getLevelRank(rc, rc, mineNumber);
            System.out.println(score + " " + rank);
            if(score <= 1200 && rank <= 1000){
                boardVo.setRow(rc);
                boardVo.setCol(rc);
                boardVo.setMineNumber(mineNumber);
                boardVo.setLevelScore(score);
                break;
            }
            else if(score >= rank * 0.8 && score <= rank * 1.2){
                boardVo.setRow(rc);
                boardVo.setCol(rc);
                boardVo.setMineNumber(mineNumber);
                boardVo.setLevelScore(score);
                break;
            }
        }
        return boardVo;
    }
}
