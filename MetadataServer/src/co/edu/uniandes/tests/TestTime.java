package co.edu.uniandes.tests;

public class TestTime {
	private long initialTime;
	private long endTime;
	
	/**
	 * @return the initialTime
	 */
	public long getInitialTime() {
		return initialTime;
	}
	
	/**
	 * @param initialTime the initialTime to set
	 */
	public void setInitialTime(long initialTime) {
		this.initialTime = initialTime;
	}
	
	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TestTime [initialTime=" + initialTime + ", endTime=" + endTime
				+ "]";
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
