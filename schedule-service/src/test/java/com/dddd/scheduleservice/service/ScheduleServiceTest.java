package com.dddd.scheduleservice.service;

import com.dddd.scheduleservice.dto.ContentOrderDTO;
import com.dddd.scheduleservice.dto.CreateScheduleRequest;
import com.dddd.scheduleservice.entity.*;
import com.dddd.scheduleservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ScheduleContentRepository scheduleContentRepository;
    @Mock
    private SchedulePanelRepository schedulePanelRepository;
    @Mock
    private FileRecordRepository fileRecordRepository;
    @Mock
    private PanelRepository panelRepository;

    private final Long mockSubscriberId = 123L;

    @BeforeEach
    void setup() {
        lenient().when(fileRecordRepository.findById(anyLong()))
                .thenReturn(Optional.of(new FileRecord()));

        lenient().when(panelRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Panel()));

        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
            Object arg = invocation.getArgument(0);
            assertNotNull(arg, "❌ scheduleRepository.save(...) 收到了 null 对象！");
            Schedule s = (Schedule) arg;
            s.setId(100L); // ✅ 模拟 DB 自动生成 ID
            return s;
        });
    }

    @Test
    void testCreateSchedule_success() {
        // Arrange
        CreateScheduleRequest request = new CreateScheduleRequest();
        request.setName("Test Schedule");
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(1));
        ContentOrderDTO c1 = new ContentOrderDTO();
        c1.setContentId(101L);
        c1.setOrderNo(1);
        ContentOrderDTO c2 = new ContentOrderDTO();
        c2.setContentId(102L);
        c2.setOrderNo(2);
        request.setContents(List.of(c1, c2));
        request.setPanelIds(List.of(10L, 20L));


        // 修复空指针：确保模拟的 Schedule 不为 null
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
            Schedule s = invocation.getArgument(0);
            assertNotNull(s, "❌ scheduleRepository.save() 传入了 null");
            s.setId(100L); // ✅ 设置 ID 模拟 DB 自动生成
            return s;
        });

        // Act
        String id = scheduleService.createSchedule(request, mockSubscriberId);

        // Assert
        assertEquals("100", id);  // ✅ 推荐断言返回 schedule ID

        verify(scheduleRepository, times(1)).save(any());
        verify(scheduleContentRepository, times(2)).save(any());
        verify(schedulePanelRepository, times(2)).save(any());
    }

}