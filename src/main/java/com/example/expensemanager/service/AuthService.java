package com.example.expensemanager.service;

import com.example.expensemanager.exception.BadRequestException;
import com.example.expensemanager.model.ERole;
import com.example.expensemanager.model.Role;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.LoginRequest;
import com.example.expensemanager.payload.request.SignupRequest;
import com.example.expensemanager.payload.response.JwtResponse;
import com.example.expensemanager.payload.response.MessageResponse;
import com.example.expensemanager.repository.RoleRepository;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.security.jwt.JwtUtils;
import com.example.expensemanager.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {

        // Xác thực người dùng bằng Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tạo JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Lấy thông tin người dùng từ `UserDetailsImpl`
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Trả về `JwtResponse` chứa thông tin đăng nhập thành công
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    public MessageResponse registerUser(SignupRequest signUpRequest) {

        // Kiểm tra xem username và email đã tồn tại hay chưa
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        // Tạo tài khoản người dùng mới
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())); // Mã hóa mật khẩu

        // Xử lý vai trò của người dùng
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            // Nếu không có vai trò nào được chỉ định, gán vai trò mặc định là `ROLE_USER`
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            // Nếu có vai trò được chỉ định, thêm vai trò tương ứng vào `roles`
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        // Trả về thông báo đăng ký thành công
        return new MessageResponse("User registered successfully!");
    }
}