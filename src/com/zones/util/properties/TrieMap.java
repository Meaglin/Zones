package com.zones.util.properties;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implements very fast dictionary storage and retrieval.
 * Only depends upon the core String class.
 * 
 * @author Melinda Green - © 2010 Superliminal Software.
 * Free for all uses with attribution.
 */
public class TrieMap<V> implements Map<String, V>{
    /*
     * Implementation of a trie tree. (see http://en.wikipedia.org/wiki/Trie)
     * though I made it faster and more compact for long key strings 
     * by building tree nodes only as needed to resolve collisions.
     * Each letter of a key is the index into the following array.
     * Values stored in the array are either a Leaf containing the user's value or
     * another TrieMap node if more than one key shares the key prefix up to that point.
     * Null elements indicate unused, I.E. available slots.
     */
    private Object[] mChars = new Object[256];
    private Object mPrefixVal; // Used only for values of prefix keys.
    
    // Simple container for a string-value pair.
    private static class Leaf{
        public String mStr;
        public Object mVal;
        public Leaf(String str, Object val) {
            mStr = str;
            mVal = val;
        }
        
		@Override
		public int hashCode() {
			return (mStr == null ? 0 : mStr.hashCode()) ^ (mVal == null ? 0 : mVal.hashCode());
		}
    }
    class LeafEntry	implements Map.Entry<String, V> {

    	private final Leaf l;
    	public LeafEntry(Leaf l) {
    		this.l = l;
    	}
		@Override
		public String getKey() {
			return l.mStr;
		}

		@SuppressWarnings("unchecked")
		@Override
		public V getValue() {
			return (V) l.mVal;
		}

		@Override
		public V setValue(V value) {
			l.mVal = value;
			return null;
		}
		
		@Override
		public int hashCode() {
			return l.hashCode();
		}
    	
    }
    
    public TrieMap() {
    }

    public boolean isEmpty() {
        if(mPrefixVal != null) {
            return false;
        }
        for(Object o : mChars) {
            if(o != null) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Inserts a key/value pair.
     * 
     * @param key may be empty or contain low-order chars 0..255 but must not be null.
     * @param val Your data. Any data class except another TrieMap. Null values erase entries.
     */
    @SuppressWarnings("unchecked")
	private void put0(String key, Object val) {
        assert key != null;
        assert !(val instanceof TrieMap); // Only we get to store TrieMap nodes. TODO: Allow it.
        if(key.length() == 0) {
            // All of the original key's chars have been nibbled away 
            // which means this node will store this key as a prefix of other keys.
            mPrefixVal = val; // Note: possibly removes or updates an item.
            return;
        }
        char c = key.charAt(0);
        Object cObj = mChars[c];
        if(cObj == null) { // Unused slot means no collision so just store and return;
            if(val == null) {
                return; // Don't create a leaf to store a null value.
            }
            mChars[c] = new Leaf(key, val);
            return;
        }
        if(cObj instanceof TrieMap) {
            // Collided with an existing sub-branch so nibble a char and recurse.
            TrieMap<V> childTrie = (TrieMap<V>)cObj;
            childTrie.put0(key.substring(1), val);
            if(val == null && childTrie.isEmpty()) {
                mChars[c] = null; // put() must have erased final entry so prune branch.
            }
            return;
        }
        // Collided with a leaf 
        if(val == null) {
            mChars[c] = null; // Null value means to remove any previously stored value.
            return;
        }
        assert cObj instanceof Leaf;
        // Sprout a new branch to hold the colliding items.
        Leaf cLeaf = (Leaf)cObj;
        TrieMap<V> branch = new TrieMap<V>();
        branch.put0(key.substring(1), val); // Store new value in new subtree.
        branch.put0(cLeaf.mStr.substring(1), cLeaf.mVal); // Plus the one we collided with.
        mChars[c] = branch;
    }


    /**
     * Retrieve a value for a given key or null if not found.
     */
    @SuppressWarnings("unchecked")
	private Object get0(String key) {
        assert key != null;
        if(key.length() == 0) {
            // All of the original key's chars have been nibbled away 
            // which means this key is a prefix of another.
            return mPrefixVal;
        }
        char c = key.charAt(0);
        Object cVal = mChars[c];
        if(cVal == null) {
            return null; // Not found.
        }
        assert cVal instanceof Leaf || cVal instanceof TrieMap;
        if(cVal instanceof TrieMap) { // Hash collision. Nibble first char, and recurse.
            return ((TrieMap<V>)cVal).get0(key.substring(1));
        }
        if(cVal instanceof Leaf) {
            // cVal contains a user datum, but does the key match its substring?
            Leaf cPair = (Leaf)cVal;
            if(key.equals(cPair.mStr)) {
                return cPair.mVal; // Return user's data value.
            }
        }
        return null; // Not found.
    }

	@SuppressWarnings("unchecked")
	@Override
	public Set<java.util.Map.Entry<String, V>> entrySet() {
		Set<Entry<String,V>> set = new HashSet<Entry<String, V>>();
		if(this.mPrefixVal != null) set.add(new LeafEntry(new Leaf("", mPrefixVal)));
		for(Object o : mChars) {
			if(o == null) continue;
			if(o instanceof Leaf) {
				set.add(new LeafEntry(((Leaf)o)));
			} else if(o instanceof TrieMap) {
				set.addAll(((TrieMap<V>)o).entrySet());
			}
		}
		return set;
	}

	@Override
	public int size() {
		int size = 0;
		for(Object o : mChars) {
			if(o == null) continue;
			if(o instanceof Leaf) {
				size++;
				continue;
			}
			if(o instanceof TrieMap) {
				size += ((TrieMap<?>)o).size();
			}
		}
		return size;
	}

	@Override
	public boolean containsKey(Object key) {
		if(!(key instanceof String)) return false;
		return get0((String)key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		if(value == null) return false;
		if(this.mPrefixVal != null && mPrefixVal.equals(value)) return true;
		for(Object o : mChars) {
			if(o == null) return false;
			if(o instanceof Leaf) {
				if(((Leaf)o).mVal.equals(value))
					return true;
			} else if (o instanceof TrieMap) {
				if(((TrieMap<?>)o).containsValue(value))
					return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		if(key == null || !(key instanceof String)) return null;
		return (V) get0((String)key);
	}

	@Override
	public V put(String key, V value) {
		put0(key, value);
		return value;
	}

	@Override
	public V remove(Object key) {
		if(key == null || !(key instanceof String)) return null;
		put0((String)key, null);
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> m) {
		for(java.util.Map.Entry<? extends String, ? extends V> e : m.entrySet()) {
			put0(e.getKey(), e.getValue());
		}
	}

	@Override
	public void clear() {
		mChars = new Object[256];
	}

	@Override
	public Set<String> keySet() {
		Set<String> keys = new HashSet<String>();
		if(mPrefixVal != null) keys.add("");
		for(Object o : mChars) {
			if(o == null) continue;
			if(o instanceof Leaf) {
				keys.add(((Leaf)o).mStr);
			} else if (o instanceof TrieMap) {
				keys.addAll(((TrieMap<?>)o).keySet());
			}
		}
		return keys;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<V> values() {
		Set<V> values = new HashSet<V>();
		if(mPrefixVal != null) values.add((V) mPrefixVal);
		for(Object o : mChars) {
			if(o == null) continue;
			if(o instanceof Leaf) {
				values.add((V) ((Leaf)o).mVal);
			} else if (o instanceof TrieMap) {
				values.addAll(((TrieMap<V>)o).values());
			}
		}
		return values;
	}
}
