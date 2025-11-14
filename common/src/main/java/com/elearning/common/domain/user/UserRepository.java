package com.elearning.common.domain.user;

import com.elearning.common.enums.UserStatus;
import com.elearning.common.payload.CBS.report.IReportCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByIdAndStatus(Long id, UserStatus status);
    Optional<User> findByUsernameAndStatus(String username, UserStatus status);
    boolean existsByUsername(String username);
    List<User> findUserByCreatedByAndStatus(Long createdBy, UserStatus status);

    boolean existsByUsernameAndIdNot(String username, Long id);

    // Add this method for updating last login time
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") Instant lastLogin);


    @Query(value = """ 
        SELECT tu.id, tu.full_name, tu.email, tr.name AS role, tu.username, tu.image, tu.status, td.name AS department, last_audit.action AS last_log, tu.last_login, tu.created_at 
        FROM tb_usr tu 
        JOIN tb_role tr ON tu.role_id = tr.id AND tr.status = '1' 
        LEFT JOIN tb_dept td ON td.id = tu.dept_id 
        LEFT JOIN ( 
            SELECT usr_id, action, created_at 
            FROM tb_usr_audit tua1 
            WHERE created_at = ( 
                SELECT MAX(created_at) 
                FROM tb_usr_audit tua2 
                WHERE tua2.usr_id = tua1.usr_id 
            ) 
        ) last_audit ON tu.id = last_audit.usr_id 
        WHERE (COALESCE(?4, '') = '' OR tu.status = ?4)
        AND tu.status <> '9'  AND tu.id <> ?5
        AND (COALESCE(?1, '') = '' OR tu.username ILIKE CONCAT('%', ?1, '%') OR tu.full_name ILIKE CONCAT('%', ?1, '%') OR tu.email ILIKE CONCAT('%', ?1, '%') ) 
    AND (( 
            COALESCE(?2, '') = '' AND COALESCE(?3, '') = '' 
            OR 
            tu.created_at BETWEEN 
                CASE WHEN COALESCE(?2, '') = '' THEN CAST('1970-01-01' AS timestamp) ELSE TO_TIMESTAMP(?2, 'YYYYMMDD') END
                AND 
                CASE WHEN COALESCE(?3, '') = '' THEN CURRENT_TIMESTAMP ELSE (TO_TIMESTAMP(?3, 'YYYYMMDD') + INTERVAL '1 day - 1 second') END
        ))
    """, countQuery = """ 
        SELECT COUNT(tu.id) 
        FROM tb_usr tu 
        JOIN tb_role tr ON tu.role_id = tr.id AND tr.status = '1' 
        WHERE (COALESCE(?4, '') = '' OR tu.status = ?4) 
        AND tu.status <> '9' AND tu.id <> ?5
        AND (COALESCE(?1, '') = '' OR tu.username ILIKE CONCAT('%', ?1, '%') OR tu.full_name ILIKE CONCAT('%', ?1, '%') OR tu.email ILIKE CONCAT('%', ?1, '%') ) 
        AND (( 
            COALESCE(?2, '') = '' AND COALESCE(?3, '') = '' 
            OR 
            tu.created_at BETWEEN 
                CASE WHEN COALESCE(?2, '') = '' THEN CAST('1970-01-01' AS timestamp) ELSE TO_TIMESTAMP(?2, 'YYYYMMDD') END
                AND 
                CASE WHEN COALESCE(?3, '') = '' THEN CURRENT_TIMESTAMP ELSE (TO_TIMESTAMP(?3, 'YYYYMMDD') + INTERVAL '1 day - 1 second') END
        ))
    """, nativeQuery = true)
    Page<IGetUsers> findAllUser(String searchValue, String startDate, String endDate, String status, Long currentUserId, Pageable pageable);


    @Query(value = """
        SELECT tu.id, tu.full_name, tu.email, tr.name as role, tu.username, tu.image, td.name as department, tua.action as last_log 
        FROM tb_usr tu 
        LEFT JOIN tb_role tr on tu.role_id = tr.id 
        LEFT JOIN tb_dept td on td.id = tu.dept_id 
        LEFT JOIN (
            SELECT * 
            FROM tb_usr_audit 
            WHERE usr_id = ?1
            ORDER BY created_at DESC 
            LIMIT 1
        ) tua on tu.id = tua.usr_id 
        WHERE tu.id = ?1
    """, nativeQuery = true)
    Optional<IGetUsers> findUserById(Long userId);

    @Query(value = "SELECT COUNT(*) FROM tb_usr", nativeQuery = true)
    Long countTotalUsers();

    @Query(value = "SELECT COUNT(*) FROM tb_usr WHERE status = 'active'  ", nativeQuery = true)
    Long countActiveUsers();

    @Query(value = "SELECT COUNT(*) FROM tb_usr WHERE status = 'inactive'", nativeQuery = true)
    Long countInactiveUsers();

    // Get all counts in one query including new users from last 30 days
    @Query(value = """
        SELECT COUNT(*) AS total_users,
               SUM(CASE WHEN tu.status = '1' THEN 1 ELSE 0 END) AS total_active_users,
               SUM(CASE WHEN tu.status = '2' THEN 1 ELSE 0 END) AS total_inactive_users,
                   SUM(CASE WHEN tu.created_at >= (CURRENT_DATE - INTERVAL '30 days')
               AND tu.created_at <= CURRENT_TIMESTAMP THEN 1 ELSE 0 END) AS total_new_users
        FROM tb_usr tu
        JOIN tb_role tr on tu.role_id = tr.id AND tr.status = '1'
        WHERE tu.status <> '9' AND tu.id <> ?1
    """,nativeQuery = true)
    Map<String, Long> getUserCounts(Long currentUserId);


    @Query(value = """
    SELECT COUNT(*) AS total_users,
               SUM(CASE WHEN tu.status = '1' THEN 1 ELSE 0 END) AS total_active_users,
               SUM(CASE WHEN tu.status = '2' THEN 1 ELSE 0 END) AS total_inactive_users
        FROM tb_usr tu
        JOIN tb_role tr on tu.role_id = tr.id AND  tr.status = '1'
        WHERE  tu.status <> '9' AND tu.id <> ?4
        AND (COALESCE(?3, '') = '' OR tu.username LIKE CONCAT('%', ?3, '%') OR tu.full_name LIKE CONCAT('%', ?3, '%') OR tu.email LIKE CONCAT('%', ?3, '%') ) 
        AND ((COALESCE(?1, '') = '' AND COALESCE(?2, '') = '') 
            OR 
            tu.created_at BETWEEN 
                CASE WHEN COALESCE(?1, '') = '' THEN CAST('1970-01-01' AS timestamp) 
                     ELSE TO_TIMESTAMP(?1, 'YYYYMMDD') END
                AND 
                CASE WHEN COALESCE(?2, '') = '' THEN CURRENT_TIMESTAMP 
                     ELSE (TO_TIMESTAMP(?2, 'YYYYMMDD') + INTERVAL '1 day - 1 second') END)
    """, nativeQuery = true)
    Map<String, Long> getUserCountsByFilterDate(String startDate, String endDate, String searchValue, Long currentUserId);


    @Query("SELECT u FROM User u WHERE u.id in ?1 ")
    List<User> findAllUserById(List<Long> userId);

    @Query(value = """
            SELECT
        (SELECT COUNT(tu.id)
         FROM tb_usr tu
         JOIN tb_role tr ON tu.role_id = tr.id AND tr.status = '1'
         WHERE tu.status <> '9' AND tu.id <> ?1) AS user_count,
        (SELECT COUNT(tp.client_id)
         FROM fep_clients tp
         WHERE tp.status <> '9') AS provider_count,
         (SELECT COUNT(fa.config_id)
         FROM fep_client_auth_configs fa
         WHERE fa.status <> '9') AS auth_config_count,
         (SELECT COUNT(fe.config_id)
         FROM fep_client_endpoints fe
         WHERE fe.status <> '9') AS api_management_count
            """, nativeQuery = true)
    IReportCount getReportCount(Long currentUserId);
}
