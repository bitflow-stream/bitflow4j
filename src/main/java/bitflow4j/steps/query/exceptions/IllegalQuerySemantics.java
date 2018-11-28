package bitflow4j.steps.query.exceptions;

/**
 * This exception is thrown if there is a error with the semantic of a query
 * string.
 */
public class IllegalQuerySemantics extends IllegalStreamingQueryException {

	public IllegalQuerySemantics(String query) {
		super(query);
	}

}
