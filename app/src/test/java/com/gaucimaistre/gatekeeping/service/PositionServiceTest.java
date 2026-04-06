package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Position;
import com.gaucimaistre.gatekeeping.model.PositionView;
import com.gaucimaistre.gatekeeping.model.enums.UserType;
import com.gaucimaistre.gatekeeping.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PositionService positionService;

    @Test
    void findAll_asAdmin_callsFindAll() {
        int userId = 1;
        Position position = buildPosition("POS-001");
        given(positionRepository.findAll()).willReturn(List.of(position));

        List<Position> result = positionService.findAll(userId, UserType.ADMIN);

        assertThat(result).hasSize(1);
        verify(positionRepository).findAll();
        verify(positionRepository, never()).findAllByFunctionIds(anyList());
    }

    @Test
    void findAll_asUser_callsFindAllByFunctionIds() {
        int userId = 2;
        List<Integer> functionIds = List.of(1, 3);
        Position position = buildPosition("POS-002");
        given(userService.getFunctionIds(userId)).willReturn(functionIds);
        given(positionRepository.findAllByFunctionIds(functionIds)).willReturn(List.of(position));

        List<Position> result = positionService.findAll(userId, UserType.USER);

        assertThat(result).hasSize(1);
        verify(userService).getFunctionIds(userId);
        verify(positionRepository).findAllByFunctionIds(functionIds);
        verify(positionRepository, never()).findAll();
    }

    @Test
    void findAll_asUser_withNoFunctions_returnsEmptyList() {
        int userId = 3;
        given(userService.getFunctionIds(userId)).willReturn(List.of());

        List<Position> result = positionService.findAll(userId, UserType.USER);

        assertThat(result).isEmpty();
        verify(positionRepository, never()).findAllByFunctionIds(anyList());
    }

    @Test
    void findViewByNumber_delegatesToRepository() {
        String number = "POS-001";
        PositionView view = buildPositionView(number);
        given(positionRepository.findViewByNumber(number)).willReturn(Optional.of(view));

        Optional<PositionView> result = positionService.findViewByNumber(number);

        assertThat(result).isPresent();
        assertThat(result.get().number()).isEqualTo(number);
        verify(positionRepository).findViewByNumber(number);
    }

    @Test
    void canUserAccessPosition_asAdmin_returnsTrue() {
        int userId = 1;

        boolean result = positionService.canUserAccessPosition(userId, UserType.ADMIN, "POS-001");

        assertThat(result).isTrue();
        verify(positionRepository, never()).findByNumber(anyString(), anyBoolean());
    }

    @Test
    void canUserAccessPosition_asUser_withMatchingFunction_returnsTrue() {
        int userId = 2;
        Position position = buildPosition("POS-002");
        given(positionRepository.findByNumber("POS-002", false)).willReturn(Optional.of(position));
        given(userService.getFunctionIds(userId)).willReturn(List.of(1));

        boolean result = positionService.canUserAccessPosition(userId, UserType.USER, "POS-002");

        assertThat(result).isTrue();
    }

    @Test
    void canUserAccessPosition_asUser_positionNotFound_returnsFalse() {
        int userId = 2;
        given(positionRepository.findByNumber("MISSING", false)).willReturn(Optional.empty());

        boolean result = positionService.canUserAccessPosition(userId, UserType.USER, "MISSING");

        assertThat(result).isFalse();
    }

    private Position buildPosition(String number) {
        return new Position(1, 1, 1, number, 1, 1, 1, 1, false,
                "Test Title", "CEO", "CEO", "John Doe",
                40, null, null, null, null, null, null, null, null);
    }

    private PositionView buildPositionView(String number) {
        return new PositionView(1, "Occupied", "Contracted", number, "Engineering", "Acme",
                "Engineering", "Engineering - Backend", false, "Test Title",
                "CEO", "CEO", "John Doe", 40, null, null,
                null, null, null, null, null, null);
    }
}
