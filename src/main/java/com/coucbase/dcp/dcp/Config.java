package com.coucbase.dcp.dcp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;

import java.util.Arrays;
import java.util.List;

@Configuration
//@EnableCouchbaseRepositories
public class Config extends AbstractCouchbaseConfiguration {

    @Value("${couchbase.host}")
    private String host;

    @Value("${couchbase.bucket.bucketName}")
    private String bucketName;

    @Value("${couchbase.bucket.password}")
    private String password;

    @Override
    protected List<String> getBootstrapHosts() {
        return Arrays.asList(this.host);
    }

    @Override
    protected String getBucketName() {
        return this.bucketName;
    }

    @Override
    protected String getBucketPassword() {
        return this.password;
    }

    @Override
    public String typeKey() {
        return MappingCouchbaseConverter.TYPEKEY_SYNCGATEWAY_COMPATIBLE;
    }
}
