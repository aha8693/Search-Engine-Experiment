package hw7.hashing;

import hw7.Map;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChainingHashMap<K, V> implements Map<K, V> {
  private int tableSize;
  private int numElements;
  private ArrayList<Data<K,V>>[] table;
  private float loadFactor;
  private int version;

  private static class Data<K, V> {
    K key;
    V value;

    // Constructor to make Data creation easier to read.
    Data(K k, V v) {
      key = k;
      value = v;
    }
  }

  /**
   * Constructor for Chaining Hash Map.
   */
  public ChainingHashMap() {
    tableSize = 10;
    numElements = 0;
    loadFactor = 0;
    table = new ArrayList[tableSize];
    for (int i = 0; i < tableSize; i++) {
      table[i] = null;
    }
  }

  @Override
  public void insert(K k, V v) throws IllegalArgumentException {
    if (k == null || has(k)) {
      throw new IllegalArgumentException("invalid key to insert (null or duplicate)");
    }
    if (needRehash()) {
      rehash();
    }
    int index = getHashCode(k);
    ArrayList<Data<K,V>> list;

    if (table[index] == null) {
      list = new ArrayList<>();
      list.add(new Data<>(k,v));
      table[index] = list;
    } else {
      list = table[index];
      list.add(new Data<>(k,v));
    }
    numElements++;
    version++;
  }

  private void rehash() {
    tableSize *= 2;
    ArrayList<Data<K,V>>[] temp = table;
    table = new ArrayList[tableSize];
    for (int i = 0; i < tableSize; i++) {
      table[i] = null;
    }
    for (ArrayList<Data<K, V>> list : temp) {
      if (list == null) {
        continue;
      }
      for (Data<K, V> data : list) {
        insert(data.key, data.value); //insert into new hash table
        numElements--; // not adding new key but merely copying
      }
    }
  }

  @Override
  public V remove(K k) throws IllegalArgumentException {
    if (k == null || !has(k)) {
      throw new IllegalArgumentException("invalid key to put (null or no key)");
    }

    int index = getHashCode(k);
    Data<K,V> data = null;
    V value = null;

    ArrayList<Data<K,V>> list = table[index];
    for (Data<K, V> element : list) {
      if (k.equals(element.key)) {
        data = element; // prevent concurrent modification error
        value = data.value;
      }
    }
    list.remove(data);
    numElements--;
    version++;
    return value;
  }

  @Override
  public void put(K k, V v) throws IllegalArgumentException {
    if (k == null) {
      throw new IllegalArgumentException("null key");
    }
    Data<K,V> found = find(k);
    if (found == null) {
      throw new IllegalArgumentException("no key found");
    }
    found.value = v; // k = find for sure
  }

  @Override
  public V get(K k) throws IllegalArgumentException {
    if (k == null) {
      throw new IllegalArgumentException("Cannot search for null key");
    }
    Data<K, V> element = find(k);
    if (element == null) {
      throw new IllegalArgumentException("No such element");
    }
    return element.value;
  }

  @Override
  public boolean has(K k) {
    if (k == null) {
      return false;
    }
    return find(k) != null;
  }

  private Data<K,V> find(K k) {
    int index = getHashCode(k);
    if (table[index] == null) {
      return null;
    }
    ArrayList<Data<K, V>> head = table[index];
    for (Data<K,V> element : head) {
      if (element == null) {
        continue;
      }
      if (k.equals(element.key)) {
        return element;
      }
    }
    return null;
  }

  private int getHashCode(K k) {
    int index = k.hashCode() % tableSize;
    if (index < 0) {
      index += tableSize;
    }
    return index;
  }

  private float setLoadFactor() {
    loadFactor = (float) numElements / (float) tableSize;
    return loadFactor;
  }

  private boolean needRehash() {
    return setLoadFactor() > 0.75; // threshold for Java's built-in Hash Table
  }

  @Override
  public int size() {
    return numElements;
  }

  @Override
  public Iterator<K> iterator() {
    return new ChainIterator();
  }

  private class ChainIterator implements Iterator<K> {
    int index;
    private final int itVersion;
    private final ArrayList<K> keys;

    ChainIterator() {
      itVersion = ChainingHashMap.this.version;
      keys = new ArrayList();
      for (ArrayList<Data<K,V>> list : table) {
        if (list == null) {
          continue;
        }
        for (Data<K,V> element:list) {
          keys.add(element.key);
        }
      }
    }

    @Override
    public boolean hasNext() {
      return index < numElements;
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      if (itVersion != ChainingHashMap.this.version) { // fail-fast
        throw new ConcurrentModificationException();
      }
      return keys.get(index++);

    }
  }

}
