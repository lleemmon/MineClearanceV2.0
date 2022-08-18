package com.mc.socket;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.json.JSONUtil;
import com.mc.common.Constant;
import com.mc.config.HttpSessionConfigurator;
import com.mc.domain.Message;
import com.mc.domain.Score;
import com.mc.domain.User;
import com.mc.vo.BattleUserVo;
import com.mc.vo.BoardVo;
import com.mc.domain.ScoreBattle;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.WsSession;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Component
@Slf4j
@Data
@ServerEndpoint(value= "/webSocket/{username}", configurator = HttpSessionConfigurator.class)
public class WebSocket {
    private Session session;
    private String httpSessionId;
    private String username;
    private final static int maxPlayerNumber = 2;//多人情况下的最多同时游戏人数
    //最多允许同时两人进行对战 否则进行阻塞
    //private static BlockingQueue<User>BattleWaitingBlock = new ArrayBlockingQueue<>(maxPlayerNumber, true);
    //正在进行对战的玩家 12互相的sessionId映射
    private static Map<String, BoardVo> userSessionToBoardVo = new ConcurrentHashMap<>();
    private static Map<String, Score> userSessionToScore = new ConcurrentHashMap<>();
    private static Map<String, String> inBattleStateOneToOne = new ConcurrentHashMap<>();
    private static Set<String> BattleWaitingSet = new ConcurrentHashSet<>();
    private static Map<String, WebSocket> webSockets = new ConcurrentHashMap<>();
    private static Map<String, String> usernameToSessionId = new ConcurrentHashMap<>();
    private static Map<String, User> sessionIdToUser = new ConcurrentHashMap<>();

