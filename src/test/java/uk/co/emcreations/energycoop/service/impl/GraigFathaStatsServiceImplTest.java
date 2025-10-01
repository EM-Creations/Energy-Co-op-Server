package uk.co.emcreations.energycoop.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraigFathaStatsServiceImplTest {
    @Mock
    private VensysGraigFathaClient client;

    @InjectMocks
    private GraigFathaStatsServiceImpl service;

    @Nested
    @DisplayName("getMeanEnergyYield tests")
    class GetMeanEnergyYieldTests {
        @Test
        @DisplayName("Returns data from client successfully")
        void getMeanEnergyYield_returnsData() {
            var meanData = VensysMeanData.builder().value(123.0).build();
            VensysMeanDataResponse response = mock(VensysMeanDataResponse.class);
            when(response.data()).thenReturn(meanData);
            when(client.getMeanEnergyYield()).thenReturn(response);

            VensysMeanData result = service.getMeanEnergyYield();

            assertEquals(meanData, result);
            verify(client).getMeanEnergyYield();
        }

        @Test
        @DisplayName("Throws NullPointerException when client response is null")
        void getMeanEnergyYield_throwsExceptionWhenClientReturnsNull() {
            when(client.getMeanEnergyYield()).thenReturn(null);

            assertThrows(NullPointerException.class, () -> service.getMeanEnergyYield());
            verify(client).getMeanEnergyYield();
        }

        @Test
        @DisplayName("Throws NullPointerException when client response data is null")
        void getMeanEnergyYield_throwsExceptionWhenClientDataIsNull() {
            VensysMeanDataResponse response = mock(VensysMeanDataResponse.class);
            when(response.data()).thenReturn(null);
            when(client.getMeanEnergyYield()).thenReturn(response);

            assertThrows(NullPointerException.class, () -> service.getMeanEnergyYield());
            verify(client).getMeanEnergyYield();
        }
    }

    @Nested
    @DisplayName("getYesterdayPerformance tests")
    class GetYesterdayPerformanceTests {
        @Test
        @DisplayName("Delegates to getPerformance with correct date range")
        void getYesterdayPerformance_delegatesToGetPerformance() {
            var perfData = VensysPerformanceData.builder().powerAvg(42.0).build();
            var yesterday = LocalDate.now().minusDays(1);
            var from = LocalDateTime.of(yesterday, LocalTime.MIDNIGHT);
            var to = LocalDateTime.of(yesterday, LocalTime.MAX);
            GraigFathaStatsServiceImpl spyService = spy(service);
            doReturn(perfData).when(spyService).getPerformance(from, to);

            VensysPerformanceData result = spyService.getYesterdayPerformance();

            assertEquals(perfData, result);
            verify(spyService).getPerformance(from, to);
        }
    }

    @Nested
    @DisplayName("getPerformance tests")
    class GetPerformanceTests {
        @Test
        @DisplayName("Returns first performance data from client")
        void getPerformance_returnsFirstDataPoint() {
            VensysPerformanceData[] perfDataArr = { VensysPerformanceData.builder().powerAvg(99.0).build() };
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(perfDataArr);
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            var from = LocalDateTime.now().minusDays(2);
            var to = LocalDateTime.now();
            VensysPerformanceData result = service.getPerformance(from, to);

            assertEquals(perfDataArr[0], result);
            verify(client).getPerformance(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Throws NullPointerException when client returns null response")
        void getPerformance_throwsExceptionWhenClientReturnsNull() {
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(null);

            var from = LocalDateTime.now().minusDays(2);
            var to = LocalDateTime.now();

            assertThrows(NullPointerException.class, () -> service.getPerformance(from, to));
            verify(client).getPerformance(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Throws ArrayIndexOutOfBoundsException when client returns empty data array")
        void getPerformance_throwsExceptionWhenNoData() {
            VensysPerformanceData[] perfDataArr = {};
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(perfDataArr);
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            var from = LocalDateTime.now().minusDays(2);
            var to = LocalDateTime.now();

            assertThrows(ArrayIndexOutOfBoundsException.class, () -> service.getPerformance(from, to));
            verify(client).getPerformance(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Throws NullPointerException when client response data is null")
        void getPerformance_throwsExceptionWhenClientDataIsNull() {
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(null);
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            var from = LocalDateTime.now().minusDays(2);
            var to = LocalDateTime.now();

            assertThrows(NullPointerException.class, () -> service.getPerformance(from, to));
            verify(client).getPerformance(anyLong(), anyLong());
        }
    }
}
