package com.mc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.domain.ScoreBattle;
import com.mc.domain.User;
import com.mc.vo.ScoreVo;

import java.util.List;

/**
 * @author 江辉彬
 * @version 1.0
 */
public interface UserService extends IService<User> {
    String insertUser(User user);
    List<User> getUserList(User user);

    String checkRegister(User user);

    String checkLogin(User user);

    String updateUser(User user);

    String processWinOrLoseGame(User user, ScoreVo scoreVo);
    String processWinOrLoseGameBattle(User user, ScoreBattle scoreBattle);

    String winGame(User user);

    String loseGame(User user);

    String winBattleGame(User user);

    String loseBattleGame(User user);

    String drawBattleGame(User user);
}
