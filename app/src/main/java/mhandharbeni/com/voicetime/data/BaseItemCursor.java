package mhandharbeni.com.voicetime.data;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

public abstract class BaseItemCursor<T extends ObjectWithId> extends CursorWrapper {
    private static final String TAG = "BaseItemCursor";

    public BaseItemCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * @return an item instance configured for the current row,
     * or null if the current row is invalid
     */
    public abstract T getItem();

    public long getId() {
        if (isBeforeFirst() || isAfterLast()) {
            Log.e(TAG, "Failed to retrieve id, cursor out of range");
            return -1;
        }
        return getLong(getColumnIndexOrThrow("_id")); // TODO: Refer to a constant instead of a hardcoded value
    }

    /**
     * Helper method to determine boolean-valued columns.
     * SQLite does not support a BOOLEAN data type.
     */
    protected boolean isTrue(String columnName) {
        return getInt(getColumnIndexOrThrow(columnName)) == 1;
    }
}
