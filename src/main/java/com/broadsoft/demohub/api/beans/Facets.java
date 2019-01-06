package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Facets{
private String facet;
private String value;
private Filter filter;
@JsonInclude(JsonInclude.Include.NON_NULL)
private Facets[] facets;
public String getFacet() {
	return facet;
}
public void setFacet(String facet) {
	this.facet = facet;
}
public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}
public Filter getFilter() {
	return filter;
}
public void setFilter(Filter filter) {
	this.filter = filter;
}
public Facets[] getFacets() {
	return facets;
}
public void setFacets(Facets[] facets) {
	this.facets = facets;
}

}
