package io.renren.ai.dao;

import io.renren.ai.entity.AiModelConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import java.util.List;

@Mapper
public interface AiModelConfigDao extends BaseMapper<AiModelConfig> {

    @Select("SELECT * FROM ai_model_config WHERE tenant_id = #{tenantId} AND deleted = 0 ORDER BY sort ASC")
    List<AiModelConfig> listByTenantId(@Param("tenantId") Long tenantId);

    @Select("SELECT * FROM ai_model_config WHERE id = #{id} AND deleted = 0")
    AiModelConfig getById(@Param("id") Long id);

    @Insert("INSERT INTO ai_model_config (tenant_id, name, display_name, type, endpoint, api_key, model, is_default, is_enabled, sort, config_json, created_by) " +
            "VALUES (#{tenantId}, #{name}, #{displayName}, #{type}, #{endpoint}, #{apiKey}, #{model}, #{isDefault}, #{isEnabled}, #{sort}, #{configJson}, #{createdBy})")
    int insert(AiModelConfig config);

    @Update("UPDATE ai_model_config SET name=#{name}, display_name=#{displayName}, type=#{type}, endpoint=#{endpoint}, api_key=#{apiKey}, model=#{model}, " +
            "is_default=#{isDefault}, is_enabled=#{isEnabled}, sort=#{sort}, config_json=#{configJson} WHERE id=#{id}")
    int updateById(AiModelConfig config);

    @Update("UPDATE ai_model_config SET is_default = 0 WHERE tenant_id = #{tenantId}")
    int clearDefault(@Param("tenantId") Long tenantId);

    @Update("UPDATE ai_model_config SET is_default = 1 WHERE id = #{id}")
    int setDefault(@Param("id") Long id);

    @Update("UPDATE ai_model_config SET is_enabled = #{isEnabled} WHERE id = #{id}")
    int setEnabled(@Param("id") Long id, @Param("isEnabled") Integer isEnabled);

    @Delete("UPDATE ai_model_config SET deleted = 1 WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
