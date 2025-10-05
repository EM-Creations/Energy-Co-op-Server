package uk.co.emcreations.energycoop.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class VensysMeanDataResponseTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {
        @Test
        @DisplayName("Should create VensysMeanDataResponse with all fields")
        void shouldCreateVensysMeanDataResponseWithAllFields() {
            // Given
            String code = "200";
            boolean success = true;
            LocalDate from = LocalDate.of(2025, 10, 1);
            LocalDate to = LocalDate.of(2025, 10, 5);
            String processTime = "100ms";
            String message = "Success";
            VensysMeanData data = VensysMeanData.builder().build();

            // When
            VensysMeanDataResponse response = VensysMeanDataResponse.builder()
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
        }

        @Test
        @DisplayName("Should create VensysMeanDataResponse with null fields")
        void shouldCreateVensysMeanDataResponseWithNullFields() {
            // When
            VensysMeanDataResponse response = VensysMeanDataResponse.builder().build();

            // Then
            assertThat(response.code()).isNull();
            assertThat(response.success()).isFalse();
            assertThat(response.from()).isNull();
            assertThat(response.to()).isNull();
            assertThat(response.processTime()).isNull();
            assertThat(response.message()).isNull();
            assertThat(response.data()).isNull();
        }
    }

    @Nested
    @DisplayName("Record Tests")
    class RecordTests {
        @Test
        @DisplayName("Should test equals and hashCode")
        void shouldTestEqualsAndHashCode() {
            // Given
            VensysMeanDataResponse response1 = VensysMeanDataResponse.builder()
                    .code("200")
                    .success(true)
                    .from(LocalDate.of(2025, 10, 1))
                    .to(LocalDate.of(2025, 10, 5))
                    .processTime("100ms")
                    .message("Success")
                    .data(VensysMeanData.builder().build())
                    .build();

            VensysMeanDataResponse response2 = VensysMeanDataResponse.builder()
                    .code("200")
                    .success(true)
                    .from(LocalDate.of(2025, 10, 1))
                    .to(LocalDate.of(2025, 10, 5))
                    .processTime("100ms")
                    .message("Success")
                    .data(VensysMeanData.builder().build())
                    .build();

            // Then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("Should test toString")
        void shouldTestToString() {
            // Given
            VensysMeanDataResponse response = VensysMeanDataResponse.builder()
                    .code("200")
                    .success(true)
                    .from(LocalDate.of(2025, 10, 1))
                    .to(LocalDate.of(2025, 10, 5))
                    .processTime("100ms")
                    .message("Success")
                    .build();

            // Then
            String toString = response.toString();
            assertThat(toString).contains("code=200");
            assertThat(toString).contains("success=true");
            assertThat(toString).contains("from=2025-10-01");
            assertThat(toString).contains("to=2025-10-05");
            assertThat(toString).contains("processTime=100ms");
            assertThat(toString).contains("message=Success");
        }
    }
}
