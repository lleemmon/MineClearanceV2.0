package com.mc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.Constant;
import com.mc.domain.Score;
import com.mc.mapper.ScoreMapper;
import com.mc.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public String insertGameRecord(Score score) {
        int state = scoreMapper.insert(score);
        if(state == 0)
            return Constant.InsertState.Failed;
        return Constant.InsertState.Success;
    }
}
