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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

            VensysMeanData result = service.getMeanEnergyYield();

            assertEquals(meanData, result);
            verify(client).getMeanEnergyYield();
        }

        @Test
        void getMeanEnergyYield_throwsExceptionWhenClientReturnsNull() {
            when(client.getMeanEnergyYield()).thenReturn(null);

            assertThrows(NullPointerException.class, () -> service.getMeanEnergyYield());
            verify(client).getMeanEnergyYield();
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

            VensysMeanData result = service.getMeanEnergyYield();

            assertEquals(456.0, result.value());
            verify(client).getMeanEnergyYield();
            verify(client).getCurrentPerformance();
        }

        @Test
        void getMeanEnergyYield_throwsExceptionWhenCurrentPerformanceNull() {
            VensysMeanDataResponse meanResponse = mock(VensysMeanDataResponse.class);
            when(meanResponse.data()).thenReturn(null);
            when(client.getMeanEnergyYield()).thenReturn(meanResponse);
            when(client.getCurrentPerformance()).thenReturn(null);

            assertThrows(NullPointerException.class, () -> service.getMeanEnergyYield());
            verify(client).getMeanEnergyYield();
            verify(client).getCurrentPerformance();
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

    @Nested
    @DisplayName("validatePerformanceData tests")
    class ValidatePerformanceDataTests {
        @Test
        @DisplayName("Sends alert when response is null")
        void validatePerformanceData_sendsAlertWhenResponseIsNull() {
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(null);

            assertThrows(NullPointerException.class, () -> service.getPerformance(LocalDateTime.now(), LocalDateTime.now()));

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
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            service.getPerformance(LocalDateTime.now(), LocalDateTime.now());

            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), argThat(message ->
                message.contains("Availability (70.0%) less than threshold (75.0%)")));
        }

        @Test
        @DisplayName("Sends alert when fire time exceeds threshold")
        void validatePerformanceData_sendsAlertWhenFireTimeExceedsThreshold() {
            var perfData = VensysPerformanceData.builder()
                    .fireTime(150.0)
                    .build();
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            service.getPerformance(LocalDateTime.now(), LocalDateTime.now());

            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), argThat(message ->
                message.contains("Fire time (150.0s) exceeds threshold (100.0s)")));
        }

        @Test
        @DisplayName("Sends alert when comm failure time exceeds threshold")
        void validatePerformanceData_sendsAlertWhenCommFailureTimeExceedsThreshold() {
            var perfData = VensysPerformanceData.builder()
                    .commFailureTime(120.0)
                    .build();
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            service.getPerformance(LocalDateTime.now(), LocalDateTime.now());

            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), argThat(message ->
                message.contains("Comm failure time (120.0s) exceeds threshold (100.0s)")));
        }

        @Test
        @DisplayName("Sends alert when grid failure time exceeds threshold")
        void validatePerformanceData_sendsAlertWhenGridFailureTimeExceedsThreshold() {
            var perfData = VensysPerformanceData.builder()
                    .gridFailureTime(110.0)
                    .build();
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            service.getPerformance(LocalDateTime.now(), LocalDateTime.now());

            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), argThat(message ->
                message.contains("Grid failure time (110.0s) exceeds threshold (100.0s)")));
        }

        @Test
        @DisplayName("Sends alert when error time exceeds threshold")
        void validatePerformanceData_sendsAlertWhenErrorTimeExceedsThreshold() {
            var perfData = VensysPerformanceData.builder()
                    .errorTime(130.0)
                    .build();
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            service.getPerformance(LocalDateTime.now(), LocalDateTime.now());

            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), argThat(message ->
                message.contains("Error time (130.0s) exceeds threshold (100.0s)")));
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

            service.getPerformance(LocalDateTime.now(), LocalDateTime.now());

            verify(alertService, never()).sendAlert(any(), any());
        }

        @Test
        @DisplayName("Includes timestamp range in alert message")
        void validatePerformanceData_includesTimestampInAlertMessage() {
            var perfData = VensysPerformanceData.builder()
                    .errorTime(130.0)
                    .build();
            VensysPerformanceDataResponse response = mock(VensysPerformanceDataResponse.class);
            when(response.data()).thenReturn(new VensysPerformanceData[]{perfData});
            when(response.from()).thenReturn("2025-10-19T00:00:00");
            when(response.to()).thenReturn("2025-10-19T23:59:59");
            when(client.getPerformance(anyLong(), anyLong())).thenReturn(response);

            service.getPerformance(LocalDateTime.now(), LocalDateTime.now());

            verify(alertService).sendAlert(eq(Site.GRAIG_FATHA), argThat(message ->
                message.contains("2025-10-19T00:00:00 -> 2025-10-19T23:59:59")));
        }
    }
}
