package org.exoplatform.social.service.rest.api.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpaceMembershipsCollections extends ResourceCollections{
  
  private List<Map<String, String>> spaceMemberships = new ArrayList<Map<String, String>>();
  
  public SpaceMembershipsCollections(int size, int offset, int limit) {
    super(size, offset, limit);
  }

  /**
   * @return the spaceMemberships
   */
  public List<Map<String, String>> getSpaceMemberships() {
    return spaceMemberships;
  }

  /**
   * @param spaceMemberships the spaceMemberships to set
   */
  public void setSpaceMemberships(List<Map<String, String>> spaceMemberships) {
    this.spaceMemberships = spaceMemberships;
  }
  
}
