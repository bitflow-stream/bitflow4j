package bitflow4j.steps.database;

import bitflow4j.Sample;
import bitflow4j.misc.Pair;
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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WriteHistogramToElasticsearch implements BatchHandler {

    protected static final Logger logger = Logger.getLogger(WriteHistogramToElasticsearch.class.getName());

    private final String hostPorts;
    private final String indexName;
    private final String identifierKey;
    private final String identifierTemplate;
    private final RestHighLevelClient client;

    /**
     * @param hostPorts          Comma-separated list of host:port pairs, e.g.: host1:port1, host2:port2
     * @param indexName          Index name of the respective index in the Elasticsearch-Database
     * @param identifierKey      Name of key-tag which is saved for each sample and used to identify/filter the entry in
     *                           the database (the same name will be applied in the DB entry as the property name)
     * @param identifierTemplate Used template to fill the named property with meaningful content (Tag-templates
     *                           should be used here)
     */
    public WriteHistogramToElasticsearch(String hostPorts, String indexName, String identifierKey, String identifierTemplate) {
        this.hostPorts = hostPorts;
        this.indexName = indexName;
        this.identifierKey = identifierKey;
        this.identifierTemplate = identifierTemplate;

        List<Pair<String, Integer>> hostPortPairs = convertHostPortArgs(hostPorts);
        HttpHost[] httpHosts = new HttpHost[hostPortPairs.size()];
        for (int i = 0; i < hostPortPairs.size(); i++) {
            Pair<String, Integer> hostport = hostPortPairs.get(i);
            httpHosts[i] = new HttpHost(hostport.getLeft(), hostport.getRight(), "http");
        }
        this.client = new RestHighLevelClient(
                RestClient.builder(httpHosts));
    }

    private static List<Pair<String, Integer>> convertHostPortArgs(String tags) {
        return Arrays.stream(tags.split(",")).map(String::trim)
                .map(s -> {
                    String[] hostPorts = s.split(":");
                    return new Pair<>(hostPorts[0], Integer.valueOf(hostPorts[1]));
                }).collect(Collectors.toList());
    }

    @Override
    public List<Sample> handleBatch(List<Sample> batch) throws IOException {

        // 'Index' in IndexRequest stands for Putting data into the DB
        IndexRequest request = new IndexRequest(indexName);

        request.id();
        // For each sample of the batch
        for (Sample sample : batch) {
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

            request.source(data);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            printResponse(response);
        }
        return batch;
    }

    private void printResponse(IndexResponse indexResponse) {
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            // Object was created in Database
            logger.log(Level.FINE, String.format("Document was created: %s", toString()));
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            logger.log(Level.FINE, String.format("Rewritten existing document: %s", toString()));
        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            logger.log(Level.WARNING, String.format("Total-Shards (%s) did not match successful Shards (%s): %s",
                    shardInfo.getTotal(), shardInfo.getSuccessful(), toString()));
        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure :
                    shardInfo.getFailures()) {
                String reason = failure.reason();
                logger.log(Level.SEVERE, String.format("Failure (%s):\n %s ", toString(), failure.reason()));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("WriteHistogramToElasticSearch: Hostports: %s \nIndex: %s, Identifier = Identifier-Value: %s = %s",
                hostPorts, indexName, identifierKey, identifierTemplate);
    }
}
