/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: TaskEnum.java, v 0.1 2022年03月17日 7:49 下午 jinsong.yjs Exp $
 */
public enum TaskEnum {

  TASK_DEMO("TASK_DEMO", "TASK_DEMO"),

  TENANT_SYNC("TENANT_SYNC", "租户列表同步"),

  TENANT_APP_META_SYNC("TENANT_APP_META_SYNC", "租户应用列表同步"),

  TENANT_APP_SERVER_UPDATE("TENANT_APP_SERVER_UPDATE", "租户单机元数据列表更新"),

  TENANT_INTEGRATION_GENERATED("TENANT_INTEGRATION_GENERATED", "租户集成配置生成"),

  TENANT_OB_SERVER_SYNC("TENANT_OB_SERVER_SYNC", "租户OB元数据列表同步"),

  CLEAN_ALERT_HISTORY("CLEAN_ALERT_HISTORY", "清理告警历史数据"),

  HOSTING_ALERT("HOSTING_ALERT", "托管告警生成"),;

  /**
   * 枚举值
   */
  private String code;

  /**
   * 描述
   */
  private String desc;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  /**
   * @param code
   * @param desc
   */
  TaskEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  /**
   * 根据code获取描述信息
   *
   * @param code 待查询的枚举值
   * @return 对应的描述信息
   */
  public static String getByCode(String code) {
    if (code != null && !"".equals(code.trim())) {
      for (TaskEnum e : TaskEnum.values()) {
        if (code.equals(e.getCode())) {
          return e.getDesc();
        }
      }
    }
    return null;
  }

  /**
   * @param code
   * @return
   */
  public static TaskEnum getEnumByCode(String code) {
    if (code != null && !"".equals(code.trim())) {
      for (TaskEnum e : TaskEnum.values()) {
        if (code.equals(e.getCode())) {
          return e;
        }
      }
    }
    return null;
  }

  /**
   * @return
   */
  public static Map<String, String> toMap() {
    Map<String, String> map = new LinkedHashMap<String, String>();
    for (TaskEnum e : TaskEnum.values()) {
      map.put(e.getCode(), e.getDesc());
    }
    return map;
  }

  public enum TaskType {
    TASK, PLUGIN;
  }
}
