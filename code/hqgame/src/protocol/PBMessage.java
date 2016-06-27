// Generated by the protocol buffer compiler.  DO NOT EDIT!

package protocol;

public final class PBMessage {
  private PBMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class CSUserLogin extends
      com.google.protobuf.GeneratedMessage {
    // Use CSUserLogin.newBuilder() to construct.
    private CSUserLogin() {}
    
    private static final CSUserLogin defaultInstance = new CSUserLogin();
    public static CSUserLogin getDefaultInstance() {
      return defaultInstance;
    }
    
    public CSUserLogin getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return protocol.PBMessage.internal_static_protocol_CSUserLogin_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return protocol.PBMessage.internal_static_protocol_CSUserLogin_fieldAccessorTable;
    }
    
    // required int32 id = 1;
    public static final int ID_FIELD_NUMBER = 1;
    private boolean hasId;
    private int id_ = 0;
    public boolean hasId() { return hasId; }
    public int getId() { return id_; }
    
    // required string name = 2;
    public static final int NAME_FIELD_NUMBER = 2;
    private boolean hasName;
    private java.lang.String name_ = "";
    public boolean hasName() { return hasName; }
    public java.lang.String getName() { return name_; }
    
    // required string pass = 3;
    public static final int PASS_FIELD_NUMBER = 3;
    private boolean hasPass;
    private java.lang.String pass_ = "";
    public boolean hasPass() { return hasPass; }
    public java.lang.String getPass() { return pass_; }
    
    public final boolean isInitialized() {
      if (!hasId) return false;
      if (!hasName) return false;
      if (!hasPass) return false;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (hasId()) {
        output.writeInt32(1, getId());
      }
      if (hasName()) {
        output.writeString(2, getName());
      }
      if (hasPass()) {
        output.writeString(3, getPass());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasId()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, getId());
      }
      if (hasName()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(2, getName());
      }
      if (hasPass()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(3, getPass());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static protocol.PBMessage.CSUserLogin parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static protocol.PBMessage.CSUserLogin parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static protocol.PBMessage.CSUserLogin parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static protocol.PBMessage.CSUserLogin parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static protocol.PBMessage.CSUserLogin parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static protocol.PBMessage.CSUserLogin parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static protocol.PBMessage.CSUserLogin parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeDelimitedFrom(input).buildParsed();
    }
    public static protocol.PBMessage.CSUserLogin parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeDelimitedFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static protocol.PBMessage.CSUserLogin parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static protocol.PBMessage.CSUserLogin parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(protocol.PBMessage.CSUserLogin prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private protocol.PBMessage.CSUserLogin result;
      
      // Construct using protocol.PBMessage.CSUserLogin.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new protocol.PBMessage.CSUserLogin();
        return builder;
      }
      
