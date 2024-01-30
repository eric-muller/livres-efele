package org.esciurus.model.opf;

/**
 * A target for a crossref pointer.
 * Crossref pointers will register with targets as they establish
 * links to them, and will unregister when the link is dropped.
 * 
 * @see XrefPointer
 */
public interface XrefTarget {

	/**
	 * Register a link to this target.
	 * 
	 * @param p the pointer which now points to this target
	 */
	public abstract void registerLink(XrefPointer p);
	
	/**
	 * Unregister a link to this target.
	 * @param p the pointer which previously pointed to this target.
	 */
	public abstract void unregisterLink(XrefPointer p);
	
	/**
	 * Retrieve the ID value used for this target when persisting
	 * it to XML representation.
	 * 
	 * @return the ID value
	 */
	public abstract String getPersistenceId();
	
}
