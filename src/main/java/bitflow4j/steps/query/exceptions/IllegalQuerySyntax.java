package bitflow4j.steps.query.exceptions;

/**
 * This exception is thrown if a query string can not be parsed without an
 * error.
 */
public class IllegalQuerySyntax extends IllegalStreamingQueryException {

	public IllegalQuerySyntax(String query) {
		super(query);
	}

}
