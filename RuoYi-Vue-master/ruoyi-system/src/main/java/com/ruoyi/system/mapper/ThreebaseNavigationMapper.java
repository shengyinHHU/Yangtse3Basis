package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 三基业务导航配置 Mapper。
 */
public interface ThreebaseNavigationMapper
{
    List<Map<String, Object>> selectModules();

    List<Map<String, Object>> selectFeatures(@Param("moduleCode") String moduleCode);

    int countModuleCode(@Param("code") String code);

    int countFeatureCode(@Param("code") String code);

    int insertModule(Map<String, Object> module);

    int updateModule(Map<String, Object> module);

    int deleteModule(@Param("code") String code);

    int deleteFeaturesByModule(@Param("moduleCode") String moduleCode);

    int insertFeature(Map<String, Object> feature);

    int updateFeature(Map<String, Object> feature);

    int deleteFeature(@Param("code") String code);

    int updateModuleSort(@Param("code") String code, @Param("orderNum") Integer orderNum);

    int updateFeatureSort(@Param("code") String code, @Param("orderNum") Integer orderNum);
}
