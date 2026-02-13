package com.github.leonardra.data_encryption_at_rest.service;

import com.github.leonardra.data_encryption_at_rest.dtos.requests.CreateUserRequest;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.CreateUserResponse;
import com.github.leonardra.data_encryption_at_rest.dtos.responses.DefaultResponse;
import com.github.leonardra.data_encryption_at_rest.dtos.EncryptionResult;
import com.github.leonardra.data_encryption_at_rest.models.Key;
import com.github.leonardra.data_encryption_at_rest.models.User;
import com.github.leonardra.data_encryption_at_rest.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService{
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final KeyService keyService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) throws Exception {
        Optional<User> optionalUser = userService.findByUsername(createUserRequest.getUsername());
        if(optionalUser.isPresent()){
            throw new RuntimeException("Username already exists");
        }

        Key key = new Key();
        key.setSystemSalt(EncryptionUtil.generateRandomKey());

        byte[] rawKu = EncryptionUtil.generateRandomKey();
        String secret = Base64.getEncoder().encodeToString(rawKu);

        byte[] ku = EncryptionUtil.generateRandomKey();
        EncryptionResult wrappedKu =  EncryptionUtil.wrapUserHalf(ku, secret);
        key.setUserHalf(wrappedKu.ciphertext());
        key.setUserHalfIv(wrappedKu.iv());

        Key savedKey = keyService.save(key);

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setEnabled(true);
        user.setKey(savedKey);

        userService.save(user);
        return CreateUserResponse.builder()
                .message("User create successfully. Keep key somewhere safe and easy to remember")
                .key(secret)
                .build();
    }
}
