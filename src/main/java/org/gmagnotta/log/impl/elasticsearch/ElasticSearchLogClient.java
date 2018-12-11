package org.gmagnotta.log.impl.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.gmagnotta.log.LogEvent;

import java.io.IOException;

public class ElasticSearchLogClient {

    private RestHighLevelClient client;
    private BulkProcessor bulkProcessor;

    BulkProcessor.Listener listener = new BulkProcessor.Listener() {
        @Override
        public void beforeBulk(long executionId, BulkRequest request) {

        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {

        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, Throwable failure) {

        }
    };

    public ElasticSearchLogClient(String host, int port, String protocol) {
        HttpHost host1 = new HttpHost(host, port, protocol);
        RestClientBuilder clientBuilder = RestClient.builder(host1);
        client = new RestHighLevelClient(clientBuilder);

        BulkProcessor.Builder bulkBuilder = BulkProcessor.builder(
                (request, bulkListener) ->
                        client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                listener);

        /*
         *  More info https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-document-bulk.html
         */
        bulkBuilder.setBulkActions(500);    // flush bulk when reach number of actions
        bulkBuilder.setBulkSize(new ByteSizeValue(5L, ByteSizeUnit.MB));   // flush bulk request when reach size
        bulkBuilder.setConcurrentRequests(0);   // use only single request to send bulk actions
        bulkBuilder.setFlushInterval(TimeValue.timeValueSeconds(10L));  // flush bulk request when reach time
        bulkBuilder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3));

        bulkProcessor = bulkBuilder.build();
    }

    public void close() throws IOException {
        bulkProcessor.close();
        client.close();
    }

    public void createLogIndex(String index, String type) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.mapping(type,
                "date", "type=date",
                "logLevel", "type=keyword");

        client.indices().create(request, RequestOptions.DEFAULT);
    }

    /*public void renameIndex(String srcIndexName, String dstIndexName) throws IOException {

        createLogIndex(dstIndexName);

        ReindexRequest request = new ReindexRequest();
        request.setSourceIndices(srcIndexName);
        request.setDestIndex(dstIndexName);

        client.reindexAsync(request, RequestOptions.DEFAULT, new ActionListener<BulkByScrollResponse>() {
            @Override
            public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }*/

    public boolean isLogIndexExist(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(indexName);

        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    public void putLogEvent(String index, String type, LogEvent logEvent) {
        IndexRequest request = new IndexRequest(index, type, null);
        request.source("date", logEvent.getDate().getTime(),
                "logLevel", logEvent.getLogLevel(),
                "sourceClass", logEvent.getSourceClass(),
                "thread", logEvent.getThreadName(),
                "message", logEvent.getMessage());
        bulkProcessor.add(request);
    }

}
