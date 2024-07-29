package com.prax.crypto.account;

import com.prax.crypto.account.dto.AppUserDto;
import com.prax.crypto.account.dto.AppUserResponseDto;
import com.prax.crypto.account.dto.AppUserWithRoleDto;
import com.prax.crypto.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @InjectMocks
    private AppUserService appUserService;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserMapper appUserMapper;

    @Test
    void create_givenAppUserDto_savesAndReturnsResponseDto() {
        // given
        var appUser = new AppUser(
                1,
                "testuser@example.com",
                "testpass",
                null,
                Role.ROLE_USER
        );

        var appUserDto = new AppUserDto(
                "testuser@example.com",
                "testpass"
        );

        var appUserResponseDto = new AppUserResponseDto(
                1,
                "testuser@example.com",
                null
        );

        // mock
        when(appUserMapper.toEntity(appUserDto)).thenReturn(appUser);
        when(appUserRepository.save(appUser)).thenReturn(appUser);
        when(appUserMapper.toResponseDto(appUser)).thenReturn(appUserResponseDto);

        // when
        var result = appUserService.create(appUserDto);

        // then
        assertEquals(appUserResponseDto, result);
        verify(appUserRepository).save(appUser);
    }

    @Test
    void createWithRole_givenAppUserWithRoleDto_savesAndReturnsResponseDto() {
        // given
        var appUser = new AppUser(
                1,
                "testuser@example.com",
                "testpass",
                null,
                Role.ROLE_USER
        );

        var appUserWithRoleDto = new AppUserWithRoleDto(
                "testuser@example.com",
                "testpass",
                Role.ROLE_ADMIN
        );

        var appUserResponseDto = new AppUserResponseDto(
                1,
                "testuser@example.com",
                null
        );

        // mock
        when(appUserMapper.toEntityWithRole(appUserWithRoleDto)).thenReturn(appUser);
        when(appUserRepository.save(appUser)).thenReturn(appUser);
        when(appUserMapper.toResponseDto(appUser)).thenReturn(appUserResponseDto);

        // when
        var result = appUserService.createWithRole(appUserWithRoleDto);

        // then
        assertEquals(appUserResponseDto, result);
        verify(appUserRepository).save(appUser);
    }

    @Test
    void findAll_executes_returnsListOfActiveAppUsers() {
        // given
        var appUser = new AppUser(
                1,
                "testuser@example.com",
                "testpass",
                null,
                Role.ROLE_USER
        );

        var appUserResponseDto = new AppUserResponseDto(
                1,
                "testuser@example.com",
                null
        );

        // mock
        when(appUserRepository.findAll()).thenReturn(List.of(appUser));
        when(appUserMapper.toResponseDto(appUser)).thenReturn(appUserResponseDto);

        // when
        var result = appUserService.findAll();

        // then
        assertEquals(1, result.size());
        assertEquals(appUserResponseDto, result.getFirst());
        verify(appUserRepository).findAll();
    }

    @Test
    void findById_givenId_returnsResponseDtoIfActive() {
        // given
        var appUser = new AppUser(
                1,
                "testuser@example.com",
                "testpass",
                null,
                Role.ROLE_USER
        );

        var appUserResponseDto = new AppUserResponseDto(
                1,
                "testuser@example.com",
                null
        );

        // mock
        when(appUserRepository.findById(1)).thenReturn(Optional.of(appUser));
        when(appUserMapper.toResponseDto(appUser)).thenReturn(appUserResponseDto);

        // when
        var result = appUserService.findById(1);

        // then
        assertEquals(appUserResponseDto, result);
        verify(appUserRepository).findById(1);
    }

    @Test
    void findById_givenWrongId_throwsUserNotFoundException() {
        // given
        var appUser = new AppUser(
                1,
                "testuser@example.com",
                "testpass",
                null,
                Role.ROLE_USER
        );

        // mock
        when(appUserRepository.findById(10)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> appUserService.findById(10));
        verify(appUserRepository).findById(10);
    }

    @Test
    void update_givenIdAndAppUserDto_savesAndReturnsResponseDto() {
        // given
        var appUser = new AppUser(
                1,
                "testuser@example.com",
                "testpass",
                null,
                Role.ROLE_USER
        );

        var appUserDto = new AppUserDto(
                "testuser@example.com",
                "testpass"
        );

        var appUserResponseDto = new AppUserResponseDto(
                1,
                "testuser@example.com",
                null
        );

        // mock
        when(appUserRepository.findById(1)).thenReturn(Optional.of(appUser));
        when(appUserRepository.save(appUser)).thenReturn(appUser);
        when(appUserMapper.toResponseDto(appUser)).thenReturn(appUserResponseDto);

        // when
        var result = appUserService.update(1, appUserDto);

        // then
        assertEquals(appUserResponseDto, result);
        verify(appUserRepository).findById(1);
        verify(appUserRepository).save(appUser);
    }

    @Test
    void update_givenWrongId_throwsUserNotFoundException() {
        // given
        var appUserDto = new AppUserDto(
                "testuser@example.com",
                "testpass"
        );

        // mock
        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> appUserService.update(1, appUserDto));
        verify(appUserRepository).findById(1);
    }

    @Test
    void delete_givenId_deletesAppUser() {
        // given
        var appUser = new AppUser(
                1,
                "testuser@example.com",
                "testpass",
                null,
                Role.ROLE_USER
        );

        // mock
        when(appUserRepository.existsById(1)).thenReturn(true);

        // when
        appUserService.delete(1);

        // then
        verify(appUserRepository).deleteById(1);
    }
}