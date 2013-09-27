package edu.wustl.query.dto;

public class QueryDTO {

	private Long queryId;
	private String queryName;
	private String queryDescription;
	private String executedOn;
	private String rootEntityName;
	private String countOfRootRecords;
	private String ownerName;

	public Long getQueryId() {
		return queryId;
	}

	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getQueryDescription() {
		return queryDescription;
	}

	public void setQueryDescription(String queryDescription) {
		this.queryDescription = queryDescription;
	}

	public String getExecutedOn() {
		return executedOn;
	}

	public void setExecutedOn(String executedOn) {
		this.executedOn = executedOn;
	}

	public String getRootEntityName() {
		return rootEntityName;
	}

	public void setRootEntityName(String rootEntityName) {
		this.rootEntityName = rootEntityName;
	}

	public String getCountOfRootRecords() {
		return countOfRootRecords;
	}

	public void setCountOfRootRecords(String countOfRootRecords) {
		this.countOfRootRecords = countOfRootRecords;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
}
