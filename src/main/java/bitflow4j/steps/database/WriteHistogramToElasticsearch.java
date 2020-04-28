package bitflow4j.steps.database;

import bitflow4j.Sample;
import bitflow4j.steps.BatchProcessingStep;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteHistogramToElasticsearch extends BatchProcessingStep.Batching {

    protected static final Logger logger = Logger.getLogger(WriteHistogramToElasticsearch.class.getName());

    private final ElasticsearchUtil elasticsearchUtil;

    /**
     * @param hostPorts          Comma-separated list of host:port pairs, e.g.: host1:port1, host2:port2
     * @param indexName          Index name of the respective index in the Elasticsearch-Database
     * @param identifierKey      Name of key-tag which is saved for each sample and used to identify/filter the entry in
     *                           the database (the same name will be applied in the DB entry as the property name)
     * @param identifierTemplate Used template to fill the named property with meaningful content (Tag-templates
     *                           should be used here)
     */
    public WriteHistogramToElasticsearch(List<String> hostPorts, String indexName, String identifierKey, String identifierTemplate, String batchSeparationTag, long timeoutMs) throws IOException {
        super(batchSeparationTag, timeoutMs);
        elasticsearchUtil = new ElasticsearchUtil(hostPorts, indexName, identifierKey, identifierTemplate);
    }

    @Override
    public List<Sample> handleBatch(List<Sample> batch) {
        if (batch.size() > 0) {
            try {
                elasticsearchUtil.write(batch);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to write to Elasticsearch", e);
            }
        }
        return batch;
    }

    @Override
    public String toString() {
        return elasticsearchUtil.toString();
    }
}
