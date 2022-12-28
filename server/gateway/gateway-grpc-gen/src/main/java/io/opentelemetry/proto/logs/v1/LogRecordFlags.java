/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opentelemetry/proto/logs/v1/logs.proto

package io.opentelemetry.proto.logs.v1;

/**
 * <pre>
 * Masks for LogRecord.flags field.
 * </pre>
 *
 * Protobuf enum {@code opentelemetry.proto.logs.v1.LogRecordFlags}
 */
public enum LogRecordFlags
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>LOG_RECORD_FLAG_UNSPECIFIED = 0;</code>
   */
  LOG_RECORD_FLAG_UNSPECIFIED(0),
  /**
   * <code>LOG_RECORD_FLAG_TRACE_FLAGS_MASK = 255;</code>
   */
  LOG_RECORD_FLAG_TRACE_FLAGS_MASK(255),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>LOG_RECORD_FLAG_UNSPECIFIED = 0;</code>
   */
  public static final int LOG_RECORD_FLAG_UNSPECIFIED_VALUE = 0;
  /**
   * <code>LOG_RECORD_FLAG_TRACE_FLAGS_MASK = 255;</code>
   */
  public static final int LOG_RECORD_FLAG_TRACE_FLAGS_MASK_VALUE = 255;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static LogRecordFlags valueOf(int value) {
    return forNumber(value);
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   */
  public static LogRecordFlags forNumber(int value) {
    switch (value) {
      case 0: return LOG_RECORD_FLAG_UNSPECIFIED;
      case 255: return LOG_RECORD_FLAG_TRACE_FLAGS_MASK;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<LogRecordFlags>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      LogRecordFlags> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<LogRecordFlags>() {
          public LogRecordFlags findValueByNumber(int number) {
            return LogRecordFlags.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalStateException(
          "Can't get the descriptor of an unrecognized enum value.");
    }
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return io.opentelemetry.proto.logs.v1.LogsProto.getDescriptor().getEnumTypes().get(1);
  }

  private static final LogRecordFlags[] VALUES = values();

  public static LogRecordFlags valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private LogRecordFlags(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:opentelemetry.proto.logs.v1.LogRecordFlags)
}

