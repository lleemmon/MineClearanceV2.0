package com.mc.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.Constant;
import com.mc.domain.User;
import com.mc.mapper.UserMapper;
import com.mc.service.ScoreBattleService;
import com.mc.service.ScoreService;
import com.mc.service.UserService;
import com.mc.domain.ScoreBattle;
import com.mc.vo.ScoreVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ScoreBattleService scoreBattleService;
    
    @Override
    public String insertUser(User user) {
        user.setCreateTime(new Date());
        int rs = userMapper.insert(user);
        if(rs > 0){
            return Constant.RegisterState.Success;
        }else{
            return Constant.UnKnowError;
        }
    }

    @Override
    public List<User> getUserList(User user) {
        return userMapper.getUserList(user);
    }

    @Override
    public String checkRegister(User user) {
        if(StringUtils.isBlank(user.getUsername())){
            return Constant.RegisterState.UserNameExist;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername() );
        Long count = userMapper.selectCount(queryWrapper);
        if(count == 0){
            return Constant.RegisterState.UserNameNotExist;
        }else{
            return Constant.RegisterState.UserNameExist;
        }
    }

    @Override
    public String checkLogin(User user) {
        if(StringUtils.isBlank(user.getUsername())){
            return Constant.LoginState.UserNameNotFind;
        }
        if(StringUtils.isBlank(user.getPassword())){
            return Constant.LoginState.PasswordError;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        List<User> userList = userMapper.selectList(queryWrapper);
        if(userList.size() == 0){
            return Constant.LoginState.UserNameNotFind;
        }else if(userList.get(0).getPassword().equals(user.getPassword())){
            return Constant.LoginState.Success;
        }else{
            return Constant.LoginState.PasswordError;
        }
    }

    @Override
    public String winGame(User user) {
        user.setWinNumber(user.getWinNumber() + 1);
        user.setGameNumber(user.getGameNumber() + 1);
        return updateUser(user);
    }

    @Override
    public String loseGame(User user) {
        user.setLoseNumber(user.getLoseNumber() + 1);
        user.setGameNumber(user.getGameNumber() + 1);
        return updateUser(user);
    }

    @Override
    public String updateUser(User user) {
        int state = userMapper.updateById(user);
        if(state == 0)
            return Constant.UpdateState.Failed;
        return Constant.UpdateState.Success;
    }

    @Override
    @Transactional
    public String processWinOrLoseGame(User user, ScoreVo scoreVo) {
        user.setRankOwn(scoreVo.getNewRank());
        String state = null;
        switch (scoreVo.getGameResult()){
            case Constant.GameResult.Win:
                state = winGame(user);
                break;
            case Constant.GameResult.Lose:
                state = loseGame(user);
                break;
        }
        assert state != null;
        if(state.equals(Constant.UpdateState.Success)){
            state = scoreService.insertGameRecord(scoreVo);
        }
        return state;
    }

    @Override
    @Transactional
    public String processWinOrLoseGameBattle(User user, ScoreBattle scoreBattle) {
        user.setRankBattle(scoreBattle.getNewRank());
        String state = null;
        switch (scoreBattle.getBattleResult()){
            case Constant.BattleResult.Win:
                state = winBattleGame(user);
                break;
            case Constant.BattleResult.Draw:
                state = drawBattleGame(user);
                break;
            case Constant.BattleResult.Lose:
                state = loseBattleGame(user);
                break;
        }
        scoreBattleService.save(scoreBattle);
        return state;
    }

    @Override
    public String winBattleGame(User user) {
        user.setBattleWinNumber(user.getBattleWinNumber() + 1);
        user.setBattleGameNumber(user.getBattleGameNumber() + 1);
        return updateUser(user);
    }

    @Override
    public String loseBattleGame(User user) {
        user.setBattleLoseNumber(user.getBattleLoseNumber() + 1);
        user.setBattleGameNumber(user.getBattleGameNumber() + 1);
        return updateUser(user);
    }

    @Override
    public String drawBattleGame(User user) {
        user.setBattleGameNumber(user.getBattleGameNumber() + 1);
        return updateUser(user);
    }
}
