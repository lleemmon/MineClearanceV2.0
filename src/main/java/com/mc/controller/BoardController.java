package com.mc.controller;

import com.mc.service.BoardService;
import com.mc.socket.WebSocket;
import com.mc.vo.BoardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author 江辉彬
 * @version 1.0
 */
@RequestMapping("/board")
@RestController
public class BoardController {
    @Autowired
    private BoardService boardService;

    @PostMapping("/create")
    private BoardVo create(@RequestBody Map map){
        boolean isOnlineBattle = (boolean) map.get("isOnlineBattle");
        BoardVo boardVo = boardService.createNewBoardByRank((int) map.get("rank"));
        synchronized(this) {
            if (isOnlineBattle) {
                boardVo = WebSocket.getBoardVoFromBattleMap((String) map.get("username1p"), boardVo);
            }
        }
        return boardVo;
    }
}
