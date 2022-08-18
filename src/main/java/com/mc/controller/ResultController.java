package com.mc.controller;

import com.mc.domain.User;
import com.mc.service.ResultService;
import com.mc.service.UserService;
import com.mc.domain.ScoreBattle;
import com.mc.vo.ScoreVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author 江辉彬
 * @version 1.0
 */
@RequestMapping("/result")
@RestController
public class ResultController {
    @Autowired
    private ResultService resultService;
    @Autowired
    private UserService userService;

    @PostMapping("/getScore")
    private ScoreVo getGameResult(@RequestBody ScoreVo scoreVo, HttpServletRequest request){
        HttpSession session = request.getSession();
        User currUser = (User)(session.getAttribute("CurrUser"));
        scoreVo.setOriginalRank(currUser.getRankOwn());
        scoreVo = resultService.getGameResult(scoreVo);
        scoreVo.setUsername(currUser.getUsername());
        userService.processWinOrLoseGame(currUser, scoreVo);
        session.setAttribute("CurrUser", currUser);
        return scoreVo;
    }

    @PostMapping("/getScoreWithNoRankChange")
    private ScoreVo getGameResultWithNoRankChange(@RequestBody ScoreVo scoreVo, HttpServletRequest request){
        HttpSession session = request.getSession();
        User currUser = (User)(session.getAttribute("CurrUser"));
        scoreVo.setOriginalRank(currUser.getRankOwn());
        scoreVo.setUsername(currUser.getUsername());
        scoreVo = resultService.getGameResultWithNoRankChange(scoreVo);
        return scoreVo;
    }

    @PostMapping("/getBattleScore")
    private ScoreBattle getGameBattleResult(@RequestBody ScoreBattle scoreBattle, HttpServletRequest request){
        HttpSession session = request.getSession();
        User currUser = (User)(session.getAttribute("CurrUser"));
        userService.processWinOrLoseGameBattle(currUser, scoreBattle);
        session.setAttribute("CurrUser", currUser);
        return scoreBattle;
    }
}
