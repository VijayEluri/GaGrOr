package se.spacejens.gagror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic system exception. Subclasses are more specific.
 * 
 * @author spacejens
 */
public class GagrorException extends Exception implements LogAware {

	/** Required by {@link java.io.Serializable} interface. */
	private static final long serialVersionUID = 1L;

	/** Logger to use for this object. */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public GagrorException() {
		this.getLog().debug("Creating instance");
	}

	public GagrorException(final String message) {
		super(message);
		this.getLog().debug("Creating instance with message");
	}

	public GagrorException(final Throwable cause) {
		super(cause);
		this.getLog().debug("Creating instance with cause");
	}

	public GagrorException(final String message, final Throwable cause) {
		super(message, cause);
		this.getLog().debug("Creating instance with message and cause");
	}

	@Override
	public Logger getLog() {
		return this.log;
	}
}
