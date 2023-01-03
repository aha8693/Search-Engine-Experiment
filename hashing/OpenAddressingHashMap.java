package hw7.hashing;

import hw7.Map;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class OpenAddressingHashMap<K, V> implements Map<K, V> {
  private int tableSize;
  private int numElements;
  private int genElements; // actual data + tombstone data
  private Data<K,V>[] table;
  private float loadFactor;
  private int version; // for fail-fast

  private static class Data<K, V> {
    K key;
    V value;
    boolean isTomb;

    // Constructor to make Data creation easier to read.
    Data(K k, V v) {
      key = k;
      value = v;
      isTomb = false;
    }

    private void tomb() {
      key = null;
      value = null;
      isTomb = true;
    }
  }

  /**
   * Constructor for Open Addressing Hash Map.
   */
  public OpenAddressingHashMap() {
    tableSize = 10;
    numElements = 0;
    loadFactor = 0;

    table = (Data<K, V>[]) Array.newInstance(Data.class, tableSize);
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
    insertHelper(table, k, v);
    numElements++;
    genElements++;
    version++;
  }

  private void insertHelper(Data<K,V>[] array, K k, V v) {
    int index = getHashCode(k);
    if (array[index] == null) {
      array[index] = new Data<K,V>(k, v);
    } else {
      for (int i = 0; i < tableSize; i++) { // linear probing
        int newIndex = (index + i) % tableSize;
        if (array[newIndex] == null) {
          array[newIndex] = new Data<K, V>(k, v);
          return;
        }
      }
    }
  }

  private void rehash() {
    tableSize *= 2;
    Data<K,V>[] temp = table;
    table = (Data<K, V>[]) Array.newInstance(Data.class, tableSize);
    for (int i = 0; i < tableSize; i++) {
      table[i] = null;
    }
    for (Data<K,V> data : temp) {
      if (data == null) {
        continue;
      }
      if (data.isTomb) {
        continue;
      }
      insertHelper(table, data.key, data.value); //insert into new hash table
    }
    genElements = numElements; // resetting genElements for rehashed table (no tombstone exists)
  }

  @Override
  public V remove(K k) throws IllegalArgumentException {
    if (k == null || !has(k)) {
      throw new IllegalArgumentException("invalid key to put (null or no key)");
    }
    V value = null;
    value = removeHelper(table, k, value);
    numElements--;
    version++;
    return value;
  }

  private V removeHelper(Data<K,V>[] array, K k, V v) {
    int index = getHashCode(k);
    if (k.equals(table[index].key)) {
      v = table[index].value;
      table[index].tomb();
    } else {
      for (int i = 0; i < tableSize; i++) { // linear probing
        int newIndex = (index + i) % tableSize;
        if (table[newIndex] == null) {
          continue;
        }
        if (k.equals(table[newIndex].key)) {
          v = table[newIndex].value;
          table[newIndex].tomb();
          break;
        }
      }
    }
    return v;

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
    if (k.equals(table[index].key)) {
      return table[index];
    } else {
      for (int i = 0; i < tableSize; i++) { // linear probing
        int newIndex = (index + i) % tableSize;
        if (table[newIndex] == null) {
          continue;
        }
        if (k.equals(table[newIndex].key)) {
          return table[newIndex];
        }
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
    loadFactor = (float) genElements / (float) tableSize;
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
    return new OaIterator();
  }

  private class OaIterator implements Iterator<K> {
    private int cur;
    private final int itVersion;
    private final Data<K,V>[] array = (Data<K, V>[]) Array.newInstance(Data.class, numElements);

    OaIterator() {
      int index = 0;
      itVersion = OpenAddressingHashMap.this.version;
      for (Data<K,V> element:table) {
        if (element == null || element.isTomb) {
          continue;
        }
        array[index++] = element;
      }
    }

    @Override
    public boolean hasNext() {
      return cur < numElements;
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      if (itVersion != OpenAddressingHashMap.this.version) { // fail-fast
        throw new ConcurrentModificationException();
      }

      return array[cur++].key;
    }
  }
}
