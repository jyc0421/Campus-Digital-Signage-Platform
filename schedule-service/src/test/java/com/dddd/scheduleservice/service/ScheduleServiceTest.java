package com.dddd.scheduleservice.service;

import com.dddd.scheduleservice.dto.*;
import com.dddd.scheduleservice.entity.*;
import com.dddd.scheduleservice.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    @Mock private ScheduleRepository scheduleRepository;
    @Mock private ScheduleContentRepository scheduleContentRepository;
    @Mock private SchedulePanelRepository schedulePanelRepository;
    @Mock private FileRecordRepository fileRecordRepository;
    @Mock private PanelRepository panelRepository;

    @InjectMocks private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CreateScheduleRequest mockCreateRequest() {
        CreateScheduleRequest req = new CreateScheduleRequest();
        req.setName("Test Schedule");
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(1));
        req.setRepeatType("daily");
        req.setPriority(1);

        ContentOrderDTO content = new ContentOrderDTO();
        content.setContentId(100L);
        content.setOrderNo(1);
        req.setContents(List.of(content));

        req.setPanelIds(List.of(200L));
        return req;
    }

    @Test
    void testCreateSchedule() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        when(scheduleRepository.save(any())).thenReturn(schedule);

        String result = scheduleService.createSchedule(mockCreateRequest(), 99L);

        assertEquals("1", result);
        verify(scheduleContentRepository).save(any(ScheduleContent.class));
        verify(schedulePanelRepository).save(any(SchedulePanel.class));
    }

    @Test
    void testGetScheduleDetail() {
        long sid = 1L;
        long uid = 123L;

        // schedule
        Schedule schedule = new Schedule();
        schedule.setId(sid);
        schedule.setSubscriberId(uid);
        schedule.setName("Test");
        schedule.setStartTime(LocalDateTime.now());
        schedule.setEndTime(LocalDateTime.now().plusMinutes(10));
        schedule.setRepeatType("daily");
        schedule.setPriority(1);

        when(scheduleRepository.findByIdAndSubscriberId(sid, uid)).thenReturn(Optional.of(schedule));

        // contents
        ScheduleContent sc = new ScheduleContent();
        sc.setScheduleId(sid);
        sc.setContentId(101L);
        sc.setOrderNo(1);
        when(scheduleContentRepository.findByScheduleId(sid)).thenReturn(List.of(sc));

        FileRecord file = new FileRecord();
        file.setId(101L);
        file.setOriginalName("file.mp4");
        file.setUrl("http://oss.com/file.mp4");
        when(fileRecordRepository.findAllById(List.of(101L))).thenReturn(List.of(file));

        // panels
        SchedulePanel sp = new SchedulePanel();
        sp.setScheduleId(sid);
        sp.setPanelId(202L);
        when(schedulePanelRepository.findByScheduleId(sid)).thenReturn(List.of(sp));

        Panel panel = new Panel();
        panel.setId(202L);
        panel.setName("Panel A");
        panel.setLocation("Room 1");
        when(panelRepository.findAllById(List.of(202L))).thenReturn(List.of(panel));

        ScheduleDetailResponse detail = scheduleService.getScheduleDetail(sid, uid);
        assertEquals("Test", detail.getName());
        assertEquals(1, detail.getContents().size());
        assertEquals(1, detail.getPanels().size());
    }

    @Test
    void testDeleteSchedule() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        when(scheduleRepository.findByIdAndSubscriberId(1L, 99L)).thenReturn(Optional.of(schedule));

        String result = scheduleService.deleteSchedule(1L, 99L);

        assertEquals("Schedule deleted", result);
        verify(scheduleRepository).delete(schedule);
    }

    @Test
    void testUpdateSchedule() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        when(scheduleRepository.findByIdAndSubscriberId(1L, 100L)).thenReturn(Optional.of(schedule));

        UpdateScheduleRequest req = new UpdateScheduleRequest();
        req.setName("Updated Name");
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(1));
        req.setRepeatType("weekly");
        req.setPriority(2);
        ContentOrderDTO content = new ContentOrderDTO();
        content.setContentId(777L);
        content.setOrderNo(1);
        req.setContents(List.of(content));
        req.setPanelIds(List.of(888L));

        String result = scheduleService.updateSchedule(1L, req, 100L);

        assertEquals("Schedule updated successfully", result);
        verify(scheduleContentRepository).deleteAllByScheduleId(1L);
        verify(scheduleContentRepository).save(any(ScheduleContent.class));
        verify(schedulePanelRepository).save(any(SchedulePanel.class));
    }
    @Test
    void testGetCurrentPlayCommand() {
        Schedule schedule = new Schedule();
        schedule.setId(55L);
        schedule.setEndTime(LocalDateTime.now().plusMinutes(10));

        when(scheduleRepository.findActiveScheduleForPanel(eq(200L), any()))
                .thenReturn(Optional.of(schedule));

        PlayCommandDto cmd = scheduleService.getCurrentPlayCommand(200L);

        assertNotNull(cmd);
        assertEquals(55L, cmd.getScheduleId());
        assertTrue(cmd.getDuration() <= 600);
    }

    @Test
    void testGetCurrentPlayCommand_NoActiveSchedule() {
        when(scheduleRepository.findActiveScheduleForPanel(eq(999L), any()))
                .thenReturn(Optional.empty());

        PlayCommandDto cmd = scheduleService.getCurrentPlayCommand(999L);
        assertNull(cmd);
    }
}
