package com.study.system.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.study.common.constant.UserConstants;
import com.study.common.core.base.BaseServiceImpl;
import com.study.common.util.StringUtils;
import com.study.system.constant.SystemRedisKey;
import com.study.system.dao.SysConfigDao;
import com.study.system.entity.SysConfig;
import com.study.system.service.SysConfigService;

@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfig> implements SysConfigService {

	@Autowired
	private SysConfigDao dao;

	@Override
	public String checkConfigKeyUnique(SysConfig config) {
		Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
		SysConfig info = dao.checkConfigKeyUnique(config);
		if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()) {
			return UserConstants.CONFIG_KEY_NOT_UNIQUE;
		}
		return UserConstants.CONFIG_KEY_UNIQUE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getConfigByKey(String configKey) {
		String key = SystemRedisKey.SYSTEM_CONFIG_PREFIX + configKey;
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		Object o = operations.get(key);
		if (null == o) {
			synchronized (this) {
				o = operations.get(key);
				if (null == o) {
					SysConfig config = new SysConfig();
					config.setConfigKey(configKey);
					o = dao.getConfigByKey(config);
					operations.set(key, o, 10, TimeUnit.DAYS);
				}
			}
		}
		return (String) o;
	}

}
