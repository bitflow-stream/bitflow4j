package bitflow4j.steps.fork;

import bitflow4j.Pipeline;
import bitflow4j.misc.Pair;
import bitflow4j.misc.TreeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface ScriptableDistributor extends Distributor, TreeFormatter.FormattedNode {

    default void setSubPipelines(Object... selectorsAndPipelines) throws IOException {
        setSubPipelines(subPipelines(selectorsAndPipelines));
    }

    default void setStaticSubPipelines(Collection<Pair<String, Pipeline>> subPipelines) throws IOException {
        setSubPipelines(subPipelines(subPipelines));
    }

    void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException;

    static Collection<Object> formattedSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) {
        List<Object> list = new ArrayList<>();
        for (Pair<String, PipelineBuilder> p : subPipelines) {
            try {
                list.add(new TitledPipeline(p.getLeft(), p.getRight()));
            } catch (IOException e) {
                throw new IllegalStateException("Failed to build example-pipeline-instance for formatting", e);
            }
        }
        return list;
    }

    static Collection<Object> formattedStaticSubPipelines(Collection<Pair<String, Pipeline>> subPipelines) {
        List<Object> list = new ArrayList<>();
        for (Pair<String, Pipeline> p : subPipelines) {
            list.add(new TitledPipeline(p.getLeft(), p.getRight()));
        }
        return list;
    }

    interface PipelineBuilder {
        Pipeline build() throws IOException;
    }

    static PipelineBuilder subPipeline(Pipeline pipeline) {
        return () -> pipeline;
    }

    static Collection<Pair<String, PipelineBuilder>> subPipelines(Collection<Pair<String, Pipeline>> subPipelines) {
        return subPipelines.stream().map((p) -> new Pair<>(p.getLeft(), subPipeline(p.getRight()))).collect(Collectors.toList());
    }

    static Collection<Pair<String, PipelineBuilder>> subPipelines(Object... selectorsAndPipelines) {
        Collection<Pair<String, PipelineBuilder>> result = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (Object obj : selectorsAndPipelines) {
            if (obj instanceof String) {
                labels.add((String) obj);
            } else if (obj instanceof Pipeline || obj instanceof PipelineBuilder) {
                if (labels.isEmpty()) {
                    throw new IllegalArgumentException("subPipelines(...): Pipelines must be preceded by at least one String label");
                }
                PipelineBuilder builder;
                if (obj instanceof Pipeline) {
                    builder = subPipeline((Pipeline) obj);
                } else {
                    builder = (PipelineBuilder) obj;
                }
                for (String label : labels) {
                    result.add(new Pair<>(label, builder));
                }
                labels.clear();
            } else {
                throw new IllegalArgumentException("subPipelines(...): Arguments must be of type String, Pipeline or ProcessingStepBuilder. Received: " + obj.getClass().getName());
            }
        }
        return result;
    }

    class TitledPipeline implements TreeFormatter.FormattedNode {

        private final String title;
        private final Pipeline pipeline;

        public TitledPipeline(String title, Pipeline pipeline) {
            this.title = title;
            this.pipeline = pipeline;
        }

        public TitledPipeline(String title, PipelineBuilder pipeline) throws IOException {
            // Build one example instance of the pipeline in order to format the pipeline steps
            this(title, pipeline.build());
        }

        @Override
        public String toString() {
            return title;
        }

        @Override
        public Collection<Object> formattedChildren() {
            return pipeline.formattedChildren();
        }

    }

}
