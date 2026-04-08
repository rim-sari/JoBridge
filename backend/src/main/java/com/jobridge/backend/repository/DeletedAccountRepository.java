package com.jobridge.backend.repository;

import com.jobridge.backend.model.DeletedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeletedAccountRepository
        extends JpaRepository<DeletedAccount, String> {

    List<DeletedAccount> findByAccountTypeOrderByDeletedAtDesc(
            String accountType);

    List<DeletedAccount> findAllByOrderByDeletedAtDesc();
}