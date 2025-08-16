package uk.co.emcreations.energycoop.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysMeanDataResponse;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceDataResponse;
import uk.co.emcreations.energycoop.sourceclient.VensysGraigFathaClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraigFathaStatsServiceImplTest {
    @Mock VensysGraigFathaClient client;
    @InjectMocks GraigFathaStatsServiceImpl service;

    @Test
    @DisplayName("getMeanEnergyYield delegates to client and returns data")
    void testGetMeanEnergyYield() {
        VensysMeanData meanData = VensysMeanData.builder().value(123.0).build();
        VensysMeanDataResponse response = mock(VensysMeanDataResponse.class);
        when(response.data()).thenReturn(meanData);
        when(client.getMeanEnergyYield()).thenReturn(response);
        VensysMeanData result = service.getMeanEnergyYield();
        assertEquals(meanData, result);
        verify(client).getMeanEnergyYield();
    }

    @Test
    @DisplayName("getYesterdayPerformance delegates to getPerformance with correct dates")
    void testGetYesterdayPerformance() {
        VensysPerformanceData perfData = VensysPerformanceData.builder().powerAvg(42.0).build();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime from = LocalDateTime.of(yesterday, LocalTime.MIDNIGHT);
        LocalDateTime to = LocalDateTime.of(yesterday, LocalTime.MAX);
        GraigFathaStatsServiceImpl spyService = spy(service);
        doReturn(perfData).when(spyService).getPerformance(from, to);
        VensysPerformanceData result = spyService.getYesterdayPerformance();
        assertEquals(perfData, result);
        verify(spyService).getPerformance(from, to);
    }

    @Test
    @DisplayName("getPerformance delegates to client and returns data")
    void testGetPerformance() {
        VensysPerformanceData[] perfDataArr = { VensysPerformanceData.builder().powerAvg(99.0).build() };
        VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
        when(response.data()).thenReturn(perfDataArr);
        when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);
        var from = LocalDateTime.now().minusDays(2);;
        var to = LocalDateTime.now();
        VensysPerformanceData result = service.getPerformance(from, to);
        assertEquals(perfDataArr[0], result);
        verify(client).getPerformance(anyLong(), anyLong());
    }
}
