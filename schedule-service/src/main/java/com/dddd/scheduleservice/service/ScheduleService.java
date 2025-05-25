package com.dddd.scheduleservice.service;

import com.dddd.scheduleservice.dto.*;
import com.dddd.scheduleservice.entity.FileRecord;
import com.dddd.scheduleservice.entity.Panel;
import com.dddd.scheduleservice.entity.Schedule;
import com.dddd.scheduleservice.entity.ScheduleContent;
import com.dddd.scheduleservice.entity.SchedulePanel;
import com.dddd.scheduleservice.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleContentRepository scheduleContentRepository;
    @Autowired
    private SchedulePanelRepository schedulePanelRepository;
    @Autowired
    private FileRecordRepository fileRecordRepository;
    @Autowired
    private PanelRepository panelRepository;

    public String createSchedule(CreateScheduleRequest req, Long subscriberId) {
        Schedule schedule = new Schedule();
        schedule.setSubscriberId(subscriberId);
        schedule.setName(req.getName());
        schedule.setStartTime(req.getStartTime());
        schedule.setEndTime(req.getEndTime());
        schedule.setRepeatType(req.getRepeatType());
        schedule.setPriority(req.getPriority());
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());

        schedule = scheduleRepository.save(schedule);

        for (ContentOrderDTO contentDTO : req.getContents()) {
            ScheduleContent sc = new ScheduleContent();
            sc.setScheduleId(schedule.getId());
            sc.setContentId(contentDTO.getContentId());
            sc.setOrderNo(contentDTO.getOrderNo());
            scheduleContentRepository.save(sc);
        }

        for (Long panelId : req.getPanelIds()) {
            SchedulePanel sp = new SchedulePanel();
            sp.setScheduleId(schedule.getId());
            sp.setPanelId(panelId);
            schedulePanelRepository.save(sp);
        }

        return schedule.getId().toString();
    }

    public Page<ScheduleSummaryResponse> listSchedulesByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedules = scheduleRepository.findBySubscriberId(userId, pageable);

        return schedules.map(schedule -> {
            ScheduleSummaryResponse resp = new ScheduleSummaryResponse();
            resp.setId(schedule.getId());
            resp.setName(schedule.getName());
            resp.setStartTime(schedule.getStartTime());
            resp.setEndTime(schedule.getEndTime());
            resp.setRepeatType(schedule.getRepeatType());
            resp.setPriority(schedule.getPriority());

            int contentCount = scheduleContentRepository.countByScheduleId(schedule.getId());
            int panelCount = schedulePanelRepository.countByScheduleId(schedule.getId());

            resp.setContentCount(contentCount);
            resp.setPanelCount(panelCount);
            return resp;
        });
    }
    public ScheduleDetailResponse getScheduleDetail(Long scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findByIdAndSubscriberId(scheduleId, userId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        ScheduleDetailResponse resp = new ScheduleDetailResponse();
        resp.setId(schedule.getId());
        resp.setName(schedule.getName());
        resp.setStartTime(schedule.getStartTime());
        resp.setEndTime(schedule.getEndTime());
        resp.setRepeatType(schedule.getRepeatType());
        resp.setPriority(schedule.getPriority());

        List<ScheduleContent> scList = scheduleContentRepository.findByScheduleId(scheduleId);
        List<Long> contentIds = scList.stream().map(ScheduleContent::getContentId).toList();

        List<FileRecord> contentList = fileRecordRepository.findAllById(contentIds);
        List<ScheduleDetailResponse.ContentItem> contentItems = contentList.stream().map(f -> {
            ScheduleDetailResponse.ContentItem ci = new ScheduleDetailResponse.ContentItem();
            ci.setId(f.getId());
            ci.setOriginalName(f.getOriginalName());
            ci.setUrl(f.getUrl());
            ci.setOrderNo(scList.get(contentIds.indexOf(f.getId())).getOrderNo());
            return ci;
        }).toList();
        resp.setContents(contentItems);

        List<SchedulePanel> spList = schedulePanelRepository.findByScheduleId(scheduleId);
        List<Long> panelIds = spList.stream().map(SchedulePanel::getPanelId).toList();

        List<Panel> panelList = panelRepository.findAllById(panelIds);
        List<ScheduleDetailResponse.PanelItem> panelItems = panelList.stream().map(p -> {
            ScheduleDetailResponse.PanelItem pi = new ScheduleDetailResponse.PanelItem();
            pi.setId(p.getId());
            pi.setName(p.getName());
            pi.setLocation(p.getLocation());
            return pi;
        }).toList();
        resp.setPanels(panelItems);

        return resp;
    }
    public String deleteSchedule(Long scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findByIdAndSubscriberId(scheduleId, userId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        scheduleRepository.delete(schedule); // 会自动级联删除内容与面板关联
        return "Schedule deleted";
    }
    @Transactional
    public String updateSchedule(Long scheduleId, UpdateScheduleRequest req, Long userId) {
        Schedule schedule = scheduleRepository.findByIdAndSubscriberId(scheduleId, userId)
                .orElseThrow(() -> new RuntimeException("Schedule not found or access denied"));

        schedule.setName(req.getName());
        schedule.setStartTime(req.getStartTime());
        schedule.setEndTime(req.getEndTime());
        schedule.setRepeatType(req.getRepeatType());
        schedule.setPriority(req.getPriority());
        schedule.setUpdatedAt(LocalDateTime.now());
        scheduleRepository.save(schedule);

        // 删除旧记录
        scheduleContentRepository.deleteAllByScheduleId(scheduleId);
        schedulePanelRepository.deleteAllByScheduleId(scheduleId);

        // ✅ 重新保存内容绑定（使用显式顺序）
        for (ContentOrderDTO item : req.getContents()) {
            ScheduleContent sc = new ScheduleContent();
            sc.setScheduleId(scheduleId);
            sc.setContentId(item.getContentId());
            sc.setOrderNo(item.getOrderNo());
            scheduleContentRepository.save(sc);
        }

        // 保存面板绑定
        for (Long panelId : req.getPanelIds()) {
            SchedulePanel sp = new SchedulePanel();
            sp.setScheduleId(scheduleId);
            sp.setPanelId(panelId);
            schedulePanelRepository.save(sp);
        }

        return "Schedule updated successfully";
    }
}