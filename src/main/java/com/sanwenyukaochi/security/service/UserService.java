package com.sanwenyukaochi.security.service;

import com.sanwenyukaochi.security.dto.AccountDTO;
import com.sanwenyukaochi.security.entity.User;
import com.sanwenyukaochi.security.repository.UserRepository;
import com.sanwenyukaochi.security.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public Page<AccountDTO> findAllUser(Pageable pageable) {
        Page<User> all = userRepository.findAll(pageable);
        return all.map(newUser -> new AccountDTO(
                newUser.getId(),
                newUser.getNickName(),
                newUser.getPhone(),
                newUser.getCreatedAt()
        ));
    }

} 