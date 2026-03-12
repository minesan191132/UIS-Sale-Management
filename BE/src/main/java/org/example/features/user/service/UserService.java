package org.example.features.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.company.entity.User;
import org.example.features.company.repository.UserRepository;
import org.example.features.user.dto.*;
import org.example.features.user.entity.UserAddress;
import org.example.features.user.repository.UserAddressRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Service
 * Handles user profile, password change, and address management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;

    // ──────── Profile ────────

    /**
     * Get full user profile
     */
    public UserProfileResponseDTO getProfile(Long userId) {
        User user = findUserById(userId);
        return toProfileResponse(user);
    }

    /**
     * Update user profile fields
     */
    @Transactional
    public UserProfileResponseDTO updateProfile(Long userId, UserProfileDTO dto) {
        User user = findUserById(userId);

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getDobDay() != null) user.setDobDay(dto.getDobDay());
        if (dto.getDobMonth() != null) user.setDobMonth(dto.getDobMonth());
        if (dto.getDobYear() != null) user.setDobYear(dto.getDobYear());

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String newEmail = dto.getEmail().trim().toLowerCase();
            if (!newEmail.equals(user.getEmail())) {
                boolean emailTaken = userRepository.findByEmail(newEmail).isPresent();
                if (emailTaken) {
                    throw new IllegalArgumentException("Email này đã được sử dụng bởi tài khoản khác");
                }
                user.setEmail(newEmail);
            }
        }

        userRepository.save(user);
        log.info("Profile updated for userId: {}", userId);
        return toProfileResponse(user);
    }

    // ──────── Password ────────

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(Long userId, UserChangePasswordDTO dto) {
        User user = findUserById(userId);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Mật khẩu hiện tại không chính xác");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for userId: {}", userId);
    }

    // ──────── Addresses ────────

    /**
     * Get all addresses for a user
     */
    public List<UserAddressResponseDTO> getAddresses(Long userId) {
        return userAddressRepository.findByUserId(userId)
                .stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList());
    }

    /**
     * Add a new address for a user
     */
    @Transactional
    public UserAddressResponseDTO addAddress(Long userId, UserAddressDTO dto) {
        User user = findUserById(userId);

        UserAddress address = new UserAddress();
        mapDtoToAddress(dto, address);
        address.setUser(user);

        // If this is the first address or explicitly set as default
        boolean hasNoAddresses = userAddressRepository.findByUserId(userId).isEmpty();
        if (hasNoAddresses || Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefaultFlags(userId);
            address.setIsDefault(true);
        }

        UserAddress saved = userAddressRepository.save(address);
        log.info("Address added for userId: {}", userId);
        return toAddressResponse(saved);
    }

    /**
     * Update an existing address
     */
    @Transactional
    public UserAddressResponseDTO updateAddress(Long userId, Long addressId, UserAddressDTO dto) {
        UserAddress address = findAddressByIdAndUserId(addressId, userId);
        mapDtoToAddress(dto, address);

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefaultFlags(userId);
            address.setIsDefault(true);
        }

        UserAddress saved = userAddressRepository.save(address);
        log.info("Address {} updated for userId: {}", addressId, userId);
        return toAddressResponse(saved);
    }

    /**
     * Delete an address
     */
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = findAddressByIdAndUserId(addressId, userId);
        userAddressRepository.delete(address);
        log.info("Address {} deleted for userId: {}", addressId, userId);
    }

    /**
     * Set an address as the default
     */
    @Transactional
    public UserAddressResponseDTO setDefault(Long userId, Long addressId) {
        UserAddress address = findAddressByIdAndUserId(addressId, userId);
        clearDefaultFlags(userId);
        address.setIsDefault(true);
        UserAddress saved = userAddressRepository.save(address);
        log.info("Address {} set as default for userId: {}", addressId, userId);
        return toAddressResponse(saved);
    }

    // ──────── Helpers ────────

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
    }

    private UserAddress findAddressByIdAndUserId(Long addressId, Long userId) {
        return userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ không tồn tại hoặc không thuộc về bạn"));
    }

    private void clearDefaultFlags(Long userId) {
        userAddressRepository.findByUserId(userId)
                .forEach(a -> {
                    a.setIsDefault(false);
                    userAddressRepository.save(a);
                });
    }

    private void mapDtoToAddress(UserAddressDTO dto, UserAddress address) {
        address.setFullName(dto.getFullName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setDistrict(dto.getDistrict());
        address.setWard(dto.getWard());
        address.setDetail(dto.getDetail());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
    }

    private UserAddressResponseDTO toAddressResponse(UserAddress address) {
        return UserAddressResponseDTO.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .detail(address.getDetail())
                .isDefault(address.getIsDefault())
                .build();
    }

    private UserProfileResponseDTO toProfileResponse(User user) {
        return UserProfileResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .dobDay(user.getDobDay())
                .dobMonth(user.getDobMonth())
                .dobYear(user.getDobYear())
                .role(user.getRole().name())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .companyName(user.getCompany() != null ? user.getCompany().getCompanyName() : null)
                .build();
    }
}
