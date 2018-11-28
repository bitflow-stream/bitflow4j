package bitflow4j.steps.query.exceptions;

/**
 * This exception is thrown if a sample misses a tag that is needed from the
 * query.
 */
public class MissingTagException extends IllegalHeaderException {

	public MissingTagException(String query) {
		super(query);
	}

}
