/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.common.model.query;

import lombok.Data;

@Data
public class Service {
  private String name;
  private ResponseMetric metric;
}
