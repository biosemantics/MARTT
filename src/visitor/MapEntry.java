package visitor;

/**
 * <p>Title: MapEntry</p>
 * <p>Description: a key-value pair</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class MapEntry {
  Object key = null;
  Object value = null;
  Object info = null;

  public MapEntry(Object key, Object value) {
    this.key = key;
    this.value = value;
  }

  public MapEntry(Object key, Object value, Object info) {
    this.key = key;
    this.value = value;
    this.info = info;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public void setKey(Object key) {
    this.key = key;
  }

  public void setInfo(Object info) {
    this.info = info;
  }

  public Object getKey() {
    return key;
  }

  public Object getInfo() {
    return info;
  }

  public Object getValue() {
    return value;
  }

}