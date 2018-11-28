package bitflow4j.steps.query.exceptions;

/**
 * This exception is thrown if a sample misses a metric that is needed from the
 * query.
 */
public class MissingMetricException extends IllegalHeaderException {

	public MissingMetricException(String query) {
		super(query);
	}

}
