package com.mc.service;

import com.mc.vo.BoardVo;

/**
 * @author 江辉彬
 * @version 1.0
 */
public interface BoardService {
    Integer getLevelRank(Integer row, Integer col, Integer mineNumber);
    BoardVo createNewBoardByRank(Integer rank);
}
