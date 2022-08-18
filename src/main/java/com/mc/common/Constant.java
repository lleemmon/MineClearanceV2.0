package com.mc.common;

/**
 * @author 江辉彬
 * @version 1.0
 */
//28
public interface Constant {
    String UnKnowError = "0";
    interface LoginState{
        String Success = "1";
        String PasswordError = "2";
        String UserNameNotFind = "3";
    }

    interface RegisterState{
        String Success = "4";
        String UserNameExist = "5";
        String UserNameNotExist = "6";
    }

    interface GameResult{
        String Win = "7";
        String Lose = "8";
    }

    interface BattleResult{
        String Win = "24";
        String Lose = "25";
        String Draw = "26";
    }

    interface UpdateState{
        String Success = "9";
        String Failed = "10";
    }

    interface MessageType{
        String NormalNotice = "11";
        String PersonNumberNotice = "12";
        String UserRankChanged = "17";
        String ForceOutOfLine = "18";
        String StartBattle = "19";
        String MatchSucceeded = "20";
        String ClientGetMatchSucceeded = "21";
        String ClientGetMatchOutOfTime = "22";
        String PushOnlineBattleScore = "23";
        String PushOnlineBattleScoreToClient = "27";
    }

    interface InsertState{
        String Success = "15";
        String Failed = "16";
    }
}
