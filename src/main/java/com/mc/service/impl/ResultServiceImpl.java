package com.mc.service.impl;

import com.mc.common.Constant;
import com.mc.service.ResultService;
import com.mc.vo.ScoreVo;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Service
public class ResultServiceImpl implements ResultService {

    @Override
    public ScoreVo getGameResult(ScoreVo scoreVo) {
        scoreVo = getGameResultWithNoRankChange(scoreVo);
        scoreVo.setNewRank((int)(scoreVo.getTotalScore() * 0.1 + scoreVo.getOriginalRank() * 0.9));
        scoreVo.setRankFloat(scoreVo.getNewRank() - scoreVo.getOriginalRank());
        return scoreVo;
    }

    @Override
    public ScoreVo getGameResultWithNoRankChange(ScoreVo scoreVo) {
        scoreVo.setPlayTime(new Date());
        String gameResult = scoreVo.getGameResult();
        int boardLevel = scoreVo.getBoardLevel();
        int openBoardNumber = scoreVo.getOpenBoardNumber();
        int totalBoardNumber = scoreVo.getTotalBoardNumber();
        int killMineNumber = scoreVo.getKillMineNumber();
        int totalMineNumber = scoreVo.getTotalMineNumber();
        int useTime = scoreVo.getUseTime() / 100;
        int predUseTime = 40 * totalMineNumber + 8 * totalBoardNumber;
        int timeScore = (int)(Math.max(0.5 * boardLevel * (predUseTime - useTime) / predUseTime, 0));
        timeScore = Math.min(timeScore, boardLevel / 2);
        switch (gameResult){
            case Constant.GameResult.Win:
                scoreVo.setWinLoseScore(boardLevel / 2);
                scoreVo.setGameResult(Constant.GameResult.Win);
                break;
            case Constant.GameResult.Lose:
                scoreVo.setWinLoseScore(0);
                scoreVo.setGameResult(Constant.GameResult.Lose);
                break;
        }
        scoreVo.setBoardScore((int)(0.4 * openBoardNumber / totalBoardNumber * boardLevel));
        scoreVo.setMineScore((int)(0.6 * killMineNumber / totalMineNumber * boardLevel));
        scoreVo.setTimeScore(timeScore);
        int totalScore = scoreVo.getWinLoseScore() + scoreVo.getBoardScore()
                + scoreVo.getMineScore() + scoreVo.getTimeScore();
        scoreVo.setTotalScore(totalScore);
        return scoreVo;
    }
}
