package uk.co.emcreations.energycoop.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.emcreations.energycoop.dto.VensysMeanData;
import uk.co.emcreations.energycoop.dto.VensysMeanDataResponse;
import uk.co.emcreations.energycoop.dto.VensysPerformanceData;
import uk.co.emcreations.energycoop.dto.VensysPerformanceDataResponse;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.AlertService;
import uk.co.emcreations.energycoop.sourceclient.VensysGraigFathaClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraigFathaStatsServiceImplTest {
    @Mock
    private VensysGraigFathaClient client;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private GraigFathaStatsServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "availabilityThreshold", 75.0);
        ReflectionTestUtils.setField(service, "failureTimeThreshold", 100.0);
    }

    @Nested
    class GetMeanEnergyYieldTests {
        @Test
        void getMeanEnergyYield_returnsData() {
            var meanData = VensysMeanData.builder().value(123.0).build();
            VensysMeanDataResponse response = mock(VensysMeanDataResponse.class);
            when(response.data()).thenReturn(meanData);
            when(client.getMeanEnergyYield()).thenReturn(response);

            Optional<VensysMeanData> result = service.getMeanEnergyYield();

            assertTrue(result.isPresent());
            assertEquals(meanData, result.get());
            verify(client).getMeanEnergyYield();
            verify(client, never()).getCurrentPerformance();
        }

        @Test
        void getMeanEnergyYield_returnsEmptyWhenAllSourcesAreMissing() {
            VensysMeanDataResponse meanResponse = mock(VensysMeanDataResponse.class);
            when(meanResponse.data()).thenReturn(null);
            when(client.getMeanEnergyYield()).thenReturn(meanResponse);

            VensysPerformanceDataResponse perfResponse = mock(VensysPerformanceDataResponse.class);
            when(perfResponse.data()).thenReturn(null);
            when(client.getCurrentPerformance()).thenReturn(perfResponse);

            Optional<VensysMeanData> result = service.getMeanEnergyYield();

            assertFalse(result.isPresent());
            verify(client).getMeanEnergyYield();
            verify(client).getCurrentPerformance();
        }

        @Test
        void getMeanEnergyYield_fallsBackToCurrentPerformance() {
            VensysMeanDataResponse meanResponse = mock(VensysMeanDataResponse.class);
            when(meanResponse.data()).thenReturn(null);
            when(client.getMeanEnergyYield()).thenReturn(meanResponse);

            var perfData = VensysPerformanceData.builder()
                    .energyYield(456.0)
                    .build();
            VensysPerformanceDataResponse perfResponse = mock(VensysPerformanceDataResponse.class);
            when(perfResponse.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(client.getCurrentPerformance()).thenReturn(perfResponse);

            Optional<VensysMeanData> result = service.getMeanEnergyYield();

            assertTrue(result.isPresent());
            assertEquals(456.0, result.get().value());
            verify(client).getMeanEnergyYield();
            verify(client).getCurrentPerformance();
        }
    }

    @Nested
    @DisplayName("getYesterdayPerformance tests")
    class GetYesterdayPerformanceTests {
        @Test
        @DisplayName("Returns performance data for yesterday")
        void getYesterdayPerformance_returnsData() {
            var perfData = VensysPerformanceData.builder()
                    .powerAvg(42.0)
                    .build();
            var perfResponse = mock(VensysPerformanceDataResponse.class);
            when(perfResponse.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(perfResponse);

            VensysPerformanceData result = service.getYesterdayPerformance();

            assertEquals(perfData, result);
            verify(client).getPerformance(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Returns default object when no data available")
        void getYesterdayPerformance_returnsDefaultWhenNoData() {
            var yesterday = LocalDate.now().minusDays(1);
            var from = LocalDateTime.of(yesterday, LocalTime.MIDNIGHT);

            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(null);
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            VensysPerformanceData result = service.getYesterdayPerformance();

            assertNotNull(result);
            assertEquals(from, result.date());
            verify(client).getPerformance(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("getPerformance tests")
    class GetPerformanceTests {
        @Test
        @DisplayName("Returns performance data when available")
        void getPerformance_returnsData() {
            VensysPerformanceData perfData = VensysPerformanceData.builder()
                    .powerAvg(99.0)
                    .build();
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            var from = LocalDateTime.now().minusDays(2);
            var to = LocalDateTime.now();
            Optional<VensysPerformanceData> result = service.getPerformance(from, to);

            assertTrue(result.isPresent());
            assertEquals(perfData, result.get());
            verify(client).getPerformance(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Returns empty Optional when response is null")
        void getPerformance_returnsEmptyWhenResponseNull() {
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(null);

            var from = LocalDateTime.now().minusDays(2);
            var to = LocalDateTime.now();
            Optional<VensysPerformanceData> result = service.getPerformance(from, to);

            assertFalse(result.isPresent());
            verify(client).getPerformance(anyLong(), anyLong());
            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), contains("Performance response is null"));
        }

        @Test
        @DisplayName("Returns empty Optional when data array is empty")
        void getPerformance_returnsEmptyWhenNoData() {
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{});
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            var from = LocalDateTime.now().minusDays(2);
            var to = LocalDateTime.now();
            Optional<VensysPerformanceData> result = service.getPerformance(from, to);

            assertFalse(result.isPresent());
            verify(client).getPerformance(anyLong(), anyLong());
            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), contains("Performance data array is empty"));
        }
    }

    @Nested
    @DisplayName("validatePerformanceData tests")
    class ValidatePerformanceDataTests {
        @Test
        @DisplayName("Sends alert when response is null")
        void validatePerformanceData_sendsAlertWhenResponseNull() {
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(null);

            var from = LocalDateTime.now().minusDays(2);
            var to = LocalDateTime.now();
            var result = service.getPerformance(from, to);

            assertFalse(result.isPresent());
            verify(client).getPerformance(anyLong(), anyLong());
            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), argThat(message ->
                message.contains("Performance response is null")));
        }

        @Test
        @DisplayName("Sends alert when availability is below threshold")
        void validatePerformanceData_sendsAlertWhenAvailabilityBelowThreshold() {
            var perfData = VensysPerformanceData.builder()
                    .availability(70.0)
                    .build();
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(response.from()).thenReturn("2025-11-05T00:00:00");
            when(response.to()).thenReturn("2025-11-05T23:59:59");
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            var from = LocalDateTime.now();
            var to = LocalDateTime.now();
            service.getPerformance(from, to);

            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), argThat(message ->
                message.contains("2025-11-05T00:00:00 -> 2025-11-05T23:59:59") &&
                message.contains("Availability (70.0%) less than threshold (75.0%)")));
        }

        @Test
        @DisplayName("Sends alert when all failure metrics exceed threshold")
        void validatePerformanceData_sendsAlertWhenAllFailureMetricsExceedThreshold() {
            var perfData = VensysPerformanceData.builder()
                    .fireTime(150.0)
                    .commFailureTime(150.0)
                    .gridFailureTime(150.0)
                    .errorTime(150.0)
                    .build();
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(response.from()).thenReturn("2025-11-05T00:00:00");
            when(response.to()).thenReturn("2025-11-05T23:59:59");
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            var from = LocalDateTime.now();
            var to = LocalDateTime.now();
            service.getPerformance(from, to);

            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), argThat(message ->
                message.contains("2025-11-05T00:00:00 -> 2025-11-05T23:59:59") &&
                message.contains("Fire time (150.0s)") &&
                message.contains("Comm failure time (150.0s)") &&
                message.contains("Grid failure time (150.0s)") &&
                message.contains("Error time (150.0s)")));
        }

        @Test
        @DisplayName("Does not send alert when all values are within thresholds")
        void validatePerformanceData_doesNotSendAlertWhenValuesWithinThresholds() {
            var perfData = VensysPerformanceData.builder()
                    .availability(80.0)
                    .fireTime(50.0)
                    .commFailureTime(50.0)
                    .gridFailureTime(50.0)
                    .errorTime(50.0)
                    .build();
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            var from = LocalDateTime.now();
            var to = LocalDateTime.now();
            service.getPerformance(from, to);

            verify(alertService, never()).sendAlert(any(), any());
        }
    }
}
