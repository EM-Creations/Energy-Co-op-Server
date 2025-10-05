package uk.co.emcreations.energycoop.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VensysPerformanceDataResponseTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {
        @Test
        @DisplayName("Should create VensysPerformanceDataResponse with all fields")
        void shouldCreateVensysPerformanceDataResponseWithAllFields() {
            // Given
            String code = "200";
            boolean success = true;
            String from = "2025-10-01";
            String to = "2025-10-05";
            String processTime = "100ms";
            String message = "Success";
            VensysPerformanceData[] data = new VensysPerformanceData[]{
                    VensysPerformanceData.builder().build(),
                    VensysPerformanceData.builder().build()
            };

            // When
            VensysPerformanceDataResponse response = VensysPerformanceDataResponse.builder()
                    .code(code)
                    .success(success)
                    .from(from)
                    .to(to)
                    .processTime(processTime)
                    .message(message)
                    .data(data)
                    .build();

            // Then
            assertThat(response.code()).isEqualTo(code);
            assertThat(response.success()).isEqualTo(success);
            assertThat(response.from()).isEqualTo(from);
            assertThat(response.to()).isEqualTo(to);
            assertThat(response.processTime()).isEqualTo(processTime);
            assertThat(response.message()).isEqualTo(message);
            assertThat(response.data()).isEqualTo(data);
            assertThat(response.data()).hasSize(2);
        }

        @Test
        @DisplayName("Should create VensysPerformanceDataResponse with null fields")
        void shouldCreateVensysPerformanceDataResponseWithNullFields() {
            // When
            VensysPerformanceDataResponse response = VensysPerformanceDataResponse.builder().build();

            // Then
            assertThat(response.code()).isNull();
            assertThat(response.success()).isFalse();
            assertThat(response.from()).isNull();
            assertThat(response.to()).isNull();
            assertThat(response.processTime()).isNull();
            assertThat(response.message()).isNull();
            assertThat(response.data()).isNull();
        }

        @Test
        @DisplayName("Should create VensysPerformanceDataResponse with empty data array")
        void shouldCreateVensysPerformanceDataResponseWithEmptyDataArray() {
            // Given
            VensysPerformanceData[] emptyData = new VensysPerformanceData[0];

            // When
            VensysPerformanceDataResponse response = VensysPerformanceDataResponse.builder()
                    .data(emptyData)
                    .build();

            // Then
            assertThat(response.data()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Record Tests")
    class RecordTests {
        @Test
        @DisplayName("Should test equals and hashCode")
        void shouldTestEqualsAndHashCode() {
            // Given
            VensysPerformanceData[] data = new VensysPerformanceData[]{VensysPerformanceData.builder().build()};

            VensysPerformanceDataResponse response1 = VensysPerformanceDataResponse.builder()
                    .code("200")
                    .success(true)
                    .from("2025-10-01")
                    .to("2025-10-05")
                    .processTime("100ms")
                    .message("Success")
                    .data(data)
                    .build();

            VensysPerformanceDataResponse response2 = VensysPerformanceDataResponse.builder()
                    .code("200")
                    .success(true)
                    .from("2025-10-01")
                    .to("2025-10-05")
                    .processTime("100ms")
                    .message("Success")
                    .data(data)
                    .build();

            // Then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("Should test toString")
        void shouldTestToString() {
            // Given
            VensysPerformanceData[] data = new VensysPerformanceData[]{VensysPerformanceData.builder().build()};

            VensysPerformanceDataResponse response = VensysPerformanceDataResponse.builder()
                    .code("200")
                    .success(true)
                    .from("2025-10-01")
                    .to("2025-10-05")
                    .processTime("100ms")
                    .message("Success")
                    .data(data)
                    .build();

            // Then
            String toString = response.toString();
            assertThat(toString).contains("code=200");
            assertThat(toString).contains("success=true");
            assertThat(toString).contains("from=2025-10-01");
            assertThat(toString).contains("to=2025-10-05");
            assertThat(toString).contains("processTime=100ms");
            assertThat(toString).contains("message=Success");
            assertThat(toString).contains("data=[");
        }
    }
}
