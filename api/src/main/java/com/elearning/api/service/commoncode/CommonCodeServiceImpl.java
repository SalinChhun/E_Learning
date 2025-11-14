package com.elearning.api.service.commoncode;

import com.elearning.api.payload.common.CommonCodeResponse;
import com.elearning.common.domain.commoncode.CommonCodeRepository;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonCodeServiceImpl implements CommonCodeService{

    public final CommonCodeRepository commonCodeRepository;



    @Override
    public Object getCommonCodeByGroupCode(String groupCode, String parentCode){

        Map<String, Object> response = new HashMap<>();

        var commonCodeList = commonCodeRepository.findByGroupCodeAndParentCode(groupCode, parentCode);
        if (commonCodeList.isEmpty()) throw new BusinessException(StatusCode.COMMON_CODE_NOT_FOUND);

        var commonCodeResponses = commonCodeList.stream().map(commonCode ->
                CommonCodeResponse.builder()
                        .code(commonCode.getCode())
                        .name(commonCode.getName())
                        .parentCode(commonCode.getParentCode())
                        .createdAt(commonCode.getCreatedAt())
                        .description(commonCode.getDescription())
                        .build())
                .collect(Collectors.toList());

        response.put("codes", commonCodeResponses);

        return response;
    }
}
