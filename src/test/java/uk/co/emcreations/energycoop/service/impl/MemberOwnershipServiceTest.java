package uk.co.emcreations.energycoop.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.emcreations.energycoop.entity.MemberOwnership;
import uk.co.emcreations.energycoop.entity.MemberOwnershipRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.emcreations.energycoop.model.Site.GRAIG_FATHA;

@ExtendWith(MockitoExtension.class)
class MemberOwnershipServiceTest {
    private static final String USER_ID = "testUser";

    @Mock
    MemberOwnershipRepository memberOwnershipRepository;

    @InjectMocks
    MemberOwnershipServiceImpl memberOwnershipService;

    @Nested
    @DisplayName("repository call tests")
    class RepoCall {
        @Test
        @DisplayName("Returns repository ownership if present for Graig Fatha")
        void returnsRepositoryOwnershipIfPresent() {
            var date = LocalDate.of(2024, 1, 1);
            var expectedOwnership = 250.0;
            var suppliedOwnership = 100.0;
            MemberOwnership memberOwnership = mock(MemberOwnership.class);
            when(memberOwnership.getWattageOwnership()).thenReturn(expectedOwnership);

            when(memberOwnershipRepository
                    .findTopByUserIdAndSiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(USER_ID, GRAIG_FATHA,
                            date))
                    .thenReturn(Optional.of(memberOwnership));

            double actualOwnership = memberOwnershipService.getMemberOwnershipForSite(GRAIG_FATHA, date, USER_ID,
                    suppliedOwnership);

            assertEquals(expectedOwnership, actualOwnership);
        }

        @Test
        @DisplayName("Returns default for Graig Fatha if not present")
        void returnsDefaultForGraigFathaIfNotPresent() {
            var suppliedOwnership = 100.0;
            var date = LocalDate.of(2024, 1, 1);

            when(memberOwnershipRepository
                    .findTopByUserIdAndSiteAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(USER_ID, GRAIG_FATHA,
                            date))
                    .thenReturn(Optional.empty());

            double actualOwnership = memberOwnershipService.getMemberOwnershipForSite(GRAIG_FATHA, date, USER_ID,
                    suppliedOwnership);

            assertEquals(suppliedOwnership, actualOwnership);
        }
    }
}
