import java.util.HashMap;

class CommandHashMap<C> extends HashMap<String, C> {
    @Override
    public C get(Object key) {
        for (Entry<String, C> entry : entrySet()) {
            String k = (String) key;
            if (k.toUpperCase().matches(entry.getKey().toUpperCase()))
                return entry.getValue();
        }
        return null;
    }

}
