package bitflow4j.steps.database;

import bitflow4j.Sample;
import bitflow4j.steps.BatchHandler;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteHistogramToElasticsearch implements BatchHandler {

    protected static final Logger logger = Logger.getLogger(WriteHistogramToElasticsearch.class.getName());

    private final String indexName;
    private final String identifierKey;
    private final String identifierTemplate;
    private final RestHighLevelClient client;
    public WriteHistogramToElasticsearch(String host, int port1, int port2, String indexName, String identifierKey, String identifierTemplate) {
        this.indexName = indexName;
        this.identifierKey = identifierKey;
        this.identifierTemplate = identifierTemplate;
        this.client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port1, "http"),
                        new HttpHost(host, port2, "http")));
    }

    @Override
    public List<Sample> handleBatch(List<Sample> batch) throws IOException {

        IndexRequest request = new IndexRequest(indexName);
        request.id();
        for (int i = 0; i < batch.size(); i++) {
            // For each sample of the batch
            Sample sample = batch.get(i);
            double[] metrics = sample.getMetrics();

            //Generate the data point which represents one frequency with its amplitudes (all have the same timestamp)
            XContentBuilder data = XContentFactory.jsonBuilder();
            data.startObject()
                    .field(identifierKey, sample.resolveTagTemplate(identifierTemplate))
                    .field("timestamp", sample.getTimestamp().getTime());

            for (int j = 0; j < metrics.length; j++) {
                data.field(sample.getHeader().header[j], metrics[j]);
            }
            data.endObject();

            //Index stands for Putting data
            request.source(data);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            printResponse(response);
        }

        return batch;
    }
    private void printResponse(IndexResponse indexResponse){
        String index = indexResponse.getIndex();
        String id = indexResponse.getId();
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            // Object was created in Database
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            logger.log(Level.INFO, "Rewritten existing document");
        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            // Something with shards.
        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure :
                    shardInfo.getFailures()) {
                String reason = failure.reason();
                logger.log(Level.SEVERE, String.format("Failure: %s ", failure.reason()));
            }
        }
    }
}