      protected protocol.PBMessage.CSUserLogin internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new protocol.PBMessage.CSUserLogin();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return protocol.PBMessage.CSUserLogin.getDescriptor();
      }
      
      public protocol.PBMessage.CSUserLogin getDefaultInstanceForType() {
        return protocol.PBMessage.CSUserLogin.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public protocol.PBMessage.CSUserLogin build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private protocol.PBMessage.CSUserLogin buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public protocol.PBMessage.CSUserLogin buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        protocol.PBMessage.CSUserLogin returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof protocol.PBMessage.CSUserLogin) {
          return mergeFrom((protocol.PBMessage.CSUserLogin)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(protocol.PBMessage.CSUserLogin other) {
        if (other == protocol.PBMessage.CSUserLogin.getDefaultInstance()) return this;
        if (other.hasId()) {
          setId(other.getId());
        }
        if (other.hasName()) {
          setName(other.getName());
        }
        if (other.hasPass()) {
          setPass(other.getPass());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 8: {
              setId(input.readInt32());
              break;
            }
            case 18: {
              setName(input.readString());
              break;
            }
            case 26: {
              setPass(input.readString());
              break;
            }
          }
        }
      }
      
      
      // required int32 id = 1;
      public boolean hasId() {
        return result.hasId();
      }
      public int getId() {
        return result.getId();
      }
      public Builder setId(int value) {
        result.hasId = true;
        result.id_ = value;
        return this;
      }
      public Builder clearId() {
        result.hasId = false;
        result.id_ = 0;
        return this;
      }
      
      // required string name = 2;
      public boolean hasName() {
        return result.hasName();
      }
      public java.lang.String getName() {
        return result.getName();
      }
      public Builder setName(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasName = true;
        result.name_ = value;
        return this;
      }
      public Builder clearName() {
        result.hasName = false;
        result.name_ = getDefaultInstance().getName();
        return this;
      }
      
      // required string pass = 3;
      public boolean hasPass() {
        return result.hasPass();
      }
      public java.lang.String getPass() {
        return result.getPass();
      }
      public Builder setPass(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasPass = true;
        result.pass_ = value;
        return this;
      }
      public Builder clearPass() {
        result.hasPass = false;
        result.pass_ = getDefaultInstance().getPass();
        return this;
      }
    }
    
    static {
      protocol.PBMessage.getDescriptor();
    }
    
    static {
      protocol.PBMessage.internalForceInit();
    }
  }
  
  public static final class SCUserLoginRet extends
      com.google.protobuf.GeneratedMessage {
    // Use SCUserLoginRet.newBuilder() to construct.
    private SCUserLoginRet() {}
    
    private static final SCUserLoginRet defaultInstance = new SCUserLoginRet();
    public static SCUserLoginRet getDefaultInstance() {
      return defaultInstance;
    }
    
    public SCUserLoginRet getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return protocol.PBMessage.internal_static_protocol_SCUserLoginRet_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return protocol.PBMessage.internal_static_protocol_SCUserLoginRet_fieldAccessorTable;
    }
    
    // required int32 result = 1;
    public static final int RESULT_FIELD_NUMBER = 1;
    private boolean hasResult;
    private int result_ = 0;
    public boolean hasResult() { return hasResult; }
    public int getResult() { return result_; }
    
    public final boolean isInitialized() {
      if (!hasResult) return false;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (hasResult()) {
        output.writeInt32(1, getResult());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasResult()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, getResult());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static protocol.PBMessage.SCUserLoginRet parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static protocol.PBMessage.SCUserLoginRet parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static protocol.PBMessage.SCUserLoginRet parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static protocol.PBMessage.SCUserLoginRet parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static protocol.PBMessage.SCUserLoginRet parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static protocol.PBMessage.SCUserLoginRet parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static protocol.PBMessage.SCUserLoginRet parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeDelimitedFrom(input).buildParsed();
    }
    public static protocol.PBMessage.SCUserLoginRet parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeDelimitedFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static protocol.PBMessage.SCUserLoginRet parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static protocol.PBMessage.SCUserLoginRet parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(protocol.PBMessage.SCUserLoginRet prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private protocol.PBMessage.SCUserLoginRet result;
      
      // Construct using protocol.PBMessage.SCUserLoginRet.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new protocol.PBMessage.SCUserLoginRet();
        return builder;
      }
      
      protected protocol.PBMessage.SCUserLoginRet internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new protocol.PBMessage.SCUserLoginRet();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return protocol.PBMessage.SCUserLoginRet.getDescriptor();
      }
      
      public protocol.PBMessage.SCUserLoginRet getDefaultInstanceForType() {
        return protocol.PBMessage.SCUserLoginRet.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public protocol.PBMessage.SCUserLoginRet build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private protocol.PBMessage.SCUserLoginRet buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public protocol.PBMessage.SCUserLoginRet buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        protocol.PBMessage.SCUserLoginRet returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof protocol.PBMessage.SCUserLoginRet) {
          return mergeFrom((protocol.PBMessage.SCUserLoginRet)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(protocol.PBMessage.SCUserLoginRet other) {
        if (other == protocol.PBMessage.SCUserLoginRet.getDefaultInstance()) return this;
        if (other.hasResult()) {
          setResult(other.getResult());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 8: {
              setResult(input.readInt32());
              break;
            }
          }
        }
      }
      
      
      // required int32 result = 1;
      public boolean hasResult() {
        return result.hasResult();
      }
      public int getResult() {
        return result.getResult();
      }
      public Builder setResult(int value) {
        result.hasResult = true;
        result.result_ = value;
        return this;
      }
      public Builder clearResult() {
        result.hasResult = false;
        result.result_ = 0;
        return this;
      }
    }
    
    static {
      protocol.PBMessage.getDescriptor();
    }
    
    static {
      protocol.PBMessage.internalForceInit();
    }
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_protocol_CSUserLogin_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_protocol_CSUserLogin_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_protocol_SCUserLoginRet_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_protocol_SCUserLoginRet_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017PBMessage.proto\022\010protocol\"5\n\013CSUserLog" +
      "in\022\n\n\002id\030\001 \002(\005\022\014\n\004name\030\002 \002(\t\022\014\n\004pass\030\003 \002" +
      "(\t\" \n\016SCUserLoginRet\022\016\n\006result\030\001 \002(\005"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_protocol_CSUserLogin_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_protocol_CSUserLogin_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_protocol_CSUserLogin_descriptor,
              new java.lang.String[] { "Id", "Name", "Pass", },
              protocol.PBMessage.CSUserLogin.class,
              protocol.PBMessage.CSUserLogin.Builder.class);
          internal_static_protocol_SCUserLoginRet_descriptor =
            getDescriptor().getMessageTypes().get(1);
          internal_static_protocol_SCUserLoginRet_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_protocol_SCUserLoginRet_descriptor,
              new java.lang.String[] { "Result", },
              protocol.PBMessage.SCUserLoginRet.class,
              protocol.PBMessage.SCUserLoginRet.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  public static void internalForceInit() {}
}