    static{
        //每秒检查一次BlockingQueue队列 如果长度为2 将队列中的人拉出去比赛
//        new Thread(() ->{
//            while (true) {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                System.out.println("监控数组状况");
//            }
//        }).start();

        new Thread(() ->{
//            while (true){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                if(BattleWaitingBlock.size() == 2){
//                    User player1;
//                    User player2;
//                    try {
//                        player1 = BattleWaitingBlock.take();
//                        player2 = BattleWaitingBlock.take();
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    String Context = "[" + player1.getUsername() + "]和["+ player2.getUsername() +"]开始对局";
//                    String basicMessageJsonStr = getBasicMessageJsonStr(
//                            Constant.MessageType.MatchSucceeded, Context);
//                    serverSendMessageToOneUser(basicMessageJsonStr , player1.getUsername());
//                    serverSendMessageToOneUser(basicMessageJsonStr , player2.getUsername());
//                    System.out.println("[" + player1.getUsername() + "]和["+ player2.getUsername() +"]开始对局");
//                }
//            }
            while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(BattleWaitingSet.size() >= 2){
                    String player1SessionId = "";
                    String player2SessionId = "";
                    int i = 0;
                    for (String s : BattleWaitingSet) {
                        if (i == 0){
                            player1SessionId = s;
                        }
                        else if (i == 1) {
                            player2SessionId = s;
                            break;
                        }
                        i++;
                    }
                    BattleWaitingSet.remove(player1SessionId);
                    BattleWaitingSet.remove(player2SessionId);
                    inBattleStateOneToOne.put(player1SessionId, player2SessionId);
                    inBattleStateOneToOne.put(player2SessionId, player1SessionId);
                    User player1 = sessionIdToUser.get(player1SessionId);
                    User player2 = sessionIdToUser.get(player2SessionId);
                    BattleUserVo battleUserVo = new BattleUserVo();
                    battleUserVo.setUsername1p(player1.getUsername());
                    battleUserVo.setUsername2p(player2.getUsername());
                    battleUserVo.setRankBattle1p(player1.getRankBattle());
                    battleUserVo.setRankBattle2p(player2.getRankBattle());
                    String basicMessageJsonStr = getBasicMessageJsonStr(
                            Constant.MessageType.MatchSucceeded, JSONUtil.toJsonPrettyStr(battleUserVo));
                    serverSendMessageToOneUserByUserName(basicMessageJsonStr , player1.getUsername());
                    serverSendMessageToOneUserByUserName(basicMessageJsonStr , player2.getUsername());
                    System.out.println("[" + player1.getUsername() + "]和["+ player2.getUsername() +"]开始对局");
                }
            }
        }).start();
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, EndpointConfig config){
        try{
            this.httpSessionId = ((WsSession) session).getHttpSessionId();
            this.session = session;
            this.username = username;
            HttpSession httpSession =  (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
            User CurrUser = (User)httpSession.getAttribute("CurrUser");
            if(HaveAlreadyOnline(username)){
                String basicMessageJsonStr = getBasicMessageJsonStr(
                        Constant.MessageType.ForceOutOfLine,
                        "你的账号在异地登录!");
                serverSendMessageToOneUserByUserName(basicMessageJsonStr , username);
            }
            usernameToSessionId.put(username, httpSessionId);
            sessionIdToUser.put(httpSessionId, CurrUser);
            webSockets.put(httpSessionId, this);
            String basicMessageJsonStr = getBasicMessageJsonStr(
                    Constant.MessageType.NormalNotice,
                    "[" + this.username + "]上线了!");
            serverSendMessageToAll(basicMessageJsonStr);
            String onlineNumberChangedMessageJsonStr = getOnlineNumberChangedMessageJsonStr();
            serverSendMessageToAll(onlineNumberChangedMessageJsonStr);
        }catch (Exception e){

        }
    }

    @OnClose
    public void onClose(){
        try{
            String otherHttpSessionId = inBattleStateOneToOne.get(this.httpSessionId);
            webSockets.remove(this.httpSessionId);
            sessionIdToUser.remove(this.httpSessionId);
            BattleWaitingSet.remove(this.httpSessionId);
            userSessionToBoardVo.remove(this.httpSessionId);
            userSessionToScore.remove(this.httpSessionId);
            inBattleStateOneToOne.remove(this.httpSessionId);
            removeSelfMap();
            if(otherHttpSessionId != null){
                userSessionToBoardVo.remove(otherHttpSessionId);
                userSessionToScore.remove(otherHttpSessionId);
                inBattleStateOneToOne.remove(otherHttpSessionId);
            }
            removeSelfMap();
            String basicMessageJsonStr = getBasicMessageJsonStr(
                    Constant.MessageType.NormalNotice,
                    "[" + this.username + "]下线了!");
            serverSendMessageToAll(basicMessageJsonStr);
            String onlineNumberChangedMessageJsonStr = getOnlineNumberChangedMessageJsonStr();
            serverSendMessageToAll(onlineNumberChangedMessageJsonStr);
        }catch (Exception e){

        }
    }

    @OnMessage
    public void OnMessage(String message, @PathParam("username") String username){
        Message messageObj = JSONUtil.toBean(message, Message.class);
        String messageType = messageObj.getMessageType();
        switch (messageType){
            case Constant.MessageType.UserRankChanged:
                serverSendMessageToAll(JSONUtil.toJsonStr(messageObj));
                break;
            case Constant.MessageType.StartBattle:
                System.out.println(username + "正在寻找对手");
                BattleWaitingSet.add(httpSessionId);
                break;
            case Constant.MessageType.ClientGetMatchOutOfTime:
                System.out.println(username + "匹配超时！！");
                //BattleWaitingSet.remove(httpSessionId);
                break;
            case Constant.MessageType.PushOnlineBattleScore:
                System.out.println(username + "已完成对局!");
                onlineBattleOneFinishedAction(httpSessionId, messageObj);
        }
    }

    private static boolean HaveAlreadyOnline(String username){
        return usernameToSessionId.containsKey(username);
    }

    private static String getOnlineNumberChangedMessageJsonStr(){
        Message message = new Message();
        message.setMessageType(Constant.MessageType.PersonNumberNotice);
        message.setContext(webSockets.size());
        return JSONUtil.toJsonStr(message);
    }

    private static String getBasicMessageJsonStr(String messageType, Object context){
        Message message = new Message();
        message.setMessageType(messageType);
        message.setContext(context);
        return JSONUtil.toJsonStr(message);
    }

    private static void serverSendMessageToAll(String message){
        for(WebSocket item: webSockets.values()){
            item.session.getAsyncRemote().sendText(message);
        }
    }

    private static void serverSendMessageToOneUserByUserName(String message, String username){
        String httpSessionId = usernameToSessionId.get(username);
        webSockets.get(httpSessionId).session.getAsyncRemote().sendText(message);
    }

    private static void serverSendMessageToOneUserBySessionId(String message, String sessionId){
        webSockets.get(sessionId).session.getAsyncRemote().sendText(message);
    }

    private void removeSelfMap(){
        boolean inMap = false;
        for(String str:usernameToSessionId.values()){
            if(str.equals(this.httpSessionId)){
                inMap = true;
            }
        }
        if(inMap)
            usernameToSessionId.remove(this.username);
    }

    public static BoardVo getBoardVoFromBattleMap(String username, BoardVo boardVo){
        String sessionId = usernameToSessionId.get(username);
        boolean HaveBoardVo = userSessionToBoardVo.containsKey(sessionId);
        if(HaveBoardVo){
            boardVo = userSessionToBoardVo.get(sessionId);
        }else{
            if(inBattleStateOneToOne.containsKey(sessionId)){
                userSessionToBoardVo.put(inBattleStateOneToOne.get(sessionId), boardVo);
                userSessionToBoardVo.put(sessionId, boardVo);
            } else{
                //掉线 不一定需要逻辑 后期一定时间没有返还 直接判负
            }
        }
        return boardVo;
    }

    private static void onlineBattleOneFinishedAction(String httpSessionId, Message messageObj){
        //1 -> 2  2 -> 1 userSessionToBoardVo记载着难度
        //1进来
        Score score = JSONUtil.toBean((String) messageObj.getContext(), Score.class);
        userSessionToScore.put(httpSessionId, score);
        if(userSessionToScore.containsKey(inBattleStateOneToOne.get(httpSessionId))){
            String otherHttpSessionId = inBattleStateOneToOne.get(httpSessionId);
            ScoreBattle scoreBattle1 = new ScoreBattle();
            ScoreBattle scoreBattle2 = new ScoreBattle();
            User user1 = sessionIdToUser.get(httpSessionId);
            User user2 = sessionIdToUser.get(otherHttpSessionId);
            Score score1 = userSessionToScore.get(httpSessionId);
            Score score2 = userSessionToScore.get(otherHttpSessionId);
            BoardVo boardVo = null;
            if(userSessionToBoardVo.containsKey(httpSessionId))
                boardVo = userSessionToBoardVo.get(httpSessionId);
            if(userSessionToBoardVo.containsKey(otherHttpSessionId))
                boardVo = userSessionToBoardVo.get(otherHttpSessionId);
            assert boardVo != null;
            scoreBattle1.setUsernameOwn(user1.getUsername());
            scoreBattle1.setUsernameOther(user2.getUsername());
            scoreBattle1.setGameResultOwn(score1.getGameResult());
            scoreBattle1.setGameResultOther(score2.getGameResult());
            scoreBattle1.setBoardLevel(boardVo.getLevelScore());
            scoreBattle1.setWinLoseScoreOwn(score1.getWinLoseScore());
            scoreBattle1.setWinLoseScoreOther(score2.getWinLoseScore());
            scoreBattle1.setBoardScoreOwn(score1.getBoardScore());
            scoreBattle1.setBoardScoreOther(score2.getBoardScore());
            scoreBattle1.setMineScoreOwn(score1.getMineScore());
            scoreBattle1.setMineScoreOther(score2.getMineScore());
            scoreBattle1.setTimeScoreOwn(score1.getTimeScore());
            scoreBattle1.setTimeScoreOther(score2.getTimeScore());
            scoreBattle1.setTotalScoreOwn(score1.getTotalScore());
            scoreBattle1.setTotalScoreOther(score2.getTotalScore());
            scoreBattle1.setOriginalRank(user1.getRankBattle());
            scoreBattle1.setPlayTime(new Date());

            scoreBattle2.setUsernameOwn(user2.getUsername());
            scoreBattle2.setUsernameOther(user1.getUsername());
            scoreBattle2.setGameResultOwn(score2.getGameResult());
            scoreBattle2.setGameResultOther(score1.getGameResult());
            scoreBattle2.setBoardLevel(boardVo.getLevelScore());
            scoreBattle2.setWinLoseScoreOwn(score2.getWinLoseScore());
            scoreBattle2.setWinLoseScoreOther(score1.getWinLoseScore());
            scoreBattle2.setBoardScoreOwn(score2.getBoardScore());
            scoreBattle2.setBoardScoreOther(score1.getBoardScore());
            scoreBattle2.setMineScoreOwn(score2.getMineScore());
            scoreBattle2.setMineScoreOther(score1.getMineScore());
            scoreBattle2.setTimeScoreOwn(score2.getTimeScore());
            scoreBattle2.setTimeScoreOther(score1.getTimeScore());
            scoreBattle2.setTotalScoreOwn(score2.getTotalScore());
            scoreBattle2.setTotalScoreOther(score1.getTotalScore());
            scoreBattle2.setOriginalRank(user2.getRankBattle());
            scoreBattle2.setPlayTime(new Date());

            Integer baseDiff = (user1.getRankBattle() - user2.getRankBattle()) / 20;// user1 比 user2高几分
            if(baseDiff > 50)
                baseDiff = 50;
            if(baseDiff < -50)
                baseDiff = -50;

            if(scoreBattle1.getTotalScoreOwn() > scoreBattle2.getTotalScoreOwn()){
                scoreBattle1.setBattleResult(Constant.BattleResult.Win);
                scoreBattle2.setBattleResult(Constant.BattleResult.Lose);
                scoreBattle1.setNewRank(scoreBattle1.getOriginalRank() + 50 - baseDiff);
                scoreBattle2.setNewRank(scoreBattle2.getOriginalRank() - 50 + baseDiff);
            }
            if(scoreBattle1.getTotalScoreOwn() < scoreBattle2.getTotalScoreOwn()){
                scoreBattle1.setBattleResult(Constant.BattleResult.Lose);
                scoreBattle2.setBattleResult(Constant.BattleResult.Win);
                scoreBattle1.setNewRank(scoreBattle1.getOriginalRank() - 50 - baseDiff);
                scoreBattle2.setNewRank(scoreBattle2.getOriginalRank() + 50 + baseDiff);
            }
            if(scoreBattle1.getTotalScoreOwn().equals(scoreBattle2.getTotalScoreOwn())){
                scoreBattle1.setNewRank(scoreBattle1.getOriginalRank() - baseDiff);
                scoreBattle2.setNewRank(scoreBattle2.getOriginalRank() + baseDiff);
                scoreBattle1.setBattleResult(Constant.BattleResult.Draw);
                scoreBattle2.setBattleResult(Constant.BattleResult.Draw);
            }

            user1.setRankBattle(scoreBattle1.getNewRank());
            user2.setRankBattle(scoreBattle2.getNewRank());
            sessionIdToUser.put(httpSessionId, user1);
            sessionIdToUser.put(otherHttpSessionId, user2);

            scoreBattle1.setRankFloat(scoreBattle1.getNewRank() - scoreBattle1.getOriginalRank());
            scoreBattle2.setRankFloat(scoreBattle2.getNewRank() - scoreBattle2.getOriginalRank());

            //给两个人发送对局结果信息
            String basicMessageJsonStr = getBasicMessageJsonStr(
                    Constant.MessageType.PushOnlineBattleScoreToClient,
                    JSONUtil.toJsonPrettyStr(scoreBattle1));
            serverSendMessageToOneUserBySessionId(basicMessageJsonStr , httpSessionId);
            String basicMessageJsonStr2 = getBasicMessageJsonStr(
                    Constant.MessageType.PushOnlineBattleScoreToClient,
                    JSONUtil.toJsonPrettyStr(scoreBattle2));
            serverSendMessageToOneUserBySessionId(basicMessageJsonStr2 , otherHttpSessionId);

            //清除所有与对局有关的信息
            userSessionToBoardVo.remove(httpSessionId);
            userSessionToBoardVo.remove(otherHttpSessionId);
            userSessionToScore.remove(httpSessionId);
            userSessionToScore.remove(otherHttpSessionId);
            inBattleStateOneToOne.remove(httpSessionId);
            inBattleStateOneToOne.remove(otherHttpSessionId);
        }
    }
}

