package com.skax.aiplatform.service.home;

import com.skax.aiplatform.dto.auth.request.AlarmCreateRequest;
import com.skax.aiplatform.dto.common.request.PayApprovalReq;
import com.skax.aiplatform.dto.common.response.ApprovalProjectInfo;
import com.skax.aiplatform.dto.common.response.ApprovalUserInfo;
import com.skax.aiplatform.entity.alarm.GpoAlarmsMas;

import java.util.List;
import java.util.Map;

public interface AlarmService {

    int createAlarm(AlarmCreateRequest alarmCreateRequest);

    List<GpoAlarmsMas> getUnreadAlarms(String username);

    List<GpoAlarmsMas> getNewAlarms(String username);

    void markAsRead(String alarmId, String username);

    void markAsReadBulk(String username);

    void markComplete(String documentId);

    void cancelAlarmInfo(String alarmId, String username);

    ApprovalUserInfo getApprovalUsrInfo(String alarmId);

}
