package com.elearning.common.domain.commoncode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, CommonCodePK> {


    @Query("select t from tb_comm_cd t where t.groupCode = ?1 and t.status <> '9' and (coalesce(?2, '') = '' or t.parentCode = ?2)")
    List<CommonCode> findByGroupCodeAndParentCode(String groupCode, String parentCode);


    Optional<CommonCode> findCommonCodeByCode(String code);
}