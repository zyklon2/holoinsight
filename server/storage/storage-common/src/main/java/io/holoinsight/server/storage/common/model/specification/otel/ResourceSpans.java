/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.common.model.specification.otel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiwliu
 * @version : ResourceSpans.java, v 0.1 2022年11月01日 12:03 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceSpans {
  private Resource resource;
  private ScopeSpans scopeSpans;
  private String schemaUrl;
}
